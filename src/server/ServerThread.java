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
                }
                else if(commandString.equals("Login")) {
                    String result = userRepo.Login(messageSplit[1], messageSplit[2]);
                    
                    if(!result.equals("")) {
                        String[] info = result.split("\\|");
                        actual_userID = info[0];
                        this.userID = actual_userID; // Cập nhật đúng userID trong bus
                        System.out.println("Login success for user ID: " + actual_userID);
                        write("Login_Success|" + result);           // Gửi thành công
                       
                    } else {
                    	System.out.println("Login failed for user ID: " + actual_userID);
                        write("Login_Failed|");                        // Gửi thất bại
                        System.out.println("Login failed for: " + messageSplit[1]);
                    }
                }else if (commandString.equals("AddFriend")) {
                    String id1 = messageSplit[1];//From
                    String id2 = messageSplit[2];//To
                    String actual_idString = AddFriend(id1, id2);
                    if(!actual_idString.equals("")) {
                    	Server.serverThreadBus.boardCastUser(actual_idString, "AddFriendSuccess");
                    	Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "AddFriendSuccess");
                    }
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
    
    public static boolean Register(String id,String name,String fullname,String email,String password) {
    	String INSERT_USERS_SQL = "INSERT INTO public.\"users\" (id, username,fullname, email, password, \"createAt\") values (?,?,?,?,?,?)";
    	String USER_EXIST = "SELECT * FROM public.\"users\" where email = ? or username = ?";
    	try (Connection connection = DriverManager.getConnection(URL, USER, PW);
   			 PreparedStatement stmt = connection.prepareStatement(USER_EXIST);
   			 // Step 2:Create a statement using connection object
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
   			preparedStatement.setString(5, password);
            preparedStatement.setDate(6, sqlDate);
            System.out.println(preparedStatement);
   			stmt.setString(1, email);
   			stmt.setString(2, name );
   			ResultSet rs = stmt.executeQuery();
   			if (rs.next()) {
   				System.out.print(rs.getString("id"));
   				return false;
   			}

   			// Step 3: Execute the query or update query
   			int count = preparedStatement.executeUpdate();
   			System.out.println(count);
   			return count > 0;
   		} catch (SQLException e) {
   			System.out.println("Unable to connect to database");
   			e.printStackTrace();
   			System.exit(1);
   			// print SQL exception information
   			return false;
   		}
	}
    
   //Login -- add to db (done)
    public static String Login(String email,String password) {
    	String FIND_USERS_SQL = "SELECT * FROM public.\"users\" where (email = ? or username = ?) and password = ? and lock = FALSE";
        String ADD_TO_LOGS_SQL = "INSERT INTO logs (username, logdate) VALUES (?, ?)";
    	try (Connection connection = DriverManager.getConnection(URL, USER, PW);
   			 // Step 2:Create a statement using connection object
   			 PreparedStatement preparedStatement = connection.prepareStatement(FIND_USERS_SQL);
                PreparedStatement preparedStatement1 = connection.prepareStatement(ADD_TO_LOGS_SQL)) {
   			preparedStatement.setString(1, email);
   			preparedStatement.setString(2, email);
   			preparedStatement.setString(3, password);

   			// Step 3: Execute the query or update query
   			ResultSet rs = preparedStatement.executeQuery();
   			if (rs.next()) {
                ZoneId utc = ZoneId.of("UTC+7");
                ZonedDateTime curDate = ZonedDateTime.now(utc);
                LocalDateTime localDateTime = curDate.toLocalDateTime();
                Timestamp timestamp = Timestamp.valueOf(localDateTime);

               preparedStatement1.setString(1, rs.getString("username"));
               preparedStatement1.setTimestamp(2, timestamp);
               int rowsAffected = preparedStatement1.executeUpdate();
               String isAdmin = rs.getBoolean("isAdmin") ? "true" : "false";
   			   return rs.getString("id") + "|" +  rs.getString("username")+"|"+rs.getString("fullname") + "|" +  rs.getString("email")+ "|" + isAdmin;   				
   			}
   			return "";
   		} catch (SQLException e) {
   			System.out.println("Unable to connect to database");
   			e.printStackTrace();
   			System.exit(1);
   			// print SQL exception information
   			return "";
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
}