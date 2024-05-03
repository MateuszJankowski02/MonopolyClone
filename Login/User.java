package Login;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class User {
    private static int idCounter = 0;
    private int id;
    private String nickname;
    private String login;
    private String password;
    private int score;
    private boolean autheticated;

    public User(String name, String login, String password) {
        this.id = idCounter++;
        this.nickname = name;
        this.login = login;
        this.password = password;
        this.score = 0;
    }

    public static class Users {
        private ArrayList<User> users = new ArrayList<>();

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
                    users.add(new User(parts[2], parts[0], parts[1]));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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

    @Override
    public String toString() {
        return login + " " + password + " " + nickname;
    }

    public String getName() {
        return nickname;
    }
}
