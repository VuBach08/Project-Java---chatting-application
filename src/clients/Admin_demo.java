package clients;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class Admin_demo extends JPanel{
    private JTabbedPane allTab;
    private JTextField inputSearch;
    private JPanel listUser;
    private GridBagConstraints gbcListUser;
    private JCheckBox checkBoxUsernamep1;
    private JCheckBox checkBoxSortNamep1;
    private JCheckBox checkBoxSortCreatep1;
    private JRadioButton btnAllStatusp1;
    private JRadioButton btnOnlinep1;
    private JRadioButton btnOfflinep1;
    private JButton btnFindp1;
    private JTextField unAddtf;
    private JTextField fnAddtf;
    private JTextField addrAddtf;
    private JTextField dobAddtf;
    private JTextField genderAddtf;
    private JTextField emailAddtf;
    private JTextField unUpdatetf;
    private JTextField fnUpdatetf;
    private JTextField addrUpdatetf;
    private JTextField emailUpdatetf;
    private JTextField unDeltf;
    private JTextField inputLock;
    private JTextField inputLabelUsername;
    private JTextField inputLabelNewPass;
    private JTextField inputLabelRePass;
    private JPanel listLoginHistory;
    private GridBagConstraints gbcListHist;
    private JTextField inputUnameHist;
    private JPanel listFriend;
    private GridBagConstraints gbcListFriend;
    private JTextField inputUnameFriend;
    private JPanel listLogin;
    private GridBagConstraints gbcListLogin;
    private JPanel listGroup;
    private GridBagConstraints gbcListGroup;
    private JTextField inputGSearch;
    private JRadioButton btnSortName;
    private JRadioButton btnSortDateCreate;
    private JPanel listMember;
    private GridBagConstraints gbcListMember;
    private JTextField inputMemGroupSearch;
    private JPanel listAdmin;
    private GridBagConstraints gbcListAdmin;
    private JTextField inputAdminSearch;
    private JPanel listSpam;
    private GridBagConstraints gbcListSpam;
    private JRadioButton btnSortNamet4;
    private JRadioButton btnSortDatet4;
    private JRadioButton btnFilterName;
    private JRadioButton btnFilterDate;
    private JTextField inputSpamSearch;
    private JTextField inputLockt4;
    private JPanel listNew;
    private GridBagConstraints gbcListNew;
    private JRadioButton btnSortNamet5;
    private JRadioButton btnSortDatet5;
    private JTextField inputNewSearch;
    private JTextField inputFromDate;
    private JTextField inputToDate;
    private JPanel listFriendPlus;
    private GridBagConstraints gbcListFriendPlus;
    private JRadioButton btnSortNamet7;
    private JRadioButton btnSortDatet7;
    private JTextField inputNameSearch;
    private JTextField inputDir_fr;
    private JPanel listOpen;
    private GridBagConstraints gbcListOpen;
    private JRadioButton btnSortNamet8;
    private JRadioButton btnSortDatet8;
    private JTextField inputNameSearcht8;
    private JTextField inputDir_open;
    private JTextField inputYearT6;
    private JTextField inputFromDatet8;
    private JTextField inputToDatet8;
    private ChartPanel chartPanel;
    private ChartPanel chartPanel1;
    private JTextField inputYearT9;
    public Application parent;
    /**
     * Launch the application.
     */
//    public static void main(String[] args) {
//        Admin_demo window = new Admin_demo();
//        window.frame.setVisible(true);
//        window.setUpSocket();
//    }

    /**
     * Create the application.
     */
    public Admin_demo(Application app) {
        this.parent = app;
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

        JPanel defaultPanel = new JPanel();
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel();
        JPanel panel5 = new JPanel();
        JPanel panel6 = new JPanel();
        JPanel panel7 = new JPanel();
        JPanel panel8 = new JPanel();
        JPanel panel9 = new JPanel();


        allTab = new JTabbedPane();
        allTab.addTab("Trang chủ", defaultPanel);
        allTab.addTab("Chức năng 1", panel1); // dua vao de bai de ghi
        allTab.addTab("Chức năng 2", panel2);
        allTab.addTab("Chức năng 3", panel3);
        allTab.addTab("Chức năng 4", panel4);
        allTab.addTab("Chức năng 5", panel5);
        allTab.addTab("Chức năng 6", panel6);
        allTab.addTab("Chức năng 7", panel7);
        allTab.addTab("Chức năng 8", panel8);
        allTab.addTab("Chức năng 9", panel9);

        defaultPanel.add(this.trangChu());
        panel1.add(this.trang1());
        panel2.add(this.trang2());
        panel3.add(this.trang3());
        panel4.add(this.trang4());
        panel5.add(this.trang5());
        panel6.add(this.trang6());
        panel7.add(this.trang7());
        panel8.add(this.trang8());
        panel9.add(this.trang9());

        this.add(allTab);
        allTab.setSelectedIndex(0);
    }

    private JPanel trangChu() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        ArrayList<JButton> listButton = this.getButton();
        ArrayList<JPanel> listFunction = new ArrayList<>();
        listFunction.add(this.getFunction1());
        listFunction.add(this.getFunction2());
        listFunction.add(this.getFunction3());
        listFunction.add(this.getFunction4());
        listFunction.add(this.getFunction5());
        listFunction.add(this.getFunction6());
        listFunction.add(this.getFunction7());
        listFunction.add(this.getFunction8());
        listFunction.add(this.getFunction9());

        gbc.insets = new Insets(0, 2, 2, 2);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 0;
        gbc.gridy = 0;

        for (int i = 0; i < listButton.size(); i++) {
            mainPanel.add(listButton.get(i), gbc);

            gbc.gridx = 1;

            mainPanel.add(listFunction.get(i), gbc);

            gbc.gridy += 1;
            gbc.gridx = 0;
        }

        return mainPanel;
    }

    private void updateListUser(ArrayList<String> listUserInString, int checkEnd) {
        if (gbcListUser.gridy == 0) {
            int compCount = listUser.getComponentCount();
            if (compCount > 6) {
                for (int i = compCount - 1; i >= 6; i--) {
                    listUser.remove(i);
                }
            }

            gbcListUser.gridy += 1;
            gbcListUser.gridx = 0;
            for (String string : listUserInString) {
                if (string.equals("no data")) {
                    break;
                }
                JLabel label = new JLabel(string);

                listUser.add(label, gbcListUser);

                gbcListUser.gridx += 1;
            }

            listUser.revalidate();
            listUser.repaint();
        } else {
            gbcListUser.gridy += 1;
            gbcListUser.gridx = 0;
            for (String string : listUserInString) {
                JLabel label = new JLabel(string);

                listUser.add(label, gbcListUser);

                gbcListUser.gridx += 1;
            }
            listUser.revalidate();
        }

        if (checkEnd == 1) {
            gbcListUser.gridy = 0;
        }
    }
    private void updateListLoginHist(ArrayList<String> listLoginHistInString, int checkEnd) {
        if (gbcListHist.gridy == 0) {
            int compCount = listLoginHistory.getComponentCount();
            if (compCount > 2) {
                for (int i = compCount - 1; i >= 2; i--) {
                    listLoginHistory.remove(i);
                }
            }

            gbcListHist.gridy += 1;
            gbcListHist.gridx = 0;
            for (String string : listLoginHistInString) {
                if (string.equals("no data")) {
                    break;
                }
                JLabel label = new JLabel(string);

                listLoginHistory.add(label, gbcListHist);

                gbcListHist.gridx += 1;
            }

            listLoginHistory.revalidate();
            listLoginHistory.repaint();
        } else {
            gbcListHist.gridy += 1;
            gbcListHist.gridx = 0;
            for (String string : listLoginHistInString) {
                JLabel label = new JLabel(string);

                listLoginHistory.add(label, gbcListHist);

                gbcListHist.gridx += 1;
            }
            listLoginHistory.revalidate();
        }

        if (checkEnd == 1) {
            gbcListHist.gridy = 0;
        }
    }
    private void updateListFriend(ArrayList<String> listFriendInString, int checkEnd) {
        if (gbcListFriend.gridy == 0) {
            int compCount = listFriend.getComponentCount();
            if (compCount > 2) {
                for (int i = compCount - 1; i >= 2; i--) {
                    listFriend.remove(i);
                }
            }

            listFriend.revalidate();
            listFriend.repaint();

            gbcListFriend.gridy += 1;
            gbcListFriend.gridx = 0;
            for (String string : listFriendInString) {
                if (string.equals("no data")) {
                    break;
                }
                JLabel label = new JLabel(string);

                listFriend.add(label, gbcListFriend);

                gbcListFriend.gridx += 1;
            }

            listFriend.revalidate();
            listFriend.repaint();
        } else {
            gbcListFriend.gridy += 1;
            gbcListFriend.gridx = 0;
            for (String string : listFriendInString) {
                JLabel label = new JLabel(string);

                listFriend.add(label, gbcListFriend);

                gbcListFriend.gridx += 1;
            }
            listFriend.revalidate();
        }

        if (checkEnd == 1) {
            gbcListFriend.gridy = 0;
        }
    }
    private void updateListLogin(ArrayList<String> listLoginInString, int checkEnd) {
        if (gbcListLogin.gridy == 0) {
            int compCount = listLogin.getComponentCount();
            if (compCount > 3) {
                for (int i = compCount - 1; i >= 3; i--) {
                    listLogin.remove(i);
                }
            }

            gbcListLogin.gridy += 1;
            gbcListLogin.gridx = 0;
            for (String string : listLoginInString) {
                if (string.equals("no data")) {
                    break;
                }
                JLabel label = new JLabel(string);

                listLogin.add(label, gbcListLogin);

                gbcListLogin.gridx += 1;
            }

            listLogin.revalidate();
            listLogin.repaint();
        } else {
            gbcListLogin.gridy += 1;
            gbcListLogin.gridx = 0;
            for (String string : listLoginInString) {
                JLabel label = new JLabel(string);

                listLogin.add(label, gbcListLogin);

                gbcListLogin.gridx += 1;
            }
            listLogin.revalidate();
        }

        if (checkEnd == 1) {
            gbcListLogin.gridy = 0;
        }
    }
    private void updateListGroup(ArrayList<String> listGroupInString, int checkEnd) {
        if (gbcListGroup.gridy == 0) {
            int compCount = listGroup.getComponentCount();
            if (compCount > 4) {
                for (int i = compCount - 1; i >= 4; i--) {
                    listGroup.remove(i);
                }
            }

            gbcListGroup.gridy += 1;
            gbcListGroup.gridx = 0;
            for (String string : listGroupInString) {
                if (string.equals("no data")) {
                    break;
                }
                JLabel label = new JLabel(string);

                listGroup.add(label, gbcListGroup);

                gbcListGroup.gridx += 1;
            }

            listGroup.revalidate();
            listGroup.repaint();
        } else {
            gbcListGroup.gridy += 1;
            gbcListGroup.gridx = 0;
            for (String string : listGroupInString) {
                JLabel label = new JLabel(string);

                listGroup.add(label, gbcListGroup);

                gbcListGroup.gridx += 1;
            }
            listGroup.revalidate();
        }

        if (checkEnd == 1) {
            gbcListGroup.gridy = 0;
        }
    }
    private void updateListMemGroup(ArrayList<String> listMemInString, int checkEnd) {
        if (gbcListMember.gridy == 0) {
            int compCount = listMember.getComponentCount();
            if (compCount > 2) {
                for (int i = compCount - 1; i >= 2; i--) {
                    listMember.remove(i);
                }
            }

            gbcListMember.gridy += 1;
            gbcListMember.gridx = 0;
            for (String string : listMemInString) {
                if (string.equals("no data")) {
                    break;
                }
                JLabel label = new JLabel(string);

                listMember.add(label, gbcListMember);

                gbcListMember.gridx += 1;
            }

            listMember.revalidate();
            listMember.repaint();
        } else {
            gbcListMember.gridy += 1;
            gbcListMember.gridx = 0;
            for (String string : listMemInString) {
                JLabel label = new JLabel(string);

                listMember.add(label, gbcListMember);

                gbcListMember.gridx += 1;
            }
            listMember.revalidate();
        }

        if (checkEnd == 1) {
            gbcListMember.gridy = 0;
        }
    }
    private void updateListAdmin(ArrayList<String> listAdminInString, int checkEnd) {
        if (gbcListAdmin.gridy == 0) {
            int compCount = listAdmin.getComponentCount();
            if (compCount > 2) {
                for (int i = compCount - 1; i >= 2; i--) {
                    listAdmin.remove(i);
                }
            }

            gbcListAdmin.gridy += 1;
            gbcListAdmin.gridx = 0;
            for (String string : listAdminInString) {
                if (string.equals("no data")) {
                    break;
                }
                JLabel label = new JLabel(string);

                listAdmin.add(label, gbcListAdmin);

                gbcListAdmin.gridx += 1;
            }

            listAdmin.revalidate();
            listAdmin.repaint();
        } else {
            gbcListAdmin.gridy += 1;
            gbcListAdmin.gridx = 0;
            for (String string : listAdminInString) {
                JLabel label = new JLabel(string);

                listAdmin.add(label, gbcListAdmin);

                gbcListAdmin.gridx += 1;
            }
            listAdmin.revalidate();
        }

        if (checkEnd == 1) {
            gbcListAdmin.gridy = 0;
        }
    }
    private void updateListSpam(ArrayList<String> listSpamInString, int checkEnd) {
        if (gbcListSpam.gridy == 0) {
            int compCount = listSpam.getComponentCount();
            if (compCount > 3) {
                for (int i = compCount - 1; i >= 3; i--) {
                    listSpam.remove(i);
                }
            }

            gbcListSpam.gridy += 1;
            gbcListSpam.gridx = 0;
            for (String string : listSpamInString) {
                if (string.equals("no data")) {
                    break;
                }
                JLabel label = new JLabel(string);

                listSpam.add(label, gbcListSpam);

                gbcListSpam.gridx += 1;
            }

            listSpam.revalidate();
            listSpam.repaint();
        } else {
            gbcListSpam.gridy += 1;
            gbcListSpam.gridx = 0;
            for (String string : listSpamInString) {
                JLabel label = new JLabel(string);

                listSpam.add(label, gbcListSpam);

                gbcListSpam.gridx += 1;
            }
            listSpam.revalidate();
        }

        if (checkEnd == 1) {
            gbcListSpam.gridy = 0;
        }
    }
    private void updateListNew(ArrayList<String> listNewInString, int checkEnd) {
        if (gbcListNew.gridy == 0) {
            int compCount = listNew.getComponentCount();
            if (compCount > 7) {
                for (int i = compCount - 1; i >= 7; i--) {
                    listNew.remove(i);
                }
            }

            gbcListNew.gridy += 1;
            gbcListNew.gridx = 0;
            for (String string : listNewInString) {
                if (string.equals("no data")) {
                    break;
                }
                JLabel label = new JLabel(string);

                listNew.add(label, gbcListNew);

                gbcListNew.gridx += 1;
            }

            listNew.revalidate();
            listNew.repaint();
        } else {
            gbcListNew.gridy += 1;
            gbcListNew.gridx = 0;
            for (String string : listNewInString) {
                JLabel label = new JLabel(string);

                listNew.add(label, gbcListNew);

                gbcListNew.gridx += 1;
            }
            listNew.revalidate();
        }

        if (checkEnd == 1) {
            gbcListNew.gridy = 0;
        }
    }
    private void updateChartNew(ArrayList<String> ChartValue, String year) {
        JFreeChart chart = this.createChart(this.createDataset(ChartValue), year);
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setRange(0, 100);
        chartPanel.setChart(null);
        chartPanel.revalidate();
        chartPanel.setChart(chart);
    }
    private void updateListFriendPlus(ArrayList<String> listFriendPlusInString, int checkEnd) {
        if (gbcListFriendPlus.gridy == 0) {
            int compCount = listFriendPlus.getComponentCount();
            if (compCount > 3) {
                for (int i = compCount - 1; i >= 3; i--) {
                    listFriendPlus.remove(i);
                }
            }

            gbcListFriendPlus.gridy += 1;
            gbcListFriendPlus.gridx = 0;
            for (String string : listFriendPlusInString) {
                if (string.equals("no data")) {
                    break;
                }
                JLabel label = new JLabel(string);

                listFriendPlus.add(label, gbcListFriendPlus);

                gbcListFriendPlus.gridx += 1;
            }

            listFriendPlus.revalidate();
            listFriendPlus.repaint();
        } else {
            gbcListFriendPlus.gridy += 1;
            gbcListFriendPlus.gridx = 0;
            for (String string : listFriendPlusInString) {
                JLabel label = new JLabel(string);

                listFriendPlus.add(label, gbcListFriendPlus);

                gbcListFriendPlus.gridx += 1;
            }
            listFriendPlus.revalidate();
        }

        if (checkEnd == 1) {
            gbcListFriendPlus.gridy = 0;
        }
    }
    private void updateListOpen(ArrayList<String> listOpenInString, int checkEnd) {
        if (gbcListOpen.gridy == 0) {
            int compCount = listOpen.getComponentCount();
            if (compCount > 4) {
                for (int i = compCount - 1; i >= 4; i--) {
                    listOpen.remove(i);
                }
            }

            gbcListOpen.gridy += 1;
            gbcListOpen.gridx = 0;
            for (String string : listOpenInString) {
                if (string.equals("no data")) {
                    break;
                }
                JLabel label = new JLabel(string);

                listOpen.add(label, gbcListOpen);

                gbcListOpen.gridx += 1;
            }

            listOpen.revalidate();
            listOpen.repaint();
        } else {
            gbcListOpen.gridy += 1;
            gbcListOpen.gridx = 0;
            for (String string : listOpenInString) {
                JLabel label = new JLabel(string);

                listOpen.add(label, gbcListOpen);

                gbcListOpen.gridx += 1;
            }
            listOpen.revalidate();
        }

        if (checkEnd == 1) {
            gbcListOpen.gridy = 0;
        }
    }
    private void updateChartOpen(ArrayList<String> ChartValue, String year) {
        JFreeChart chart = this.createChart1(this.createDataset1(ChartValue), year);
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setRange(0, 100);
        chartPanel1.setChart(null);
        chartPanel1.revalidate();
        chartPanel1.setChart(chart);
    }

    private JScrollPane trang1() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.insets = new Insets(0, 0, 2, 0);

        // chức năng 1a
        listUser = new JPanel();
        listUser.setSize(800, 800);
        listUser.setLayout(new GridBagLayout());
        gbcListUser = new GridBagConstraints();
        gbcListUser.insets = new Insets(0, 2, 5, 10);

        JLabel uname = new JLabel("Tên đăng nhập");
        JLabel fname = new JLabel("Họ tên");
        JLabel addr = new JLabel("Địa chỉ");
        JLabel dob = new JLabel("Ngày sinh");
        JLabel gender = new JLabel("Giới tính");
        JLabel email = new JLabel("Email");

        // set the style for the label
        setLabel(uname);
        setLabel(fname);
        setLabel(addr);
        setLabel(dob);
        setLabel(gender);
        setLabel(email);

        // set the position and add to the list user panel
        gbcListUser.gridx = 0;
        gbcListUser.gridy = 0;
        listUser.add(uname, gbcListUser);

        gbcListUser.gridx = 1;
        gbcListUser.gridy = 0;
        listUser.add(fname, gbcListUser);

        gbcListUser.gridx = 2;
        gbcListUser.gridy = 0;
        listUser.add(addr, gbcListUser);

        gbcListUser.gridx = 3;
        gbcListUser.gridy = 0;
        listUser.add(dob, gbcListUser);

        gbcListUser.gridx = 4;
        gbcListUser.gridy = 0;
        listUser.add(gender, gbcListUser);

        gbcListUser.gridx = 5;
        gbcListUser.gridy = 0;
        listUser.add(email, gbcListUser);

        // set the style for the list user panel
        listUser.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // add the list user panel to the scrollpane & set up the function for the scrollpane
        JScrollPane myScroll = new JScrollPane(listUser);
        myScroll.setPreferredSize(new Dimension(1000, 300));
        myScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        myScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        myScroll.getVerticalScrollBar().setUnitIncrement(20);

        // add the scrollpane of list user to the main panel
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.gridwidth = 3;
        mainPanel.add(myScroll, gbcMain);

        gbcMain.gridwidth = 1;

        // declare components to filter the list user
        inputSearch = new JTextField();
        checkBoxUsernamep1 = new JCheckBox("Tên đăng nhập");
        checkBoxSortNamep1 = new JCheckBox("Sắp xếp theo tên");
        checkBoxSortCreatep1 = new JCheckBox("Sắp xếp theo ngày tạo");
        btnAllStatusp1 = new JRadioButton("Trực tuyến & ngoại tuyến");
        btnOnlinep1 = new JRadioButton("Trực tuyến");
        btnOfflinep1 = new JRadioButton("Ngoại tuyến");
        btnFindp1 = new JButton("Tìm kiếm");

        setTextfield(inputSearch);

        // set the function for the button "find"

        // put 3 buttons in a group
        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(btnAllStatusp1);
        btnGroup.add(btnOnlinep1);
        btnGroup.add(btnOfflinep1);

        // add the input textfield to the main panel
        gbcMain.gridx = 0;
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 1;
        gbcMain.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(inputSearch, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(checkBoxUsernamep1, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btnAllStatusp1, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btnOnlinep1, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btnOfflinep1, gbcMain);

        // add a separator to panel to separate checkbox, radio button
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 1;
        gbcMain.insets = new Insets(3, 0, 3, 0);
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(sep, gbcMain);
        gbcMain.fill = GridBagConstraints.NONE;
        gbcMain.insets = new Insets(0, 0, 2, 0);

        // add checkbox to the panel
        gbcMain.gridy += 1;
        mainPanel.add(checkBoxSortNamep1, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(checkBoxSortCreatep1, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btnFindp1, gbcMain);

        // add a separator to separate the function
        JSeparator sep1 = new JSeparator(SwingConstants.HORIZONTAL);
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 3;
        gbcMain.insets = new Insets(3, 0, 3, 0);
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(sep1, gbcMain);
        gbcMain.fill = GridBagConstraints.NONE;
        gbcMain.insets = new Insets(0, 0, 2, 0);

        // chức năng 1b

        // panel used to add an user
        JPanel userAdd = new JPanel();
        userAdd.setLayout(new GridBagLayout());
        GridBagConstraints gbcUserAdd = new GridBagConstraints();
        gbcUserAdd.insets = new Insets(0, 2, 4, 2);
        gbcUserAdd.anchor = GridBagConstraints.LINE_START;

        // panel used to update an user
        JPanel userUpdate = new JPanel();
        userUpdate.setLayout(new GridBagLayout());
        GridBagConstraints gbcUserUpdate = new GridBagConstraints();
        gbcUserUpdate.insets = new Insets(0, 2, 4, 2);
        gbcUserUpdate.anchor = GridBagConstraints.LINE_START;

        // panel used to delete an user
        JPanel userDel = new JPanel();
        userDel.setLayout(new GridBagLayout());
        GridBagConstraints gbcUserDel = new GridBagConstraints();
        gbcUserDel.insets = new Insets(0, 100, 4, 2);
        gbcUserDel.anchor = GridBagConstraints.LINE_START;

        // label cho tất cả 3 chức năng
        JLabel unAdd = new JLabel("Tên đăng nhập");
        JLabel fnAdd = new JLabel("Họ & tên");
        JLabel addrAdd = new JLabel("Địa chỉ");
        JLabel dobAdd = new JLabel("Ngày sinh");
        JLabel genderAdd = new JLabel("Giới tính");
        JLabel emailAdd = new JLabel("Email");

        JLabel unUpdate = new JLabel("Tên đăng nhập mới");
        JLabel fnUpdate = new JLabel("Họ & tên");
        JLabel addrUpdate = new JLabel("Địa chỉ");
        JLabel emailUpdate = new JLabel("Email hiện tại");

        JLabel unDel = new JLabel("Tên đăng nhập");

        // set the textfield for 3 function add, update and delete user
        unAddtf = new JTextField(20);
        fnAddtf = new JTextField(20);
        addrAddtf = new JTextField(20);
        dobAddtf = new JTextField(10);
        genderAddtf = new JTextField(6);
        emailAddtf = new JTextField(20);

        unUpdatetf = new JTextField(20);
        fnUpdatetf = new JTextField(20);
        addrUpdatetf = new JTextField(20);
        emailUpdatetf = new JTextField(20);

        unDeltf = new JTextField(20);

        // declare button to execute those function
        JButton toAdd = new JButton("Thêm người dùng");
        JButton toUpdate = new JButton("Cập nhật người dùng");
        JButton toDel = new JButton("Xóa người dùng");

        // set the function to add an user

        // set the function to update an user

        // set the function to delete an user

        // thêm người dùng
        gbcUserAdd.gridx = 0;
        gbcUserAdd.gridy = 0;
        userAdd.add(unAdd, gbcUserAdd);
        gbcUserAdd.gridy += 1;
        userAdd.add(unAddtf, gbcUserAdd);

        gbcUserAdd.gridy += 1;
        userAdd.add(fnAdd, gbcUserAdd);
        gbcUserAdd.gridy += 1;
        userAdd.add(fnAddtf, gbcUserAdd);

        gbcUserAdd.gridy += 1;
        userAdd.add(addrAdd, gbcUserAdd);
        gbcUserAdd.gridy += 1;
        userAdd.add(addrAddtf, gbcUserAdd);

        gbcUserAdd.gridy += 1;
        userAdd.add(dobAdd, gbcUserAdd);
        gbcUserAdd.gridy += 1;
        userAdd.add(dobAddtf, gbcUserAdd);

        gbcUserAdd.gridy += 1;
        userAdd.add(genderAdd, gbcUserAdd);
        gbcUserAdd.gridy += 1;
        userAdd.add(genderAddtf, gbcUserAdd);

        gbcUserAdd.gridy += 1;
        userAdd.add(emailAdd, gbcUserAdd);
        gbcUserAdd.gridy += 1;
        userAdd.add(emailAddtf, gbcUserAdd);

        gbcUserAdd.gridy += 1;
        userAdd.add(toAdd, gbcUserAdd);

        // cập nhật người dùng
        gbcUserUpdate.gridx = 0;
        gbcUserUpdate.gridy = 0;
        userUpdate.add(unUpdate, gbcUserUpdate);
        gbcUserUpdate.gridy += 1;
        userUpdate.add(unUpdatetf, gbcUserUpdate);

        gbcUserUpdate.gridy += 1;
        userUpdate.add(fnUpdate, gbcUserUpdate);
        gbcUserUpdate.gridy += 1;
        userUpdate.add(fnUpdatetf, gbcUserUpdate);

        gbcUserUpdate.gridy += 1;
        userUpdate.add(addrUpdate, gbcUserUpdate);
        gbcUserUpdate.gridy += 1;
        userUpdate.add(addrUpdatetf, gbcUserUpdate);

        gbcUserUpdate.gridy += 1;
        userUpdate.add(emailUpdate, gbcUserUpdate);
        gbcUserUpdate.gridy += 1;
        userUpdate.add(emailUpdatetf, gbcUserUpdate);

        gbcUserUpdate.gridy += 1;
        userUpdate.add(toUpdate, gbcUserUpdate);

        // xóa người dùng
        gbcUserDel.gridx = 0;
        gbcUserDel.gridy = 0;
        userDel.add(unDel, gbcUserDel);
        gbcUserDel.gridy += 1;
        userDel.add(unDeltf, gbcUserDel);

        gbcUserDel.gridy += 1;
        userDel.add(toDel, gbcUserDel);

        // add 3 panels to the main panel
        gbcMain.gridx = 0;
        gbcMain.gridy += 1;
        mainPanel.add(userAdd, gbcMain);

        gbcMain.gridx = 1;
        mainPanel.add(userUpdate, gbcMain);

        gbcMain.gridx = 2;
        mainPanel.add(userDel, gbcMain);

        gbcMain.gridx = 0;

        JSeparator sep2 = new JSeparator(SwingConstants.HORIZONTAL);
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 3;
        gbcMain.insets = new Insets(3, 0, 3, 0);
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(sep2, gbcMain);
        gbcMain.fill = GridBagConstraints.NONE;
        gbcMain.insets = new Insets(0, 0, 2, 0);

        // chức năng 1c
        JPanel accountLock = new JPanel();
        JLabel label = new JLabel("Tên đăng nhập");
        inputLock = new JTextField(20);
        JButton lock = new JButton("Khóa tài khoản");
        JButton unlock = new JButton("Mở khóa tài khoản");
        // set the event for the button

        // add the components to the panel
        accountLock.add(label);
        accountLock.add(inputLock);
        accountLock.add(lock);
        accountLock.add(unlock);

        // add the panel to the main panel
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 2;
        mainPanel.add(accountLock, gbcMain);

        JSeparator sep3 = new JSeparator(SwingConstants.HORIZONTAL);
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 3;
        gbcMain.insets = new Insets(3, 0, 3, 0);
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(sep3, gbcMain);
        gbcMain.fill = GridBagConstraints.NONE;
        gbcMain.insets = new Insets(0, 0, 2, 0);

        // chức năng 1d
        JLabel label1 = new JLabel("Tên đăng nhập");
        JLabel label2 = new JLabel("Cập nhật mật khẩu mới");
        JLabel label3 = new JLabel("Nhập lại mật khẩu");
        inputLabelUsername = new JTextField(18);
        inputLabelNewPass = new JTextField(18);
        inputLabelRePass = new JTextField(18);
        JButton btnUpdatePass = new JButton("Cập nhật");

        // set the event for the button

        JPanel tempPanel1 = new JPanel();
        JPanel tempPanel2 = new JPanel();
        JPanel tempPanel3 = new JPanel();

        tempPanel1.add(label1);
        tempPanel1.add(inputLabelUsername);
        tempPanel2.add(label2);
        tempPanel2.add(inputLabelNewPass);
        tempPanel3.add(label3);
        tempPanel3.add(inputLabelRePass);

        gbcMain.gridy += 1;
        gbcMain.gridwidth = 1;
        gbcMain.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(tempPanel1, gbcMain);
        gbcMain.gridy += 1;
        mainPanel.add(tempPanel2, gbcMain);
        gbcMain.gridy += 1;
        mainPanel.add(tempPanel3, gbcMain);
        gbcMain.gridy += 1;
        mainPanel.add(btnUpdatePass, gbcMain);

        JSeparator sep4 = new JSeparator(SwingConstants.HORIZONTAL);
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 3;
        gbcMain.insets = new Insets(3, 0, 3, 0);
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(sep4, gbcMain);
        gbcMain.fill = GridBagConstraints.NONE;
        gbcMain.insets = new Insets(0, 0, 2, 0);

        //chức năng 1e
        listLoginHistory = new JPanel();
        listLoginHistory.setSize(300, 800);
        listLoginHistory.setLayout(new GridBagLayout());
        gbcListHist = new GridBagConstraints();
        gbcListHist.insets = new Insets(0, 2, 5, 10);

        JLabel unameLoginHist = new JLabel("Tên đăng nhập");
        JLabel loginDate = new JLabel("Thời gian đăng nhập");

        setLabel(unameLoginHist);
        setLabel(loginDate);

        gbcListHist.gridx = 0;
        gbcListHist.gridy = 0;
        listLoginHistory.add(unameLoginHist, gbcListHist);

        gbcListHist.gridx = 1;
        gbcListHist.gridy = 0;
        listLoginHistory.add(loginDate, gbcListHist);

        listLoginHistory.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JScrollPane myScrollLoginHist = new JScrollPane(listLoginHistory);
        myScrollLoginHist.setPreferredSize(new Dimension(600, 300));
        myScrollLoginHist.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        myScrollLoginHist.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        myScrollLoginHist.getVerticalScrollBar().setUnitIncrement(20);

        gbcMain.gridx = 0;
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 3;
        gbcMain.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(myScrollLoginHist, gbcMain);

        gbcMain.gridwidth = 1;

        inputUnameHist = new JTextField(15);
        inputUnameHist.setText("Nhập tên đăng nhập");
        JButton btnLoginHist = new JButton("Xem lịch sử đăng nhập");

        gbcMain.gridx = 0;
        gbcMain.gridy += 1;
        gbcMain.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(inputUnameHist, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btnLoginHist, gbcMain);

        JSeparator sep5 = new JSeparator(SwingConstants.HORIZONTAL);
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 3;
        gbcMain.insets = new Insets(3, 0, 3, 0);
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(sep5, gbcMain);
        gbcMain.fill = GridBagConstraints.NONE;
        gbcMain.insets = new Insets(0, 0, 2, 0);

        // chức năng 1f
        listFriend = new JPanel();
        listFriend.setSize(300, 800);
        listFriend.setLayout(new GridBagLayout());
        gbcListFriend = new GridBagConstraints();
        gbcListFriend.insets = new Insets(0, 2, 5, 10);

        JLabel unameFriend = new JLabel("Tên đăng nhập (bạn bè)");
        JLabel status = new JLabel("Trạng thái");

        setLabel(unameFriend);
        setLabel(status);

        gbcListFriend.gridx = 0;
        gbcListFriend.gridy = 0;
        listFriend.add(unameFriend, gbcListFriend);

        gbcListFriend.gridx = 1;
        gbcListFriend.gridy = 0;
        listFriend.add(status, gbcListFriend);

        listFriend.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JScrollPane myScrollFriend = new JScrollPane(listFriend);
        myScrollFriend.setPreferredSize(new Dimension(600, 300));
        myScrollFriend.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        myScrollFriend.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        myScrollFriend.getVerticalScrollBar().setUnitIncrement(20);

        gbcMain.gridx = 0;
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 3;
        gbcMain.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(myScrollFriend, gbcMain);

        gbcMain.gridwidth = 1;

        inputUnameFriend = new JTextField(15);
        inputUnameFriend.setText("Nhập tên đăng nhập");
        JButton btnFriend = new JButton("Xem danh sách bạn bè");


        gbcMain.gridx = 0;
        gbcMain.gridy += 1;
        gbcMain.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(inputUnameFriend, gbcMain);

        gbcMain.gridx = 0;
        gbcMain.gridy += 1;
        mainPanel.add(btnFriend, gbcMain);


        JScrollPane outerScrollPane = new JScrollPane(mainPanel);
        outerScrollPane.setPreferredSize(new Dimension(1600, 750));
        outerScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outerScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outerScrollPane.getVerticalScrollBar().setUnitIncrement(40);

        return outerScrollPane;
    }
    private JScrollPane trang2() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.insets = new Insets(0, 0, 2, 0);

        // chức năng 2
        listLogin = new JPanel();
        listLogin.setSize(800, 800);
        listLogin.setLayout(new GridBagLayout());
        gbcListLogin = new GridBagConstraints();
        gbcListLogin.insets = new Insets(0, 2, 5, 2);

        JLabel time = new JLabel("Thời gian");
        JLabel uname = new JLabel("Tên đăng nhập");
        JLabel fname = new JLabel("Họ tên");

        setLabel(uname);
        setLabel(fname);
        setLabel(time);

        gbcListLogin.gridx = 0;
        gbcListLogin.gridy = 0;
        listLogin.add(time, gbcListLogin);

        gbcListLogin.gridx = 1;
        gbcListLogin.gridy = 0;
        listLogin.add(uname, gbcListLogin);

        gbcListLogin.gridx = 2;
        gbcListLogin.gridy = 0;
        listLogin.add(fname, gbcListLogin);

        listLogin.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JScrollPane myScroll = new JScrollPane(listLogin);
        myScroll.setPreferredSize(new Dimension(600, 300));
        myScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        myScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        myScroll.getVerticalScrollBar().setUnitIncrement(20);

        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.gridwidth = 3;
        mainPanel.add(myScroll, gbcMain);

        gbcMain.gridwidth = 1;

        JButton btn = new JButton("Xem danh sách đăng nhâp");

        // add action to the button
        gbcMain.gridy += 1;
        mainPanel.add(btn, gbcMain);

        JScrollPane outerScrollPane = new JScrollPane(mainPanel);
        outerScrollPane.setPreferredSize(new Dimension(1600, 750));
        outerScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outerScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outerScrollPane.getVerticalScrollBar().setUnitIncrement(40);

        return outerScrollPane;
    }
    private JScrollPane trang3() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.insets = new Insets(0, 0, 2, 0);

        // chức năng 3a & 3b
        listGroup = new JPanel();
        listGroup.setSize(800, 800);
        listGroup.setLayout(new GridBagLayout());
        gbcListGroup = new GridBagConstraints();
        gbcListGroup.insets = new Insets(0, 5, 5, 5);

        JLabel gname = new JLabel("Tên nhóm");
        JLabel nummember = new JLabel("Số thành viên");
        JLabel gadmin = new JLabel("Quản trị viên");
        JLabel timecreate = new JLabel("Thời gian tạo");

        setLabel(gname);
        setLabel(nummember);
        setLabel(gadmin);
        setLabel(timecreate);

        gbcListGroup.gridx = 0;
        gbcListGroup.gridy = 0;
        listGroup.add(gname, gbcListGroup);

        gbcListGroup.gridx = 1;
        gbcListGroup.gridy = 0;
        listGroup.add(nummember, gbcListGroup);

        gbcListGroup.gridx = 2;
        gbcListGroup.gridy = 0;
        listGroup.add(gadmin, gbcListGroup);

        gbcListGroup.gridx = 3;
        gbcListGroup.gridy = 0;
        listGroup.add(timecreate, gbcListGroup);

        listGroup.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JScrollPane myScroll = new JScrollPane(listGroup);
        myScroll.setPreferredSize(new Dimension(900, 300));
        myScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        myScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        myScroll.getVerticalScrollBar().setUnitIncrement(20);

        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.gridwidth = 3;
        mainPanel.add(myScroll, gbcMain);

        gbcMain.gridwidth = 1;

        btnSortName = new JRadioButton("Sắp xếp theo tên");
        btnSortDateCreate = new JRadioButton("Sắp xếp theo thời gian tạo");
        inputGSearch = new JTextField();
        JButton btn = new JButton("Xem danh sách nhóm");
        JLabel labelGName = new JLabel("Tên nhóm");

        setTextfield(inputGSearch);
        setLabel(labelGName);

        // add action to the button to find group

        ButtonGroup btnG = new ButtonGroup();
        btnG.add(btnSortName);
        btnG.add(btnSortDateCreate);

        gbcMain.anchor = GridBagConstraints.LINE_START;
        gbcMain.gridy += 1;
        mainPanel.add(btnSortName, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btnSortDateCreate, gbcMain);

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 1;
        gbcMain.insets = new Insets(3, 0, 3, 0);
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(sep, gbcMain);
        gbcMain.fill = GridBagConstraints.NONE;
        gbcMain.insets = new Insets(0, 0, 2, 0);

        gbcMain.gridy += 1;
        mainPanel.add(labelGName, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(inputGSearch, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btn, gbcMain);

        JSeparator sep1 = new JSeparator(SwingConstants.HORIZONTAL);
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 3;
        gbcMain.insets = new Insets(3, 0, 3, 0);
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(sep1, gbcMain);
        gbcMain.fill = GridBagConstraints.NONE;
        gbcMain.insets = new Insets(0, 0, 2, 0);

        // chức năng 3c
        listMember = new JPanel();
        listMember.setSize(800, 800);
        listMember.setLayout(new GridBagLayout());
        gbcListMember = new GridBagConstraints();
        gbcListMember.insets = new Insets(0, 2, 5, 2);

        JLabel unameMember = new JLabel("Tên đăng nhập (thành viên)");
        JLabel roleMember = new JLabel("Vai trò");

        setLabel(unameMember);
        setLabel(roleMember);

        gbcListMember.gridx = 0;
        gbcListMember.gridy = 0;
        listMember.add(unameMember, gbcListMember);

        gbcListMember.gridx = 1;
        gbcListMember.gridy = 0;
        listMember.add(roleMember, gbcListMember);

        listMember.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JScrollPane myScrollMember = new JScrollPane(listMember);
        myScrollMember.setPreferredSize(new Dimension(600, 300));
        myScrollMember.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        myScrollMember.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        myScrollMember.getVerticalScrollBar().setUnitIncrement(20);

        gbcMain.gridx = 0;
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 3;
        mainPanel.add(myScrollMember, gbcMain);

        gbcMain.gridwidth = 1;

        inputMemGroupSearch = new JTextField();
        JButton btnMem = new JButton("Xem danh sách thành viên");
        JLabel labelGName1 = new JLabel("Tên nhóm");


        setTextfield(inputMemGroupSearch);
        setLabel(labelGName1);

        gbcMain.gridy += 1;
        mainPanel.add(labelGName1, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(inputMemGroupSearch, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btnMem, gbcMain);

        JSeparator sep2 = new JSeparator(SwingConstants.HORIZONTAL);
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 3;
        gbcMain.insets = new Insets(3, 0, 3, 0);
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(sep2, gbcMain);
        gbcMain.fill = GridBagConstraints.NONE;
        gbcMain.insets = new Insets(0, 0, 2, 0);

        // chức năng 3d
        listAdmin = new JPanel();
        listAdmin.setSize(800, 800);
        listAdmin.setLayout(new GridBagLayout());
        gbcListAdmin = new GridBagConstraints();
        gbcListAdmin.insets = new Insets(0, 2, 5, 2);

        JLabel unameAdmin = new JLabel("Tên đăng nhập (Quản trị viên)");
        JLabel gnameAdmin = new JLabel("Tên nhóm");

        setLabel(unameAdmin);
        setLabel(gnameAdmin);

        gbcListAdmin.gridx = 0;
        gbcListAdmin.gridy = 0;
        listAdmin.add(unameAdmin, gbcListAdmin);

        gbcListAdmin.gridx = 1;
        gbcListAdmin.gridy = 0;
        listAdmin.add(gnameAdmin, gbcListAdmin);

        listAdmin.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JScrollPane myScrollAdmin = new JScrollPane(listAdmin);
        myScrollAdmin.setPreferredSize(new Dimension(600, 300));
        myScrollAdmin.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        myScrollAdmin.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        myScrollAdmin.getVerticalScrollBar().setUnitIncrement(20);

        gbcMain.gridx = 0;
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 3;
        mainPanel.add(myScrollAdmin, gbcMain);

        gbcMain.gridwidth = 1;

        inputAdminSearch = new JTextField();
        JButton btnAdmin = new JButton("Xem danh sách quản trị viên");
        JLabel labelGName2 = new JLabel("Tên nhóm");


        setTextfield(inputAdminSearch);
        setLabel(labelGName2);

        gbcMain.gridy += 1;
        mainPanel.add(labelGName2, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(inputAdminSearch, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btnAdmin, gbcMain);

        JScrollPane outerScrollPane = new JScrollPane(mainPanel);
        outerScrollPane.setPreferredSize(new Dimension(1600, 750));
        outerScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outerScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outerScrollPane.getVerticalScrollBar().setUnitIncrement(40);

        return outerScrollPane;
    }
    private JScrollPane trang4() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.insets = new Insets(0, 0, 2, 0);

        // chức năng 4a & 4b & 4c
        listSpam = new JPanel();
        listSpam.setSize(800, 800);
        listSpam.setLayout(new GridBagLayout());
        gbcListSpam = new GridBagConstraints();
        gbcListSpam.insets = new Insets(0, 2, 5, 2);

        JLabel uname = new JLabel("Tên đăng nhập");
        JLabel timespam = new JLabel("Thời gian báo cáo");
        JLabel byuser = new JLabel("Người báo cáo");

        setLabel(uname);
        setLabel(byuser);
        setLabel(timespam);

        gbcListSpam.gridx = 0;
        gbcListSpam.gridy = 0;
        listSpam.add(uname, gbcListSpam);

        gbcListSpam.gridx = 1;
        gbcListSpam.gridy = 0;
        listSpam.add(timespam, gbcListSpam);

        gbcListSpam.gridx = 2;
        gbcListSpam.gridy = 0;
        listSpam.add(byuser, gbcListSpam);

        listSpam.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JScrollPane myScrollSpam = new JScrollPane(listSpam);
        myScrollSpam.setPreferredSize(new Dimension(600, 300));
        myScrollSpam.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        myScrollSpam.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        myScrollSpam.getVerticalScrollBar().setUnitIncrement(20);

        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.gridwidth = 3;
        gbcMain.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(myScrollSpam, gbcMain);

        gbcMain.gridwidth = 1;

        btnSortNamet4 = new JRadioButton("Sắp xếp theo tên đăng nhập");
        btnSortDatet4 = new JRadioButton("Sắp xếp theo thời gian báo cáo");
        btnFilterName = new JRadioButton("Lọc theo tên đăng nhập");
        btnFilterDate = new JRadioButton("Lọc theo thời gian");
        inputSpamSearch = new JTextField();
        JButton btn = new JButton("Xem danh sách báo cáo spam");

        

        setTextfield(inputSpamSearch);

        ButtonGroup btnG = new ButtonGroup();
        btnG.add(btnSortNamet4);
        btnG.add(btnSortDatet4);

        ButtonGroup btnG1 = new ButtonGroup();
        btnG1.add(btnFilterName);
        btnG1.add(btnFilterDate);

        gbcMain.anchor = GridBagConstraints.LINE_START;
        gbcMain.gridy += 1;
        mainPanel.add(btnSortNamet4, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btnSortDatet4, gbcMain);

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 1;
        gbcMain.insets = new Insets(3, 0, 3, 0);
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(sep, gbcMain);
        gbcMain.fill = GridBagConstraints.NONE;
        gbcMain.insets = new Insets(0, 0, 2, 0);

        gbcMain.gridy += 1;
        mainPanel.add(btnFilterName, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btnFilterDate, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(inputSpamSearch, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btn, gbcMain);

        JSeparator sep1 = new JSeparator(SwingConstants.HORIZONTAL);
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 3;
        gbcMain.insets = new Insets(3, 0, 3, 0);
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(sep1, gbcMain);
        gbcMain.fill = GridBagConstraints.NONE;
        gbcMain.insets = new Insets(0, 0, 2, 0);

        JPanel accountLock = new JPanel();
        JLabel label = new JLabel("Tên đăng nhập");
        inputLockt4 = new JTextField(20);
        JButton lock = new JButton("Khóa tài khoản");


        setLabel(label);
        setTextfield(inputLockt4);

        accountLock.add(label);
        accountLock.add(inputLockt4);
        accountLock.add(lock);

        gbcMain.gridy += 1;
        gbcMain.gridwidth = 2;
        mainPanel.add(accountLock, gbcMain);

        JScrollPane outerScrollPane = new JScrollPane(mainPanel);
        outerScrollPane.setPreferredSize(new Dimension(1600, 750));
        outerScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outerScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outerScrollPane.getVerticalScrollBar().setUnitIncrement(40);

        return outerScrollPane;
    }
    private JScrollPane trang5() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.insets = new Insets(0, 0, 2, 0);

        // chức năng 5a & 5b
        listNew = new JPanel();
        listNew.setSize(800, 800);
        listNew.setLayout(new GridBagLayout());
        gbcListNew = new GridBagConstraints();
        gbcListNew.insets = new Insets(0, 5, 5, 5);

        JLabel uname = new JLabel("Tên đăng nhập");
        JLabel fname = new JLabel("Họ & tên");
        JLabel addr = new JLabel("Địa chỉ");
        JLabel dob = new JLabel("Ngày sinh");
        JLabel gender = new JLabel("Giới tính");
        JLabel email = new JLabel("Email");
        JLabel timecreate = new JLabel("Thời gian tạo tài khoản");

        setLabel(uname);
        setLabel(fname);
        setLabel(addr);
        setLabel(dob);
        setLabel(gender);
        setLabel(email);
        setLabel(timecreate);

        gbcListNew.gridx = 0;
        gbcListNew.gridy = 0;
        listNew.add(uname, gbcListNew);

        gbcListNew.gridx = 1;
        gbcListNew.gridy = 0;
        listNew.add(fname, gbcListNew);

        gbcListNew.gridx = 2;
        gbcListNew.gridy = 0;
        listNew.add(addr, gbcListNew);

        gbcListNew.gridx = 3;
        gbcListNew.gridy = 0;
        listNew.add(dob, gbcListNew);

        gbcListNew.gridx = 4;
        gbcListNew.gridy = 0;
        listNew.add(gender, gbcListNew);

        gbcListNew.gridx = 5;
        gbcListNew.gridy = 0;
        listNew.add(email, gbcListNew);

        gbcListNew.gridx = 6;
        gbcListNew.gridy = 0;
        listNew.add(timecreate, gbcListNew);

        listNew.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JScrollPane myScroll = new JScrollPane(listNew);
        myScroll.setPreferredSize(new Dimension(1200, 300));
        myScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        myScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        myScroll.getVerticalScrollBar().setUnitIncrement(20);

        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.gridwidth = 3;
        mainPanel.add(myScroll, gbcMain);

        gbcMain.gridwidth = 1;

        btnSortNamet5 = new JRadioButton("Sắp xếp theo tên");
        btnSortDatet5 = new JRadioButton("Sắp xếp theo thời gian tạo");
        inputNewSearch = new JTextField();
        JButton btn = new JButton("Xem danh sách đăng ký mới");
        JLabel labelGName = new JLabel("Tên người dùng");
        JLabel fromDate = new JLabel("Từ ngày");
        JLabel toDate = new JLabel("Đến ngày");
        inputFromDate = new JTextField();
        inputToDate = new JTextField();


        setLabel(fromDate);
        setLabel(toDate);
        setTextfield(inputFromDate);
        setTextfield(inputToDate);
        setTextfield(inputNewSearch);
        setLabel(labelGName);

        ButtonGroup btnG = new ButtonGroup();
        btnG.add(btnSortNamet5);
        btnG.add(btnSortDatet5);

        gbcMain.anchor = GridBagConstraints.LINE_START;
        gbcMain.gridy += 1;
        mainPanel.add(fromDate, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(inputFromDate, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(toDate, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(inputToDate, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btnSortNamet5, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btnSortDatet5, gbcMain);

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 1;
        gbcMain.insets = new Insets(3, 0, 3, 0);
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(sep, gbcMain);
        gbcMain.fill = GridBagConstraints.NONE;
        gbcMain.insets = new Insets(0, 0, 2, 0);

        gbcMain.gridy += 1;
        mainPanel.add(labelGName, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(inputNewSearch, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btn, gbcMain);

        JScrollPane outerScrollPane = new JScrollPane(mainPanel);
        outerScrollPane.setPreferredSize(new Dimension(1600, 750));
        outerScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outerScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outerScrollPane.getVerticalScrollBar().setUnitIncrement(40);

        return outerScrollPane;
    }
    private JPanel trang6() {
        String[] month = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i < 24; i+= 2) {
            int k = i / 2;
            temp.add(i, month[k]);
            temp.add(i + 1, "0");
        }

        JFreeChart chart = this.createChart(this.createDataset(temp), "...");
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setRange(0, 100);

        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(900, 370));

        JLabel year = new JLabel("Năm");
        inputYearT6 = new JTextField();
        JButton btn = new JButton("Xem biểu đồ");


        setLabel(year);
        setTextfield(inputYearT6);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.anchor = GridBagConstraints.LINE_START;

        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        mainPanel.add(chartPanel, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(year, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(inputYearT6, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btn, gbcMain);

        return mainPanel;
    }
    private JScrollPane trang7() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.insets = new Insets(0, 0, 2, 0);

        // chức năng 7a & 7b & 7c
        listFriendPlus = new JPanel();
        listFriendPlus.setSize(800, 800);
        listFriendPlus.setLayout(new GridBagLayout());
        gbcListFriendPlus = new GridBagConstraints();
        gbcListFriendPlus.insets = new Insets(0, 5, 5, 5);

        JLabel uname = new JLabel("Tên đăng nhập");
        JLabel fr = new JLabel("Bạn bè (trực tiếp)");
        JLabel fr_fr = new JLabel("Bạn bè (trực tiếp & bạn của bạn)");

        setLabel(uname);
        setLabel(fr);
        setLabel(fr_fr);

        gbcListFriendPlus.gridx = 0;
        gbcListFriendPlus.gridy = 0;
        listFriendPlus.add(uname, gbcListFriendPlus);

        gbcListFriendPlus.gridx = 1;
        gbcListFriendPlus.gridy = 0;
        listFriendPlus.add(fr, gbcListFriendPlus);

        gbcListFriendPlus.gridx = 2;
        gbcListFriendPlus.gridy = 0;
        listFriendPlus.add(fr_fr, gbcListFriendPlus);

        listFriendPlus.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JScrollPane myScroll = new JScrollPane(listFriendPlus);
        myScroll.setPreferredSize(new Dimension(900, 300));
        myScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        myScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        myScroll.getVerticalScrollBar().setUnitIncrement(20);

        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.gridwidth = 3;
        mainPanel.add(myScroll, gbcMain);

        gbcMain.gridwidth = 1;

        btnSortNamet7 = new JRadioButton("Sắp xếp theo tên");
        btnSortDatet7 = new JRadioButton("Sắp xếp theo thời gian tạo");
        JButton btn = new JButton("Xem danh sách bạn bè");
        JLabel labelNName = new JLabel("Tên người dùng");
        inputNameSearch = new JTextField();
        JLabel dir_fr = new JLabel("Số lượng bạn bè trực tiếp");
        inputDir_fr = new JTextField();


        setLabel(dir_fr);
        setLabel(labelNName);
        setTextfield(inputDir_fr);
        setTextfield(inputNameSearch);

        ButtonGroup btnG = new ButtonGroup();
        btnG.add(btnSortNamet7);
        btnG.add(btnSortDatet7);

        gbcMain.anchor = GridBagConstraints.LINE_START;

        gbcMain.gridy += 1;
        mainPanel.add(btnSortNamet7, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btnSortDatet7, gbcMain);

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 1;
        gbcMain.insets = new Insets(3, 0, 3, 0);
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(sep, gbcMain);
        gbcMain.fill = GridBagConstraints.NONE;
        gbcMain.insets = new Insets(0, 0, 2, 0);

        gbcMain.gridy += 1;
        mainPanel.add(labelNName, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(inputNameSearch, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(dir_fr, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(inputDir_fr, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btn, gbcMain);

        JScrollPane outerScrollPane = new JScrollPane(mainPanel);
        outerScrollPane.setPreferredSize(new Dimension(1600, 750));
        outerScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outerScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outerScrollPane.getVerticalScrollBar().setUnitIncrement(40);

        return outerScrollPane;
    }
    private JScrollPane trang8() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.insets = new Insets(0, 0, 2, 0);

        // chức năng 8a & 8b & 8c
        listOpen = new JPanel();
        listOpen.setSize(800, 800);
        listOpen.setLayout(new GridBagLayout());
        gbcListOpen = new GridBagConstraints();
        gbcListOpen.insets = new Insets(0, 5, 5, 5);

        JLabel uname = new JLabel("Tên đăng nhập");
        JLabel open = new JLabel("Số lần mở ứng dụng");
        JLabel chatPeop = new JLabel("Số lượng người đã chat");
        JLabel chatGroup = new JLabel("Số lượng nhóm đã chat");

        setLabel(uname);
        setLabel(open);
        setLabel(chatPeop);
        setLabel(chatGroup);

        gbcListOpen.gridx = 0;
        gbcListOpen.gridy = 0;
        listOpen.add(uname, gbcListOpen);

        gbcListOpen.gridx = 1;
        listOpen.add(open, gbcListOpen);

        gbcListOpen.gridx = 2;
        listOpen.add(chatPeop, gbcListOpen);

        gbcListOpen.gridx = 3;
        listOpen.add(chatGroup, gbcListOpen);

        listOpen.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JScrollPane myScroll = new JScrollPane(listOpen);
        myScroll.setPreferredSize(new Dimension(900, 300));
        myScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        myScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        myScroll.getVerticalScrollBar().setUnitIncrement(20);

        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.gridwidth = 3;
        mainPanel.add(myScroll, gbcMain);

        gbcMain.gridwidth = 1;

        btnSortNamet8 = new JRadioButton("Sắp xếp theo tên");
        btnSortDatet8 = new JRadioButton("Sắp xếp theo thời gian tạo");
        JButton btn = new JButton("Xem danh sách người dùng hoạt động");
        JLabel labelNName = new JLabel("Tên người dùng");
        inputNameSearcht8 = new JTextField();
        JLabel dir_open = new JLabel("Số lượng hoạt động");
        inputDir_open = new JTextField();
        JLabel fromDate = new JLabel("Từ ngày");
        JLabel toDate = new JLabel("Đến ngày");
        inputFromDatet8 = new JTextField();
        inputToDatet8 = new JTextField();



        setLabel(dir_open);
        setLabel(labelNName);
        setLabel(fromDate);
        setLabel(toDate);
        setTextfield(inputDir_open);
        setTextfield(inputNameSearcht8);
        setTextfield(inputFromDatet8);
        setTextfield(inputToDatet8);

        ButtonGroup btnG = new ButtonGroup();
        btnG.add(btnSortNamet8);
        btnG.add(btnSortDatet8);

        gbcMain.anchor = GridBagConstraints.LINE_START;

        gbcMain.gridy += 1;
        mainPanel.add(btnSortNamet8, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btnSortDatet8, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(fromDate, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(inputFromDatet8, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(toDate, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(inputToDatet8, gbcMain);

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        gbcMain.gridy += 1;
        gbcMain.gridwidth = 1;
        gbcMain.insets = new Insets(3, 0, 3, 0);
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(sep, gbcMain);
        gbcMain.fill = GridBagConstraints.NONE;
        gbcMain.insets = new Insets(0, 0, 2, 0);

        gbcMain.gridy += 1;
        mainPanel.add(labelNName, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(inputNameSearcht8, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(dir_open, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(inputDir_open, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btn, gbcMain);

        JScrollPane outerScrollPane = new JScrollPane(mainPanel);
        outerScrollPane.setPreferredSize(new Dimension(1600, 750));
        outerScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outerScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outerScrollPane.getVerticalScrollBar().setUnitIncrement(40);

        return outerScrollPane;
    }
    private JPanel trang9() {
        String[] month = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i < 24; i+= 2) {
            int k = i / 2;
            temp.add(i, month[k]);
            temp.add(i + 1, "0");
        }

        JFreeChart chart = this.createChart1(this.createDataset1(temp), "...");
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setRange(0, 100);

        chartPanel1 = new ChartPanel(chart);
        chartPanel1.setPreferredSize(new java.awt.Dimension(900, 370));

        JLabel year = new JLabel("Năm");
        inputYearT9 = new JTextField();
        JButton btn = new JButton("Xem biểu đồ");



        setLabel(year);
        setTextfield(inputYearT9);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.anchor = GridBagConstraints.LINE_START;

        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        mainPanel.add(chartPanel1, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(year, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(inputYearT9, gbcMain);

        gbcMain.gridy += 1;
        mainPanel.add(btn, gbcMain);

        return mainPanel;
    }

    private CategoryDataset createDataset(ArrayList<String> ChartValue) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < ChartValue.size(); i+= 2) {
            dataset.addValue(Integer.parseInt(ChartValue.get(i + 1)), "Người đăng ký mới", ChartValue.get(i));
        }
        return dataset;
    }
    private JFreeChart createChart(CategoryDataset dataset, String year) {
        return ChartFactory.createBarChart(
                "Biểu đồ số lượng người đăng ký mới năm " + year,
                "Tháng",
                "Số lượng",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }

    private CategoryDataset createDataset1(ArrayList<String> ChartValue) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < ChartValue.size(); i+= 2) {
            dataset.addValue(Integer.parseInt(ChartValue.get(i + 1)), "Người mở ứng dụng", ChartValue.get(i));
        }
        return dataset;
    }
    private JFreeChart createChart1(CategoryDataset dataset, String year) {
        return ChartFactory.createBarChart(
                "Biểu đồ số lượng người mở ứng dụng năm " + year,
                "Tháng",
                "Số lượng",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }

    private void setTextfield(JTextField textfield) {
        textfield.setFont(new Font("Serif", Font.PLAIN, 20));
        textfield.setPreferredSize(new Dimension(260, 30));
    }

    private void setLabel(JLabel label) {
        label.setFont(new Font("Serif", Font.BOLD, 20));
    }

    private ArrayList<JButton> getButton() {
        JButton btn1 = new JButton("Chức năng 1");
        JButton btn2 = new JButton("Chức năng 2");
        JButton btn3 = new JButton("Chức năng 3");
        JButton btn4 = new JButton("Chức năng 4");
        JButton btn5 = new JButton("Chức năng 5");
        JButton btn6 = new JButton("Chức năng 6");
        JButton btn7 = new JButton("Chức năng 7");
        JButton btn8 = new JButton("Chức năng 8");
        JButton btn9 = new JButton("Chức năng 9");

        ArrayList<JButton> list = new ArrayList<>();

        list.add(btn1);
        list.add(btn2);
        list.add(btn3);
        list.add(btn4);
        list.add(btn5);
        list.add(btn6);
        list.add(btn7);
        list.add(btn8);
        list.add(btn9);

        return list;
    }
    private JPanel getFunction1() {
        JPanel outerPanel1 = new JPanel();
        JPanel panel1 = new JPanel(new GridBagLayout());
        GridBagConstraints gbcPanel1 = new GridBagConstraints();

        gbcPanel1.gridx = 0;
        gbcPanel1.gridy = 0;
        gbcPanel1.insets = new Insets(0, 0, 3, 0);
        gbcPanel1.anchor = GridBagConstraints.LINE_START;

        JLabel label1 = new JLabel("Quản lý danh sách người dùng. Thông tin người dùng gồm: tên đăng nhập, họ tên, địa chỉ, ngày sinh, giới tính, email.");
        JLabel label1a = new JLabel("a. Xem danh sách cho phép lọc theo tên/tên đăng nhập/trạng thái, sắp xếp theo tên/ngày tạo.");
        JLabel label1b = new JLabel("b. Thêm/cập nhật/xoá.");
        JLabel label1c = new JLabel("c. Khoá/mở khóa tài khoản.");
        JLabel label1d = new JLabel("d. Cập nhật mật khẩu.");
        JLabel label1e = new JLabel("e. Xem lịch sử đăng nhập.");
        JLabel label1f = new JLabel("f. Danh sách bạn bè.");

        label1.setFont(new Font("Serif", Font.ITALIC, 12));

        ArrayList<JLabel> list = new ArrayList<>();
        list.add(label1);
        list.add(label1a);
        list.add(label1b);
        list.add(label1c);
        list.add(label1d);
        list.add(label1e);
        list.add(label1f);

        for (JLabel element : list) {
            panel1.add(element, gbcPanel1);
            gbcPanel1.gridy += 1;
        }

        outerPanel1.add(panel1);
        outerPanel1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        return outerPanel1;
    }
    private JPanel getFunction2() {
        JPanel outerPanel2 = new JPanel();
        JPanel panel2 = new JPanel(new GridBagLayout());
        GridBagConstraints gbcPanel2 = new GridBagConstraints();

        gbcPanel2.gridx = 0;
        gbcPanel2.gridy = 0;
        gbcPanel2.insets = new Insets(0, 0, 3, 0);
        gbcPanel2.anchor = GridBagConstraints.LINE_START;

        JLabel label2 = new JLabel("Xem danh sách đăng nhập theo thứ tự thời gian. Thông tin gồm: thời gian, tên đăng nhập, họ tên.");

        label2.setFont(new Font("Serif", Font.ITALIC, 12));

        ArrayList<JLabel> list = new ArrayList<>();
        list.add(label2);

        for (JLabel element : list) {
            panel2.add(element, gbcPanel2);
            gbcPanel2.gridy += 1;
        }

        outerPanel2.add(panel2);
        outerPanel2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        return outerPanel2;
    }
    private JPanel getFunction3() {
        JPanel outerPanel3 = new JPanel();
        JPanel panel3 = new JPanel(new GridBagLayout());
        GridBagConstraints gbcPanel3 = new GridBagConstraints();

        gbcPanel3.gridx = 0;
        gbcPanel3.gridy = 0;
        gbcPanel3.insets = new Insets(0, 0, 3, 0);
        gbcPanel3.anchor = GridBagConstraints.LINE_START;

        JLabel label3 = new JLabel("Xem danh sách các nhóm chat.");
        JLabel label3a = new JLabel("a. Sắp xếp theo tên/thời gian tạo.");
        JLabel label3b = new JLabel("b. Lọc theo tên.");
        JLabel label3c = new JLabel("c. Xem danh sách thành viên 1 nhóm.");
        JLabel label3d = new JLabel("d. Xem danh sách admin 1 nhóm.");

        label3.setFont(new Font("Serif", Font.ITALIC, 12));

        ArrayList<JLabel> list = new ArrayList<>();
        list.add(label3);
        list.add(label3a);
        list.add(label3b);
        list.add(label3c);
        list.add(label3d);

        for (JLabel element : list) {
            panel3.add(element, gbcPanel3);
            gbcPanel3.gridy += 1;
        }

        outerPanel3.add(panel3);
        outerPanel3.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        return outerPanel3;
    }
    private JPanel getFunction4() {
        JPanel outerPanel4 = new JPanel();
        JPanel panel4 = new JPanel(new GridBagLayout());
        GridBagConstraints gbcPanel4 = new GridBagConstraints();

        gbcPanel4.gridx = 0;
        gbcPanel4.gridy = 0;
        gbcPanel4.insets = new Insets(0, 0, 3, 0);
        gbcPanel4.anchor = GridBagConstraints.LINE_START;

        JLabel label4 = new JLabel("Xem danh sách báo cáo spam.");
        JLabel label4a = new JLabel("a. Sắp xếp theo thời gian/tên đăng nhập.");
        JLabel label4b = new JLabel("b. Lọc theo thời gian.");
        JLabel label4c = new JLabel("c. Lọc theo tên đăng nhập.");
        JLabel label4d = new JLabel("d. Khóa tài khoản người dùng.");

        label4.setFont(new Font("Serif", Font.ITALIC, 12));

        ArrayList<JLabel> list = new ArrayList<>();
        list.add(label4);
        list.add(label4a);
        list.add(label4b);
        list.add(label4c);
        list.add(label4d);

        for (JLabel element : list) {
            panel4.add(element, gbcPanel4);
            gbcPanel4.gridy += 1;
        }

        outerPanel4.add(panel4);
        outerPanel4.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        return outerPanel4;
    }
    private JPanel getFunction5() {
        JPanel outerPanel5 = new JPanel();
        JPanel panel5 = new JPanel(new GridBagLayout());
        GridBagConstraints gbcPanel5 = new GridBagConstraints();

        gbcPanel5.gridx = 0;
        gbcPanel5.gridy = 0;
        gbcPanel5.insets = new Insets(0, 0, 3, 0);
        gbcPanel5.anchor = GridBagConstraints.LINE_START;

        JLabel label5 = new JLabel("Xem danh sách người dùng đăng ký mới: chọn khoảng thời gian, hiện ra danh sách người dùng đăng ký mới.");
        JLabel label5a = new JLabel("a. Sắp xếp theo tên/thời gian tạo.");
        JLabel label5b = new JLabel("b. Lọc theo tên.");

        label5.setFont(new Font("Serif", Font.ITALIC, 12));

        ArrayList<JLabel> list = new ArrayList<>();
        list.add(label5);
        list.add(label5a);
        list.add(label5b);

        for (JLabel element : list) {
            panel5.add(element, gbcPanel5);
            gbcPanel5.gridy += 1;
        }

        outerPanel5.add(panel5);
        outerPanel5.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        return outerPanel5;
    }
    private JPanel getFunction6() {
        JPanel outerPanel6 = new JPanel();
        JPanel panel6 = new JPanel(new GridBagLayout());
        GridBagConstraints gbcPanel6 = new GridBagConstraints();

        gbcPanel6.gridx = 0;
        gbcPanel6.gridy = 0;
        gbcPanel6.insets = new Insets(0, 0, 3, 0);
        gbcPanel6.anchor = GridBagConstraints.LINE_START;

        JLabel label6 = new JLabel("Biểu đồ số lượng người đăng ký mới theo năm: chọn năm, vẽ biểu đồ với trục hoành là tháng, trục tung là số lượng người đăng ký mới.");

        label6.setFont(new Font("Serif", Font.ITALIC, 12));

        ArrayList<JLabel> list = new ArrayList<>();
        list.add(label6);

        for (JLabel element : list) {
            panel6.add(element, gbcPanel6);
            gbcPanel6.gridy += 1;
        }

        outerPanel6.add(panel6);
        outerPanel6.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        return outerPanel6;
    }
    private JPanel getFunction7() {
        JPanel outerPanel7 = new JPanel();
        JPanel panel7 = new JPanel(new GridBagLayout());
        GridBagConstraints gbcPanel7 = new GridBagConstraints();

        gbcPanel7.gridx = 0;
        gbcPanel7.gridy = 0;
        gbcPanel7.insets = new Insets(0, 0, 3, 0);
        gbcPanel7.anchor = GridBagConstraints.LINE_START;

        JLabel label7 = new JLabel("Xem danh sách người dùng và số lượng bạn bè (1 cột bạn bè trực tiếp, 1 cột tính luôn số lượng bạn của bạn).");
        JLabel label7a = new JLabel("a. Sắp xếp theo tên/thời gian tạo.");
        JLabel label7b = new JLabel("b. Lọc theo tên.");
        JLabel label7c = new JLabel("c. Lọc theo số lượng bạn trực tiếp (bằng, nhỏ hơn, lớn hơn 1 số được nhập).");

        label7.setFont(new Font("Serif", Font.ITALIC, 12));

        ArrayList<JLabel> list = new ArrayList<>();
        list.add(label7);
        list.add(label7a);
        list.add(label7b);
        list.add(label7c);

        for (JLabel element : list) {
            panel7.add(element, gbcPanel7);
            gbcPanel7.gridy += 1;
        }

        outerPanel7.add(panel7);
        outerPanel7.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        return outerPanel7;
    }
    private JPanel getFunction8() {
        JPanel outerPanel8 = new JPanel();
        JPanel panel8 = new JPanel(new GridBagLayout());
        GridBagConstraints gbcPanel8 = new GridBagConstraints();

        gbcPanel8.gridx = 0;
        gbcPanel8.gridy = 0;
        gbcPanel8.insets = new Insets(0, 0, 3, 0);
        gbcPanel8.anchor = GridBagConstraints.LINE_START;

        JLabel label8 = new JLabel("Xem danh sách người dùng hoạt động: chọn khoảng thời gian, hiện ra danh sách người dùng có hoạt đặng và các số liệu (mở ứng dụng, chat với bao nhiêu người, chat bao nhiêu nhóm).");
        JLabel label8a = new JLabel("a. Sắp xếp theo tên/thời gian tạo.");
        JLabel label8b = new JLabel("b. Lọc theo tên.");
        JLabel label8c = new JLabel("c. Lọc theo số lượng hoạt động (bằng, nhỏ hơn, lớn hơn 1 số được nhập).");

        label8.setFont(new Font("Serif", Font.ITALIC, 12));

        ArrayList<JLabel> list = new ArrayList<>();
        list.add(label8);
        list.add(label8a);
        list.add(label8b);
        list.add(label8c);

        for (JLabel element : list) {
            panel8.add(element, gbcPanel8);
            gbcPanel8.gridy += 1;
        }

        outerPanel8.add(panel8);
        outerPanel8.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        return outerPanel8;
    }
    private JPanel getFunction9() {
        JPanel outerPanel9 = new JPanel();
        JPanel panel9 = new JPanel(new GridBagLayout());
        GridBagConstraints gbcPanel9 = new GridBagConstraints();

        gbcPanel9.gridx = 0;
        gbcPanel9.gridy = 0;
        gbcPanel9.insets = new Insets(0, 0, 3, 0);
        gbcPanel9.anchor = GridBagConstraints.LINE_START;

        JLabel label9 = new JLabel("Biểu đồ số lượng người hoạt động theo năm: chọn năm, vẽ biểu đồ với trục hoành là tháng, trục tung là số lượng người có mở ứng dụng.");

        label9.setFont(new Font("Serif", Font.ITALIC, 12));

        ArrayList<JLabel> list = new ArrayList<>();
        list.add(label9);

        for (JLabel element : list) {
            panel9.add(element, gbcPanel9);
            gbcPanel9.gridy += 1;
        }

        outerPanel9.add(panel9);
        outerPanel9.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        return outerPanel9;
    }

//    private void write(String message) throws IOException {
//        os.write(message);
//        os.newLine();
//        os.flush();
//    }

    public void setUpSocket(String message) {
        String dataPart = message.split("\\|")[1];

        String[] strings = dataPart.split(", ");

        ArrayList<String> result = new ArrayList<>(Arrays.asList(strings));
        if (message.startsWith("AdminGetListUser|")) {
            if (message.split("\\|").length > 2) {
                updateListUser(result, 1);
            } else {
                updateListUser(result, 0);
            }
        } else if (message.startsWith("AdminGetListLoginHistory|")) {
            if (message.split("\\|").length > 2) {
                updateListLoginHist(result, 1);
            } else {
                updateListLoginHist(result, 0);
            }
        } else if (message.startsWith("AdminGetListFriend|")) {
            if (message.split("\\|").length > 2) {
                updateListFriend(result, 1);
            } else {
                updateListFriend(result, 0);
            }
        } else if (message.startsWith("AdminGetListLogin|")) {
            if (message.split("\\|").length > 2) {
                updateListLogin(result, 1);
            } else {
                updateListLogin(result, 0);
            }
        } else if (message.startsWith("AdminGetListGroup|")) {
            if (message.split("\\|").length > 2) {
                updateListGroup(result, 1);
            } else {
                updateListGroup(result, 0);
            }
        } else if (message.startsWith("AdminGetListMemGroup|")) {
            if (message.split("\\|").length > 2) {
                updateListMemGroup(result, 1);
            } else {
                updateListMemGroup(result, 0);
            }
        } else if (message.startsWith("AdminGetListAdmin|")) {
            if (message.split("\\|").length > 2) {
                updateListAdmin(result, 1);
            } else {
                updateListAdmin(result, 0);
            }
        } else if (message.startsWith("AdminGetListSpam|")) {
            if (message.split("\\|").length > 2) {
                updateListSpam(result, 1);
            } else {
                updateListSpam(result, 0);
            }
        } else if (message.startsWith("AdminGetListNew|")) {
            if (message.split("\\|").length > 2) {
                updateListNew(result, 1);
            } else {
                updateListNew(result, 0);
            }
        } else if (message.startsWith("AdminGetChartNew|")) {
            updateChartNew(result, message.split("\\|")[2]);
        } else if (message.startsWith("AdminGetListFriendPlus|")) {
            if (message.split("\\|").length > 2) {
                updateListFriendPlus(result, 1);
            } else {
                updateListFriendPlus(result, 0);
            }
        } else if (message.startsWith("AdminGetChartOpen|")) {
            updateChartOpen(result, message.split("\\|")[2]);
        } else if (message.startsWith("AdminGetListOpen|")) {
            if (message.split("\\|").length > 2) {
                updateListOpen(result, 1);
            } else {
                updateListOpen(result, 0);
            }
        }
    }
}
