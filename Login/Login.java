package Login;

public class Login {
    private Authenticate auth;

    public Login() {
        this.auth = new Authenticate();
    }

    public User loginUser(String login, String password) {
        return auth.authenticateUser(login, password);
    }
}