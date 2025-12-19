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

import clients.models.*;
import com.sun.tools.jconsole.JConsolePlugin;
import server.repo.userRepo;
import server.repo.AdminRepo;

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
                            String onlineList = userRepo.GetListFriendsAndGroups(actual_userID);
                            Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "OnlineList"+onlineList);
                        }
                       
                    }else {
                		Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1],"LoginFailed");
                	}
                }else if(commandString.equals("OnlineList")) {
                	String onlineList = userRepo.GetListFriendsAndGroups(messageSplit[1]);
                	Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "OnlineList"+onlineList);
                }else if (commandString.equals("MessageData")) {
                	if(messageSplit[1].equals("user")) {
	                    String id1 = messageSplit[2];//Người gửi
	                    String id2 = messageSplit[3];//Từ user
	                    String[] mess = userRepo.GetMessage(id1+"|"+ id2);
	                    if(!mess[0].equals("")) {
	                    	Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "MessageData|"+mess[0]+"||"+mess[1]);
	                    }
                    }else if(messageSplit[1].equals("group")) {
                    	String id = messageSplit[2];//Người gửi
	                    String[] mess = userRepo.GetGroup(id);
	                    if(!mess[0].equals("")) {
	                    	Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "GroupData|"+mess[0]+"|||"+mess[1] +"|" + mess[2]);
	                    }
                    }
                }else if (commandString.equals("DirectMessage")) {
                    String id1 = messageSplit[1];//Người gửi
                    String id2 = messageSplit[2];//Từ user
                    String content = messageSplit[3];
                    String[] mess = userRepo.CheckMessageExists(id1, id2);
                    if (!mess[0].equals("")) {
                        userRepo.UpdateExistsMessage(id1, id2, content);
                    } else {
                        userRepo.InsertMessage(id1, id2, content);
                    }
                    Server.serverThreadBus.boardCastUser(id2, "SendToUser|" + id1 + "|" + content);
                }else if (commandString.equals("AddFriend")) {
                    String id1 = messageSplit[1];//From
                    String id2 = messageSplit[2];//To
                    String actual_idString = userRepo.AddFriend(id1, id2);
                    if(!actual_idString.equals("")) {
                    	Server.serverThreadBus.boardCastUser(actual_idString, "AddFriendSuccess");
                    	Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "AddFriendSuccess");
                    }
                }else if (commandString.equals("UpdateUserProfile")) {
                    System.out.println("UpdateUserProfile");
                    String username = messageSplit[1];
                    String newFullName = messageSplit[2];
                    String newAddress = messageSplit[3];
                    String newDob = messageSplit[4];
                    String newGender = messageSplit[5];
                    boolean success = userRepo.UpdateUserProfile(username, newFullName, newAddress, newDob, newGender);
                    if (success) {
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "UpdateProfileSuccess|");
                    } else {
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "UpdateProfileFailed|");
                    }
                }else if (commandString.equals("DeleteFriend")) {
                    String id1 = messageSplit[1];//Người gửi
                    String id2 = messageSplit[2];//Từ user
                    userRepo.RemoveFriend(id1, id2);
                    System.out.println("DeleteFriend");
                }else if (commandString.equals("DeleteMessage")) {
                    String id1 = messageSplit[1];//người muốn xoá
                    String id2 = messageSplit[2];// người xoá
                    userRepo.RemoveMessage(id1 + "|" + id2);
                }else if (commandString.equals("CreateGroup")) {
                    System.out.println("CreateGroup "+ message);
                    String[] newDataString = message.split("\\|\\|");
                    String name = newDataString[1];//Người gửi
                    String id = newDataString[2];//Từ user
                    String members = newDataString[3];
                    userRepo.InsertGroup(name, id,members);
                }else if (commandString.equals("GetFriend")) {
                    String result = userRepo.GetFriendList(messageSplit[1]);
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "GetFriend" +result);
                }else if (commandString.equals("Online")) {
                    String id = messageSplit[1];//Từ user
                    userRepo.SetOnline(id);
                } else if (commandString.equals("Offline")) {
                    System.out.println("Offline");
                    String id = messageSplit[1];//Từ user
                    userRepo.SetOffline(id);
                }else if(commandString.equals("GlobalSearch")) {
                	String id = messageSplit[1];//người muốn chặn
                    String content = messageSplit[2];// người chặn
                	String msg = userRepo.SearchMessageGlobal(id,content);
                	Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "GlobalSearch" + msg);
                }else if (commandString.equals("BlockAccount")) {
                    System.out.println("BlockAccount");
                    String id1 = messageSplit[1];//người muốn chặn
                    String id2 = messageSplit[2];// người chặn
                    userRepo.BlockAccount(id1, id2);
                }else if (commandString.equals("ChangeGroupName")) {
                    System.out.println("ChangeGroupName");
                    String groupid = messageSplit[1];
                    String id = messageSplit[2];
                    String newName = messageSplit[3];
                    userRepo.ChangeGroupName(groupid, id, newName);
                } else if (commandString.equals("AddMemberToGroup")) {
                    String groupid = messageSplit[1];
                    String id = messageSplit[2];
                    userRepo.AddMemberToGroup(groupid, id);
                }else if (commandString.equals("SetAdminGroup")) {
                    String groupid = messageSplit[1];
                    String id = messageSplit[2];
                    userRepo.SetAdmin(groupid, id);
                }else if (commandString.equals("RemoveAdminGroup")) {
                    String groupid = messageSplit[1];
                    String id = messageSplit[2];
                    userRepo.RemoveAdmin(groupid, id);
                }else if (commandString.equals("RemoveMemberGroup")) {
                    System.out.println("RemoveMemberGroup");
                    String groupid = messageSplit[1];
                    String id = messageSplit[2];
                    userRepo.RemoveMemberGroup(groupid, id);
                }else if (commandString.equals("GroupChat")) {
                    System.out.println("GroupChat");
                    String groupid = messageSplit[1];
                    String content = messageSplit[2];
                    userRepo.UpdateGroupChatMessage(groupid, content);
                }else if (commandString.equals("ReportSpam")) {
                    System.out.println("ReportSpam");
                    String username = messageSplit[1];
                    String byUser = messageSplit[2];
                    userRepo.ReportSpam(username, byUser);
                }else if (commandString.equals("GetUserProfile")) {
                    System.out.println("GetUserProfile");
                    String username = messageSplit[1];
                    String result = userRepo.GetUserProfile(username);
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], result);
                }else if (commandString.equals("UpdateUserFullName")) {
                    System.out.println("UpdateUserFullName");
                    String username = messageSplit[1];
                    String newFullName = messageSplit[2];
                    boolean success = userRepo.UpdateUserFullName(username, newFullName);
                    if (success) {
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "UpdateFullNameSuccess|");
                    } else {
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "UpdateFullNameFailed|");
                    }
                }else if(commandString.equals("ResetPassword")) {
                	if(userRepo.ForgotPassword(messageSplit[1])) {
                		Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], "Reset_password|");
                	}
                }else if (commandString.equals("ChangePassword")) {
                    String username = messageSplit[1];
                    String currentPassword = messageSplit[2];
                    String newPassword = messageSplit[3];
                    String result = userRepo.ChangePassword(username, currentPassword, newPassword);
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length -1], result);
                // =====================================
                // chức năng ADMIN
            	}else if (commandString.equals("AdminGetListUser")) {
            		AdminRepo.AdminGetListUser(messageSplit);
            	}else if (commandString.equals("AdminAddNewAccount")){
            		AdminRepo.AdminAddNewAccount(messageSplit);
            	}else if (commandString.equals("AdminUpdateAccount")) {
                    AdminRepo.AdminUpdateAccount(messageSplit);
                }else if (commandString.equals("AdminDeleteAccount")) {
                    AdminRepo.AdminDeleteAccount(messageSplit);
                }else if (commandString.equals("AdminLockAccount")) {
                    AdminRepo.AdminLockAccount(messageSplit);
                }else if (commandString.equals("AdminUnlockAccount")) {
                    AdminRepo.AdminUnlockAccount(messageSplit);
                }else if (commandString.equals("AdminRenewPassword")) {
                    AdminRepo.AdminRenewPassword(messageSplit);
                }else if (commandString.equals("AdminGetListLoginHistory")) {
                    AdminRepo.AdminGetListLoginHistory(messageSplit);
                }else if (commandString.equals("AdminGetListFriend")) {
                    AdminRepo.AdminGetListFriend(messageSplit);
                }else if (commandString.equals("AdminGetListLogin")) {
                    AdminRepo.AdminGetListLogin(messageSplit);
                }else if (commandString.equals("AdminGetListGroup")) {
                    AdminRepo.AdminGetListGroup(messageSplit);
                }else if (commandString.equals("AdminGetListMemGroup")) {
                    AdminRepo.AdminGetListMemGroup(messageSplit);
                }else if (commandString.equals("AdminGetListAdmin")) {
                    AdminRepo.AdminGetListAdmin(messageSplit);
                }else if (commandString.equals("AdminGetListSpam")) {
                    AdminRepo.AdminGetListSpam(messageSplit);
                }else if (commandString.equals("AdminGetListNew")) {
                    AdminRepo.AdminGetListNew(messageSplit);
                }else if (commandString.equals("AdminGetChartNew")) {
                    AdminRepo.AdminGetChartNew(messageSplit);
                }else if (commandString.equals("AdminGetListFriendPlus")) {
                    AdminRepo.AdminGetListFriendPlus(messageSplit);
                }else if (commandString.equals("AdminGetListOpen")) {
                    AdminRepo.AdminGetListOpen(messageSplit);
                } else if (commandString.equals("AdminGetChartOpen")) {
                    AdminRepo.AdminGetChartOpen(messageSplit);
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
}
