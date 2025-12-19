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
    private static final Font BIGGER_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 18);

    static class CustomRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setFont(BIGGER_FONT);
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
                setForeground(Color.WHITE);
            }

            return renderer;
        }
    }

    private void showPopupMenuDirect(int x, int y, JList<Object> list, User user) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem spam = new JMenuItem("Report For Spam");
        JMenuItem blockUser = new JMenuItem("Block User");
        JMenuItem clearChatHistory = new JMenuItem("Clear Chat History");
        JMenuItem removeFriend = new JMenuItem("Unfriend User");

        spam.addActionListener(e -> {
            Object selected = list.getSelectedValue();
            if (selected != null && selected instanceof User) {
                User reportedUser = (User) selected;
                String reportedName = reportedUser.getName();
                String byUserName = user.getName();
                try {
                    parent.write("ReportSpam|" + reportedName + "|" + byUserName);
                    JOptionPane.showMessageDialog(this, "The user is successfully reported!");
                    parent.write("BlockAccount|" + user.getId() + "|" + reportedUser.getId());
                    sideList.removeElement(selected);
                } catch (IOException ex) {
                    System.out.println("Unable to write");
                    ex.printStackTrace();
                }
            }
        });
        
        blockUser.addActionListener(e -> {
            Object selected = list.getSelectedValue();
            if (selected != null && selected instanceof User) {
                User blockedUser = (User) selected;
                try {
                    parent.write("BlockAccount|" + user.getId() + "|" + blockedUser.getId());
                    sideList.removeElement(selected);
                    JOptionPane.showMessageDialog(this, "The user is blocked!");
                } catch (IOException ex) {
                    System.out.println("Unable to write");
                    ex.printStackTrace();
                }
            }
        });

        clearChatHistory.addActionListener(e -> {
            Object selected = list.getSelectedValue();
            if (selected != null && selected instanceof User) {
                User u = (User) selected;
                // You can perform an action here, e.g., based on the selected item

                int choice = JOptionPane.showConfirmDialog(this, "Would you like to clear all of the chat history? (You cannot undo after this)", "Clear Chat History?", JOptionPane.YES_NO_OPTION);
                //Deal with task in accordance to choice
                if (choice == JOptionPane.YES_OPTION) {
                    try {
                        parent.write("DeleteMessage|" + parent.currentUser.getId() + "|" + u.getId());
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(this, "Chat history cleared");
                }
            }
        });
        
        removeFriend.addActionListener(e -> {
            Object selected = list.getSelectedValue();
            if (selected != null && selected instanceof User) {
                User unfriendUser = (User) selected;
                try {
                    JOptionPane.showMessageDialog(this, "Unfriend successfully!");
                    parent.write("DeleteFriend|" + user.getId() + "|" + unfriendUser.getId());
                    sideList.removeElement(selected);
                } catch (IOException ex) {
                    System.out.println("Unable to write");
                    ex.printStackTrace();
                }
            }
        });

        popupMenu.add(spam);
        popupMenu.add(clearChatHistory);
        popupMenu.add(removeFriend);
        popupMenu.show(list, x, y);
    }

    private void showPopupOptions(int x, int y, JList<Object> list, User user) {
        JPopupMenu options = new JPopupMenu();
        JMenuItem refresh = new JMenuItem("Refresh");

        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sideList.clear();
                int i = 0;
                for (int j = 0; j < user.getOnlineList().size(); ++i, ++j) {
                    sideList.add(i, user.getOnlineList().get(j));
                }

                for (int j = 0; j < user.getGroupList().size(); ++i, ++j) {
                    sideList.add(i, user.getGroupList().get(j));
                }
            }
        });

        options.add(refresh);
        options.show(list, x, y);
    }

    private void SetPlaceholder(JTextField textField, String placeholder) {
        textField.setForeground(new Color(255, 255, 255));
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

    public onlineUsers(Application app, User user) {
        this.parent = app;
        this.setLayout(new BorderLayout());
        navigation = new JLabel("Welcome, " + user.getName());
        navigation.setForeground(new Color(255, 255, 255));
        navigation.setFont(new Font("Source Code Pro", Font.BOLD, 14));
        navigation.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Padding
        navigation.setOpaque(true);
        navigation.setBackground(new Color(128, 128, 128));

        searchBar = new JTextField();
        searchBar.setBackground(new Color(192, 192, 192));
        searchBar.setMargin(new Insets(15, 10, 15, 10));
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY), // Border color
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        searchBar.setSize(new Dimension(600, 200));
        SetPlaceholder(searchBar, "Chat With A Friend");

        searchBar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            private void filter() {
                String filter = searchBar.getText();
                filterModel(sideList, filter);
            }
        });

        JPanel usersAndgroupsPanel = new JPanel(new BorderLayout());

        sideList = new DefaultListModel<>();
        //we can dynamically add users/groups here
        int i = 0;
        for (int j = 0; j < user.getOnlineList().size(); ++i, ++j) {
            sideList.add(i, user.getOnlineList().get(j));
        }

        for (int j = 0; j < user.getGroupList().size(); ++i, ++j) {
            sideList.add(i, user.getGroupList().get(j));
        }

        usersAndgroups = new JList<>(sideList);
        usersAndgroups.setForeground(new Color(255, 255, 255));
        usersAndgroups.setBackground(new Color(128, 128, 128));
        usersAndgroups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersAndgroups.setCellRenderer(new CustomRenderer());
        usersAndgroups.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    Object selected = usersAndgroups.getSelectedValue();
                    if (selected == null) {
                        usersAndgroups.clearSelection();
                    } else {
                        if (selected.getClass().getSimpleName().equals("User")) {
                            User selectedUser = (User) selected;
                            String id = selectedUser.getId();
                            selectedUser.chatWithU = false;
                            parent.focusIDString = selectedUser.getId();
                            parent.focusNameString = selectedUser.fullname;
                            if (parent.mainPanel instanceof home) {
                                home h = (home) parent.mainPanel;
                                chatting userChat = (chatting) h.chatPanel;
                                userChat.ClearChat();
                            }
                            try {
                                parent.write("MessageData" + "|" + "user" + "|" + user.getId() + "|" + id);

                            } catch (IOException ex) {
                                System.out.println("Unable to write");
                                ex.printStackTrace();
                            }

                        } else if (selected.getClass().getSimpleName().equals("groupChat")) {
                            groupChat group = (groupChat) selected;
                            String id = group.getGroupID();
                            parent.focusIDString = id;
                            parent.focusNameString = group.getGroupName();
                            if (parent.mainPanel instanceof home) {
                                home h = (home) parent.mainPanel;
                                chatting userChat = (chatting) h.chatPanel;
                                userChat.ClearChat();
                            }
                            try {
                                parent.write("MessageData" + "|" + "group" + "|" + id);

                            } catch (IOException ex) {
                                System.out.println("Unable to write");
                                ex.printStackTrace();
                            }
                        }
                    }

                }
            }
        });

        usersAndgroups.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int index = usersAndgroups.locationToIndex(e.getPoint());
                    if (index != -1) {
                        Rectangle bounds = usersAndgroups.getCellBounds(index, index);
                        if (bounds != null && bounds.contains(e.getPoint())) {
                            usersAndgroups.setSelectedIndex(index);
                            if (!usersAndgroups.getModel().getElementAt(index).getClass().getSimpleName().equalsIgnoreCase("groupChat")) {
                                showPopupMenuDirect(e.getX(), e.getY(), usersAndgroups, user);
                            }
                        } else showPopupOptions(e.getX(), e.getY(), usersAndgroups, user);
                    }
                    if (index == -1 && sideList.isEmpty()) {
                        showPopupOptions(e.getX(), e.getY(), usersAndgroups, user);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(usersAndgroups);
        scrollPane.setSize(320, 400);
        scrollPane.setVerticalScrollBar(new JScrollBar());

        usersAndgroupsPanel.add(scrollPane, BorderLayout.CENTER);
        this.add(navigation, BorderLayout.NORTH);
        this.add(usersAndgroupsPanel, BorderLayout.CENTER);
        this.add(searchBar, BorderLayout.AFTER_LAST_LINE);
    }

    public void SetOffline(String id) {
        for (int i = 0; i < sideList.size(); ++i) {
            User user = (User) sideList.get(i);
            if (user.id.equals(id)) {
                sideList.remove(i);
                user.setOnline(false);
                sideList.add(i, user);
                break;
            }
        }
    }

    public void SetMessage(String id) {
        for (int i = 0; i < sideList.size(); ++i) {
            if (sideList.get(i) instanceof User) {
                User user = (User) sideList.get(i);
                if (user.id.equals(id)) {
                    ((User) sideList.get(i)).chatWithU = true;
                }
            }
        }
    }

    public void filterModel(DefaultListModel<Object> model, String filter) {
        if (!filter.trim().equals("") && !filter.equals("Chat With A Friend")) {
            for (Object element : parent.currentUser.friends) {
                {
                    if (element instanceof User) {
                        User s = (User) element;
                        if (!s.name.contains(filter)) {
                            if (model.contains(s)) {
                                model.removeElement(s);
                            }
                        } else {
                            if (!model.contains(s)) {
                                model.addElement(s);
                            }
                        }
                    }
                }

            }
            for (Object element : parent.currentUser.getGroupList()) {
                {
                    if (element instanceof groupChat) {
                        groupChat g = (groupChat) element;
                        if (!g.getGroupName().contains(filter)) {
                            if (model.contains(g)) {
                                model.removeElement(g);
                            }
                        } else {
                            if (!model.contains(g)) {
                                model.addElement(g);
                            }
                        }
                    }
                }
            }
        } else {
            model.clear();
            for (User element : parent.currentUser.friends) {
                model.addElement(element);

            }
            for (groupChat element : parent.currentUser.getGroupList()) {
                model.addElement(element);
            }
        }
    }

    public void SetOnline(String id) {
        for (int i = 0; i < sideList.size(); ++i) {
            User user = (User) sideList.get(i);
            if (user.id.equals(id)) {
                sideList.remove(i);
                user.setOnline(true);
                sideList.add(i, user);
                break;
            }
        }
    }

    public void ClearChat() {
        sideList.clear();
    }

    public void UpdateList(User user) {

        //we can dynamically add users/groups here
        int i = 0;
        for (int j = 0; j < user.friends.size(); ++i, ++j) {
            sideList.addElement(user.friends.get(j));
            System.out.println("call _ user onlinelist " + user.friends.size());
        }

        for (int j = 0; j < user.getGroupList().size(); ++i, ++j) {
            sideList.add(i, user.getGroupList().get(j));
        }

        System.out.println("end user list");
    }

    public void UpdateNewMessageList(String id) {

        for (int i = 0; i < sideList.size(); ++i) {
            if (sideList.get(i) instanceof User) {
                if (((User) sideList.get(i)).id.equals(id))
                    ((User) sideList.get(i)).chatWithU = true;
            }
        }

    }
}
