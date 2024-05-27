package Login;

import java.sql.*;


public class Register {
    public static void registerUser(String name, String login, String password) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/monopolydb", "root", "");
             PreparedStatement statement = connection.prepareStatement("INSERT INTO user (nickname, login, password, isLoggedIn) VALUES (?, ?, ?, false)")) {
            statement.setString(1, name);
            statement.setString(2, login);
            statement.setString(3, password);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while registering user.");
        }
    }

    public static boolean checkLogin(String login) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/monopolydb", "root", "");
             Statement statement = connection.createStatement();) {
            String query = "SELECT * FROM user WHERE login = " + login;
            ResultSet resultSet = statement.executeQuery(query);
            if(!resultSet.isBeforeFirst()){
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}