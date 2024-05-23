package Client;

import Login.Login;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import Login.User;
import Server.ServerMain;

public class ClientMain extends Application {
    private static Socket socket;
    private static DataInputStream dataIn;
    private static DataOutputStream dataOut;
    public static User loggedUser = null;
    public ListView<String> lobbiesList;
    public ListView<String> lobbyPlayersList;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        socket = new Socket("localhost", 8080);
        dataIn = new DataInputStream(socket.getInputStream());
        dataOut = new DataOutputStream(socket.getOutputStream());

        // Login scene
        LoginLayout loginLayout = new LoginLayout();
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

        // Main menu scene
        Button createLobbyButton = new Button("Create lobby");
        Button listLobbiesButton = new Button("List lobbies");
        Button exitButton = new Button("Exit");
        VBox mainMenuLayout = new VBox(10, createLobbyButton, listLobbiesButton, exitButton);
        mainMenuLayout.setAlignment(Pos.CENTER);
        Scene mainMenuScene = new Scene(mainMenuLayout, 300, 200);

        // Create lobby scene
        TextField lobbyNameField = new TextField();
        lobbyNameField.setPromptText("Enter lobby name");
        TextField maxPlayersField = new TextField();
        maxPlayersField.setPromptText("Enter max players");
        Button confirmCreateLobbyButton = new Button("Confirm");
        VBox createLobbyLayout = new VBox(10, lobbyNameField, maxPlayersField, confirmCreateLobbyButton);
        createLobbyLayout.setAlignment(Pos.CENTER);
        Scene createLobbyScene = new Scene(createLobbyLayout, 300, 200);

        // Combined list and join lobbies scene
        lobbiesList = new ListView<>();
        Button refreshLobbiesButton = new Button("Refresh");
        VBox listLobbiesLayout = new VBox(10, lobbiesList, refreshLobbiesButton);
        listLobbiesLayout.setAlignment(Pos.CENTER);
        Scene listLobbiesScene = new Scene(listLobbiesLayout, 300, 200);

        // Lobby scene
        lobbyPlayersList = new ListView<>();
        Button leaveLobbyButton = new Button("Leave Lobby");
        VBox lobbyLayout = new VBox(10, new Label("Lobby Players:"), lobbyPlayersList, leaveLobbyButton);
        lobbyLayout.setAlignment(Pos.CENTER);
        Scene lobbyScene = new Scene(lobbyLayout, 300, 200);

        // Handle button actions
        loginLayout.getLoginButton().setOnAction(e -> {
            try {
                System.out.println("Sending login request to server...");
                dataOut.writeUTF("login");
                dataOut.writeUTF(loginLayout.getUsernameField().getText());
                dataOut.writeUTF(loginLayout.getPasswordField().getText());
                System.out.println("Waiting for response from server...");
                if (dataIn.readBoolean()) {
                    loggedUser = ServerMain.users.getUserByLogin(loginLayout.getUsernameField().getText());
                    System.out.println("Login successful");
                    primaryStage.setScene(mainMenuScene);
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

        loginLayout.getRegisterButton().setOnAction(e -> {
            primaryStage.setScene(registerScene);
        });

        createLobbyButton.setOnAction(e -> {
            primaryStage.setScene(createLobbyScene);
        });

        listLobbiesButton.setOnAction(e -> {
            refreshLobbies();
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
                    refreshLobbyPlayers(lobbyName); // Refresh lobby players
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Max players must be a number", ButtonType.OK);
                alert.showAndWait();
            }
        });

        // Handle double-click to join a lobby
        lobbiesList.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                String selectedLobby = lobbiesList.getSelectionModel().getSelectedItem();
                if (selectedLobby != null) {
                    try {
                        // Send request to server to join the selected lobby
                        dataOut.writeUTF("joinLobby");
                        dataOut.writeUTF(selectedLobby.split(":")[1].split(",")[0].trim());  // Assuming lobbyName is the first part
                        dataOut.writeInt(loggedUser.getId());

                        // Read the response from the server
                        String serverResponse = dataIn.readUTF();
                        boolean success = dataIn.readBoolean();
                        Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, serverResponse, ButtonType.OK);
                        alert.showAndWait();

                        if (success) {
                            // Switch to the lobby scene
                            refreshLobbyPlayers(selectedLobby.split(",")[0]); // Assuming lobbyName is the first part
                            primaryStage.setScene(lobbyScene);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        refreshLobbiesButton.setOnAction(e -> refreshLobbies());

        leaveLobbyButton.setOnAction(e -> {
            // Handle leaving the lobby
            primaryStage.setScene(mainMenuScene);
        });

        exitButton.setOnAction(e -> {
            try {
                socket.close();
                System.exit(0);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(Login::logoutUser));

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    private void refreshLobbies() {
        try {
            // Send request to server to fetch list of lobbies
            dataOut.writeUTF("listLobbies");

            // Read the number of lobbies from the server
            int lobbiesSize = dataIn.readInt();

            // Clear the current list of lobbies
            lobbiesList.getItems().clear();

            // Read each lobby's details and add to the list view
            for (int i = 0; i < lobbiesSize; i++) {
                String lobbyName = dataIn.readUTF();
                String players = dataIn.readUTF();
                String maxPlayers = dataIn.readUTF();
                String owner = dataIn.readUTF();
                lobbiesList.getItems().add(String.format("Lobby: %s, Players: %s/%s, Owner: %s", lobbyName, players, maxPlayers, owner));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void refreshLobbyPlayers(String lobbyName) {
        try {
            // Send request to server to fetch list of players in the lobby
            dataOut.writeUTF("getLobbyPlayers");
            dataOut.writeUTF(lobbyName);

            // Read the number of players in the lobby
            int playersSize = dataIn.readInt();

            // Clear the current list of players
            lobbyPlayersList.getItems().clear();

            // Read each player's details and add to the list view
            for (int i = 0; i < playersSize; i++) {
                String playerName = dataIn.readUTF();
                lobbyPlayersList.getItems().add(playerName);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
