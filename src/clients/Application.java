package clients;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Application {
    public static JFrame applicationFrame;
    private Thread thread;
    private static BufferedWriter os;
    private static BufferedReader is;
    private Socket socketOfClient;

    public static Application app;
    public JPanel mainPanel;
    public String focusIDString;
    public String focusNameString;
    public boolean isClosed;
 

    public Application() {
        try {
        	applicationFrame = new JFrame();
        	User currentUser = null;
        	onlineUsers onlList = new onlineUsers(app, currentUser);
        	friends flist = new friends(app,currentUser);
			chatting c = new chatting(app);
			globalChatHistory gbc = new globalChatHistory(app);
			ClearTab();
			boolean isAdmin = false;
			if (isAdmin) {
				ChangeTab(new Admin_demo(app), 1000, 1300);
			} else {
			
				applicationFrame.getContentPane().setLayout(new BorderLayout());
				ChangeTab(new home(app,applicationFrame,onlList, flist, c, gbc),600, 600);
			}
        	Application.app = this;
            applicationFrame.setForeground(Color.BLACK);
            applicationFrame.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
            applicationFrame.getContentPane().setBackground(Color.WHITE);
            applicationFrame.setBackground(Color.WHITE);
            applicationFrame.getContentPane().setFont(new Font("Comics San MS", Font.PLAIN, 11));
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
    }
}