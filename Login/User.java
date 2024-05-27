package Login;


import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;

public class User implements Serializable {
    private static final long serialVersionUID = 3L;
    private static int idCounter = 0;
    private int id;
    private String nickname;
    private String login;
    private String password;
    private int score;
    private boolean isLoggedIn;

    public User(String name, String login, String password, boolean isLoggedIn) {
        this.id = idCounter++;
        this.nickname = name;
        this.login = login;
        this.password = password;
        this.score = 0;
        this.isLoggedIn = isLoggedIn;
    }

    public static class Users {
        public ArrayList<User> users = new ArrayList<>();

        public Users() {
            refresh();
        }

        public void addUser(User user) {
            users.add(user);
        }

        public User getUserById(int id) {
            for (User user : users) {
                if (user.getId() == id) {
                    return user;
                }
            }
            return null;
        }

        public User getUserByLogin(String login) {
            for (User user : users) {
                if (user.getLogin().equals(login)) {
                    return user;
                }
            }
            return null;
        }

        public void refresh() {
            users.clear();
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/monopolydb", "root", "");
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM user");
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String nickname = resultSet.getString("nickname");
                    String login = resultSet.getString("login");
                    String password = resultSet.getString("password");
                    boolean isLoggedIn = resultSet.getBoolean("isLoggedIn");
                    users.add(new User(nickname, login, password, isLoggedIn));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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

    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        score++;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return login + " " + password + " " + nickname + " " + isLoggedIn;
    }

    public String getName() {
        return nickname;
    }
}
