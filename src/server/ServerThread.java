package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.DoubleToIntFunction;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import clients.groupChat;
import com.sun.tools.jconsole.JConsolePlugin;
import server.repo.userRepo;

public class ServerThread implements Runnable {
    static final String URL = "jdbc:postgresql://localhost:5432/chatting-application";
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String USER = "postgres";
    static final String PW = "123456";

    private Socket socketOfServer;
    private String userID;//SocketID
    private String actual_userID;
    private BufferedReader is;
    private BufferedWriter os;
    private boolean isClosed;

    private static final String GET_ADMIN_INGROUP_SQL = "select p.id,p.name from public.\"groups\" u join public.\"users\" "
            + "p on p.id = any(u.admin) where u.id = '?' group by p.id,p.name";

    public BufferedReader getIs() {
        return is;
    }

    public void setuserID(String id) {
        userID = id;
    }

    public String getUserID() {
        return userID;
    }

    public BufferedWriter getOs() {
        return os;
    }

    public String getuserID() {
        return userID;
    }

    public String getActualUserID() {
        return actual_userID;
    }

    public ServerThread(Socket socketOfServer, String userID) {
        this.socketOfServer = socketOfServer;
        this.userID = userID;
        System.out.println("Server thread number " + userID + " started");
        isClosed = false;
    }

    @Override
    public void run() {
        try {
            // Mở luồng vào ra trên Socket tại Server.
            is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
            os = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));
            System.out.println("Khởi động luông mới thành công, ID là: " + userID);
//            Server.serverThreadBus.sendOnlineList();
            //Server.serverThreadBus.mutilCastSend("global-message"+","+"---Client "+this.userID+" đã đăng nhập---");
            Server.serverThreadBus.boardCast(userID, "id|" + userID);

