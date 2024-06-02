package User;


import java.io.Serializable;
import java.sql.*;
import java.util.HashMap;

public class User implements Serializable {
    private static final long serialVersionUID = 3734252398452134L;
    private String nickname;
    private String login;
    private String password;
    private boolean isLoggedIn;

    public User(String login, String password, String nickname, boolean isLoggedIn) {
        this.login = login;
        this.password = password;
        this.nickname = nickname;
        this.isLoggedIn = isLoggedIn;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public String getLogin() {
        return login;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return login + " " + password + " " + nickname + " " + isLoggedIn;
    }

    public static class Users {
        public HashMap<String, User> users = new HashMap<>();

        public Users() {
            fetchUsers();
        }

        public void addUser(User user) {
            users.put(user.getLogin(), user);
        }

        public User getUserByLogin(String login) {
            return users.get(login);
        }

        public String registerUser(String login, String password, String nickname){
            return UserHandler.registerUser(login, password, nickname, this);
        }

        public User loginUser(String login, String password){
            return UserHandler.loginUser(login, password, this);
        }

        public Boolean logoutUser(User user){
            return UserHandler.logoutUser(user, this);
        }

        public void fetchUsers() {
            users.clear();
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/monopolydb", "root", "");
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM user");
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id             = resultSet.getInt("id");
                    String login       = resultSet.getString("login");
                    String password    = resultSet.getString("password");
                    String nickname    = resultSet.getString("nickname");
                    boolean isLoggedIn = resultSet.getBoolean("isLoggedIn");
                    addUser(new User( login, password, nickname, isLoggedIn));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            for (User user : users.values()) {
                result.append(user.toString()).append("\n");
            }
            return result.toString();
        }
    }
}
