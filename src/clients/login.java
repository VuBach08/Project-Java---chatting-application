package clients;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class login extends JPanel {
	private JTextField email;
	private JPasswordField password;
	private Application parent;
	public login(Application parent) {
	    this.parent = parent;
	    initialize();
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

	private void initialize() {
	    setForeground(Color.BLACK);
	    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

	    JPanel panel_1 = new JPanel();
	    panel_1.setBackground(new Color(192, 192, 192));
	    add(panel_1);
	    panel_1.setLayout(null);

	    JLabel lblNewLabel = new JLabel("Login");
	    lblNewLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
	    lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    lblNewLabel.setBounds(157, 70, 290, 28);
	    panel_1.add(lblNewLabel);

	    email = new JTextField();
	    email.setFont(new Font("Source Code Pro", Font.PLAIN, 11));
	    email.setPreferredSize(new Dimension(7, 22));
	    email.setBounds(201, 129, 202, 28);
	    panel_1.add(email);
	    email.setColumns(10);

	    JLabel lblNewLabel_1 = new JLabel("email or username:");
	    lblNewLabel_1.setFont(new Font("Comic Sans MS", Font.BOLD, 11));
	    lblNewLabel_1.setBounds(201, 108, 202, 14);
	    panel_1.add(lblNewLabel_1);

	    password = new JPasswordField();
	    password.setFont(new Font("Source Code Pro", Font.PLAIN, 11));
	    password.setPreferredSize(new Dimension(7, 22));
	    password.setColumns(10);
	    password.setBounds(201, 189, 202, 28);
	    panel_1.add(password);

	    JLabel lblNewLabel_1_1 = new JLabel("password:");
	    lblNewLabel_1_1.setFont(new Font("Comic Sans MS", Font.BOLD, 11));
	    lblNewLabel_1_1.setBounds(201, 168, 202, 14);
	    panel_1.add(lblNewLabel_1_1);

	    JButton btnNewButton = new JButton("Login");
	    btnNewButton.setBorder(UIManager.getBorder("Button.border"));
	    btnNewButton.setContentAreaFilled(false);
	    btnNewButton.setOpaque(true);
	    btnNewButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        }
	    });
	    btnNewButton.setForeground(Color.BLACK);
	    btnNewButton.setBackground(Color.GREEN);
	    btnNewButton.setFont(new Font("Comic Sans MS", Font.BOLD, 11));
	    btnNewButton.setBounds(201, 246, 203, 38);
	    panel_1.add(btnNewButton);

	    JButton btnlogin = new JButton("Register");
	    btnlogin.setContentAreaFilled(false);
	    btnlogin.setOpaque(true);
	    btnlogin.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            parent.ClearTab();
	            parent.ChangeTab(new register(parent), 605, 476);
	        }
	    });
	    btnlogin.setForeground(Color.BLACK);
	    btnlogin.setFont(new Font("Comic Sans MS", Font.BOLD, 11));
	    btnlogin.setBackground(Color.YELLOW);
	    btnlogin.setBounds(201, 294, 203, 38);
	    panel_1.add(btnlogin);

	    JButton resetPW = new JButton("Reset Password");
	    resetPW.setBorder(UIManager.getBorder("Button.border"));
	    resetPW.setContentAreaFilled(false);
	    resetPW.setOpaque(true);
	    resetPW.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            String toEmail = JOptionPane.showInputDialog(
	                    "Please specify your email for password recovery");
	        }
	    });
	    resetPW.setForeground(Color.BLACK);
	    resetPW.setBackground(new Color(255, 0, 0));
	    resetPW.setFont(new Font("Comic Sans MS", Font.BOLD, 11));
	    resetPW.setBounds(201, 342, 203, 38);
	    panel_1.add(resetPW);

	    setBounds(100, 100, 605, 476);
	}

}
