package Login;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private static final long serialVersionUID = 3L;
    private static int idCounter = 0;
    private int id;
    private String nickname;
    private String login;
    private String password;
    private int score;
    private boolean isloggedIn;

    public User(String name, String login, String password, Boolean isloggedIn) {
        this.id = idCounter++;
        this.nickname = name;
        this.login = login;
        this.password = password;
        this.score = 0;
        this.isloggedIn = isloggedIn;
    }

    public static class Users {
        public ArrayList<User> users = new ArrayList<>();

        public Users(){
            refresh();
        }

        public void addUser(User user) {
            users.add(user);
        }

        public User getUserById(int id){
            for(User user : users){
                if(user.getId() == id){
                    return user;
                }
            }
            return null;
        }

        public User getUserByLogin(String login){
            for(User user : users){
                if(user.getLogin().equals(login)){
                    return user;
                }
            }
            return null;
        }

        public void refresh(){
            users.clear();
            try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" ");
                    users.add(new User(parts[2], parts[0], parts[1], parts[3].equals("true")));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public Boolean getIsLoggedIn() {
        return isloggedIn;
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
        return login + " " + password + " " + nickname + " " + isloggedIn;
    }

    public String getName() {
        return nickname;
    }
}
