package clients;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.IOException;

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

	    chatDisplay = new JList<String>(chatResults);
	    chatDisplay.setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
	    chatDisplay.setBackground(new Color(128, 128, 128));
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
