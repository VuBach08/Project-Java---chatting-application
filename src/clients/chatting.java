package clients;


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
import java.awt.SystemColor;


public class chatting extends JPanel {
    private JPanel chatArea;
    private JTextField chatInput;
    private JButton sendButton;
    private DefaultListModel<String> sideList;
    private JList jList;
    private JScrollPane jScrollPane;
    private ArrayList<String> chatContent = new ArrayList<>();

    public Application parent;
    public boolean isGroup;
    JButton information;

    static class CustomRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            return renderer;
        }
    }
    public chatting() {
        initialize("usr1", "usr2");
    }
    /**
     * Create the application.
     */
    public chatting(Application application) {
        parent = application;
        initialize("usr1", "usr2");
    }

    public void ClearChat() {
        
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
        this.setBackground(Color.BLUE);
        this.setForeground(Color.BLUE);

        this.setFont(new Font("Source Code Pro", Font.PLAIN, 14));
        this.setBounds(100, 100, 360, 800);

        sideList = new DefaultListModel<>();

        chatArea = new JPanel();
        jList = new JList(sideList);
        jList.setForeground(new Color(255, 255, 255));
        jList.setBackground(new Color(0, 0, 0));
        jScrollPane = new JScrollPane(jList);
        chatArea.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        chatArea.setSize(new Dimension(360, 500));
        jScrollPane.setSize(new Dimension(360, 500));
        jScrollPane.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        JPanel container = new JPanel();
        container.setBackground(new Color(0, 0, 0));
        JTextField searchBar = new JTextField();
        searchBar.setBackground(new Color(128, 128, 128));
        searchBar.setPreferredSize(new Dimension(200, 28));
        container.add(searchBar);
        jList.setSize(new Dimension(360, 500));
        this.add(chatArea, BorderLayout.CENTER);
        container.setLayout(new FlowLayout());
        information = new JButton("Information");
        information.setForeground(Color.WHITE);                     // white text
        information.setBackground(new Color(0, 102, 204));          // nice solid blue (or use Color.BLUE.darker())
        information.setOpaque(true);                                // must have
        information.setContentAreaFilled(true);                     // must have
        information.setBorderPainted(false);                        // removes the ugly border/gradient that hides text
        information.setFocusPainted(false);                         // removes dotted focus rectangle
        information.setFont(new Font("Comic Sans MS", Font.PLAIN, 10));
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
        Font font = new Font("Arial", Font.BOLD, 14); // Font(name, style, size)
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
                
            }
        });
        inputPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        inputPanel.add(sendButton, BorderLayout.CENTER);
        this.add(inputPanel, BorderLayout.SOUTH);
    }

    public void openMemberList() {
        
    }

    private void showPopupMenuDirect() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem remove = new JMenuItem("Delete user");
        JMenuItem addAdmin = new JMenuItem("Assign admin privileges");
        JMenuItem deleteAdmin = new JMenuItem("Remove admin privileges");

        remove.addActionListener(e -> {
            
        });

        addAdmin.addActionListener(e -> {
            
        });

        deleteAdmin.addActionListener(e -> {
            

        });

        popupMenu.add(remove);
        popupMenu.add(addAdmin);
        popupMenu.add(deleteAdmin);

    }


    public void filterModel(DefaultListModel<String> model, String filter) {
       
    }

    private void showPopupOptions(int x, int y) {
       
    }
}
