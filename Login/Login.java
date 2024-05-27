package Login;

import java.sql.*;


public class Login {
    public static User loginUser(String login, String password) {

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/monopolydb", "root", "");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM user WHERE login = ? AND password = ? AND isLoggedIn = false")) {
            statement.setString(1, login);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Update isLoggedIn status in the database
                    PreparedStatement updateStatement = connection.prepareStatement("UPDATE user SET isLoggedIn = true WHERE id = ?");
                    updateStatement.setInt(1, resultSet.getInt("id"));
                    updateStatement.executeUpdate();
                    return new User(resultSet.getString("nickname"), resultSet.getString("login"), resultSet.getString("password"), true);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void logoutUser(User user) {
        System.out.println("Logging out user: " + user.getLogin() + " " + user.getNickname() + " " + user.getId());
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/monopolydb", "root", "");
             PreparedStatement statement = connection.prepareStatement("UPDATE user SET isLoggedIn = false WHERE login = ?")) {
            statement.setString(1, user.getLogin());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}