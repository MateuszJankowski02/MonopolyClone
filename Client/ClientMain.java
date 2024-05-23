package Client;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import Login.User;
import Login.Login;
import Server.ServerMain;

public class ClientMain extends Application {
    private static Socket socket;
    private static DataInputStream dataIn;
    private static DataOutputStream dataOut;
    public static User loggedUser = null;
    private static boolean connectedToLobby = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        socket = new Socket("localhost", 8080);
        dataIn = new DataInputStream(socket.getInputStream());
        dataOut = new DataOutputStream(socket.getOutputStream());

        // Login scene
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        Button loginButton = new Button("Login");
        Button registerSceneButton = new Button("Register");

        // Create a ListView to display the list of users
        ListView<String> usersList = new ListView<>();

        // Create a new instance of the Users class
        User.Users users = new User.Users();

        // Loop through the list of users and add each user's login to the ListView
        for (User user : users.users) {
            usersList.getItems().add(user.getLogin());
        }

        VBox loginLayout = new VBox(10, usernameField, passwordField, loginButton, registerSceneButton, usersList);
        loginLayout.setAlignment(Pos.CENTER);
        Scene loginScene = new Scene(loginLayout, 300, 200);

        // Registration scene
        TextField regUsernameField = new TextField();
        regUsernameField.setPromptText("Enter your username");
        TextField regNicknameField = new TextField();
        regNicknameField.setPromptText("Enter your nickname");
        PasswordField regPasswordField = new PasswordField();
        regPasswordField.setPromptText("Enter your password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm your password");
        Button confirmRegisterButton = new Button("Confirm Registration");
        VBox registerLayout = new VBox(10, regUsernameField, regNicknameField, regPasswordField, confirmPasswordField, confirmRegisterButton);
        registerLayout.setAlignment(Pos.CENTER);
        Scene registerScene = new Scene(registerLayout, 300, 200);

        // Lobby scene
        Button createLobbyButton = new Button("Create lobby");
        Button listLobbiesButton = new Button("List lobbies");
        Button joinLobbyButton = new Button("Join lobby");
        Button exitButton = new Button("Exit");
        VBox lobbyLayout = new VBox(10, createLobbyButton, listLobbiesButton, joinLobbyButton, exitButton);
        lobbyLayout.setAlignment(Pos.CENTER);
        Scene lobbyScene = new Scene(lobbyLayout, 300, 200);

        // Create lobby scene
        TextField lobbyNameField = new TextField();
        lobbyNameField.setPromptText("Enter lobby name");
        TextField maxPlayersField = new TextField();
        maxPlayersField.setPromptText("Enter max players");
        Button confirmCreateLobbyButton = new Button("Confirm");
        VBox createLobbyLayout = new VBox(10, lobbyNameField, maxPlayersField, confirmCreateLobbyButton);
        createLobbyLayout.setAlignment(Pos.CENTER);
        Scene createLobbyScene = new Scene(createLobbyLayout, 300, 200);

        // List lobbies scene
        ListView<String> lobbiesList = new ListView<>();
        Button refreshLobbiesButton = new Button("Refresh");
        VBox listLobbiesLayout = new VBox(10, lobbiesList, refreshLobbiesButton);
        listLobbiesLayout.setAlignment(Pos.CENTER);
        Scene listLobbiesScene = new Scene(listLobbiesLayout, 300, 200);


        // Handle button actions
        loginButton.setOnAction(e -> {
            try {
                System.out.println("Sending login request to server...");
                dataOut.writeUTF("login");
                dataOut.writeUTF(usernameField.getText());
                dataOut.writeUTF(passwordField.getText());
                System.out.println("Waiting for response from server...");
                if (dataIn.readBoolean()) {
                    loggedUser = ServerMain.users.getUserByLogin(usernameField.getText());
                    System.out.println("Login successful");
                    primaryStage.setScene(lobbyScene);
                } else {
                    System.out.println("Login failed");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Login failed", ButtonType.OK);
                    alert.showAndWait();
                }
            } catch (IOException ex) {
                System.out.println("An error occurred during the login process");
                ex.printStackTrace();
            }
        });

        registerSceneButton.setOnAction(e -> {
            primaryStage.setScene(registerScene);
        });

        createLobbyButton.setOnAction(e -> {
            primaryStage.setScene(createLobbyScene);
        });

        listLobbiesButton.setOnAction(e -> {
            primaryStage.setScene(listLobbiesScene);
        });