            String message;
            while (!isClosed) {
            	message = is.readLine();
                if (message == null) {
                    break;
                }
                System.out.println("cmd:"+" "+message);
                String[] messageSplit = message.split("\\|");
                String commandString = messageSplit[0];
                System.out.println(commandString);
                if(commandString.equals("Register")) {
                	
                	if(userRepo.register(messageSplit[1],messageSplit[2],messageSplit[3],messageSplit[4],messageSplit[5])) {
                		Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "Register_Success|");
                	}
                }else if(commandString.equals("Login")) {
                    String result = userRepo.Login(messageSplit[1], messageSplit[2]);
                    System.out.println(result);
                    
                    if(!result.equals("")) {
                    	actual_userID = result.split("\\|")[0];
                		Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1],"Login_Success|"+result);
                		if (result.split("\\|")[4].equals("false")) {
                            String onlineList = GetListFriendsAndGroups(actual_userID);
                            Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "OnlineList"+onlineList);
                        }
                       
                    }else {
                		Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1],"LoginFailed");
                	}
                }else if(commandString.equals("OnlineList")) {
                	String onlineList = GetListFriendsAndGroups(messageSplit[1]);
                	Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "OnlineList"+onlineList);
                }else if (commandString.equals("MessageData")) {
                	if(messageSplit[1].equals("user")) {
	                    String id1 = messageSplit[2];//Người gửi
	                    String id2 = messageSplit[3];//Từ user
	                    String[] mess = GetMessage(id1+"|"+ id2);
	                    if(!mess[0].equals("")) {
	                    	Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "MessageData|"+mess[0]+"||"+mess[1]);
	                    }
                    }else if(messageSplit[1].equals("group")) {
                    	String id = messageSplit[2];//Người gửi
	                    String[] mess = GetGroup(id);
	                    if(!mess[0].equals("")) {
	                    	Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "GroupData|"+mess[0]+"|||"+mess[1] +"|" + mess[2]);
	                    }
                    }
                }else if (commandString.equals("DirectMessage")) {
                    String id1 = messageSplit[1];//Người gửi
                    String id2 = messageSplit[2];//Từ user
                    String content = messageSplit[3];
                    String[] mess = CheckMessageExists(id1, id2);
                    if (!mess[0].equals("")) {
                        ServerThread.UpdateExistsMessage(id1, id2, content);
                    } else {
                        ServerThread.InsertMessage(id1, id2, content);
                    }
                    Server.serverThreadBus.boardCastUser(id2, "SendToUser|" + id1 + "|" + content);
                }else if (commandString.equals("CreateGroup")) {
                    System.out.println("CreateGroup "+ message);
                    String[] newDataString = message.split("\\|\\|");
                    String name = newDataString[1];//Người gửi
                    String id = newDataString[2];//Từ user
                    String members = newDataString[3];
                    InsertGroup(name, id,members);
                }else if (commandString.equals("Online")) {
                    String id = messageSplit[1];//Từ user
                    SetOnline(id);
                } else if (commandString.equals("Offline")) {
                    System.out.println("Offline");
                    String id = messageSplit[1];//Từ user
                    SetOffline(id);
                }else if(commandString.equals("GlobalSearch")) {
                	String id = messageSplit[1];//người muốn chặn
                    String content = messageSplit[2];// người chặn
                	String msg = SearchMessageGlobal(id,content);
                	Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "GlobalSearch" + msg);
                }else if (commandString.equals("AddFriend")) {
                    String id1 = messageSplit[1];//From
                    String id2 = messageSplit[2];//To
                    String actual_idString = AddFriend(id1, id2);
                    if(!actual_idString.equals("")) {
                    	Server.serverThreadBus.boardCastUser(actual_idString, "AddFriendSuccess");
                    	Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "AddFriendSuccess");
                    }
                }else if (commandString.equals("ChangeGroupName")) {
                    System.out.println("ChangeGroupName");
                    String groupid = messageSplit[1];
                    String id = messageSplit[2];
                    String newName = messageSplit[3];
                    ChangeGroupName(groupid, id, newName);
                } else if (commandString.equals("AddMemberToGroup")) {
                    String groupid = messageSplit[1];
                    String id = messageSplit[2];
                    AddMemberToGroup(groupid, id);
                }else if (commandString.equals("SetAdminGroup")) {
                    String groupid = messageSplit[1];
                    String id = messageSplit[2];
                    SetAdmin(groupid, id);
                }else if (commandString.equals("GroupChat")) {
                    System.out.println("GroupChat");
                    String groupid = messageSplit[1];
                    String content = messageSplit[2];
                    UpdateGroupChatMessage(groupid, content);
                // =====================================
                // chức năng ADMIN
            	}else if (commandString.equals("AdminGetListUser")) {
            		AdminGetListUser(messageSplit);
            	}else if (commandString.equals("AdminAddNewAccount")){
            		AdminAddNewAccount(messageSplit);
            	}else if (commandString.equals("AdminUpdateAccount")) {
                    AdminUpdateAccount(messageSplit);
                }else if (commandString.equals("AdminDeleteAccount")) {
                    AdminDeleteAccount(messageSplit);
                }else if (commandString.equals("AdminLockAccount")) {
                    AdminLockAccount(messageSplit);
                }else if (commandString.equals("AdminUnlockAccount")) {
                    AdminUnlockAccount(messageSplit);
                }else if (commandString.equals("AdminRenewPassword")) {
                    AdminRenewPassword(messageSplit);
                }else if (commandString.equals("AdminGetListLoginHistory")) {
                    AdminGetListLoginHistory(messageSplit);
                }else if (commandString.equals("AdminGetListFriend")) {
                    AdminGetListFriend(messageSplit);
                }else if (commandString.equals("AdminGetListLogin")) {
                    AdminGetListLogin(messageSplit);
                }else if (commandString.equals("AdminGetListMemGroup")) {
                    AdminGetListMemGroup(messageSplit);
                }else if (commandString.equals("AdminGetListAdmin")) {
                    AdminGetListAdmin(messageSplit);
                }else if (commandString.equals("AdminGetListSpam")) {
                    AdminGetListSpam(messageSplit);
                }else if (commandString.equals("AdminGetListNew")) {
                    AdminGetListNew(messageSplit);
                }else if (commandString.equals("AdminGetChartNew")) {
                    AdminGetChartNew(messageSplit);
                }
            }
        } catch (IOException e) {
            isClosed = true;
            
            // Safe null check
            if (actual_userID != null && !actual_userID.isEmpty()) {
                userRepo.SetOffline(actual_userID);
            }
            
            Server.serverThreadBus.remove(userID);
            System.out.println("Client " + userID + " disconnected" + 
                              (actual_userID != null ? " (user: " + actual_userID + ")" : ""));
        }
    }

    public void write(String message) throws IOException {
        os.write(message);
        os.newLine();
        os.flush();
    }
    
    public static String GetListFriendsAndGroups(String id) {
    	String FIND_ONLINE_FRIENDS = "SELECT u2.id, u2.fullname, u2.lock, u2.\"isOnline\" ,u2.blocks FROM public.\"users\" u JOIN public.\"users\" u2 "
    			+ "ON u2.id = ANY (u.friends) where u.id = ? and ( not(u2.id = ANY(u.blocks)) OR u.blocks is null)";
    	
    	String FIND_GROUPS = "SELECT groupid,groupname FROM public.\"groups\" where ? = any(users)";
    	
    	try (Connection connection = DriverManager.getConnection(URL, USER, PW)) {
			PreparedStatement online = connection.prepareStatement(FIND_ONLINE_FRIENDS);
			PreparedStatement group = connection.prepareStatement(FIND_GROUPS);
			
			online.setString(1,id);
			group.setString(1,id);
			
			ResultSet friendList = online.executeQuery();
			ResultSet groupList = group.executeQuery();
			
			String friendString = new String("");
			while (friendList.next())
			{
				String _id = friendList.getString("id");
				boolean isOnline = friendList.getBoolean("isOnline");
				String _name = friendList.getString("fullname");

				friendString += "||"+"user"+"|"+_id +"|"+ _name + "|"+isOnline;
				System.out.print(friendString);
			}
			while (groupList.next()) {
				String groupid = groupList.getString("groupid");
				String groupname = groupList.getString("groupname");
				
				friendString += "||"+"group"+"|"+groupid +"|"+ groupname;
			}
			return friendString;

		} catch (SQLException sqlException) {
			System.out.println("Unable to connect to database");
			sqlException.printStackTrace();
			return "";
		}
    }
    
  //Get Message Data
    public static String[] GetMessage(String id) {
        String FIND_MESSAGE_SQL = "SELECT \"idChat\",content FROM public.\"messages\" where \"idChat\" = ?";
        String GET_USER1_SQL = "SELECT username FROM users WHERE id = ?";
        String GET_USER2_SQL = "SELECT username FROM users WHERE id = ?";

        String[] result = new String[2];
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);

             PreparedStatement preparedStatement = connection.prepareStatement(FIND_MESSAGE_SQL);
             PreparedStatement preparedStatement1 = connection.prepareStatement(GET_USER1_SQL);
             PreparedStatement preparedStatement2 = connection.prepareStatement(GET_USER2_SQL)) {
            preparedStatement.setString(1, id);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
            	Array  arr =  rs.getArray("content");
                result[0] = rs.getString("idChat");

                result[1] = "";
                String[] m = (String[]) arr.getArray();
                for(int i = 0;i<m.length;++i) {
                	if(i == m.length-1)
                		result[1] += m[i];
                	else
                		result[1] += m[i] + "|";
                }
                System.out.println("TESTING");
                System.out.println(result[0]);
                System.out.println("DONE TESTING");

                String[] getAllId = result[0].split("\\|");

                preparedStatement1.setString(1, getAllId[0]);
                preparedStatement2.setString(1, getAllId[1]);

                ResultSet resultSet = preparedStatement1.executeQuery();
                ResultSet resultSet1 = preparedStatement2.executeQuery();
                String username1 = "";
                String username2 = "";
                if (resultSet.next() && resultSet1.next()) {
                    username1 = resultSet.getString("username");
                    username2 = resultSet1.getString("username");
                }

                System.out.println(username1 + " " + username2);

                result[1] = result[1].replace(getAllId[0] + " -", "(" + username1 + ")");
                result[1] = result[1].replace(getAllId[1] + " -", "(" + username2 + ")");


            } else {
                result[0] = "";
                result[1] ="";
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return result;
        }
    }
    
    public static String[] GetGroup(String id) {
        String FIND_MESSAGE_SQL = "SELECT groupid,content FROM public.\"groups\" where groupid = ?";
        String FIND_MEMBER_SQL = "SELECT u.id,u.fullname,CASE WHEN u.id = ANY(mbs.admin) "
        		+ "THEN CAST(TRUE AS BOOL) ELSE CAST(FALSE AS BOOL) END as isAdmin FROM (SELECT unnest(users) as id, admin FROM public.\"groups\" where groupid = ?) as mbs "
        		+ " JOIN public.\"users\" u on mbs.id = u.id";
        String[] result = new String[3];
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
        		PreparedStatement smt1 = connection.prepareStatement(FIND_MEMBER_SQL);
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_MESSAGE_SQL)) {
           
        	preparedStatement.setString(1, id);
            smt1.setString(1, id);
            
            ResultSet rs = preparedStatement.executeQuery();
            ResultSet rs1 = smt1.executeQuery();
            
            if (rs.next()) {
            	Array  arr =  rs.getArray("content");
                result[0] = rs.getString("groupid");
                String[] m = (String[]) arr.getArray();
                
                result[1] = "";
                
                for(int i = 0;i<m.length;++i) {
                	if(i == m.length-1)
                		result[1] += m[i];
                	else
                		result[1] += m[i] + "|";
                }
                System.out.println(result[1]);
                
            } else {
                result[0] = "";
                result[1] ="";
            }
            result[2] = "";
            while(rs1.next()) {
            	String _id =  rs1.getString("id");
            	String name =  rs1.getString("fullname");
            	String isAdmin =  rs1.getBoolean("isAdmin") ? "true" : "false";
            	result[2] += "||"+  _id+ "|" + name + "|" + isAdmin;
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return result;
        }
    }
    
    public static String SearchMessageGlobal(String idUser,String content) {
    	String like_content = "%" + content+ "%";
        String FIND_MESSAGE_SQL = "SELECT * FROM ("
        		+ "SELECT u.fullname,u.username,u.id,unnest(content) as ct"
        		+ "	FROM public.messages join public.users u on u.id = any(users)"
        		+ "	where \"idChat\" like ? and id <> ?"
        		+ "	) as DT"
        		+ " WHERE DT.ct like ?";
        String result = new String("");
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);

            PreparedStatement preparedStatement = connection.prepareStatement(FIND_MESSAGE_SQL)) {
            preparedStatement.setString(1, idUser + "|%");
            preparedStatement.setString(2, idUser);
            preparedStatement.setString(3, like_content);
            System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
           
            while (rs.next()) {
            	String  name =  rs.getString("fullname");
                String msgData = rs.getString("ct");
                String id = rs.getString("id");
                result += "||" +"user"+ "|" + name +"|" + msgData;
            } 
            System.out.println(result);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return result;
        }
    }
    
    public static String[] CheckMessageExists(String id, String id2) {
        String FIND_MESSAGE_SQL = "SELECT \"idChat\",content FROM public.\"messages\" where \"idChat\" = ? or \"idChat\" = ?";
        String idChat1 = id + "|" + id2;
        String idChat2 = id2 + "|" + id;
        String[] result = new String[2];
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);

             PreparedStatement preparedStatement = connection.prepareStatement(FIND_MESSAGE_SQL)) {
            preparedStatement.setString(1, idChat1);
            preparedStatement.setString(2, idChat2);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                result[0] = rs.getString("idChat");
                result[1] = rs.getArray("content").toString();
            } else {
                result[0] = "";
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return result;
        }
    }
    
    public static boolean UpdateExistsMessage(String id, String id2, String content) {
        String idChat1 = id + "|" + id2;
        String idChat2 = id2 + "|" + id;
        String UPDATE_MESSAGE_SQL = "Update public.\"messages\" SET content =array_append(content,?) WHERE \"idChat\" = ? or \"idChat\" = ?";
        String ADD_TO_SYSTEMS_SQL = "INSERT INTO systems (username, type, \"idChat\", time) VALUES (?, ?, ?, ?)";
        String GET_USER_SQL = "SELECT username FROM users WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MESSAGE_SQL);
             PreparedStatement preparedStatement1 = connection.prepareStatement(GET_USER_SQL);
             PreparedStatement preparedStatement2 = connection.prepareStatement(ADD_TO_SYSTEMS_SQL)) {
            preparedStatement.setString(1, content);
            preparedStatement.setString(2, idChat1);
            preparedStatement.setString(3, idChat2);

            preparedStatement1.setString(1, id);
            ResultSet resultSet = preparedStatement1.executeQuery();
            String usernameToAdd = "";

            if (resultSet.next()) {
                usernameToAdd = resultSet.getString("username");
            }

            ZoneId utc = ZoneId.of("UTC+7");
            ZonedDateTime curDate = ZonedDateTime.now(utc);
            LocalDateTime localDateTime = curDate.toLocalDateTime();
            Timestamp timestamp = Timestamp.valueOf(localDateTime);

            preparedStatement2.setString(1, usernameToAdd);
            preparedStatement2.setInt(2, 1);
            preparedStatement2.setString(3, idChat1);
            preparedStatement2.setTimestamp(4, timestamp);

            int rowsAffected = preparedStatement2.executeUpdate();

            int count = preparedStatement.executeUpdate();

            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }
    
 // -- add to db (done)
    public static boolean InsertMessage(String id, String id2, String content) {
        String idChat1 = id + "|" + id2;
        String idChat2 = id2 + "|" + id;
        String INSERT_MESSAGE_SQL = "INSERT INTO public.\"messages\" (\"idChat\",users,content) values (?,?,?)";
        String GET_IDCHAT_SQL = "SELECT \"idChat\" FROM messages WHERE users && ?";
        String GET_USERNAME_SQL = "SELECT fullname FROM users WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_MESSAGE_SQL);
             PreparedStatement preparedStatement1 = connection.prepareStatement(INSERT_MESSAGE_SQL);
             PreparedStatement preparedStatement2 = connection.prepareStatement(GET_IDCHAT_SQL);
             PreparedStatement preparedStatement3 = connection.prepareStatement(GET_USERNAME_SQL);
             ) {
            String[] contents = new String[1];
            contents[0] = content;
            Array array = connection.createArrayOf("TEXT", contents);

            String[] users = new String[2];
            users[0] = id;
            users[1] = id2;
            Array u = connection.createArrayOf("TEXT", users);
            preparedStatement.setString(1, idChat1);
            preparedStatement.setArray(2, u);
            preparedStatement.setArray(3, array);

            preparedStatement1.setString(1, idChat2);
            preparedStatement1.setArray(2, u);
            preparedStatement1.setArray(3, array);

            preparedStatement2.setArray(1, u);
            preparedStatement3.setString(1, id);

            ResultSet resultSet = preparedStatement2.executeQuery();
            ResultSet resultSet1 = preparedStatement3.executeQuery();
            String username = "";
            String idChat = "";
            if (resultSet.next()) {
                idChat = resultSet.getString("idChat");
            }
            if (resultSet1.next()) {
                username = resultSet1.getString("fullname");
            }

            int count = preparedStatement.executeUpdate();
            int count2 = preparedStatement1.executeUpdate();
            return count > 0 && count2 > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }
    
    public static String AddFriend(String userId, String FriendName) {
        String ADD_FRIEND_SQL = "UPDATE public.\"users\" SET friends = array_append(friends,?)"
                + "WHERE id =? and exists (select * from public.\"users\" where id = ?)";
        String FIND_USER = "SELECT id FROM public.\"users\" WHERE id = ? or username = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(ADD_FRIEND_SQL);
             PreparedStatement preparedStatement1 = connection.prepareStatement(ADD_FRIEND_SQL);
        		PreparedStatement preparedStatement2 = connection.prepareStatement(FIND_USER)) {
        	String FriendId;
        	preparedStatement2.setString(1, FriendName);
        	preparedStatement2.setString(2, FriendName);
        	ResultSet rSet = preparedStatement2.executeQuery();
        	if(rSet.next()) {
        		FriendId = rSet.getString("id");
        		if(!FriendId.equals(userId)) {
		            preparedStatement.setString(1, userId);
		            preparedStatement.setString(2, FriendId);
		            preparedStatement.setString(3, userId);
		            preparedStatement1.setString(1, FriendId);
		            preparedStatement1.setString(2, userId);
		            preparedStatement1.setString(3, userId);
	
		            int count = preparedStatement.executeUpdate();
		            int count1 = preparedStatement1.executeUpdate();
		            return FriendId;
        		}
        	}
           return "";
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return "";
        }
    }
    
  //CREATE GROUP
    public static boolean InsertGroup(String group_name, String creator,String users) {
        String CREATE_GROUP_SQL = "INSERT INTO public.groups "
                + "	(groupid, admin, groupname, users, content)\r\n"
                + "	VALUES (?, ?, ?, ?, '{}');";
        String GET_USER_ID = "SELECT id FROM public.users where username = ? or id = ?";
        long id = (long) (Math.random() * (10000000000l - 1)) + 1;
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_GROUP_SQL)) {
            preparedStatement.setString(1, id + "");
            String[] user = new String[1];
            String[] memberNames = users.split("\\|");
            ArrayList<String> members = new ArrayList<>();
            for (String memberName : memberNames) {
            	PreparedStatement ps = connection.prepareStatement(GET_USER_ID);
            	ps.setString(1, memberName);
            	ps.setString(2, memberName);

            	ResultSet rs =ps.executeQuery();
            	if(rs.next()) {
            		String idString = rs.getString("id");
            		String onlineList = GetListFriendsAndGroups(idString);
            		Server.serverThreadBus.boardCastUser(idString,"OnlineList"+onlineList);
            		members.add(idString);
            	}
            }
            user[0] = creator;
            preparedStatement.setArray(2, connection.createArrayOf("TEXT", user));
            preparedStatement.setString(3, group_name);
            preparedStatement.setArray(4, connection.createArrayOf("TEXT", members.toArray()));
            
            String onlineList = GetListFriendsAndGroups(creator);
            Server.serverThreadBus.boardCastUser(creator,"OnlineList"+onlineList);
            
            int count = preparedStatement.executeUpdate();

            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return true;
        }
    }
    
    public static String GetFriendList(String id) {
        String GET_FRIEND_LIST_SQL = "select p.id,p.fullname from public.\"users\" u join public.\"users\" "
                + "p on p.id = any(u.friends) where u.id = ? group by p.id,u.fullname,u.id";

        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_FRIEND_LIST_SQL)) {
            preparedStatement.setString(1, id);

            ResultSet rs = preparedStatement.executeQuery();
            String tempString ="";
            while (rs.next()) {
                tempString += "||"+ rs.getString("id") + "|" + rs.getString("fullname");
            }
            return tempString;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return "";
        }
    }
    
    //SET ONLINE
    public static boolean SetOnline(String id) {
        String SET_ONLINE_SQL = "UPDATE public.\"users\" SET \"isOnline\" = true where id = ?";
        String GET_FRIEND_SQL = "SELECT friends from public.\"users\" where id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(SET_ONLINE_SQL);
        		PreparedStatement preparedStatement1 = connection.prepareStatement(GET_FRIEND_SQL)) {
            preparedStatement.setString(1, id);
            preparedStatement1.setString(1, id);

            int count = preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement1.executeQuery();

            if(resultSet.next()) {
           	 Array  arr =  resultSet.getArray("friends");

                String[] m = (String[]) arr.getArray();
                for (String element : m) {
                	Server.serverThreadBus.boardCastUser(element,"IsOnline|"+id);
                }
           }
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }

    //SET OFFLINE
    public static boolean SetOffline(String id) {
    	System.out.println("Offline");
        String SET_ONLINE_SQL = "UPDATE public.\"users\" SET \"isOnline\" = false where id = ?";
        String GET_FRIEND_SQL = "SELECT friends from public.\"users\" where id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(SET_ONLINE_SQL);
        	PreparedStatement preparedStatement1 = connection.prepareStatement(GET_FRIEND_SQL)) {
            preparedStatement.setString(1, id);
            preparedStatement1.setString(1, id);

            int count = preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement1.executeQuery();
            if(resultSet.next()) {
            	 Array  arr =  resultSet.getArray("friends");

                 String[] m = (String[]) arr.getArray();
                 for (String element : m) {
                 	Server.serverThreadBus.boardCastUser(element,"IsOffline|"+id);
                 }
            }
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }
  //Change Group Name
    public static boolean ChangeGroupName(String groupID, String adminId, String newName) {
        String UPDATE_GROUP_NAME_SQL = "UPDATE public.groups"
                + " SET groupname = ?"
                + " WHERE groupid = ? and ? = any(admin) ";

        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_GROUP_NAME_SQL)) {
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, groupID);
            preparedStatement.setString(3, adminId);
            System.out.print(preparedStatement);
            int count = preparedStatement.executeUpdate();

            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return true;
        }
    }

    //Add Member To Group
    public static boolean AddMemberToGroup(String groupID, String name) {
        String ADD_MEMBER_SQL = "UPDATE public.\"groups\" SET users = array_append(users,?)"
                + "WHERE groupid =?";
        String GET_USER = "SELECT id FROM public.\"users\" WHERE id = ? or username = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(ADD_MEMBER_SQL);
        		PreparedStatement smt = connection.prepareStatement(GET_USER)) {
        	smt.setString(1, name);
        	smt.setString(2, name);
        	
        	ResultSet rSet = smt.executeQuery();
        	if(rSet.next()) {
        		String memeberid = rSet.getString("id");
	            preparedStatement.setString(1, memeberid);
	            preparedStatement.setString(2, groupID);
	
	            int count = preparedStatement.executeUpdate();
	
	            return count > 0;
        	}
        	return false;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return true;
        }
    }
    
    //Set admin Group
    public static boolean SetAdmin(String groupID, String id) {
        String checkValid = "SELECT * FROM public.\"groups\" where "
        		+ "? <> any(admin) and groupid = ? and ? = any(users)";
        String AddAdmin = "UPDATE public.\"groups\" SET admin = array_append(admin,?) "
        		+ "WHERE groupid =?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(AddAdmin);
        		PreparedStatement smt = connection.prepareStatement(checkValid)) {
        	smt.setString(1, id);
        	smt.setString(2, groupID);
        	smt.setString(3, id);
        	
        	ResultSet rSet = smt.executeQuery();
        	if(rSet.next()) {
	            preparedStatement.setString(1, id);
	            preparedStatement.setString(2, groupID);
	
	            int count = preparedStatement.executeUpdate();
	
	            return count > 0;
        	}
        	return false;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return true;
        }
    }
  //Send message to group chat
    public static boolean UpdateGroupChatMessage(String id, String content) {
        String UPDATE_MESSAGE_SQL = "Update public.\"groups\" SET content = array_append(content,?) WHERE groupid = ?";
        String GET_MEMBER_MESSAGE_SQL = "SELECT users FROM public.\"groups\" WHERE groupid = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MESSAGE_SQL);
        		PreparedStatement smt = connection.prepareStatement(GET_MEMBER_MESSAGE_SQL);) {
            preparedStatement.setString(1, content);
            preparedStatement.setString(2, id);
            
            smt.setString(1, id);
            
            ResultSet rsResultSet = smt.executeQuery();
            if(rsResultSet.next()) {
            	Array arr =  rsResultSet.getArray("users");
            	String[] m = (String[]) arr.getArray();
            	
            	for (String u:m) {
                	Server.serverThreadBus.boardCastUser(u, "UpdateMessage|"+id+"|"+content);
                }
            }
            int count = preparedStatement.executeUpdate();

            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }
    
    //==================================
    // chức năng ADMIN
    public static void AdminGetListUser(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_USER_SQL = "SELECT id, username, fullname, email, \"isAdmin\", lock, \"isOnline\", \"createAt\", address, dob, gender FROM public.\"users\"";

            if (!messageSplit[1].equals("")) {
                ADMIN_GET_LIST_USER_SQL += " WHERE username ILIKE ?";
            }

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_USER_SQL)) {

                if (!messageSplit[1].equals("")) {
                    preparedStatement.setString(1, "%" + messageSplit[1] + "%");
                }

                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListUser|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("id")).append(", ");
                        result.append(rs.getString("username")).append(", ");
                        result.append(rs.getString("fullname")).append(", ");
                        result.append(rs.getString("email")).append(", ");
                        result.append(rs.getBoolean("isAdmin")).append(", ");
                        result.append(rs.getBoolean("lock")).append(", ");
                        result.append(rs.getBoolean("isOnline")).append(", ");
                        result.append(rs.getString("address")).append(", ");
                        result.append(rs.getDate("dob")).append(", ");
                        result.append(rs.getString("gender")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getDate("createAt")).append("|END");
                        } else {
                            result.append(rs.getDate("createAt"));
                        }

                        String fullReturn = "AdminGetListUser|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void AdminAddNewAccount(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_ADD_NEW_ACCOUNT_SQL = "INSERT INTO public.\"users\" (id, username, fullname, email, password, \"isAdmin\", lock, \"createAt\", address, dob, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            ZoneId utc = ZoneId.of("UTC+7");
            ZonedDateTime curDate = ZonedDateTime.now(utc);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = curDate.format(formatter);
            Date sqlDate = Date.valueOf(formattedDate);

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_ADD_NEW_ACCOUNT_SQL)) {

                preparedStatement.setString(1, messageSplit[1]); // id
                preparedStatement.setString(2, messageSplit[2]); // username
                preparedStatement.setString(3, messageSplit[3]); // fullname
                preparedStatement.setString(4, messageSplit[4]); // email
                preparedStatement.setString(5, messageSplit[5]); // password
                preparedStatement.setBoolean(6, Boolean.parseBoolean(messageSplit[6])); // isAdmin
                preparedStatement.setBoolean(7, Boolean.parseBoolean(messageSplit[7])); // lock
                preparedStatement.setDate(8, sqlDate); // createAt
                preparedStatement.setString(9, messageSplit[8].equals("null") ? null : messageSplit[8]); // address
                if (messageSplit[9].equals("null")) {
                    preparedStatement.setNull(10, java.sql.Types.DATE); // dob
                } else {
                    preparedStatement.setDate(10, Date.valueOf(messageSplit[9])); // dob
                }
                preparedStatement.setString(11, messageSplit[10].equals("null") ? null : messageSplit[10]); // gender

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminAddNewAccount|success");
                } else {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminAddNewAccount|fail");
                }
            } catch (SQLException e) {
                Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminAddNewAccount|fail");
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void AdminUpdateAccount(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_UPDATE_ACCOUNT_SQL = "UPDATE public.\"users\" SET username = ?, fullname = ?, email = ?, password = ?, \"isAdmin\" = ?, lock = ?, address = ?, dob = ?, gender = ? WHERE id = ?";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_UPDATE_ACCOUNT_SQL)) {

                preparedStatement.setString(1, messageSplit[1]); // username
                preparedStatement.setString(2, messageSplit[2]); // fullname
                preparedStatement.setString(3, messageSplit[3]); // email
                preparedStatement.setString(4, messageSplit[4]); // password
                preparedStatement.setBoolean(5, Boolean.parseBoolean(messageSplit[5])); // isAdmin
                preparedStatement.setBoolean(6, Boolean.parseBoolean(messageSplit[6])); // lock
                preparedStatement.setString(7, messageSplit[7].equals("null") ? null : messageSplit[7]); // address
                
                if (messageSplit[8].equals("null")) {
                    preparedStatement.setNull(8, java.sql.Types.DATE); // dob
                } else {
                    preparedStatement.setDate(8, Date.valueOf(messageSplit[8])); // dob
                }
                
                preparedStatement.setString(9, messageSplit[9].equals("null") ? null : messageSplit[9]); // gender
                preparedStatement.setString(10, messageSplit[10]); // id (WHERE clause)

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminUpdateAccount|success");
                } else {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminUpdateAccount|fail");
                }
            } catch (SQLException e) {
                Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminUpdateAccount|fail");
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void AdminDeleteAccount(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_DELETE_ACCOUNT_SQL = "DELETE FROM public.\"users\" WHERE id = ?";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_DELETE_ACCOUNT_SQL)) {

                preparedStatement.setString(1, messageSplit[1]); // id

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminDeleteAccount|success");
                } else {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminDeleteAccount|fail");
                }
            } catch (SQLException e) {
                Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminDeleteAccount|fail");
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void AdminLockAccount(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_LOCK_ACCOUNT_SQL = "UPDATE public.\"users\" SET lock = TRUE WHERE id = ?";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_LOCK_ACCOUNT_SQL)) {

                preparedStatement.setString(1, messageSplit[1]); // id

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminLockAccount|success");
                } else {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminLockAccount|fail");
                }
            } catch (SQLException e) {
                Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminLockAccount|fail");
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void AdminUnlockAccount(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_UNLOCK_ACCOUNT_SQL = "UPDATE public.\"users\" SET lock = FALSE WHERE id = ?";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_UNLOCK_ACCOUNT_SQL)) {

                preparedStatement.setString(1, messageSplit[1]); // id

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminUnlockAccount|success");
                } else {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminUnlockAccount|fail");
                }
            } catch (SQLException e) {
                Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminUnlockAccount|fail");
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void AdminRenewPassword(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_RENEW_PASSWORD_SQL = "UPDATE public.\"users\" SET password = ? WHERE id = ?";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_RENEW_PASSWORD_SQL)) {

                preparedStatement.setString(1, messageSplit[2]); // new password
                preparedStatement.setString(2, messageSplit[1]); // id

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminRenewPassword|success");
                } else {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminRenewPassword|fail");
                }
            } catch (SQLException e) {
                Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminRenewPassword|fail");
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void AdminGetListLoginHistory(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_LOGIN_HISTORY_SQL = "SELECT username, logdate AT TIME ZONE 'UTC+7' AS logdate FROM public.logs WHERE username = ? ORDER BY logdate DESC";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_LOGIN_HISTORY_SQL)) {

                preparedStatement.setString(1, messageSplit[1]); // username

                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListLoginHistory|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("username")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getTimestamp("logdate")).append("|END");
                        } else {
                            result.append(rs.getTimestamp("logdate"));
                        }

                        String fullReturn = "AdminGetListLoginHistory|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void AdminGetListFriend(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_FRIEND_SQL = "SELECT u1.username AS user1, u2.username AS user2 " +
                    "FROM public.\"users\" u1, public.\"users\" u2 " +
                    "WHERE u2.id = ANY(u1.friends) AND u1.username = ? " +
                    "ORDER BY u2.username";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_FRIEND_SQL)) {

                preparedStatement.setString(1, messageSplit[1]);

                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListFriend|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("user1")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getString("user2")).append("|END");
                        } else {
                            result.append(rs.getString("user2"));
                        }

                        String fullReturn = "AdminGetListFriend|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void AdminGetListLogin(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_LOGIN_SQL;

            ADMIN_GET_LIST_LOGIN_SQL = "SELECT u.username, u.fullname, l.logdate FROM public.\"users\" as u JOIN public.\"logs\" as l ON u.username = l.username;";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_LOGIN_SQL)) {
                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListLogin|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("logdate")).append(", ");
                        result.append(rs.getString("username")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getString("fullname")).append("|END");
                        } else {
                            result.append(rs.getString("fullname")).append(", ");
                        }

                        String fullReturn = "AdminGetListLogin|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void AdminGetListMemGroup(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_MEM_GROUP_SQL = "SELECT groupname, username FROM public.\"groups\" g, public.\"users\" u WHERE u.id = ANY(g.users) AND g.groupid = ?";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_MEM_GROUP_SQL)) {

                preparedStatement.setString(1, messageSplit[1]); // groupid

                ResultSet rs = preparedStatement.executeQuery();
                ResultSet rsAd = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListMemGroup|no data|END");
                } else {
                    StringBuilder resultGroup = new StringBuilder();
                    resultGroup.append(rs.getString("groupname"));

                    String fullReturnGroup = "AdminGetListMemGroup|" + resultGroup;
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturnGroup);

                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("username"));

                        if (rsAd.next() && rsAd.isLast()) {
                            result.append("Quản trị viên");
                        }

                        String fullReturn = "AdminGetListMemGroup|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void AdminGetListAdmin(String[] messageSplit) {
        try {
            System.out.println(Arrays.toString(messageSplit));
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_ADMIN_SQL = "SELECT\n" +
                    "    g.groupname,\n" +
                    "    ARRAY_TO_STRING(u.username_array, ' - ') AS ad\n" +
                    "FROM\n" +
                    "    public.\"groups\" g\n" +
                    "LEFT JOIN LATERAL (\n" +
                    "    SELECT\n" +
                    "        ARRAY(SELECT username FROM public.users WHERE id = ANY(g.admin)) AS username_array\n" +
                    ") u ON TRUE\n" +
                    "WHERE\n" +
                    "    g.groupname ILIKE ?\n";
            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_ADMIN_SQL)) {
                preparedStatement.setString(1, "%" + messageSplit[1] + "%");

                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListAdmin|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("ad")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getString("groupname")).append("|END");
                        } else {
                            result.append(rs.getString("groupname"));
                        }
                        String fullReturn = "AdminGetListAdmin|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void AdminGetListSpam(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_SPAM_SQL = "SELECT * FROM public.\"spams\"";

            if (messageSplit.length == 3) {} else {
                if (messageSplit[2].equals("1")) {
                    ADMIN_GET_LIST_SPAM_SQL += " WHERE username ILIKE ?";
                } else if (messageSplit[2].equals("-1")) {
                    ADMIN_GET_LIST_SPAM_SQL += " WHERE EXTRACT(YEAR FROM date)::TEXT ILIKE ?";
                }
            }

            if (messageSplit[1].equals("1")) {
                ADMIN_GET_LIST_SPAM_SQL += " ORDER BY username ASC";
            } else if (messageSplit[1].equals("-1")) {
                ADMIN_GET_LIST_SPAM_SQL += " ORDER BY date ASC";
            }

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_SPAM_SQL)) {

                if (messageSplit.length != 3) {
                    preparedStatement.setString(1, "%" + messageSplit[3] + "%");
                }

                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListSpam|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("username")).append(", ");
                        result.append(rs.getString("date")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getString("ByUser")).append("|END");
                        } else {
                            result.append(rs.getString("ByUser"));
                        }

                        String fullReturn = "AdminGetListSpam|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void AdminGetListNew(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_NEW_SQL = "SELECT * FROM public.\"users\"";
            System.out.println(Arrays.toString(messageSplit));

            ADMIN_GET_LIST_NEW_SQL += " WHERE \"createAt\" BETWEEN ?::DATE AND ?::DATE AND username ILIKE ?";
            if (messageSplit[3].equals("1")) {
                ADMIN_GET_LIST_NEW_SQL += " ORDER BY fullname ASC";
            } else if (messageSplit[3].equals("-1")) {
                ADMIN_GET_LIST_NEW_SQL += " ORDER BY \"createAt\" ASC";
            }

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_NEW_SQL)) {

                preparedStatement.setString(1, messageSplit[1]);
                preparedStatement.setString(2, messageSplit[2]);
                preparedStatement.setString(3, "%" + messageSplit[4] + "%");

                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListNew|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("username")).append(", ");
                        result.append(rs.getString("fullname")).append(", ");
                        result.append(rs.getString("address")).append(", ");
                        result.append(rs.getString("dob")).append(", ");
                        result.append(rs.getString("gender")).append(", ");
                        result.append(rs.getString("email")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getString("createAt")).append("|END");
                        } else {
                            result.append(rs.getString("createAt"));
                        }

                        String fullReturn = "AdminGetListNew|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void AdminGetChartNew(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_CHART_NEW_SQL = " WITH months AS (\n" +
                    "                SELECT generate_series(1, 12) AS month\n" +
                    "            )\n" +
                    "            SELECT months.month,\n" +
                    "                   COUNT(public.users.\"createAt\") AS row_count\n" +
                    "            FROM months\n" +
                    "            LEFT JOIN public.users ON EXTRACT(MONTH FROM public.users.\"createAt\") = months.month\n" +
                    "                               AND EXTRACT(YEAR FROM public.users.\"createAt\") = ?\n" +
                    "            GROUP BY months.month\n" +
                    "            ORDER BY months.month;";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_CHART_NEW_SQL)) {
                preparedStatement.setInt(1, Integer.parseInt(messageSplit[1]));

                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetChartNew|no data|END");
                } else {
                    StringBuilder result = new StringBuilder();
                    do {
                        result.append(rs.getInt("month")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getBigDecimal("row_count"));
                        } else {
                            result.append(rs.getBigDecimal("row_count")).append(", ");
                        }
                    } while (rs.next());
                    String fullReturn = "AdminGetChartNew|" + result + "|" + messageSplit[1];
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}