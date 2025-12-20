package server.repo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import clients.models.User;
import server.Server;

public class AdminRepo {

    static final String URL = "jdbc:postgresql://localhost:5432/chatting-application";
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String USER = "postgres";
    static final String PW = "123456";

    //HASH PASSWORD
    public static String hashPassword(String pw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(pw.getBytes());

            // Convert byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte hashedByte : hashedBytes) {
                String hex = Integer.toHexString(0xff & hashedByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            String hashedPW = hexString.toString();
            return hashedPW;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Hashing algorithm not found");
            e.printStackTrace();
            return null;
        }
    }

    // Admin Get List User
    public static void AdminGetListUser(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_USER_SQL;
            System.out.println(Arrays.toString(messageSplit));

            if (messageSplit[4].isEmpty()) {
                if (Objects.equals(messageSplit[5], "Both")) {
                    ADMIN_GET_LIST_USER_SQL = "SELECT * FROM public.\"users\"";
                } else {
                    ADMIN_GET_LIST_USER_SQL = "SELECT * FROM public.\"users\" WHERE \"isOnline\" = ?";
                }

                if (messageSplit[2].equals("1") && messageSplit[3].equals("1")) {
                    ADMIN_GET_LIST_USER_SQL += " ORDER BY username DESC, \"createAt\" DESC";
                } else if (messageSplit[2].equals("1")) {
                    ADMIN_GET_LIST_USER_SQL += " ORDER BY username DESC";
                } else if (messageSplit[3].equals("1")) {
                    ADMIN_GET_LIST_USER_SQL += " ORDER BY \"createAt\" DESC";
                }
            } else {
                if (messageSplit[1].equals("1")) {
                    if (Objects.equals(messageSplit[5], "Both")) {
                        ADMIN_GET_LIST_USER_SQL = "SELECT * FROM public.\"users\" WHERE username ILIKE ?";
                    } else {
                        ADMIN_GET_LIST_USER_SQL = "SELECT * FROM public.\"users\" WHERE username ILIKE ? AND \"isOnline\" = ?";
                    }

                    if (messageSplit[2].equals("1") && messageSplit[3].equals("1")) {
                        ADMIN_GET_LIST_USER_SQL += " ORDER BY username DESC, \"createAt\" DESC";
                    } else if (messageSplit[2].equals("1")) {
                        ADMIN_GET_LIST_USER_SQL += " ORDER BY username DESC";
                    } else if (messageSplit[3].equals("1")) {
                        ADMIN_GET_LIST_USER_SQL += " ORDER BY \"createAt\" DESC";
                    }
                } else {
                    if (Objects.equals(messageSplit[5], "Both")) {
                        ADMIN_GET_LIST_USER_SQL = "SELECT * FROM public.\"users\" WHERE fullname ILIKE ?";
                    } else {
                        ADMIN_GET_LIST_USER_SQL = "SELECT * FROM public.\"users\" WHERE fullname ILIKE ? AND \"isOnline\" = ?";
                    }

                    if (messageSplit[2].equals("1") && messageSplit[3].equals("1")) {
                        System.out.println("IN");
                        ADMIN_GET_LIST_USER_SQL += " ORDER BY fullname DESC, \"createAt\" DESC";
                    } else if (messageSplit[2].equals("1")) {
                        ADMIN_GET_LIST_USER_SQL += " ORDER BY fullname DESC";
                    } else if (messageSplit[3].equals("1")) {
                        ADMIN_GET_LIST_USER_SQL += " ORDER BY \"createAt\" DESC";
                    }
                }
            }

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_USER_SQL)) {

                if (messageSplit[4].isEmpty()) {
                    if (!Objects.equals(messageSplit[5], "Both")) {
                        preparedStatement.setBoolean(1, messageSplit[5].equals("Online") ? true : false);
                    }
                } else {
                    if (messageSplit[1].equals("1")) {
                        if (Objects.equals(messageSplit[5], "Both")) {
                            preparedStatement.setString(1, "%" + messageSplit[4] + "%");
                        } else {
                            preparedStatement.setString(1, "%" + messageSplit[4] + "%");
                            preparedStatement.setBoolean(2, messageSplit[5].equals("Online") ? true : false);
                        }
                    } else {
                        if (Objects.equals(messageSplit[5], "Both")) {
                            preparedStatement.setString(1, "%" + messageSplit[4] + "%");
                        } else {
                            preparedStatement.setString(1, "%" + messageSplit[4] + "%");
                            preparedStatement.setBoolean(2, messageSplit[5].equals("Online") ? true : false);
                        }
                    }
                }

                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListUser|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("username")).append(", ");
                        result.append(rs.getString("fullname")).append(", ");
                        result.append(rs.getString("address")).append(", ");
                        result.append(rs.getString("dob")).append(", ");
                        result.append(rs.getString("gender")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getString("email")).append("|END");
                        } else {
                            result.append(rs.getString("email")).append(", ");
                        }

                        String fullReturn = "AdminGetListUser|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AdminAddNewAccount(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_ADD_NEW_ACCOUNT_SQL;
            String ADMIN_CHECK_USERNAME;
            String ADMIN_CHECK_EMAIL;

            ADMIN_ADD_NEW_ACCOUNT_SQL = "INSERT INTO public.\"users\" (username, fullname, address, dob, gender, email, \"isOnline\", lock, \"createAt\", password, id, \"isAdmin\") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ADMIN_CHECK_USERNAME = "SELECT * FROM public.\"users\" WHERE username = ?";
            ADMIN_CHECK_EMAIL = "SELECT * FROM public.\"users\" WHERE email = ?";

            String dateString = messageSplit[4];
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            LocalDate curDate = LocalDate.now();
            try {
                java.util.Date parsedDate = dateFormat.parse(dateString);
                java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

                java.sql.Date sqlDateCreate = java.sql.Date.valueOf(curDate);

                try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                     PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_ADD_NEW_ACCOUNT_SQL);
                     PreparedStatement preparedStatement1 = connection.prepareStatement(ADMIN_CHECK_USERNAME);
                     PreparedStatement preparedStatement2 = connection.prepareStatement(ADMIN_CHECK_EMAIL)) {

                    preparedStatement1.setString(1, messageSplit[1]);
                    preparedStatement2.setString(1, messageSplit[6]);
                    ResultSet set1 = preparedStatement1.executeQuery();
                    ResultSet set2 = preparedStatement2.executeQuery();

                    if (!set1.next() && !set2.next()) {
                        preparedStatement.setString(1, messageSplit[1]);
                        preparedStatement.setString(2, messageSplit[2]);
                        preparedStatement.setString(3, messageSplit[3]);
                        preparedStatement.setDate(4, sqlDate);
                        preparedStatement.setString(5, messageSplit[5]);
                        preparedStatement.setString(6, messageSplit[6]);
                        preparedStatement.setBoolean(7, false);
                        preparedStatement.setBoolean(8, false);
                        preparedStatement.setDate(9, sqlDateCreate);
                        String pw = hashPassword("1");
                        preparedStatement.setString(10, pw);
                        preparedStatement.setString(11, UUID.randomUUID().toString());
                        preparedStatement.setBoolean(12, false);

                        int rowsAffected = preparedStatement.executeUpdate();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AdminUpdateAccount(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_UPDATE_ACCOUNT_SQL;
            String ADMIN_CHECK_USERNAME;

            ADMIN_UPDATE_ACCOUNT_SQL = "UPDATE public.\"users\" SET username = ?, fullname = ?, address = ? WHERE email = ?";
            ADMIN_CHECK_USERNAME = "SELECT * FROM public.\"users\" WHERE username = ?";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_UPDATE_ACCOUNT_SQL);
                 PreparedStatement preparedStatement1 = connection.prepareStatement(ADMIN_CHECK_USERNAME)) {
                preparedStatement1.setString(1, messageSplit[1]);

                ResultSet set1 = preparedStatement1.executeQuery();
                if (!set1.next()) {
                    preparedStatement.setString(1, messageSplit[1]);
                    preparedStatement.setString(2, messageSplit[2]);
                    preparedStatement.setString(3, messageSplit[3]);
                    preparedStatement.setString(4, messageSplit[4]);

                    int rowsAffected = preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AdminDeleteAccount(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_DELETE_ACCOUNT_SQL;

            ADMIN_DELETE_ACCOUNT_SQL = "DELETE FROM public.\"users\" WHERE username = ?";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_DELETE_ACCOUNT_SQL)) {
                preparedStatement.setString(1, messageSplit[1]);

                int rowsAffected = preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AdminLockAccount(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_LOCK_ACCOUNT_SQL;

            ADMIN_LOCK_ACCOUNT_SQL = "UPDATE public.\"users\" SET lock = ? WHERE username = ?";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_LOCK_ACCOUNT_SQL)) {
                preparedStatement.setBoolean(1, true);
                preparedStatement.setString(2, messageSplit[1]);

                int rowsAffected = preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AdminUnlockAccount(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_UNLOCK_ACCOUNT_SQL;

            ADMIN_UNLOCK_ACCOUNT_SQL = "UPDATE public.\"users\" SET lock = ? WHERE username = ?";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_UNLOCK_ACCOUNT_SQL)) {
                preparedStatement.setBoolean(1, false);
                preparedStatement.setString(2, messageSplit[1]);

                int rowsAffected = preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AdminRenewPassword(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_RENEW_PASSWORD_SQL;

            ADMIN_RENEW_PASSWORD_SQL = "UPDATE public.\"users\" SET password = ? WHERE username = ?";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_RENEW_PASSWORD_SQL)) {
                String hash = User.hashPassword(messageSplit[2]);
                preparedStatement.setString(1, hashPassword(hash));
                preparedStatement.setString(2, messageSplit[1]);

                int rowsAffected = preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AdminGetListLoginHistory(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_LOGIN_HISTORY_SQL;

            ADMIN_GET_LIST_LOGIN_HISTORY_SQL = "SELECT * FROM public.\"logs\" WHERE username = ?";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_LOGIN_HISTORY_SQL)) {

                preparedStatement.setString(1, messageSplit[1]);
                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListUser|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("username")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getString("logdate")).append("|END");
                        } else {
                            result.append(rs.getString("logdate")).append(", ");
                        }

                        String fullReturn = "AdminGetListLoginHistory|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AdminGetListFriend(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_FRIEND_SQL;

            ADMIN_GET_LIST_FRIEND_SQL = "SELECT * FROM public.\"users\" as Fr WHERE Fr.id IN (SELECT unnest(array_agg(friends)) FROM public.\"users\" WHERE username = ? AND (SELECT COALESCE(ARRAY_LENGTH(friends, 1), 0) AS num_elements FROM public.\"users\" as Ch WHERE Ch.username = ?) > 0);";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_FRIEND_SQL)) {

                preparedStatement.setString(1, messageSplit[1]);
                preparedStatement.setString(2, messageSplit[1]);
                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListFriend|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("username")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getBoolean("isOnline") ? "Trực tuyến" : "Ngoại tuyến").append("|END");
                        } else {
                            result.append(rs.getBoolean("isOnline") ? "Trực tuyến" : "Ngoại tuyến").append(", ");
                        }

                        String fullReturn = "AdminGetListFriend|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AdminGetListLogin(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_LOGIN_SQL;

            ADMIN_GET_LIST_LOGIN_SQL = "SELECT u.username, u.fullname, l.logdate FROM public.\"users\" as u JOIN public.\"logs\" as l ON u.username = l.username;";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_LOGIN_SQL)) {
                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListLogin|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("logdate")).append(", ");
                        result.append(rs.getString("username")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getString("fullname")).append("|END");
                        } else {
                            result.append(rs.getString("fullname")).append(", ");
                        }

                        String fullReturn = "AdminGetListLogin|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AdminGetListGroup(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_GROUP_SQL;

            ADMIN_GET_LIST_GROUP_SQL = "SELECT\n" +
                    "    g.groupname,\n" +
                    "    ARRAY_TO_STRING(u.username_array, ' - ') AS ad,\n" +
                    "    ARRAY_LENGTH(g.users, 1) AS mems,\n" +
                    "    g.\"createAt\" AS createat\n" +
                    "FROM\n" +
                    "    public.\"groups\" g\n" +
                    "LEFT JOIN LATERAL (\n" +
                    "    SELECT ARRAY(SELECT username FROM public.users WHERE id = ANY(g.admin)) AS username_array\n" +
                    ") u ON TRUE\n" +
                    "WHERE\n" +
                    "    g.groupname ILIKE ?\n";

            if (messageSplit[1].equals("1") && messageSplit[2].equals("1")) {
                ADMIN_GET_LIST_GROUP_SQL += " ORDER BY groupname DESC, \"createAt\" DESC";
            } else if (messageSplit[1].equals("1")) {
                ADMIN_GET_LIST_GROUP_SQL += " ORDER BY groupname DESC";
            } else if (messageSplit[2].equals("1")) {
                ADMIN_GET_LIST_GROUP_SQL += " ORDER BY \"createAt\" DESC";
            }

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_GROUP_SQL)) {
                preparedStatement.setString(1, "%" + messageSplit[3] + "%");
                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListGroup|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("groupname")).append(", ");
                        result.append(rs.getInt("mems")).append(", ");
                        result.append(rs.getString("ad")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getString("createat")).append("|END");
                        } else {
                            result.append(rs.getString("createat")).append(", ");
                        }

                        String fullReturn = "AdminGetListGroup|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AdminGetListMemGroup(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_MEM_GROUP_SQL = "SELECT\n" +
                    "    ARRAY_TO_STRING(u.non_admin_users, ' - ') AS username\n" +
                    "FROM\n" +
                    "    public.\"groups\" g\n" +
                    "LEFT JOIN LATERAL (\n" +
                    "    SELECT\n" +
                    "        ARRAY(SELECT username FROM public.users WHERE id = ANY(g.users) AND id <> ALL(g.admin)) AS non_admin_users\n" +
                    ") u ON TRUE\n" +
                    "WHERE\n" +
                    "    g.groupname ILIKE ?\n";
            String ADMIN_GET_LIST_MEM_GROUP_AD_SQL = "SELECT\n" +
                    "    UNNEST(u.username_array) AS username\n" +
                    "FROM\n" +
                    "    public.\"groups\" g\n" +
                    "LEFT JOIN LATERAL (\n" +
                    "    SELECT\n" +
                    "        ARRAY(SELECT username FROM public.users WHERE id = ANY(g.admin)) AS username_array\n" +
                    ") u ON TRUE\n" +
                    "WHERE\n" +
                    "    g.groupname ILIKE ?\n";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_MEM_GROUP_SQL);
                 PreparedStatement preparedStatementAd = connection.prepareStatement(ADMIN_GET_LIST_MEM_GROUP_AD_SQL);) {
                preparedStatement.setString(1, "%" + messageSplit[1] + "%");

                preparedStatementAd.setString(1, "%" + messageSplit[1] + "%");
                ResultSet rsAd = preparedStatementAd.executeQuery();
                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListMemGroup|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("username")).append(", ");
                        result.append("Thành viên");

                        String fullReturn = "AdminGetListMemGroup|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }

                if (!rsAd.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListMemGroup|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rsAd.getString("username")).append(", ");
                        if (rsAd.isLast()) {
                            result.append("Quản trị viên").append("|END");
                        } else {
                            result.append("Quản trị viên");
                        }

                        String fullReturn = "AdminGetListMemGroup|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rsAd.next());
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AdminGetListAdmin(String[] messageSplit) {
        try {
            System.out.println(Arrays.toString(messageSplit));
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_ADMIN_SQL = "SELECT\n" +
                    "    g.groupname,\n" +
                    "    ARRAY_TO_STRING(u.username_array, ' - ') AS ad\n" +
                    "FROM\n" +
                    "    public.\"groups\" g\n" +
                    "LEFT JOIN LATERAL (\n" +
                    "    SELECT\n" +
                    "        ARRAY(SELECT username FROM public.users WHERE id = ANY(g.admin)) AS username_array\n" +
                    ") u ON TRUE\n" +
                    "WHERE\n" +
                    "    g.groupname ILIKE ?\n";
            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_ADMIN_SQL)) {
                preparedStatement.setString(1, "%" + messageSplit[1] + "%");

                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListAdmin|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("ad")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getString("groupname")).append("|END");
                        } else {
                            result.append(rs.getString("groupname"));
                        }
                        String fullReturn = "AdminGetListAdmin|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AdminGetListSpam(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_SPAM_SQL = "SELECT * FROM public.\"spams\"";

            if (messageSplit.length == 3) {} else {
                if (messageSplit[2].equals("1")) {
                    ADMIN_GET_LIST_SPAM_SQL += " WHERE username ILIKE ?";
                } else if (messageSplit[2].equals("-1")) {
                    ADMIN_GET_LIST_SPAM_SQL += " WHERE EXTRACT(YEAR FROM date)::TEXT ILIKE ?";
                }
            }

            if (messageSplit[1].equals("1")) {
                ADMIN_GET_LIST_SPAM_SQL += " ORDER BY username ASC";
            } else if (messageSplit[1].equals("-1")) {
                ADMIN_GET_LIST_SPAM_SQL += " ORDER BY date ASC";
            }

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_SPAM_SQL)) {

                if (messageSplit.length != 3) {
                    preparedStatement.setString(1, "%" + messageSplit[3] + "%");
                }

                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListSpam|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("username")).append(", ");
                        result.append(rs.getString("date")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getString("ByUser")).append("|END");
                        } else {
                            result.append(rs.getString("ByUser"));
                        }

                        String fullReturn = "AdminGetListSpam|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AdminGetListNew(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_NEW_SQL = "SELECT * FROM public.\"users\"";
            System.out.println(Arrays.toString(messageSplit));

            ADMIN_GET_LIST_NEW_SQL += " WHERE \"createAt\" BETWEEN ?::DATE AND ?::DATE AND username ILIKE ?";
            if (messageSplit[3].equals("1")) {
                ADMIN_GET_LIST_NEW_SQL += " ORDER BY fullname ASC";
            } else if (messageSplit[3].equals("-1")) {
                ADMIN_GET_LIST_NEW_SQL += " ORDER BY \"createAt\" ASC";
            }

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_NEW_SQL)) {

                preparedStatement.setString(1, messageSplit[1]);
                preparedStatement.setString(2, messageSplit[2]);
                preparedStatement.setString(3, "%" + messageSplit[4] + "%");

                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListNew|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("username")).append(", ");
                        result.append(rs.getString("fullname")).append(", ");
                        result.append(rs.getString("address")).append(", ");
                        result.append(rs.getString("dob")).append(", ");
                        result.append(rs.getString("gender")).append(", ");
                        result.append(rs.getString("email")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getString("createAt")).append("|END");
                        } else {
                            result.append(rs.getString("createAt"));
                        }

                        String fullReturn = "AdminGetListNew|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AdminGetChartNew(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_CHART_NEW_SQL = " WITH months AS (\n" +
                    "                SELECT generate_series(1, 12) AS month\n" +
                    "            )\n" +
                    "            SELECT months.month,\n" +
                    "                   COUNT(public.users.\"createAt\") AS row_count\n" +
                    "            FROM months\n" +
                    "            LEFT JOIN public.users ON EXTRACT(MONTH FROM public.users.\"createAt\") = months.month\n" +
                    "                               AND EXTRACT(YEAR FROM public.users.\"createAt\") = ?\n" +
                    "            GROUP BY months.month\n" +
                    "            ORDER BY months.month;";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_CHART_NEW_SQL)) {
                preparedStatement.setInt(1, Integer.parseInt(messageSplit[1]));

                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetChartNew|no data|END");
                } else {
                    StringBuilder result = new StringBuilder();
                    do {
                        result.append(rs.getInt("month")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getBigDecimal("row_count"));
                        } else {
                            result.append(rs.getBigDecimal("row_count")).append(", ");
                        }
                    } while (rs.next());
                    String fullReturn = "AdminGetChartNew|" + result + "|" + messageSplit[1];
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AdminGetListFriendPlus(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_FRIEND_PLUS_SQL = "SELECT * FROM \n" +
                    "(\n" +
                    "\tSELECT * FROM (SELECT tu1.id, array_length(tu1.friends, 1) AS dirfr, SUM(array_length(tu2.friends, 1)) AS total FROM users tu1 LEFT JOIN users tu2 ON tu2.id = ANY(tu1.friends) GROUP BY tu1.id, tu1.friends) AS fr JOIN users tu3 ON tu3.id = fr.id\n" +
                    ") AS newtable ";

            if (messageSplit.length >= 4) {
                ADMIN_GET_LIST_FRIEND_PLUS_SQL += " WHERE username ILIKE ?";
            }
            if (messageSplit.length == 5) {
                ADMIN_GET_LIST_FRIEND_PLUS_SQL += " AND dirfr " + messageSplit[3];
            }

            if (messageSplit[1].equals("1")) {
                ADMIN_GET_LIST_FRIEND_PLUS_SQL += " ORDER BY username";
            } else if (messageSplit[1].equals("-1")) {
                ADMIN_GET_LIST_FRIEND_PLUS_SQL += " ORDER BY \"createAt\"";
            }
            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_FRIEND_PLUS_SQL)) {
                if (messageSplit.length >= 4) {
                    preparedStatement.setString(1, messageSplit[2] + "%");
                }

                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListFriendPlus|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("username")).append(", ");
                        result.append(rs.getInt("dirfr")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getInt("total") + rs.getInt("dirfr")).append("|END");
                        } else {
                            result.append(rs.getInt("total") + rs.getInt("dirfr"));
                        }

                        String fullReturn = "AdminGetListFriendPlus|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AdminGetListOpen(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_LIST_OPEN_SQL = "SELECT createat.username,\n" +
                    "    COALESCE(\"openApp\", 0) AS \"openApp\",\n" +
                    "    COALESCE(\"singleChat\", 0) AS \"singleChat\",\n" +
                    "    COALESCE(\"groupChat\", 0) AS \"groupChat\",\n" +
                    "    createat.\"createAt\"\n" +
                    "FROM (\n" +
                    "    SELECT username, COUNT(*) AS \"openApp\"\n" +
                    "    FROM logs\n" +
                    "    WHERE username ILIKE ? AND logs.logdate::DATE BETWEEN ?::DATE AND ?::DATE\n" +
                    "    GROUP BY username\n" +
                    ") AS openapp\n" +
                    "JOIN (\n" +
                    "    SELECT username, \"createAt\"\n" +
                    "    FROM users\n" +
                    "    WHERE username ILIKE ?\n" +
                    ") AS createat ON openapp.username = createat.username\n" +
                    "LEFT JOIN (\n" +
                    "    SELECT username, COUNT(DISTINCT \"idChat\") AS \"singleChat\"\n" +
                    "    FROM systems\n" +
                    "    WHERE username ILIKE ? AND date(systems.time AT TIME ZONE 'UTC+7') BETWEEN ?::DATE AND ?::DATE AND type = 1\n" +
                    "    GROUP BY username\n" +
                    ") AS singlechat ON createat.username = singlechat.username\n" +
                    "LEFT JOIN (\n" +
                    "    SELECT username, COUNT(DISTINCT \"idChat\") AS \"groupChat\"\n" +
                    "    FROM systems\n" +
                    "    WHERE username ILIKE ? AND date(systems.time AT TIME ZONE 'UTC+7') BETWEEN ?::DATE AND ?::DATE AND type = 2\n" +
                    "    GROUP BY username\n" +
                    ") AS groupchat  ON createat.username = groupchat.username\n";
            if (messageSplit.length == 6) {
                ADMIN_GET_LIST_OPEN_SQL += "WHERE \"openApp\" " + messageSplit[4] + "\n";
            } else if (messageSplit.length == 8) {
                ADMIN_GET_LIST_OPEN_SQL += "WHERE \"openApp\" " + messageSplit[5] + "\n";
            }

            if (messageSplit[1].equals("1")) {
                ADMIN_GET_LIST_OPEN_SQL += " ORDER BY username";
            } else if (messageSplit[1].equals("-1")) {
                ADMIN_GET_LIST_OPEN_SQL += " ORDER BY \"createAt\"";
            }

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_LIST_OPEN_SQL)) {
                if (messageSplit.length == 5 || messageSplit.length == 6) {
                    preparedStatement.setString(1, "%");
                    preparedStatement.setString(4, "%");
                    preparedStatement.setString(5, "%");
                    preparedStatement.setString(8, "%");
                } else if (messageSplit.length == 7 || messageSplit.length == 8) {
                    preparedStatement.setString(1, "%" + messageSplit[4] + "%");
                    preparedStatement.setString(4, "%" + messageSplit[4] + "%");
                    preparedStatement.setString(5, "%" + messageSplit[4] + "%");
                    preparedStatement.setString(8, "%" + messageSplit[4] + "%");
                }

                preparedStatement.setString(2, messageSplit[2]);
                preparedStatement.setString(3, messageSplit[3]);
                preparedStatement.setString(6, messageSplit[2]);
                preparedStatement.setString(7, messageSplit[3]);
                preparedStatement.setString(9, messageSplit[2]);
                preparedStatement.setString(10, messageSplit[3]);

                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetListOpen|no data|END");
                } else {
                    do {
                        StringBuilder result = new StringBuilder();
                        result.append(rs.getString("username")).append(", ");
                        result.append(rs.getInt("openApp")).append(", ");
                        result.append(rs.getInt("singleChat")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getInt("groupChat")).append(", ").append("|END");
                        } else {
                            result.append(rs.getInt("groupChat")).append(", ");
                        }

                        String fullReturn = "AdminGetListOpen|" + result;
                        Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                    } while (rs.next());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AdminGetChartOpen(String[] messageSplit) {
        try {
            Class.forName(JDBC_DRIVER);
            String ADMIN_GET_CHART_NEW_SQL = " WITH months AS (\n" +
                    "                SELECT generate_series(1, 12) AS month\n" +
                    "            )\n" +
                    "            SELECT months.month,\n" +
                    "                   COUNT(logs.logdate) AS row_count\n" +
                    "            FROM months\n" +
                    "            LEFT JOIN logs ON EXTRACT(MONTH FROM logs.logdate) = months.month\n" +
                    "                               AND EXTRACT(YEAR FROM logs.logdate) = ?\n" +
                    "            GROUP BY months.month\n" +
                    "            ORDER BY months.month;";

            try (Connection connection = DriverManager.getConnection(URL, USER, PW);
                 PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_GET_CHART_NEW_SQL)) {
                preparedStatement.setInt(1, Integer.parseInt(messageSplit[1]));

                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next()) {
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], "AdminGetChartOpen|no data|END");
                } else {
                    StringBuilder result = new StringBuilder();
                    do {
                        result.append(rs.getInt("month")).append(", ");
                        if (rs.isLast()) {
                            result.append(rs.getBigDecimal("row_count"));
                        } else {
                            result.append(rs.getBigDecimal("row_count")).append(", ");
                        }
                    } while (rs.next());
                    String fullReturn = "AdminGetChartOpen|" + result + "|" + messageSplit[1];
                    Server.serverThreadBus.boardCast(messageSplit[messageSplit.length - 1], fullReturn);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
