package User;

import User.User;

import java.sql.*;

public class UserHandler {

    protected static User loginUser(String login, String password, User.Users users) {

        if(users.users.containsKey(login)) {
            User user = users.users.get(login);
            if(user.getPassword().equals(password)) {
                try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/monopolydb", "root", "");
                     PreparedStatement statement = connection.prepareStatement("UPDATE user SET isLoggedIn = true WHERE login = ?")) {
                    statement.setString(1, login);
                    statement.executeUpdate();
                    users.fetchUsers();
                    return user;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    protected static Boolean logoutUser(User user, User.Users users) {
        if (user == null) return false;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/monopolydb", "root", "");
             PreparedStatement statement = connection.prepareStatement("UPDATE user SET isLoggedIn = false WHERE login = ?")) {
            statement.setString(1, user.getLogin());
            statement.executeUpdate();
            users.fetchUsers();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected static String registerUser(String login, String password, String nickname, User.Users users) {
        if (users.users.containsKey(login)) return "exists";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/monopolydb", "root", "");
             PreparedStatement statement = connection.prepareStatement("INSERT INTO user (login, password, nickname, isLoggedIn) VALUES (?, ?, ?, false)")) {
            statement.setString(1, login);
            statement.setString(2, password);
            statement.setString(3, nickname);
            statement.executeUpdate();
            users.fetchUsers();
            return "success";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "error";
    }
}
