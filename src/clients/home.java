package clients;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;

public class home extends JPanel implements ActionListener {
	final static String CHAT_PANEL = "Chat Panel";
	final static String FRIENDS_PANEL = "Friends Panel";
	final static String GLOBAL_CHAT_HISTORY = "Search In Chat";
	final static String CREATE_GROUP = "Create Group";

	public JPanel chatPanel;
	public JPanel userPanel;
	public JPanel mainPanel;
	public JPanel friendsList;
	public JPanel chatHistory;
	JPanel mainContainer;
	public Application parent;

	@Override
	public void actionPerformed(ActionEvent e) {
	    CardLayout cardLayout = (CardLayout) (mainContainer.getLayout());
	    cardLayout.show(mainContainer, e.getActionCommand());
	}

	public JPanel getChatPanel() {
	    return chatPanel;
	}

	public void setChatPanel(JPanel chat) {
	    if (chatPanel != null) {
	        mainPanel.remove(chatPanel);
	    }
	    chatPanel = chat;
	    mainPanel.add(chatPanel, BorderLayout.CENTER);
	    mainPanel.revalidate();
	    mainPanel.repaint();
	}

	public home(Application app, JFrame mainFrame,
	            JPanel users, JPanel friends, JPanel chat, JPanel history) {

	    this.parent = app;

	    this.setLayout(new BorderLayout());

	    mainContainer = new JPanel(new CardLayout());
	    mainPanel = new JPanel(new BorderLayout());
	    mainPanel.setForeground(new Color(255, 255, 255));
	    mainPanel.setBackground(new Color(128, 128, 128));

	    int totalWidth = 600;
	    int userWidth = (int) (totalWidth * 0.4);
	    int chatWidth = (int) (totalWidth * 0.6);

	    userPanel = users;
	    friendsList = friends;
	    chatPanel = chat;
	    chatHistory = history;

	    chatPanel.setPreferredSize(new Dimension(chatWidth, 800));
	    userPanel.setPreferredSize(new Dimension(userWidth, 800));

	    mainPanel.add(userPanel, BorderLayout.WEST);
	    mainPanel.add(chatPanel, BorderLayout.CENTER);

	    JButton toChat = new JButton("Chat With Friends");
	    toChat.setFont(new Font("Comic Sans MS", Font.PLAIN, 10));
	    toChat.setBackground(new Color(0, 128, 255));
	    toChat.setForeground(Color.WHITE);
	    toChat.setOpaque(true);
	    toChat.setBorderPainted(false);
	    toChat.setActionCommand(CHAT_PANEL);
	    toChat.addActionListener(this);

	    JButton toFriends = new JButton("Find Friends");
	    toFriends.setFont(new Font("Comic Sans MS", Font.PLAIN, 10));
	    toFriends.setBackground(new Color(0, 128, 255));
	    toFriends.setForeground(Color.WHITE);
	    toFriends.setOpaque(true);
	    toFriends.setBorderPainted(false);
	    toFriends.setActionCommand(FRIENDS_PANEL);
	    toFriends.addActionListener(this);

	    JButton toChatHistory = new JButton("Search In Chat");
	    toChatHistory.setFont(new Font("Comic Sans MS", Font.PLAIN, 10));
	    toChatHistory.setBackground(new Color(0, 128, 255));
	    toChatHistory.setForeground(new Color(255, 255, 255));
	    toChatHistory.setOpaque(true);
	    toChatHistory.setBorderPainted(false);
	    toChatHistory.setActionCommand(GLOBAL_CHAT_HISTORY);
	    toChatHistory.addActionListener(this);
	    
	    JButton toCreateGroup = new JButton("Create Group");
	    toCreateGroup.setBackground(new Color(0, 128, 255)); 
	    toCreateGroup.setForeground(Color.WHITE);
	    toCreateGroup.setOpaque(true);
	    toCreateGroup.setBorderPainted(false);
	    toCreateGroup.setFont(new Font("Comic Sans MS", Font.PLAIN, 10));
	    toCreateGroup.addActionListener(new ActionListener() {
	    	@Override
			public void actionPerformed(ActionEvent e) {
				JTextField name = new JTextField();
				JLabel guide = new JLabel("Please type your friend name separated by (|)");
				JTextField members = new JTextField();
				Object[] message = {
				    "Group name:", name,
				    "Guide: ",guide,
				    "Friend Names:", members
				};

				int option = JOptionPane.showConfirmDialog(null, message, "Create Group", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION) {
				    if (!name.getText().equals("") && !members.getText().equals("")) {
				        try {
							parent.write("CreateGroup||"+ name.getText()+ "||" + parent.currentUser.id + "||" + members.getText() + "|" + parent.currentUser.id+ "|");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				    }
				}
			 }
	    });

	    JPanel controlPanel = new JPanel(new GridLayout(1, 4));
	    controlPanel.add(toChat);
	    controlPanel.add(toFriends);
	    controlPanel.add(toChatHistory);
	    controlPanel.add(toCreateGroup);

	    mainContainer.add(mainPanel, CHAT_PANEL);
	    mainContainer.add(friendsList, FRIENDS_PANEL);
	    mainContainer.add(chatHistory, GLOBAL_CHAT_HISTORY);

	    this.add(controlPanel, BorderLayout.NORTH);
	    this.add(mainContainer, BorderLayout.CENTER);

	    mainFrame.setSize(800, 600);
	    mainFrame.setLocationRelativeTo(null);
	}
}