        confirmRegisterButton.setOnAction(e -> {
            try {
                String username = regUsernameField.getText();
                String nickname = regNicknameField.getText();
                String password = regPasswordField.getText();
                String confirmPassword = confirmPasswordField.getText();
                if (username.isEmpty() || nickname.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "All fields must be filled", ButtonType.OK);
                    alert.showAndWait();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Passwords do not match", ButtonType.OK);
                    alert.showAndWait();
                    return;
                }
                dataOut.writeUTF("register");
                dataOut.writeUTF(nickname);
                dataOut.writeUTF(username);
                dataOut.writeUTF(password);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, dataIn.readUTF(), ButtonType.OK);
                alert.showAndWait();
                primaryStage.setScene(loginScene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        confirmCreateLobbyButton.setOnAction(e -> {
            try {
                String lobbyName = lobbyNameField.getText();
                int maxPlayers = Integer.parseInt(maxPlayersField.getText());

                // Send request to server to create a new lobby
                dataOut.writeUTF("createLobby");
                dataOut.writeUTF(lobbyName);
                dataOut.writeInt(maxPlayers);
                dataOut.writeInt(loggedUser.getId());

                // Read the response from the server
                String serverResponse = dataIn.readUTF();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, serverResponse, ButtonType.OK);
                alert.showAndWait();

                // If the lobby was created successfully, switch to the lobby scene
                if (dataIn.readBoolean()) {
                    primaryStage.setScene(lobbyScene);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Max players must be a number", ButtonType.OK);
                alert.showAndWait();
            }
        });

        joinLobbyButton.setOnAction(e -> {
            // Handle join lobby action
        });

        refreshLobbiesButton.setOnAction(e -> {
            try {
                // Send request to server to fetch list of lobbies
                dataOut.writeUTF("listLobbies");

                // Read the number of lobbies from the server
                int lobbiesSize = dataIn.readInt();

                // Clear the current list of lobbies
                lobbiesList.getItems().clear();

                // Read each lobby from the server and add it to the ListView
                for(int i = 0; i < lobbiesSize; i++) {
                    String lobbyName = dataIn.readUTF();
                    lobbiesList.getItems().add(lobbyName);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        exitButton.setOnAction(e -> System.exit(0));

        Runtime.getRuntime().addShutdownHook(new Thread(Login::logoutUser));

        primaryStage.setScene(loginScene);
        primaryStage.show();
    }
}

/*
package Client;

import Login.Login;
import Login.Register;
import Login.User;
import Server.ServerMain;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientMain {
    private static Socket socket;
    private static DataInputStream dataIn;
    private static DataOutputStream dataOut;
    private static User loggedUser = null;
    private static boolean connectedToLobby = false;

    public static void main(String[] args) throws IOException {
        socket = new Socket("localhost", 8080);
        System.out.println("Connected to server");
        dataIn = new DataInputStream(socket.getInputStream());
        dataOut = new DataOutputStream(socket.getOutputStream());

        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Monopoly!");
        while(loggedUser == null) {
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1:
                    dataOut.writeUTF("login"); // write option to server
                    System.out.print("Enter your username: ");
                    String username = scanner.nextLine();
                    dataOut.writeUTF(username); // write username to server
                    System.out.print("Enter your password: ");
                    String password = scanner.nextLine();
                    dataOut.writeUTF(password); // write password to server
                    System.out.print(dataIn.readUTF()); // read response from server
                    if (dataIn.readBoolean()) {
                        loggedUser = ServerMain.users.getUserByLogin(username);
                    }
                    break;
                case 2:
                    dataOut.writeUTF("register"); // write option to server 1
                    System.out.print("Enter your name: ");
                    String nickname = scanner.nextLine();
                    dataOut.writeUTF(nickname); // write nickname to server 2
                    System.out.print("Enter your username: ");
                    String login = scanner.nextLine();
                    dataOut.writeUTF(login); // write login to server 3
                    System.out.print("Enter your password: ");
                    String pass = scanner.nextLine();
                    dataOut.writeUTF(pass); // write password to server 4
                    System.out.print(dataIn.readUTF()); // read response from server 5
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }

        while (!connectedToLobby) {
            System.out.println("1. Create lobby");
            System.out.println("2. List lobbies");
            System.out.println("3. Join lobby");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1:
                    dataOut.writeUTF("createLobby");
                    System.out.print("Enter lobby name: ");
                    String lobbyName = scanner.nextLine();
                    dataOut.writeUTF(lobbyName);
                    System.out.print("Enter max players: ");
                    int maxPlayers = scanner.nextInt();
                    dataOut.writeInt(maxPlayers);
                    dataOut.writeInt(loggedUser.getId());
                    connectedToLobby = true;
                    // Wait for a response from the server
                    String serverResponseCreate = dataIn.readUTF();
                    System.out.println(serverResponseCreate);
                    break;
                case 2:
                    dataOut.writeUTF("listLobbies");
                    int lobbiesSize = dataIn.readInt();
                    for(int i = 0; i < lobbiesSize; i++) {
                        System.out.print("Name: " + dataIn.readUTF() + " | ");
                        System.out.print("Player count: " + dataIn.readUTF() + " | ");
                        System.out.print("Max players: " + dataIn.readUTF() + " | ");
                        System.out.print("Owner: " + dataIn.readUTF() + " | ");
                        System.out.print("\n");
                    }
                    break;
                case 3:
                    dataOut.writeUTF("joinLobby");
                    System.out.print("Enter lobby name: ");
                    String lobbyNameJoin = scanner.nextLine();
                    dataOut.writeUTF(lobbyNameJoin);
                    dataOut.writeInt(loggedUser.getId());
                    System.out.println(dataIn.readUTF());
                    if (dataIn.readBoolean()) {
                        connectedToLobby = true;
                    }
                    // Wait for a response from the server
                    String serverResponseJoin = dataIn.readUTF();
                    System.out.println(serverResponseJoin);
                    break;
                case 4:
                    System.exit(0);
                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}
 */