package clients;

import server.ServerThread;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class chatting extends JPanel {
    private JPanel chatArea;
    private JTextField chatInput;
    private JButton sendButton;
    private String id;
    private DefaultListModel<String> sideList;
    private JList jList;
    private JScrollPane jScrollPane;
    private ArrayList<String> chatContent = new ArrayList<>();
    ArrayList<User> members;
    DefaultListModel<User> Lmembers;
    public Application parent;
    public boolean isGroup;
    JButton information;
    User lastChoice = null;
    public User currentUser;

    static class CustomRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof User) {
                String status = ((User) value).isAdmin() ? "(admin)" : "(member)";
                setText(status + " " + " " + ((User) value).getId() + " " + ((User) value).getName());
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

    /**
     * Create the application.
     */
    public chatting(Application application) {
        parent = application;
        id = "";
        initialize("usr1", "usr2");
        //setUpSocket();
    }

    public void ClearChat() {
        sideList.clear();
        chatContent.clear();
    }

    public void AddChat(String newString) {
        chatContent.add(newString);
        sideList.addElement(newString);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize(String id1, String id2) {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setForeground(Color.WHITE);

        this.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        this.setBounds(100, 100, 360, 800);

        sideList = new DefaultListModel<>();
        Lmembers = new DefaultListModel<User>();

        chatArea = new JPanel();
        jList = new JList(sideList);
        jScrollPane = new JScrollPane(jList);
        chatArea.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        chatArea.setSize(new Dimension(360, 500));
        jScrollPane.setSize(new Dimension(360, 500));
        jScrollPane.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        JPanel container = new JPanel();
        JTextField searchBar = new JTextField();
        searchBar.setPreferredSize(new Dimension(200, 28));
        container.add(searchBar);
        jList.setSize(new Dimension(360, 500));
        this.add(chatArea, BorderLayout.CENTER);
        container.setLayout(new FlowLayout());
        information = new JButton("Information");

        container.add(information);
//        if(isGroup) {
//        	addMemberButton.setVisible(true);
//        }else {
//        	addMemberButton.setVisible(false);
//        }
        information.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openMemberList();
            }
        });

        this.add(container, BorderLayout.NORTH);
        chatArea.add(jScrollPane, BorderLayout.CENTER);
        chatArea.setLayout(new BorderLayout());
        Font font = new Font("Comic Sans MS", Font.BOLD, 14); // Font(name, style, size)
        chatArea.setFont(font);

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

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.setSize(360, 200);

        chatInput = new JTextField();
        chatInput.setPreferredSize(new Dimension(250, 30));
        inputPanel.add(chatInput);

        sendButton = new JButton("Send");
        sendButton.setAlignmentX(360);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = chatInput.getText();
                if (!parent.focusIDString.equals("") && !msg.equals("")) {
                    try {
                        if (!isGroup) {
                            String send = parent.currentUser.getId() + " - " + msg; //identify send format here
                            parent.write("DirectMessage|" + parent.currentUser.getId() + "|" + parent.focusIDString + "|" + send);
                            sideList.addElement("(" + parent.currentUser.name + ") " + chatInput.getText());
                        } else {
                            String send = parent.currentUser.getName() + " - " + msg;
                            parent.write("GroupChat|" + parent.focusIDString + "|" + send);
                        }
                        chatInput.setText("");
                    } catch (IOException ioe) {
                        System.out.println("IO Exception found");
                        ioe.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        });
        inputPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        inputPanel.add(sendButton, BorderLayout.CENTER);
        this.add(inputPanel, BorderLayout.SOUTH);
    }

    public void openMemberList() {
        JFrame frame = new JFrame("Group Info");

        JList<User> users = new JList<>(Lmembers);
        users.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        users.setCellRenderer(new CustomRenderer());
        JScrollPane scrollPane = new JScrollPane(users);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        JButton changeNameButton = new JButton("Change group name");
        JButton addMemberButton = new JButton("Add member");
        JPanel controllerJPanel = new JPanel();
        controllerJPanel.setLayout(new FlowLayout());

        controllerJPanel.add(changeNameButton);
        controllerJPanel.add(addMemberButton);

        frame.add(controllerJPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        users.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    User selected = users.getSelectedValue();
                    if (selected != null) {
                        currentUser = selected;
                    }
                }
            }
        });

        users.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int index = users.locationToIndex(e.getPoint());

                    if (index != -1 && parent.currentUser.isAdmin()) {
                        Rectangle bounds = users.getCellBounds(index, index);
                        if (bounds != null && bounds.contains(e.getPoint())) {
                            users.setSelectedIndex(index);
                            showPopupMenuDirect(e.getX(), e.getY(), users, currentUser);
                        }
                        else showPopupOptions(e.getX(), e.getY(), users, currentUser);
                    }
                    else showPopupOptions(e.getX(), e.getY(), users, currentUser);
                }
            }
        });


        changeNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newName = JOptionPane.showInputDialog("Update new name to group");
                try {
                    parent.write("ChangeGroupName|" + parent.focusIDString + "|" + parent.currentUser.getId() + "|" + newName);
                } catch (IOException ex) {
                    System.out.println("An error occurred");
                    ex.printStackTrace();
                }
            }
        });

        addMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newMember = JOptionPane.showInputDialog("Enter User name to add member");
                try {
                    parent.write("AddMemberToGroup|" + parent.focusIDString + "|" + newMember);
                } catch (IOException ex) {
                    System.out.println("An error occurred");
                    ex.printStackTrace();
                }
            }
        });

        frame.setSize(350, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private void showPopupMenuDirect(int x, int y, JList<User> list, User user) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem remove = new JMenuItem("Delete user");
        JMenuItem addAdmin = new JMenuItem("Assign admin privileges");
        JMenuItem deleteAdmin = new JMenuItem("Remove admin privileges");

        remove.addActionListener(e -> {
            Object selected = list.getSelectedValue();
            if (selected != null && selected instanceof User && !parent.focusIDString.equals("")) {
                User reportedUser = (User) selected;
                String uid = reportedUser.getId();
                for (int i = 0; i < Lmembers.size(); ++i) {
                    if (Lmembers.get(i).getId().equals(uid)) {
                        Lmembers.remove(i);
                        break;
                    }
                }
                String groupID = parent.focusIDString;
                try {
                    parent.write("RemoveMemberGroup|" + groupID + "|" + uid);

                } catch (IOException ex) {
                    System.out.println("Unable to write");
                    ex.printStackTrace();
                }
            }
        });

        addAdmin.addActionListener(e -> {
            Object selected = list.getSelectedValue();
            if (selected != null && selected instanceof User && !parent.focusIDString.equals("")) {
                User reportedUser = (User) selected;
                String uid = reportedUser.getId();
                for (int i = 0; i < Lmembers.size(); ++i) {
                    if (Lmembers.get(i).getId().equals(uid)) {
                        Lmembers.get(i).setAdmin(true);
                        break;
                    }
                }
                String groupID = parent.focusIDString;
                try {
                    parent.write("SetAdminGroup|" + groupID + "|" + uid);

                } catch (IOException ex) {
                    System.out.println("Unable to write");
                    ex.printStackTrace();
                }
            }
        });

        deleteAdmin.addActionListener(e -> {
            Object selected = list.getSelectedValue();
            if (selected != null && selected instanceof User && !parent.focusIDString.equals("")) {
                User reportedUser = (User) selected;
                String uid = reportedUser.getId();
                for (int i = 0; i < Lmembers.size(); ++i) {
                    if (Lmembers.get(i).getId().equals(uid)) {
                        Lmembers.get(i).setAdmin(false);
                        break;
                    }
                }
                String groupID = parent.focusIDString;
                try {
                    parent.write("RemoveAdminGroup|" + groupID + "|" + uid);

                } catch (IOException ex) {
                    System.out.println("Unable to write");
                    ex.printStackTrace();
                }
            }

        });

        popupMenu.add(remove);
        popupMenu.add(addAdmin);
        popupMenu.add(deleteAdmin);

        popupMenu.show(list, x, y);
    }


    public void filterModel(DefaultListModel<String> model, String filter) {
        if (!filter.trim().equals("")) {
            for (String s : chatContent) {
                {
                    if (!s.contains(filter)) {
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
        } else {
            model.clear();

            for (String s : chatContent) {
                model.addElement(s);
            }
        }
    }

    private void showPopupOptions(int x, int y, JList<User> list, User user) {
        JPopupMenu options = new JPopupMenu();
        JMenuItem refresh = new JMenuItem("Refresh");

        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Lmembers.clear();
                String groupID = parent.focusIDString;
                ClearChat();
                try {
                    parent.write("MessageData" + "|" + "group" + "|" + groupID);
                } catch (IOException ex) {
                    System.out.println("Unable to write");
                    ex.printStackTrace();
                }
            }
        });

        options.add(refresh);
        options.show(list, x, y);
    }
}
