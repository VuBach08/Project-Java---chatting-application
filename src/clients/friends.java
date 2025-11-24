package clients;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.Font;

public class friends extends JPanel {
    DefaultListModel<User> allFriends;
    private JList<User> userList;
    private JTextField searchBar;
    private Application parent;

    static class CustomCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof User) {
                setText(((User) value).getName());
                setForeground(Color.BLUE);
            }

            return renderer;
        }
    }

    public void UpdateList(User user) {
        //we can dynamically add users/groups here
        int i = 0;
        for (int j = 0; j < user.friends.size(); ++i, ++j) {
        	allFriends.addElement(user.friends.get(j));
        }
    }
    
    public void ClearList() {
    	allFriends.clear();
    }

    private void SetPlaceholder(JTextField textField) {
        textField.setForeground(new Color(255, 255, 255));
        textField.setText("Add A New Friend");
    }

    private void showPopupMenu(int x, int y, JList<User> list, User user) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem unfriend = new JMenuItem("Unfriend");
        JMenuItem block = new JMenuItem("Block");

        popupMenu.add(block);
        popupMenu.add(unfriend);
        popupMenu.show(list, x, y);
    }

    private void showPopupOptions(int x, int y, JList<User> list, User user) {
        JPopupMenu options = new JPopupMenu();
        JMenuItem refresh = new JMenuItem("Refresh");

        options.add(refresh);
        options.show(list, x, y);
    }

    public friends(Application app,User user) {
    	this.parent = app;
        this.setLayout(new BorderLayout());
        searchBar = new JTextField();
        searchBar.setFont(new Font("Comic Sans MS", Font.ITALIC, 11));
        searchBar.setBackground(new Color(0, 128, 255));
        searchBar.setMargin(new Insets(15, 10, 15, 10));
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY), // Border color
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        searchBar.setSize(new Dimension(600, 200));
        SetPlaceholder(searchBar);

        //add event for enter key -> query user -> add...

        JPanel userListPanel = new JPanel(new BorderLayout());
        userListPanel.setSize(new Dimension(600, 600));

        allFriends = new DefaultListModel<>();
        //we can dynamically add users here
        for (int j = 0; j < 2; ++j) {
            allFriends.addElement(new User("1", "Bach", true));
        }

        userList = new JList<>(allFriends);
        userList.setBackground(new Color(128, 128, 128));
        userList.setForeground(new Color(255, 255, 255));
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setCellRenderer(new CustomCellRenderer());

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setSize(600, 400);
        userListPanel.add(scrollPane, BorderLayout.CENTER);
        this.add(searchBar, BorderLayout.NORTH);
        this.add(userListPanel, BorderLayout.CENTER);
    }
}
