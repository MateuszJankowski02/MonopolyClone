package Client;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.application.Application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

import User.User;
import Lobby.Lobby;


public class ClientMainNew extends Application {

    final static String HOST = "localhost";
    final static int PORT = 8080;
    private User currentUser = null;
    private Lobby currentLobby = null;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        InetAddress ip = InetAddress.getByName(HOST);
        Socket socket = new Socket(ip, PORT);

        System.out.println("Connected to server: " + socket);

        ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());

        // Login scene
        LoginLayout loginLayout = new LoginLayout();
        Scene loginScene = new Scene(loginLayout, 300, 200);

        // Register scene
        RegisterLayout registerLayout = new RegisterLayout();
        Scene registerScene = new Scene(registerLayout, 300, 200);

        // Main menu scene
        MainMenuLayout mainMenuLayout = new MainMenuLayout();
        Scene mainMenuScene = new Scene(mainMenuLayout, 300, 200);

        // Create lobby scene
        CreateLobbyLayout createLobbyLayout = new CreateLobbyLayout();
        Scene createLobbyScene = new Scene(createLobbyLayout, 300, 200);

        // List lobbies scene
        ListLobbiesLayout listLobbiesLayout = new ListLobbiesLayout();
        Scene listLobbiesScene = new Scene(listLobbiesLayout, 300, 200);

        // Lobby scene
        LobbyLayout lobbyLayout = new LobbyLayout();
        Scene lobbyScene = new Scene(lobbyLayout, 300, 200);

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Client");
        primaryStage.show();

        /*

        Button handlers

        */

        // Login Scene - Login button
        loginLayout.getLoginButton().setOnAction(e -> {
            if (loginLayout.getUsernameField().getText().isEmpty() || loginLayout.getPasswordField().getText().isEmpty()) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Empty fields");
                    alert.setContentText("Please fill in all fields");
                    alert.showAndWait();
                });
                return;
            }
            Thread loginThread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        objectOut.writeObject("login");
                        if(!(Boolean) objectIn.readObject()) return;
                        objectOut.writeObject(loginLayout.getUsernameField().getText());
                        objectOut.writeObject(loginLayout.getPasswordField().getText());
                        objectOut.flush();

                        Boolean response = (Boolean) objectIn.readObject();

                        if (!response){
                            System.out.println("Login failed");
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("Login failed");
                                alert.setContentText("Invalid username, password or user already logged in");
                                alert.showAndWait();
                            });
                            return;
                        }

                        currentUser = (User) objectIn.readObject();
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText("Login successful");
                            alert.setContentText("Welcome, " + currentUser.getNickname());
                            alert.showAndWait();
                            primaryStage.setScene(mainMenuScene);
                            loginLayout.clearFields();
                        });

                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            loginThread.start();
        });

        // Login Scene - Register button
        loginLayout.getRegisterButton().setOnAction(e -> {
            primaryStage.setScene(registerScene);
        });

        // Register scene - Register button
        registerLayout.getRegisterButton().setOnAction(e -> {
            if (    registerLayout.getUsernameField().getText().isEmpty() ||
                    registerLayout.getPasswordField().getText().isEmpty() ||
                    registerLayout.getNicknameField().getText().isEmpty()) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Empty fields");
                alert.setContentText("Please fill in all fields");
                alert.showAndWait();
                return;
            }

            if (!registerLayout.getPasswordField().getText().equals(registerLayout.getConfirmPasswordField().getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Passwords do not match");
                alert.setContentText("Please make sure the passwords match");
                alert.showAndWait();
                return;
            }
            Thread registerThread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        objectOut.writeObject("register");
                        if(!(Boolean) objectIn.readObject()) return;
                        objectOut.writeObject(registerLayout.getUsernameField().getText());
                        objectOut.writeObject(registerLayout.getPasswordField().getText());
                        objectOut.writeObject(registerLayout.getNicknameField().getText());
                        objectOut.flush();

                        String response = (String) objectIn.readObject();
                        if (response.equals("exists")){
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Registration failed");
                            alert.setContentText("Username already exists, change your username");
                            alert.showAndWait();
                            return;
                        }

                        if (response.equals("error")){
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Registration failed");
                            alert.setContentText("An error occurred, please try again later");
                            alert.showAndWait();
                            return;
                        }

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText("Registration successful");
                        alert.setContentText("You can now login with your new account");
                        alert.showAndWait();

                        primaryStage.setScene(loginScene);
                        registerLayout.clearFields();

                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            registerThread.start();
        });

        // Register scene - Back to login button
        registerLayout.getBackToLoginButton().setOnAction(e -> {
            primaryStage.setScene(loginScene);
        });

        // Main menu scene - Create lobby button
        mainMenuLayout.getCreateLobbyButton().setOnAction(e -> {
            primaryStage.setScene(createLobbyScene);
        });

        // Main menu scene - List lobbies button
        mainMenuLayout.getListLobbiesButton().setOnAction(e -> {
            primaryStage.setScene(listLobbiesScene);
        });

        // Main menu scene - Logout button
        mainMenuLayout.getLogoutButton().setOnAction(e -> {
            if(currentUser == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Not logged in");
                alert.setContentText("You are not logged in");
                alert.showAndWait();
                return;
            }
            Thread logoutThread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        objectOut.writeObject("logout");
                        if(!(Boolean) objectIn.readObject()) return;
                        System.out.println("Logging out user: " + currentUser.getLogin());
                        System.out.println(currentUser);
                        objectOut.writeObject(currentUser);
                        objectOut.flush();
                        Boolean response = (Boolean) objectIn.readObject();
                        if (!response){
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("Logout failed");
                                alert.setContentText("Error occurred during logout, please try again later");
                                alert.showAndWait();
                            });
                            return;
                        }
                        currentUser = null;

                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText("Logout successful");
                            alert.setContentText("You have been logged out");
                            alert.showAndWait();

                            primaryStage.setScene(loginScene);
                        });
                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            logoutThread.start();
        });

        // Main menu scene - Exit button
        mainMenuLayout.getExitButton().setOnAction(e -> {
            Thread exitThread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        objectOut.writeObject("exit");
                        if(!(Boolean) objectIn.readObject()) return;
                        objectOut.writeObject(currentUser.getLogin());
                        objectOut.flush();
                        socket.close();
                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            exitThread.start();
            primaryStage.close();
        });

        // Create lobby scene - Create lobby button
        createLobbyLayout.getCreateLobbyButton().setOnAction(e -> {
            if (    createLobbyLayout.getLobbyNameField().getText().isEmpty() ||
                    createLobbyLayout.getMaxPlayersField().getText().isEmpty()) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Empty fields");
                alert.setContentText("Please fill in all fields");
                alert.showAndWait();
                return;
            }

            int maxPlayersTest = Integer.parseInt(createLobbyLayout.getMaxPlayersField().getText());
            if (maxPlayersTest < 2 || maxPlayersTest > 4) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid number of players");
                if (maxPlayersTest > 4) alert.setContentText("Maximum number of players is 4");
                if (maxPlayersTest < 2) alert.setContentText("Minimum number of players is 2");
                maxPlayersTest = 0;
                alert.showAndWait();
                return;
            }
            Thread createLobbyThread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        objectOut.writeObject("createLobby");
                        if(!(Boolean) objectIn.readObject()) return;
                        objectOut.writeObject(createLobbyLayout.getLobbyNameField().getText());
                        objectOut.writeObject(Integer.parseInt(createLobbyLayout.getMaxPlayersField().getText()));
                        System.out.println(currentUser);
                        objectOut.writeObject(currentUser);
                        objectOut.flush();

                        Boolean response = (Boolean) objectIn.readObject();
                        if (!response){
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("Lobby creation failed");
                                alert.setContentText("Lobby with that name already exists");
                                alert.showAndWait();
                            });
                            return;
                        }
                        Lobby lobby = (Lobby) objectIn.readObject();
                        currentLobby = lobby;
                        ListView<String> lobbyUsersList = new ListView<>();
                        lobby.getUsers().forEach(user -> {
                            lobbyUsersList.getItems().add(user.getNickname());
                        });

                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText("Lobby created");
                            alert.setContentText("Lobby has been created");
                            alert.showAndWait();

                            lobbyLayout.setLobbyPlayersList(lobbyUsersList);
                            primaryStage.setScene(lobbyScene);
                        });
                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            createLobbyThread.start();
        });
    }
}
