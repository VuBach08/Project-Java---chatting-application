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
                String[] messageSplit = message.split("\\|");
                String commandString = messageSplit[0];
                commandString = commandString.trim();
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
                        write("Login_Success|" + result);           // Gửi thành công
                        System.out.println("Login success for user ID: " + actual_userID);
                    } else {
                        write("Login_Failed|");                        // Gửi thất bại
                        System.out.println("Login failed for: " + messageSplit[1]);
                    }
                }
            }
        } 
            catch (IOException e) {
            isClosed = true;
            if(!actual_userID.equals("")) {
            	userRepo.SetOffline(actual_userID);
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
}