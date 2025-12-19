package clients.user;

import clients.Application;
import clients.models.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.UUID;

import javax.swing.JOptionPane;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class register extends JPanel {
	private JTextField email;
	private JPasswordField password;
	private JTextField name;

	private static int maxUserID = 1000000;
	private static int minUserID = 1;
	private Application parent;
	private JTextField fullname;

	/**
	 * Create the application.
	 */
	public register(Application app) {
		this.parent = app;
		initialize();
	}

	private void initialize() {
	    setForeground(Color.BLACK);
	    setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
	    setBackground(Color.WHITE);
	    setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
	    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

	    JPanel panel_1 = new JPanel();
	    panel_1.setBackground(new Color(128, 128, 128));
	    add(panel_1);
	    panel_1.setLayout(null);

	    JLabel lblNewLabel = new JLabel("Register");
	    lblNewLabel.setForeground(new Color(255, 255, 255));
	    lblNewLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
	    lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    lblNewLabel.setBounds(157, 36, 290, 28);
	    panel_1.add(lblNewLabel);

	    email = new JTextField();
	    email.setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
	    email.setPreferredSize(new Dimension(7, 22));
	    email.setBounds(201, 216, 202, 28);
	    panel_1.add(email);
	    email.setColumns(10);

	    JLabel lblNewLabel_1 = new JLabel("email:");
	    lblNewLabel_1.setForeground(new Color(255, 255, 255));
	    lblNewLabel_1.setFont(new Font("Comic Sans MS", Font.BOLD, 11));
	    lblNewLabel_1.setBounds(201, 195, 202, 14);
	    panel_1.add(lblNewLabel_1);

	    password = new JPasswordField();
	    password.setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
	    password.setPreferredSize(new Dimension(7, 22));
	    password.setColumns(10);
	    password.setBounds(201, 276, 202, 28);
	    panel_1.add(password);

	    JLabel lblNewLabel_1_1 = new JLabel("password:");
	    lblNewLabel_1_1.setForeground(new Color(255, 255, 255));
	    lblNewLabel_1_1.setFont(new Font("Comic Sans MS", Font.BOLD, 11));
	    lblNewLabel_1_1.setBounds(201, 255, 202, 14);
	    panel_1.add(lblNewLabel_1_1);

	    JButton btnNewButton = new JButton("Register");
	    btnNewButton.setBorder(null);
	    btnNewButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            if (name.getText().isEmpty() || password.getText().isEmpty() || email.getText().isEmpty()) {
	                JOptionPane.showMessageDialog(register.this, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
	                return;
	            }

	            String id = UUID.randomUUID().toString();
	            String hashedPW = User.hashPassword(password.getText());
	            if (hashedPW == null) hashedPW = password.getText();

	            try {
	                parent.write("Register|" + id + "|" + name.getText() + "|" + fullname.getText() + "|" + email.getText() + "|" + hashedPW);
	                // Don't switch yet — wait for server response!
	            } catch (IOException ex) {
	                JOptionPane.showMessageDialog(register.this, "Connection error!", "Error", JOptionPane.ERROR_MESSAGE);
	                ex.printStackTrace();
	            }
	        }
	    });
	    btnNewButton.setForeground(Color.WHITE);
	    btnNewButton.setBackground(new Color(32, 178, 170));
	    btnNewButton.setFont(new Font("Comic Sans MS", Font.BOLD, 11));
	    btnNewButton.setBounds(201, 330, 203, 38);
	    panel_1.add(btnNewButton);

	    JButton btnLogin = new JButton("Login");
	    btnLogin.setBorder(null);
	    btnLogin.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            parent.ClearTab();
	            parent.ChangeTab(new login(parent), 605, 476);
	        }
	    });
	    btnLogin.setForeground(Color.WHITE);
	    btnLogin.setBackground(new Color(0, 144, 255));
	    btnLogin.setFont(new Font("Comic Sans MS", Font.BOLD, 11));
	    btnLogin.setBounds(201, 378, 203, 38);
	    panel_1.add(btnLogin);

	    name = new JTextField();
	    name.setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
	    name.setPreferredSize(new Dimension(7, 22));
	    name.setColumns(10);
	    name.setBounds(201, 161, 202, 28);
	    panel_1.add(name);

	    JLabel lblNewLabel_1_2 = new JLabel("name:");
	    lblNewLabel_1_2.setBackground(new Color(255, 255, 255));
	    lblNewLabel_1_2.setForeground(new Color(255, 255, 255));
	    lblNewLabel_1_2.setFont(new Font("Comic Sans MS", Font.BOLD, 11));
	    lblNewLabel_1_2.setBounds(201, 140, 202, 14);
	    panel_1.add(lblNewLabel_1_2);

	    JLabel lblNewLabel_1_2_1 = new JLabel("fullname:");
	    lblNewLabel_1_2_1.setForeground(new Color(255, 255, 255));
	    lblNewLabel_1_2_1.setBackground(new Color(255, 255, 255));
	    lblNewLabel_1_2_1.setFont(new Font("Comic Sans MS", Font.BOLD, 11));
	    lblNewLabel_1_2_1.setBounds(201, 80, 202, 14);
	    panel_1.add(lblNewLabel_1_2_1);

	    fullname = new JTextField();
	    fullname.setPreferredSize(new Dimension(7, 22));
	    fullname.setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
	    fullname.setColumns(10);
	    fullname.setBounds(201, 101, 202, 28);
	    panel_1.add(fullname);

	    setBounds(100, 100, 605, 492);
	}
}
