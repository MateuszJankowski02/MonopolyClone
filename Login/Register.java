package Login;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Register {
    public void registerUser(String name, String login, String password) {
        User newUser = new User(name, login, password);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(newUser.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}