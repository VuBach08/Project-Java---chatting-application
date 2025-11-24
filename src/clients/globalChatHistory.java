package clients;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.Font;

public class globalChatHistory extends JPanel {
	DefaultListModel<String> chatResults;
	private JList<String> chatDisplay;
	private JTextField searchBar;
	Application parent;
	private void SetPlaceholder(JTextField textField, String placeholder) {
	    textField.setForeground(new Color(255, 255, 255));
	    textField.setText(placeholder);

	    textField.addFocusListener(new java.awt.event.FocusAdapter() {
	        @Override
	        public void focusGained(java.awt.event.FocusEvent e) {
	            if (textField.getText().equals(placeholder)) {
	                textField.setText("");
	                textField.setForeground(Color.BLACK);
	            }
	        }

	        @Override
	        public void focusLost(java.awt.event.FocusEvent e) {
	            if (textField.getText().isEmpty()) {
	                textField.setForeground(Color.GRAY);
	                textField.setText(placeholder);
	            }
	        }
	    });
	}

	public globalChatHistory(Application app) {
	    this.parent = app;
	    this.setLayout(new BorderLayout());

	    searchBar = new JTextField();
	    searchBar.setFont(new Font("Comic Sans MS", Font.ITALIC, 10));
	    searchBar.setBackground(new Color(0, 128, 255));
	    chatResults = new DefaultListModel<>();

	    searchBar.setMargin(new Insets(15, 10, 15, 10));
	    searchBar.setBorder(BorderFactory.createCompoundBorder(
	            BorderFactory.createLineBorder(Color.GRAY),
	            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
	    searchBar.setSize(new Dimension(600, 200));
	    SetPlaceholder(searchBar, "Type A Sentence You Want To Search For");

	    chatDisplay = new JList<>(chatResults);
	    chatDisplay.setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
	    chatDisplay.setBackground(new Color(128, 128, 128));

	    searchBar.addActionListener(e -> {
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
