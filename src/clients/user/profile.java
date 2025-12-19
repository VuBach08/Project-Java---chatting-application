package clients.user;

import clients.Application;
import clients.models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class profile extends JPanel {
    private Application parent;
    private User currentUser;
    
    private JTextField usernameField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField addressField;
    private JTextField dobField;
    private JTextField genderField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    
    public profile(Application app, User user) {
        this.parent = app;
        this.currentUser = user;
        
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // Title
        JLabel titleLabel = new JLabel("User Profile");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 128, 255));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Profile information panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username (read-only)
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameTitleLabel = new JLabel("Username:");
        usernameTitleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        infoPanel.add(usernameTitleLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        usernameField = new JTextField(currentUser.name != null ? currentUser.name : currentUser.id);
        usernameField.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(300, 30));
        usernameField.setEditable(false);
        usernameField.setBackground(new Color(240, 240, 240));
        infoPanel.add(usernameField, gbc);
        
        // Full Name (editable)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel fullNameTitleLabel = new JLabel("Full Name:");
        fullNameTitleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        infoPanel.add(fullNameTitleLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        fullNameField = new JTextField(currentUser.fullname != null ? currentUser.fullname : "");
        fullNameField.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        fullNameField.setPreferredSize(new Dimension(300, 30));
        infoPanel.add(fullNameField, gbc);
        
        // Email (editable)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        JLabel emailTitleLabel = new JLabel("Email:");
        emailTitleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        infoPanel.add(emailTitleLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        emailField = new JTextField(currentUser.email != null ? currentUser.email : "");
        emailField.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(300, 30));
        emailField.setEditable(false);
        emailField.setBackground(new Color(240, 240, 240));
        infoPanel.add(emailField, gbc);
        
        // Address (editable)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        JLabel addressTitleLabel = new JLabel("Address:");
        addressTitleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        infoPanel.add(addressTitleLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        addressField = new JTextField("");
        addressField.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        addressField.setPreferredSize(new Dimension(300, 30));
        infoPanel.add(addressField, gbc);
        
        // Date of Birth (editable)
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        JLabel dobTitleLabel = new JLabel("Date of Birth (YYYY-MM-DD):");
        dobTitleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        infoPanel.add(dobTitleLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dobField = new JTextField("");
        dobField.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        dobField.setPreferredSize(new Dimension(300, 30));
        infoPanel.add(dobField, gbc);
        
        // Gender (editable)
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        JLabel genderTitleLabel = new JLabel("Gender:");
        genderTitleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        infoPanel.add(genderTitleLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        genderField = new JTextField("");
        genderField.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        genderField.setPreferredSize(new Dimension(300, 30));
        infoPanel.add(genderField, gbc);
        
        mainPanel.add(infoPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Button panel for profile update
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton updateButton = new JButton("Update Profile");
        updateButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        updateButton.setBackground(new Color(0, 128, 255));
        updateButton.setForeground(Color.WHITE);
        updateButton.setOpaque(true);
        updateButton.setBorderPainted(false);
        updateButton.setPreferredSize(new Dimension(200, 40));
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProfile();
            }
        });
        
        JButton refreshButton = new JButton("Refresh Profile");
        refreshButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        refreshButton.setBackground(new Color(34, 139, 34));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setOpaque(true);
        refreshButton.setBorderPainted(false);
        refreshButton.setPreferredSize(new Dimension(200, 40));
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadUserProfile();
            }
        });
        
        buttonPanel.add(updateButton);
        buttonPanel.add(refreshButton);
        
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setPreferredSize(new Dimension(500, 2));
        separator.setForeground(new Color(200, 200, 200));
        JPanel sepPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sepPanel.setBackground(Color.WHITE);
        sepPanel.add(separator);
        mainPanel.add(sepPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Change Password Section
        JLabel passwordTitleLabel = new JLabel("Change Password");
        passwordTitleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        passwordTitleLabel.setForeground(new Color(0, 128, 255));
        passwordTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(passwordTitleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Password change panel
        JPanel passwordPanel = new JPanel(new GridBagLayout());
        passwordPanel.setBackground(Color.WHITE);
        GridBagConstraints gbcPw = new GridBagConstraints();
        gbcPw.insets = new Insets(10, 10, 10, 10);
        gbcPw.anchor = GridBagConstraints.WEST;
        gbcPw.fill = GridBagConstraints.HORIZONTAL;
        
        // Current Password
        gbcPw.gridx = 0;
        gbcPw.gridy = 0;
        gbcPw.weightx = 0.0;
        JLabel currentPwLabel = new JLabel("Current Password:");
        currentPwLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        passwordPanel.add(currentPwLabel, gbcPw);
        
        gbcPw.gridx = 1;
        gbcPw.weightx = 1.0;
        currentPasswordField = new JPasswordField();
        currentPasswordField.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        currentPasswordField.setPreferredSize(new Dimension(300, 30));
        passwordPanel.add(currentPasswordField, gbcPw);
        
        // New Password
        gbcPw.gridx = 0;
        gbcPw.gridy = 1;
        gbcPw.weightx = 0.0;
        JLabel newPwLabel = new JLabel("New Password:");
        newPwLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        passwordPanel.add(newPwLabel, gbcPw);
        
        gbcPw.gridx = 1;
        gbcPw.weightx = 1.0;
        newPasswordField = new JPasswordField();
        newPasswordField.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        newPasswordField.setPreferredSize(new Dimension(300, 30));
        passwordPanel.add(newPasswordField, gbcPw);
        
        // Confirm Password
        gbcPw.gridx = 0;
        gbcPw.gridy = 2;
        gbcPw.weightx = 0.0;
        JLabel confirmPwLabel = new JLabel("Confirm Password:");
        confirmPwLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        passwordPanel.add(confirmPwLabel, gbcPw);
        
        gbcPw.gridx = 1;
        gbcPw.weightx = 1.0;
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        confirmPasswordField.setPreferredSize(new Dimension(300, 30));
        passwordPanel.add(confirmPasswordField, gbcPw);
        
        mainPanel.add(passwordPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Change password button
        JPanel passwordButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        passwordButtonPanel.setBackground(Color.WHITE);
        
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        changePasswordButton.setBackground(new Color(220, 20, 60));
        changePasswordButton.setForeground(Color.WHITE);
        changePasswordButton.setOpaque(true);
        changePasswordButton.setBorderPainted(false);
        changePasswordButton.setPreferredSize(new Dimension(200, 40));
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePassword();
            }
        });
        
        passwordButtonPanel.add(changePasswordButton);
        mainPanel.add(passwordButtonPanel);
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Load user profile data when panel is created
        loadUserProfile();
    }
    
    private void loadUserProfile() {
        try {
            parent.write("GetUserProfile|" + currentUser.id);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to load user profile: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateProfile() {
        String newFullName = fullNameField.getText().trim();
        String newAddress = addressField.getText().trim();
        String newDob = dobField.getText().trim();
        String newGender = genderField.getText().trim();
        
        if (newFullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Full name cannot be empty!", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Send update request to server (you'll need to create this command in server)
            parent.write("UpdateUserProfile|" + currentUser.id + "|" + newFullName + "|" + newAddress + "|" + newDob + "|" + newGender);
            
            // Update local user object
            currentUser.fullname = newFullName;
            
            JOptionPane.showMessageDialog(this, 
                "Profile updated successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to update profile: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void changePassword() {
        String currentPassword = new String(currentPasswordField.getPassword()).trim();
        String newPassword = new String(newPasswordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "All password fields are required!", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "New password and confirmation do not match!", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (currentPassword.equals(newPassword)) {
            JOptionPane.showMessageDialog(this, 
                "New password must be different from current password!", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
        	parent.write("ChangePassword|" + currentUser.name + "|" +User.hashPassword(currentPassword) + "|" + User.hashPassword(newPassword));
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to change password: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void updateProfileDisplay(String username, String fullname, String email, String address, String dob, String gender) {
        SwingUtilities.invokeLater(() -> {
            usernameField.setText(username != null && !username.isEmpty() ? username : "N/A");
            fullNameField.setText(fullname != null && !fullname.isEmpty() ? fullname : "");
            emailField.setText(email != null && !email.isEmpty() ? email : "");
            addressField.setText(address != null && !address.isEmpty() ? address : "");
            dobField.setText(dob != null && !dob.isEmpty() ? dob : "");
            genderField.setText(gender != null && !gender.isEmpty() ? gender : "");
        });
    }
}