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
                    	}
                        else if(dataSplit[0].equals("Register_Success")) {
                        	System.out.print("Register_Success");
                        	JOptionPane.showMessageDialog(applicationFrame, "You are successfully registered, you will be redirected to the login page shortly");
                        	ClearTab();
                        	ChangeTab(new login(app),605, 476);
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
        	applicationFrame.add(new register(this));
        	Application.app = this;
            applicationFrame.setForeground(Color.BLACK);
            applicationFrame.setTitle("Login");
            applicationFrame.setFont(new Font("Source Code Pro Light", Font.PLAIN, 12));
            applicationFrame.getContentPane().setBackground(Color.WHITE);
            applicationFrame.setBackground(Color.WHITE);
            applicationFrame.getContentPane().setFont(new Font("Source Code Pro Medium", Font.PLAIN, 11));
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
        applicationFrame.setFont(new Font("Comics San MS", Font.PLAIN, 12));
        applicationFrame.getContentPane().setBackground(Color.WHITE);
        applicationFrame.setBackground(Color.WHITE);
        applicationFrame.getContentPane().setFont(new Font("Comics San MS", Font.PLAIN, 11));
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