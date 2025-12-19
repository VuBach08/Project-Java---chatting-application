package clients.user;

import clients.Application;
import clients.models.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class globalChatHistory extends JPanel {
    DefaultListModel<String> chatResults;
    private JList<String> chatDisplay;
    private JTextField searchBar;
    Application parent;
    private void SetPlaceholder(JTextField textField, String placeholder)
    {
        textField.setForeground(Color.GRAY);
        textField.setText(placeholder);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK); // Set text color to default when focused
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder); // Reset placeholder text when focus is lost
                }
            }
        });
    }

    public globalChatHistory(Application app) {
    	this.parent = app;
        this.setLayout(new BorderLayout());
        
        searchBar = new JTextField();
        chatResults = new DefaultListModel<>();
        searchBar.setMargin(new Insets(15,10,15,10));
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY), // Border color
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        searchBar.setSize(new Dimension(600, 200));
        SetPlaceholder(searchBar, "Type A Sentence You Want To Search For");
        chatDisplay = new JList<String>(chatResults);
        searchBar.addActionListener(e -> {
            if (e.getSource() == searchBar) {
                String id = parent.currentUser.getId();
                String content = searchBar.getText();

                if (!searchBar.getText().equals("") && parent.currentUser != null) {
                    try {
                    	parent.write("GlobalSearch|" + id + "|" + content);
                        searchBar.setText("");
                    } catch (IOException ex) {
                        ex.getStackTrace();
                        System.out.println("Unable to carry out action");
                    }
                }
            }
        });

        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setSize(new Dimension(600, 600));

        JScrollPane scrollPane = new JScrollPane(chatDisplay);
        scrollPane.setSize(600, 400);
        displayPanel.add(scrollPane, BorderLayout.CENTER);
        this.add(searchBar, BorderLayout.NORTH);
        this.add(displayPanel, BorderLayout.CENTER);
    }
    public void ClearResult() {
    	chatResults.clear();
    }
    public void AddResult(String msg) {
    	chatResults.addElement(msg);
    }
}
