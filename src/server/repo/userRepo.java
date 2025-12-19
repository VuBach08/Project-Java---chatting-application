package server.repo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;

import server.Server;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class userRepo {

    static final String URL = "jdbc:postgresql://localhost:5432/chatting-application";
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String USER = "postgres";
    static final String PW = "123456";
    
    public static boolean UpdateUserProfile(String username, String newFullName, String newAddress, String newDob, String newGender) {
        String UPDATE_PROFILE_SQL = "UPDATE public.\"users\" SET fullname = ?, address = ?, dob = ?, gender = ? WHERE username = ? OR id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PROFILE_SQL)) {
            
            preparedStatement.setString(1, newFullName);
            preparedStatement.setString(2, newAddress.isEmpty() ? null : newAddress);
            
            // Parse date
            if (!newDob.isEmpty()) {
                try {
                    Date sqlDate = Date.valueOf(newDob); // Format: YYYY-MM-DD
                    preparedStatement.setDate(3, sqlDate);
                } catch (IllegalArgumentException e) {
                    preparedStatement.setDate(3, null);
                }
            } else {
                preparedStatement.setDate(3, null);
            }
            
            preparedStatement.setString(4, newGender.isEmpty() ? null : newGender);
            preparedStatement.setString(5, username);
            preparedStatement.setString(6, username);
            
            int count = preparedStatement.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean ForgotPassword(String email) {
    	final String email_password = "kirito1212";
		String from = "nvbach21@clc.fitus.edu.vn";
		String host = "smtp.gmail.com";//or IP address

		Properties props = new Properties();
		 props.put("mail.smtp.auth", "true");
	      props.put("mail.smtp.starttls.enable", "true");
	      props.put("mail.smtp.ssl.trust", "*");
	      props.put("mail.smtp.ssl.protocols", "TLSv1.2");
	      props.put("mail.smtp.host", host);
	      props.put("mail.smtp.port", "587");


	   Session session = Session.getDefaultInstance(props,
			   new javax.mail.Authenticator() {
            @Override
			protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(from, email_password);
            }
         });

	   //Compose the message
	    try {
	     MimeMessage message = new MimeMessage(session);
	     message.setFrom(new InternetAddress(from));
	     message.addRecipient(Message.RecipientType.TO,new InternetAddress(email));
	     message.setSubject("Reset Password");
	     String password = generateRandomPassword(15);
	     message.setText("You received this email because you issued a password reset through this email\n"+"Your updated password is :" + password + "\nIf you did not request this, please send us an email to this email");
	     UpdatePassword(email,hashPassword(password));
	    //send the message
	     Transport.send(message);

	     System.out.println("message sent successfully...");
	     return true;
	     } catch (MessagingException err) {err.printStackTrace();}
	    return false;
    }
    public static boolean register(String id, String name, String fullname, String email, String password) {
        String INSERT_USERS_SQL = "INSERT INTO public.\"users\" (id, username, fullname, email, password, \"createAt\") values (?,?,?,?,?,?)";
        String USER_EXIST = "SELECT * FROM public.\"users\" where email = ? or username = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement stmt = connection.prepareStatement(USER_EXIST);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {

            ZoneId utc = ZoneId.of("UTC+7");
            ZonedDateTime curDate = ZonedDateTime.now(utc);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = curDate.format(formatter);
            Date sqlDate = Date.valueOf(formattedDate);

            preparedStatement.setString(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, fullname);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, hashPassword(password));  // đã hash ở đây
            preparedStatement.setDate(6, sqlDate);

            stmt.setString(1, email);
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return false; // đã tồn tại
            }

            int count = preparedStatement.executeUpdate();
            return count > 0;

        } catch (SQLException e) {
            System.out.println("Unable to connect to database");
            e.printStackTrace();
            return false;
        }
    }

    public static String Login(String email, String password) {
        String FIND_USERS_SQL = "SELECT * FROM public.\"users\" where (email = ? or username = ?) and password = ? and lock = FALSE";
        String ADD_TO_LOGS_SQL = "INSERT INTO logs (username, logdate) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_USERS_SQL);
             PreparedStatement preparedStatement1 = connection.prepareStatement(ADD_TO_LOGS_SQL)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, hashPassword(password));

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                ZoneId utc = ZoneId.of("UTC+7");
                ZonedDateTime curDate = ZonedDateTime.now(utc);
                LocalDateTime localDateTime = curDate.toLocalDateTime();
                Timestamp timestamp = Timestamp.valueOf(localDateTime);

                preparedStatement1.setString(1, rs.getString("username"));
                preparedStatement1.setTimestamp(2, timestamp);
                preparedStatement1.executeUpdate();

                String isAdmin = rs.getBoolean("isAdmin") ? "true" : "false";
                return rs.getString("id") + "|" + rs.getString("username") + "|" + rs.getString("fullname") + "|" + rs.getString("email") + "|" + isAdmin;
            }
            return "";
        } catch (SQLException e) {
            System.out.println("Unable to connect to database");
            e.printStackTrace();
            return "";
        }
    }


    public static boolean SetOffline(String id) {
        System.out.println("Offline");
        String SET_ONLINE_SQL = "UPDATE public.\"users\" SET \"isOnline\" = false where id = ?";
        String GET_FRIEND_SQL = "SELECT friends from public.\"users\" where id = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(SET_ONLINE_SQL);
             PreparedStatement preparedStatement1 = connection.prepareStatement(GET_FRIEND_SQL)) {

            preparedStatement.setString(1, id);
            preparedStatement1.setString(1, id);

            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement1.executeQuery();
            if (resultSet.next()) {
                Array arr = resultSet.getArray("friends");
                if (arr != null) {
                    String[] m = (String[]) arr.getArray();
                    for (String element : m) {
                        Server.serverThreadBus.boardCastUser(element, "IsOffline|" + id);
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static String hashPassword(String pw) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = digest.digest(pw.getBytes());

			// Convert byte array to a hexadecimal string
			StringBuilder hexString = new StringBuilder();
			for (byte hashedByte : hashedBytes) {
				String hex = Integer.toHexString(0xff & hashedByte);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}

			String hashedPW = hexString.toString();
			return hashedPW;
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Hashing algorithm not found");
			e.printStackTrace();
			return null;
		}
	}

    public static String generateRandomPassword(int length) {
        String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(validChars.length());
            password.append(validChars.charAt(randomIndex));
        }
        return password.toString();
    }

    public static boolean UpdatePassword(String email, String password) {
        String UPDATE_USERS_SQL = "UPDATE public.\"users\" set \"password\" = ? where email=?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USERS_SQL)) {

            preparedStatement.setString(1, hashPassword(password));
            preparedStatement.setString(2, email);

            int count = preparedStatement.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            System.out.println("Unable to connect to database");
            e.printStackTrace();
            return false;
        }
    }

    //GET FRIEND/GROUP LIST
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

    //GET MESSAGE DATA
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

    //GET GROUP
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

    // MESSAGE SEARCH
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

    // CHECK IF MESSAGE EXISTED OR NOT
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

    // UPDATE MESSAGE
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

    // INSERT MESSAGE
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
             PreparedStatement preparedStatement3 = connection.prepareStatement(GET_USERNAME_SQL)) {
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

    //ADD FRIEND
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
                preparedStatement.setString(1, FriendId);
                preparedStatement.setString(2, userId);
                preparedStatement.setString(3, FriendId);
                preparedStatement1.setString(1, userId);
                preparedStatement1.setString(2, FriendId);
                preparedStatement1.setString(3, userId);
                int count = preparedStatement.executeUpdate();
                int count1 = preparedStatement1.executeUpdate();
                if (count > 0 && count1 > 0) {
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

    //DELETE FRIEND
    public static boolean RemoveFriend(String userId, String FriendId) {
        String REMOVE_FRIEND_LIST_SQL = "UPDATE public.\"users\" SET friends = array_remove(friends, ?) WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_FRIEND_LIST_SQL);
             PreparedStatement preparedStatement1 = connection.prepareStatement(REMOVE_FRIEND_LIST_SQL)) {
            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, FriendId);
            preparedStatement1.setString(1, FriendId);
            preparedStatement1.setString(2, userId);

            int count = preparedStatement.executeUpdate();
            int count1 = preparedStatement1.executeUpdate();
            return count > 0 && count1 > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }

    //REMOVE MESSAGE
    public static boolean RemoveMessage(String id) {
        String REMOVE_MESSAGE_SQL = "Update public.\"messages\" SET content = '{}' WHERE \"idChat\" = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_MESSAGE_SQL)) {
            preparedStatement.setString(1, id);

            int count = preparedStatement.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return false;
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
                PreparedStatement smt = connection.prepareStatement(GET_USER_ID);
                smt.setString(1, memberName);
                smt.setString(2, memberName);
                ResultSet rs = smt.executeQuery();
                if(rs.next()) {
                    members.add(rs.getString("id"));
                }
            }
            if(!members.contains(creator))
                members.add(creator);
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

    //GET FRIEND LIST
    public static String GetFriendList(String id) {
        String GET_FRIEND_LIST_SQL = "select p.id,p.fullname from public.\"users\" u join public.\"users\" "
                + "p on p.id = any(u.friends) where u.id = ? group by p.id,u.fullname,u.id";

        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_FRIEND_LIST_SQL)) {
            preparedStatement.setString(1, id);

            ResultSet rs = preparedStatement.executeQuery();
            String tempString ="";
            while (rs.next()) {
                tempString += "||" + rs.getString("id") + "|" + rs.getString("fullname");
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
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement1.executeQuery();
            if (resultSet.next()) {
                Array arr = resultSet.getArray("friends");
                if (arr != null) {
                    String[] m = (String[]) arr.getArray();
                    for (String element : m) {
                        Server.serverThreadBus.boardCastUser(element, "IsOnline|" + id);
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //BLOCK ACCOUNT (For spamming report)
    public static boolean BlockAccount(String id, String id2) {
        String BLOCK_ACCOUNT_SQL = "UPDATE public.\"users\" SET blocks = array_append(blocks,?)"
                + "WHERE id =? and exists (select * from public.\"users\" where id = ?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(BLOCK_ACCOUNT_SQL)) {
            preparedStatement.setString(1, id2);
            preparedStatement.setString(2, id);
            preparedStatement.setString(3, id2);
            int count = preparedStatement.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }

    //CHANGE GROUPCHAT NAME
    public static boolean ChangeGroupName(String groupID, String adminId, String newName) {
        String UPDATE_GROUP_NAME_SQL = "UPDATE public.groups"
                + " SET groupname = ?"
                + " WHERE groupid = ? and ? = any(admin) ";

        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_GROUP_NAME_SQL)) {
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, groupID);
            preparedStatement.setString(3, adminId);
            int count = preparedStatement.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }

    //ADD MEMBER TO GROUP
    public static boolean AddMemberToGroup(String groupID, String name) {
        String ADD_MEMBER_SQL = "UPDATE public.\"groups\" SET users = array_append(users,?)"
                + "WHERE groupid =?";
        String GET_USER = "SELECT id FROM public.\"users\" WHERE id = ? or username = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(ADD_MEMBER_SQL);
                PreparedStatement smt = connection.prepareStatement(GET_USER)) {
            smt.setString(1, name);
            smt.setString(2, name);
            ResultSet rs = smt.executeQuery();
            if(rs.next()) {
                String id = rs.getString("id");
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, groupID);
                int count = preparedStatement.executeUpdate();
                return count > 0;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }

    //SET ADMIN IN GROUP
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
            ResultSet rs = smt.executeQuery();
            if(rs.next()) {
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, groupID);
                int count = preparedStatement.executeUpdate();
                return count > 0;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }

    //Remove admin
    public static boolean RemoveAdmin(String groupID, String id) {
        String checkValid = "SELECT * FROM public.\"groups\" where "
                + "? = any(admin) and groupid = ?";
        String AddAdmin = "UPDATE public.\"groups\" SET admin = array_remove(admin,?) "
                + "WHERE groupid =?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(AddAdmin);
                PreparedStatement smt = connection.prepareStatement(checkValid)) {
            smt.setString(1, id);
            smt.setString(2, groupID);
            ResultSet rs = smt.executeQuery();
            if(rs.next()) {
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, groupID);
                int count = preparedStatement.executeUpdate();
                return count > 0;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }

    //Remove Member out of Group
    public static boolean RemoveMemberGroup(String groupID, String memberid) {
        String ADD_MEMBER_SQL = "UPDATE public.\"groups\" SET users = array_remove(users,?)"
                + "WHERE groupid =?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(ADD_MEMBER_SQL)) {
            preparedStatement.setString(1, memberid);
            preparedStatement.setString(2, groupID);
            int count = preparedStatement.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }

    //SEND MESSAGE TO GROUPCHAT
    public static boolean UpdateGroupChatMessage(String id, String content) {
        String UPDATE_MESSAGE_SQL = "Update public.\"groups\" SET content = array_append(content,?) WHERE groupid = ?";
        String GET_MEMBER_MESSAGE_SQL = "SELECT users FROM public.\"groups\" WHERE groupid = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MESSAGE_SQL);
                PreparedStatement smt = connection.prepareStatement(GET_MEMBER_MESSAGE_SQL);) {
            preparedStatement.setString(1, content);
            preparedStatement.setString(2, id);
            smt.setString(1, id);
            ResultSet rs = smt.executeQuery();
            if(rs.next()) {
                Array arr = rs.getArray("users");
                String[] m = (String[]) arr.getArray();
                for (String element : m) {
                    Server.serverThreadBus.boardCastUser(element, "SendToGroup|" + id + "|" + content);
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

    //Report Spam
    public static boolean ReportSpam(String username, String byUser) {
        String UPDATE_MESSAGE_SQL = "Insert into public.\"spams\" (username, \"ByUser\", date) Values (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MESSAGE_SQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, byUser);
            
            ZoneId utc = ZoneId.of("UTC+7");
            ZonedDateTime curDate = ZonedDateTime.now(utc);
            LocalDateTime localDateTime = curDate.toLocalDateTime();
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            preparedStatement.setTimestamp(3, timestamp);
            
            int count = preparedStatement.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }

    // Get User Profile
    public static String GetUserProfile(String username) {
        String GET_USER_SQL = "SELECT username, fullname, email, address, dob, gender FROM public.\"users\" WHERE username = ? OR id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_SQL)) {
            
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, username);
            
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String usernameResult = rs.getString("username") != null ? rs.getString("username") : "";
                String fullname = rs.getString("fullname") != null ? rs.getString("fullname") : "";
                String email = rs.getString("email") != null ? rs.getString("email") : "";
                String address = rs.getString("address") != null ? rs.getString("address") : "";
                String dob = rs.getDate("dob") != null ? rs.getDate("dob").toString() : "";
                String gender = rs.getString("gender") != null ? rs.getString("gender") : "";
                
                return "UserProfileData|" + usernameResult + "|" + fullname + "|" + email + "|" + address + "|" + dob + "|" + gender;
            }
            return "UserProfileData|||||";
        } catch (SQLException e) {
            e.printStackTrace();
            return "UserProfileData|||||";
        }
    }

    // Update User Full Name
    public static boolean UpdateUserFullName(String username, String newFullName) {
        String UPDATE_FULLNAME_SQL = "UPDATE public.\"users\" SET fullname = ? WHERE username = ? OR id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_FULLNAME_SQL)) {
            
            preparedStatement.setString(1, newFullName);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, username);
            
            int count = preparedStatement.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Change Password
    public static String ChangePassword(String username, String currentPassword, String newPassword) {
        String GET_PASSWORD_SQL = "SELECT password FROM public.\"users\" WHERE username = ? OR id = ?";
        String UPDATE_PASSWORD_SQL = "UPDATE public.\"users\" SET password = ? WHERE username = ? OR id = ?";
        
        try (Connection connection = DriverManager.getConnection(URL, USER, PW)) {
            // Step 1: Verify current password
            PreparedStatement getPasswordStmt = connection.prepareStatement(GET_PASSWORD_SQL);
            getPasswordStmt.setString(1, username);
            getPasswordStmt.setString(2, username);
            
            ResultSet rs = getPasswordStmt.getResultSet();
            getPasswordStmt.executeQuery();
            rs = getPasswordStmt.getResultSet();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String hashedCurrentPassword = hashPassword(currentPassword);
                
                // Verify current password matches
                if (!storedPassword.equals(hashedCurrentPassword)) {
                    return "ChangePasswordFailed|Current password is incorrect!";
                }
                
                // Step 2: Update to new password
                PreparedStatement updatePasswordStmt = connection.prepareStatement(UPDATE_PASSWORD_SQL);
                String hashedNewPassword = hashPassword(newPassword);
                updatePasswordStmt.setString(1, hashedNewPassword);
                updatePasswordStmt.setString(2, username);
                updatePasswordStmt.setString(3, username);
                
                int count = updatePasswordStmt.executeUpdate();
                if (count > 0) {
                    return "ChangePasswordSuccess|";
                } else {
                    return "ChangePasswordFailed|Failed to update password in database!";
                }
            } else {
                return "ChangePasswordFailed|User not found!";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "ChangePasswordFailed|Database error: " + e.getMessage();
        }
    }
}