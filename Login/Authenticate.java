package Login;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Authenticate {
    public static User authenticateUser(String login, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts[0].equals(login) && parts[1].equals(password)) {
                    return new User(parts[2], parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}