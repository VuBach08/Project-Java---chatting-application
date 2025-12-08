package clients;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Application {
    public static JFrame applicationFrame;
    private Thread thread;
    private static BufferedWriter os;
    private static BufferedReader is;
    private Socket socketOfClient;
    public User currentUser;
    public static String id;
    public static String userID;
    public static Application app;
    public JPanel mainPanel;
    public String focusIDString;
    public String focusNameString;
    public boolean isClosed;
    
    public void write(String message) throws IOException{
        os.write(message + "|" + id);
        os.newLine();
        os.flush();
    }
    
    public void setUpSocket() {
        try {
        	thread = new Thread() {
            @Override
            public void run() {
                try {
                	socketOfClient = new Socket("127.0.0.1", 7777);
                    System.out.println("Successfully Connected!");
                    // Tạo luồng đầu ra tại client (Gửi dữ liệu tới server)
                    os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
                    // Luồng đầu vào tại Client (Nhận dữ liệu từ server).
                    is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
                    String message;
                    while (!isClosed) {
                        message = is.readLine();
                        System.out.println("cmd:"+" "+message);
                        String[] dataSplit = message.split("\\|");

                        if (message == null) {
                            break;
                        }
                        //Setup send ID
                        if(dataSplit[0].equals("id")) {
                        	id = dataSplit[1];
                        	System.out.println(id);
                        }
           
                        if(dataSplit[0].equals("Login_Success")) {
                        	
                        	currentUser = new User(dataSplit[1],dataSplit[2],dataSplit[3],dataSplit[4],dataSplit[5].equals("true") ? true:false);
                        	onlineUsers onlList = new onlineUsers(app, currentUser);
                        	friends flist = new friends(app,currentUser);
							chatting c = new chatting(app);
							globalChatHistory gbc = new globalChatHistory(app);
							ClearTab();
							boolean isAdmin = dataSplit[5].equals("true");
							if (isAdmin) {
								ChangeTab(new Admin_demo(app), 1000, 1300);
							} else {
								try {
									write("Online|"+ currentUser.getId());
								}catch (IOException ex) {
									System.out.println("An error occurred");
									ex.printStackTrace();
								}
								applicationFrame.setLayout(new BorderLayout());
								ChangeTab(new home(app,applicationFrame,onlList, flist, c, gbc),600, 600);
							}
                        }else if(dataSplit[0].equals("LoginFailed")) {
                        	JOptionPane.showMessageDialog(applicationFrame, "User got locked or entered the wrong password");
                        }else if(dataSplit[0].equals("Reset_password")){
                        	JOptionPane.showMessageDialog(applicationFrame,"Please Check your email");
                    	}else if(dataSplit[0].equals("Reset_password")){
                        	JOptionPane.showMessageDialog(applicationFrame,"Please Check your email");
                    	}else if(dataSplit[0].equals("Register_Success")) {
                        	System.out.print("Register_Success");
                        	JOptionPane.showMessageDialog(applicationFrame, "You are successfully registered, you will be redirected to the login page shortly");
                        	ClearTab();
                        	ChangeTab(new login(app),605, 476);
                        }else if(dataSplit[0].equals("OnlineList")) {
                        	if(mainPanel instanceof home) {
                        		home home = (home) mainPanel;
                        		onlineUsers olUsers  = (onlineUsers)home.userPanel;
                        		friends flist  = (friends)home.friendsList;
                        		currentUser.friends.clear();
                        		currentUser.groupList.clear();
                        		String[] current = message.split("\\|\\|");
                        		for(int i= 1;i < current.length;++i) {
                        			String[] m = current[i].split("\\|");

                        			if(m[0].equals("user")) {
                        				currentUser.friends.add(new User(m[1],m[2],m[3].equals("true") ? true : false));
                        			}
                        			if(m[0].equals("group")) {
                        				currentUser.groupList.add(new groupChat(m[1], m[2]));
                        			}
                        		}

                        		olUsers.ClearChat();
                        		olUsers.UpdateList(currentUser);
                        	}
                        }else if(dataSplit[0].equals("GetFriend")) {
                        	if(mainPanel instanceof home) {
                        		home home = (home) mainPanel;
                        		friends flist  = (friends)home.friendsList;
                        		
                        		currentUser.friends.clear();

                        		String[] current = message.split("\\|\\|");
                        		for(int i= 1;i < current.length;++i) {
                        			String[] m = current[i].split("\\|");

                        			currentUser.friends.add(new User(m[0],m[1],false));
                        			
                        		}
                        		flist.ClearList();
                        		flist.UpdateList(currentUser);
                        	}
                        }else if(dataSplit[0].equals("MessageData")) {

                        	System.out.println("call in MessageData");
                        	if(mainPanel instanceof home) {
                        		home home = (home) mainPanel;
                        		chatting chatting  = (chatting)home.chatPanel;
                        		//chatting.ClearChat();
                        		String[] chatSplit = message.split("\\|\\|");
                        		String[] messageStrings = chatSplit[1].split("\\|");
                        		chatting.isGroup = false;
                        		chatting.information.setVisible(false);
                        		for (String messageString : messageStrings) {
                        			String msg = messageString.replace(app.focusIDString +" -","("+app.focusNameString+")")
                        					.replace(app.currentUser.getId() +" -","("+app.currentUser.fullname+")");
                        			chatting.AddChat(messageString);
                        		}
                        	}
                        }else if(dataSplit[0].equals("GroupData")) {
                        	if(mainPanel instanceof home) {
                        		home home = (home) mainPanel;
                        		chatting chatting  = (chatting)home.chatPanel;
                        		//chatting.ClearChat();
                        		String[] chatSplit = message.split("\\|\\|\\|");
                        		String[] messageStrings = chatSplit[1].split("\\|");
                        		String[] members = chatSplit[2].split("\\|\\|");
                        		chatting.isGroup = true;
                        		chatting.Lmembers.clear();
                        		
                        		chatting.information.setVisible(true);
                        		for (String messageString : messageStrings) {
                        			chatting.AddChat(messageString);
                        		}
                        		

                        		System.out.print(chatSplit[2] + " " + members.length);
                        		for (String member : members) {
                        			String[] memberDataStrings = member.split("\\|");
                        			User mUser = new User(memberDataStrings[0],memberDataStrings[1],true,memberDataStrings[2].equals("true") ? true : false);
                        			if(mUser.id.equals(currentUser.id)) {
                        				currentUser.setAdmin(mUser.isAdmin());
                        			}
                        			chatting.Lmembers.addElement(mUser);
                        			System.out.println(memberDataStrings[0] + " " + memberDataStrings[1]);
                        		}
                        	}
                        }else if(dataSplit[0].equals("SendToUser")) {
                        	if(mainPanel instanceof home) {
                        		home home = (home) mainPanel;
                        		chatting chatting  = (chatting)home.chatPanel;

                        		onlineUsers olOnlineUsers  = (onlineUsers)home.userPanel;
                        		olOnlineUsers.ClearChat();

                        		currentUser.updateFriend(dataSplit[1]);
                        		olOnlineUsers.UpdateList(currentUser);
                        		System.out.println(dataSplit[1] + " " + app.focusIDString);

                        	}
                        }else if(dataSplit[0].equals("UpdateMessage")) {
                        	if(mainPanel instanceof home) {
                        		home home = (home) mainPanel;
                        		chatting chatting  = (chatting)home.chatPanel;
                        		if(dataSplit[1].equals(app.focusIDString)) {
                        			chatting.AddChat(dataSplit[2]);
                        		}
                        	}
                    	}else if(dataSplit[0].equals("IsOffline")) {
                        	System.out.print(dataSplit[1]);
                        	if(mainPanel instanceof home) {
                        		home home = (home) mainPanel;
                        		onlineUsers olUsers  = (onlineUsers)home.userPanel;
                        		olUsers.SetOffline(dataSplit[1]);
                        	}
                        }else if(dataSplit[0].equals("IsOnline")) {
                        	System.out.print(dataSplit[1]);
                        	if(mainPanel instanceof home) {
                        		home home = (home) mainPanel;
                        		onlineUsers olUsers  = (onlineUsers)home.userPanel;
                        		olUsers.SetOnline(dataSplit[1]);
                        	}
                        }else if(dataSplit[0].equals("AddFriendSuccess")) {
                        	JOptionPane.showMessageDialog(mainPanel, "You just added new friend to the friends list");
                        }
                    }
                    os.close();
                    is.close();
                    socketOfClient.close();
                }
	                catch (UnknownHostException e) {
	                	isClosed = true;
	                    e.printStackTrace();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
            	}
        	};

            thread.run();
        } catch (Exception e) {
        	isClosed = true;
        }
    }
    

    public Application() {
        try {
        	applicationFrame = new JFrame();
        	Image icon = new ImageIcon(getClass().getResource("/icons/discord.png")).getImage();
            applicationFrame.setIconImage(icon);
        	applicationFrame.add(new login(this));
        	Application.app = this;
            applicationFrame.setForeground(Color.BLACK);
            applicationFrame.setTitle("Login");
            applicationFrame.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
            applicationFrame.getContentPane().setBackground(Color.WHITE);
            applicationFrame.setBackground(Color.WHITE);
            applicationFrame.getContentPane().setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
            applicationFrame.getContentPane().setLayout(new BoxLayout(applicationFrame.getContentPane(), BoxLayout.X_AXIS));
        	applicationFrame.setBounds(100, 100, 605, 476);
            applicationFrame.setVisible(true);
            applicationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (Exception e) {
            e.printStackTrace();
            isClosed= true;
        }
    }

    public static JFrame getApplicationFrame() {
        return applicationFrame;
    }

    public void ClearTab() {
    	applicationFrame.getContentPane().removeAll();
    }

    public void ChangeTab(String name) {
    	applicationFrame.setTitle(name);
    }

    public void ChangeTab(JPanel newPanel,int h,int w) {
    	applicationFrame.getContentPane().add(newPanel);

    	applicationFrame.setForeground(Color.BLACK);
        applicationFrame.setTitle("Login");
        applicationFrame.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        applicationFrame.getContentPane().setBackground(Color.WHITE);
        applicationFrame.setBackground(Color.WHITE);
        applicationFrame.getContentPane().setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
        applicationFrame.getContentPane().setLayout(new BoxLayout(applicationFrame.getContentPane(), BoxLayout.X_AXIS));
    	applicationFrame.pack();
        applicationFrame.setVisible(true);
        applicationFrame.setSize(h, w);
        mainPanel = newPanel;
    }

    public static void main(String[] args) {
        app = new Application();
        app.setUpSocket();
    }
}