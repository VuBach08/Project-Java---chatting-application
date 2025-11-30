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
            System.out.println("Khời động luông mới thành công, ID là: " + userID);
//            Server.serverThreadBus.sendOnlineList();
            //Server.serverThreadBus.mutilCastSend("global-message"+","+"---Client "+this.userID+" đã đăng nhập---");
            Server.serverThreadBus.boardCast(userID, "id" + "|" + userID);

            String message;
            while (!isClosed) {
                message = is.readLine();
                if (message == null) {
                    break;
                }
                String[] messageSplit = message.split("\\|");
                String commandString = messageSplit[0];
                System.out.println(commandString);
                if(commandString.equals("Register")) {
                	if(Register(messageSplit[1],messageSplit[2],messageSplit[3],messageSplit[4],messageSplit[5])) {
                		Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "Register_Success|");
                	}
                }
            }
        } 
            catch (IOException e) {
            isClosed = true;
            if(!actual_userID.equals("")) {
            	SetOffline(actual_userID);
            }
            Server.serverThreadBus.remove(userID);
            
            System.out.println(userID + " exited " + actual_userID);
        }
    }

    public void write(String message) throws IOException {
        os.write(message);
        os.newLine();
        os.flush();
    }
    //Register -- thêm thông tin người dùng mới vào db
    public static boolean Register(String id,String name,String fullname,String email,String password) {
    	String INSERT_USERS_SQL = "INSERT INTO public.\"users\" (id, username,fullname, email, password, \"createAt\") values (?,?,?,?,?,?)";
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

   			int count = preparedStatement.executeUpdate();
   			System.out.println(count);
   			return count > 0;
   		} catch (SQLException e) {
   			System.out.println("Unable to connect to database");
   			e.printStackTrace();
   			System.exit(1);
   			// In ra lỗi SQL exception
   			return false;
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
    
   //Login -- thêm người dùng vào db
    public static String Login(String email,String password) {
    	String FIND_USERS_SQL = "SELECT * FROM public.\"users\" where (email = ? or username = ?) and password = ? and lock = FALSE";
        String ADD_TO_LOGS_SQL = "INSERT INTO logs (username, logdate) VALUES (?, ?)";
    	try (Connection connection = DriverManager.getConnection(URL, USER, PW);
   			 PreparedStatement preparedStatement = connection.prepareStatement(FIND_USERS_SQL);
                PreparedStatement preparedStatement1 = connection.prepareStatement(ADD_TO_LOGS_SQL)) {
   			preparedStatement.setString(1, email);
   			preparedStatement.setString(2, email);
   			preparedStatement.setString(3, password);

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
   			return "";
   		}
    }
    //Update mật khẩu khi người dùng quên
    public static boolean UpdatePassword(String email,String password) {
    	String UPDATE_USERS_SQL = "UPDATE public.\"users\" set \"password\" = ? where email=?";
    	try (Connection connection = DriverManager.getConnection(URL, USER, PW);
   			 PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USERS_SQL)) {
   			preparedStatement.setString(1, password);
   			preparedStatement.setString(2, email);

   			int count = preparedStatement.executeUpdate();
   			System.out.println(count);
   			return count > 0;
   		} catch (SQLException e) {
   			System.out.println("Unable to connect to database");
   			e.printStackTrace();
   			System.exit(1);
   			return false;
   		}
	}
    
    // Tạo mật khẩu random
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

    // Mã hóa mật khẩu người dùng bằng SHA-256
    public static String hashPassword(String pw) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = digest.digest(pw.getBytes());

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

    public static boolean ForgotPassword(String email) {
    	final String email_password = "vubachnguyen1212";
		String from = "vubach21@clc.fitus.edu.vn";
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

	   //Soạn message
	    try {
	     MimeMessage message = new MimeMessage(session);
	     message.setFrom(new InternetAddress(from));
	     message.addRecipient(Message.RecipientType.TO,new InternetAddress(email));
	     message.setSubject("Reset Password");
	     String password = generateRandomPassword(15);
	     message.setText("You received this email because you issued a password reset through this email\n"+"Your updated password is :" + password + "\nIf you did not request this, please send us an email to this email");
	     UpdatePassword(email,hashPassword(password));
	    //Gửi message
	     Transport.send(message);

	     System.out.println("message sent successfully...");
	     return true;
	     } catch (MessagingException err) {err.printStackTrace();}
	    return false;
    }

    //------------------------------------------------------------------------------------------------------------
}

