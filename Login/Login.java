package Login;

public class Login {

    public User loginUser(String login, String password) {
        User.Users users = new User.Users();
        for (User user : users.users) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password) && !user.getIsLoggedIn()) {
                return user;
            }
        }
        return null;
    }
}