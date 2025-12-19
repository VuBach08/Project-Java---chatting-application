package clients.user;

import clients.Application;
import clients.models.*;
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
        textField.setForeground(Color.GRAY);
        textField.setText("Add A New Friend");

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals("Add A New Friend")) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK); // Set text color to default when focused
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText("Add A New Friend"); // Reset placeholder text when focus is lost
                }
            }
        });
    }

    private void showPopupMenu(int x, int y, JList<User> list, User user) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem unfriend = new JMenuItem("Unfriend");
        JMenuItem block = new JMenuItem("Block");

        unfriend.addActionListener(e -> {
            User deletedFriend = list.getSelectedValue();

            String fromUser = user.getId();
            String deletedUser = deletedFriend.getId();

            if (deletedUser != null) {
                if (JOptionPane.showConfirmDialog(this, "Are you sure you want to remove " + deletedFriend.getName() + " from your friends list?","Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    try {
                    	parent.write("DeleteFriend|"+fromUser+"|"+deletedUser);
                        JOptionPane.showMessageDialog(this, "Successfully removed " + deletedFriend.getName() + " from the friends list");

                        allFriends.removeElement(deletedFriend);
                    } catch (IOException ex) {
                        ex.getStackTrace();
                        System.out.println("Unable to carry out action");
                    }
                }
            }
        });

        block.addActionListener(e -> {
            User blockedFriend = list.getSelectedValue();

            String fromUser = user.getId();
            String blockedUser = blockedFriend.getId();

            if (blockedUser != null) {
                if (JOptionPane.showConfirmDialog(this, "Are you sure you want to block " + blockedFriend.getName() + "?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                {
                    try {
                    	parent.write("BlockAccount|"+fromUser+"|"+blockedUser);
                        JOptionPane.showMessageDialog(this, "User " + blockedFriend.getName() + " blocked from the friends list");

                        allFriends.removeElement(blockedFriend);
                    } catch (IOException ex) {
                        ex.getStackTrace();
                        System.out.println("Unable to carry out action");
                    }
                }
            }
        });

        popupMenu.add(block);
        popupMenu.add(unfriend);
        popupMenu.show(list, x, y);
    }

    private void showPopupOptions(int x, int y, JList<User> list, User user) {
        JPopupMenu options = new JPopupMenu();
        JMenuItem refresh = new JMenuItem("Refresh");

        refresh.addActionListener(e -> {
            allFriends.clear();
            for (User friend : user.getFriends()) {
                allFriends.addElement(friend);
            }
        });

        options.add(refresh);
        options.show(list, x, y);
    }

    public friends(Application app,User user) {
    	this.parent = app;
        this.setLayout(new BorderLayout());
        searchBar = new JTextField();
        searchBar.setMargin(new Insets(15, 10, 15, 10));
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY), // Border color
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        searchBar.setSize(new Dimension(600, 200));
        SetPlaceholder(searchBar);

        //add event for enter key -> query user -> add...
        searchBar.addActionListener(e -> {
            if (e.getSource() == searchBar) {
                String fromUser = app.currentUser.getId();
                String toUser = searchBar.getText();
//                User newFriend = UserAuthentication.idToUser(toUser);

                if (!searchBar.getText().equals("") && app.currentUser != null) {
                    try {
                    	parent.write("AddFriend|" + fromUser + "|" + toUser);
                        searchBar.setText("");
                    } catch (IOException ex) {
                        ex.getStackTrace();
                        System.out.println("Unable to carry out action");
                    }
                }
            }
        });

        JPanel userListPanel = new JPanel(new BorderLayout());
        userListPanel.setSize(new Dimension(600, 600));

        allFriends = new DefaultListModel<>();
        //we can dynamically add users here
        for (User friend : user.getFriends()) {
            allFriends.addElement(friend);
        }

        userList = new JList<>(allFriends);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setCellRenderer(new CustomCellRenderer());
        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int index = userList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        Rectangle bounds = userList.getCellBounds(index, index);
                        if (bounds != null && bounds.contains(e.getPoint()))
                        {
                            userList.setSelectedIndex(index);
                            showPopupMenu(e.getX(), e.getY(), userList, user);
                        }
                        else showPopupOptions(e.getX(), e.getY(), userList, user);
                    }
                    if (index == -1 && allFriends.isEmpty())
                    {
                        showPopupOptions(e.getX(), e.getY(), userList, user);
                    }
                }
            }


        });
        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setSize(600, 400);
        userListPanel.add(scrollPane, BorderLayout.CENTER);
        this.add(searchBar, BorderLayout.NORTH);
        this.add(userListPanel, BorderLayout.CENTER);
    }
}
