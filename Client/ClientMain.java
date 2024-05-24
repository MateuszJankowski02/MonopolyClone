package Client;

import Login.Login;
import javafx.application.Application;
import javafx.application.Platform;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Login.User;
import Server.ServerMain;

public class ClientMain extends Application {
    private static Socket socket;
    private static DataInputStream dataIn;
    private static DataOutputStream dataOut;
    public static User loggedUser = null;
    public ListView<String> lobbiesList;
    public ListView<String> lobbyPlayersList;
    private ExecutorService executorService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        socket = new Socket("localhost", 8080);
        dataIn = new DataInputStream(socket.getInputStream());
        dataOut = new DataOutputStream(socket.getOutputStream());
        executorService = Executors.newCachedThreadPool();

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
            executorService.submit(() -> {
                try {
                    dataOut.writeUTF("login");
                    dataOut.writeUTF(loginLayout.getUsernameField().getText());
                    dataOut.writeUTF(loginLayout.getPasswordField().getText());
                    boolean loginSuccess = dataIn.readBoolean();
                    Platform.runLater(() -> {
                        if (loginSuccess) {
                            loggedUser = ServerMain.users.getUserByLogin(loginLayout.getUsernameField().getText());
                            primaryStage.setScene(mainMenuScene);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Login failed", ButtonType.OK);
                            alert.showAndWait();
                        }
                    });
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        });

        loginLayout.getRegisterButton().setOnAction(e -> primaryStage.setScene(registerScene));

        createLobbyButton.setOnAction(e -> primaryStage.setScene(createLobbyScene));

        listLobbiesButton.setOnAction(e -> {
            refreshLobbies();
            primaryStage.setScene(listLobbiesScene);
        });

        confirmRegisterButton.setOnAction(e -> {
            executorService.submit(() -> {
                try {
                    String username = regUsernameField.getText();
                    String nickname = regNicknameField.getText();
                    String password = regPasswordField.getText();
                    String confirmPassword = confirmPasswordField.getText();
                    if (username.isEmpty() || nickname.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "All fields must be filled", ButtonType.OK);
                            alert.showAndWait();
                        });
                        return;
                    }
                    if (!password.equals(confirmPassword)) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Passwords do not match", ButtonType.OK);
                            alert.showAndWait();
                        });
                        return;
                    }
                    dataOut.writeUTF("register");
                    dataOut.writeUTF(nickname);
                    dataOut.writeUTF(username);
                    dataOut.writeUTF(password);
                    String response = dataIn.readUTF();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, response, ButtonType.OK);
                        alert.showAndWait();
                        primaryStage.setScene(loginScene);
                    });
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        });

        confirmCreateLobbyButton.setOnAction(e -> {
            executorService.submit(() -> {
                try {
                    String lobbyName = lobbyNameField.getText();
                    int maxPlayers = Integer.parseInt(maxPlayersField.getText());

                    dataOut.writeUTF("createLobby");
                    dataOut.writeUTF(lobbyName);
                    dataOut.writeInt(maxPlayers);
                    dataOut.writeInt(loggedUser.getId());

                    String serverResponse = dataIn.readUTF();
                    boolean success = dataIn.readBoolean();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, serverResponse, ButtonType.OK);
                        alert.showAndWait();
                        if (success) {
                            primaryStage.setScene(lobbyScene);
                            refreshLobbyPlayers(lobbyName);
                        }
                    });
                } catch (IOException | NumberFormatException ex) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Error creating lobby", ButtonType.OK);
                        alert.showAndWait();
                    });
                    ex.printStackTrace();
                }
            });
        });

        lobbiesList.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                String selectedLobby = lobbiesList.getSelectionModel().getSelectedItem();
                if (selectedLobby != null) {
                    executorService.submit(() -> {
                        try {
                            String lobbyName = selectedLobby.split(":")[1].split(",")[0].trim();
                            dataOut.writeUTF("joinLobby");
                            dataOut.writeUTF(lobbyName);
                            dataOut.writeInt(loggedUser.getId());

                            String serverResponse = dataIn.readUTF();
                            boolean success = dataIn.readBoolean();
                            Platform.runLater(() -> {
                                Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, serverResponse, ButtonType.OK);
                                alert.showAndWait();
                                if (success) {
                                    refreshLobbyPlayers(lobbyName);
                                    primaryStage.setScene(lobbyScene);
                                }
                            });
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            }
        });

        refreshLobbiesButton.setOnAction(e -> refreshLobbies());

        leaveLobbyButton.setOnAction(e -> primaryStage.setScene(mainMenuScene));

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
        executorService.submit(() -> {
            try {
                dataOut.writeUTF("listLobbies");
                int lobbiesSize = dataIn.readInt();
                ObservableList<String> lobbies = FXCollections.observableArrayList();
                for (int i = 0; i < lobbiesSize; i++) {
                    String lobbyName = dataIn.readUTF();
                    String players = dataIn.readUTF();
                    String maxPlayers = dataIn.readUTF();
                    String owner = dataIn.readUTF();
                    lobbies.add(String.format("Lobby: %s, Players: %s/%s, Owner: %s", lobbyName, players, maxPlayers, owner));
                }
                Platform.runLater(() -> lobbiesList.setItems(lobbies));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void refreshLobbyPlayers(String lobbyName) {
        executorService.submit(() -> {
            try {
                dataOut.writeUTF("getLobbyPlayers");
                dataOut.writeUTF(lobbyName);
                int playersSize = dataIn.readInt();
                ObservableList<String> players = FXCollections.observableArrayList();
                for (int i = 0; i < playersSize; i++) {
                    String playerName = dataIn.readUTF();
                    players.add(playerName);
                }
                Platform.runLater(() -> lobbyPlayersList.setItems(players));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}