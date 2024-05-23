package Login;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;

import static Client.ClientMain.loggedUser;


public class Login {

    public static User loginUser(String login, String password) {
        List<String> lines = new ArrayList<>();
        User loggedInUser = null;

        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts[0].equals(login) && parts[1].equals(password) && parts[3].equals("false")) {
                    loggedInUser = new User(parts[2], parts[0], parts[1], true);
                    line = parts[0] + " " + parts[1] + " " + parts[2] + " true";
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt"))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loggedInUser;
    }

    public static void logoutUser() {
        if (loggedUser != null) {
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" ");
                    if (parts[0].equals(loggedUser.getLogin())) {
                        line = parts[0] + " " + parts[1] + " " + parts[2] + " false";
                    }
                    lines.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt"))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}