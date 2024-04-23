package Utilities;

public class User {
    private static int idCounter = 0;
    private int id;
    private String nickname;
    private String login;
    private String password;
    private int score;

    public User(String name, String login, String password) {
        this.id = idCounter++;
        this.nickname = name;
        this.login = login;
        this.password = password;
        this.score = 0;
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
}
