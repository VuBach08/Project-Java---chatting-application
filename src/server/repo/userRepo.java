package server.repo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import server.Server;

public class userRepo {

    static final String URL = "jdbc:postgresql://localhost:5432/chatting-application";
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String USER = "postgres";
    static final String PW = "123456";

    public static boolean register(String id, String name, String fullname, String email, String password) {
        String INSERT_USERS_SQL = "INSERT INTO public.\"users\" (id, username, fullname, email, password, \"createAt\") values (?,?,?,?,?,?)";
        String USER_EXIST = "SELECT * FROM public.\"users\" where email = ? or username = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement stmt = connection.prepareStatement(USER_EXIST);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {

            ZoneId utc = ZoneId.of("UTC+7");
            ZonedDateTime curDate = ZonedDateTime.now(utc);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = curDate.format(formatter);
            Date sqlDate = Date.valueOf(formattedDate);

            preparedStatement.setString(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, fullname);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, hashPassword(password));  // đã hash ở đây
            preparedStatement.setDate(6, sqlDate);

            stmt.setString(1, email);
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return false; // đã tồn tại
            }

            int count = preparedStatement.executeUpdate();
            return count > 0;

        } catch (SQLException e) {
            System.out.println("Unable to connect to database");
            e.printStackTrace();
            return false;
        }
    }

    public static String Login(String email, String password) {
        String FIND_USERS_SQL = "SELECT * FROM public.\"users\" where (email = ? or username = ?) and password = ? and lock = FALSE";
        String ADD_TO_LOGS_SQL = "INSERT INTO logs (username, logdate) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_USERS_SQL);
             PreparedStatement preparedStatement1 = connection.prepareStatement(ADD_TO_LOGS_SQL)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, hashPassword(password));

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                ZoneId utc = ZoneId.of("UTC+7");
                ZonedDateTime curDate = ZonedDateTime.now(utc);
                LocalDateTime localDateTime = curDate.toLocalDateTime();
                Timestamp timestamp = Timestamp.valueOf(localDateTime);

                preparedStatement1.setString(1, rs.getString("username"));
                preparedStatement1.setTimestamp(2, timestamp);
                preparedStatement1.executeUpdate();

                String isAdmin = rs.getBoolean("isAdmin") ? "true" : "false";
                return rs.getString("id") + "|" + rs.getString("username") + "|" + rs.getString("fullname") + "|" + rs.getString("email") + "|" + isAdmin;
            }
            return "";
        } catch (SQLException e) {
            System.out.println("Unable to connect to database");
            e.printStackTrace();
            return "";
        }
    }


    public static boolean SetOffline(String id) {
        System.out.println("Offline");
        String SET_ONLINE_SQL = "UPDATE public.\"users\" SET \"isOnline\" = false where id = ?";
        String GET_FRIEND_SQL = "SELECT friends from public.\"users\" where id = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(SET_ONLINE_SQL);
             PreparedStatement preparedStatement1 = connection.prepareStatement(GET_FRIEND_SQL)) {

            preparedStatement.setString(1, id);
            preparedStatement1.setString(1, id);

            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement1.executeQuery();
            if (resultSet.next()) {
                Array arr = resultSet.getArray("friends");
                if (arr != null) {
                    String[] m = (String[]) arr.getArray();
                    for (String element : m) {
                        Server.serverThreadBus.boardCastUser(element, "IsOffline|" + id);
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
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

    public static String generateRandomPassword(int length) {
        String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(validChars.length());
            password.append(validChars.charAt(randomIndex));
        }
        return password.toString();
    }

    public static boolean UpdatePassword(String email, String password) {
        String UPDATE_USERS_SQL = "UPDATE public.\"users\" set \"password\" = ? where email=?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USERS_SQL)) {

            preparedStatement.setString(1, hashPassword(password));
            preparedStatement.setString(2, email);

            int count = preparedStatement.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            System.out.println("Unable to connect to database");
            e.printStackTrace();
            return false;
        }
    }
}