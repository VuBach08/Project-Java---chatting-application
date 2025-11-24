package clients;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class onlineUsers extends JPanel {
    private DefaultListModel<Object> sideList;
    public JList<Object> usersAndgroups;
    private JTextField searchBar;
    private JLabel navigation;
    private Application parent;

    static class CustomRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof User) {
                String status = ((User) value).isOnline() ? "online" : "offline";
                setText(((User) value).getName() + " - Status: " + status);
                if (((User) value).chatWithU) {
                    setForeground(Color.RED);
                } else {
                    setForeground(Color.BLUE);
                }
            }
            if (value instanceof groupChat) {
                setText("(group) - " + ((groupChat) value).getGroupName());
                setForeground(Color.PINK);
            }

            return renderer;
        }
    }

    private void showPopupMenuDirect(int x, int y, JList<Object> list, User user) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem spam = new JMenuItem("Report For Spam");
        JMenuItem clearChatHistory = new JMenuItem("Clear Chat History");

        popupMenu.add(spam);
        popupMenu.add(clearChatHistory);
        popupMenu.show(list, x, y);
    }

    private void showPopupOptions(int x, int y, JList<Object> list, User user) {
        JPopupMenu options = new JPopupMenu();
        JMenuItem refresh = new JMenuItem("Refresh");

        options.add(refresh);
        options.show(list, x, y);
    }

    private void SetPlaceholder(JTextField textField, String placeholder) {
        textField.setForeground(new Color(255, 255, 255));
        textField.setText(placeholder);
    }

    public onlineUsers(Application app, User user) {
        this.parent = app;
        this.setLayout(new BorderLayout());
        navigation = new JLabel("Welcome, Bach ");
        navigation.setForeground(new Color(255, 255, 255));
        navigation.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        navigation.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Padding
        navigation.setOpaque(true);
        navigation.setBackground(new Color(0, 128, 255));

        searchBar = new JTextField();
        searchBar.setBackground(new Color(128, 128, 128));
        searchBar.setFont(new Font("Comic Sans MS", Font.ITALIC, 11));
        searchBar.setMargin(new Insets(15, 10, 15, 10));
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY), // Border color
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        searchBar.setSize(new Dimension(600, 200));
        SetPlaceholder(searchBar, "Chat With A Friend");

        JPanel usersAndgroupsPanel = new JPanel(new BorderLayout());

        sideList = new DefaultListModel<>();
        //we can dynamically add users/groups here
        int i = 0;
        for (int j = 0; j < 2; ++i, ++j) {
            sideList.add(i, new User("1", "Bach",true));
        }

        for (int j = 0; j < 2; ++i, ++j) {
        	sideList.add(i, new User("1", "Bach",true));
        }

        usersAndgroups = new JList<>(sideList);
        usersAndgroups.setForeground(new Color(255, 255, 255));
        usersAndgroups.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        usersAndgroups.setBackground(new Color(128, 128, 128));
        usersAndgroups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersAndgroups.setCellRenderer(new CustomRenderer());

        JScrollPane scrollPane = new JScrollPane(usersAndgroups);
        scrollPane.setSize(320, 400);
        scrollPane.setVerticalScrollBar(new JScrollBar());

        usersAndgroupsPanel.add(scrollPane, BorderLayout.CENTER);
        this.add(navigation, BorderLayout.NORTH);
        this.add(usersAndgroupsPanel, BorderLayout.CENTER);
        this.add(searchBar, BorderLayout.AFTER_LAST_LINE);
    }
}
