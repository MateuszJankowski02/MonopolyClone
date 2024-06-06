package Client;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.application.Application;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import User.User;


public class ClientMainNew extends Application {

    final static String HOST = "localhost";
    final static int PORT = 8080;
    private Thread lobbyListener;
    private String currentUserLogin = null;
    private String currentLobbyName = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        InetAddress ip = InetAddress.getByName(HOST);
        Socket socket = new Socket(ip, PORT);

        System.out.println("Connected to server: " + socket);

        DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
        DataInputStream dataIn = new DataInputStream(socket.getInputStream());

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
            if (loginLayout.getLoginField().getText().isEmpty() || loginLayout.getPasswordField().getText().isEmpty()) {
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
                        dataOut.writeUTF("login");;
                        String userLogin = loginLayout.getLoginField().getText();
                        dataOut.writeUTF(userLogin);
                        dataOut.writeUTF(loginLayout.getPasswordField().getText());
                        dataOut.flush();

                        boolean response = (boolean) dataIn.readBoolean();

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

                        currentUserLogin = userLogin;
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText("Login successful");
                            alert.setContentText("Welcome, " + currentUserLogin);
                            alert.showAndWait();
                            primaryStage.setScene(mainMenuScene);
                            loginLayout.clearFields();
                        });

                    } catch (IOException ex) {
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
                        dataOut.writeUTF("register");
                        dataOut.writeUTF(registerLayout.getUsernameField().getText());
                        dataOut.writeUTF(registerLayout.getPasswordField().getText());
                        dataOut.writeUTF(registerLayout.getNicknameField().getText());
                        dataOut.flush();

                        String response = dataIn.readUTF();
                        if (response.equals("exists")){
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("Registration failed");
                                alert.setContentText("Username already exists, change your username");
                                alert.showAndWait();
                            });
                            return;
                        }

                        if (response.equals("error")){
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("Registration failed");
                                alert.setContentText("An error occurred, please try again later");
                                alert.showAndWait();
                            });
                            return;
                        }

                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText("Registration successful");
                            alert.setContentText("You can now login with your new account");
                            alert.showAndWait();

                            primaryStage.setScene(loginScene);
                            registerLayout.clearFields();
                        });
                    } catch (IOException ex) {
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
            if(currentUserLogin == null){
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
                        dataOut.writeUTF("logout");
                        System.out.println("Logging out user: " + currentUserLogin);
                        System.out.println(currentUserLogin);
                        dataOut.writeUTF(currentUserLogin);
                        dataOut.flush();
                        boolean response = dataIn.readBoolean();
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
                        currentUserLogin = null;

                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText("Logout successful");
                            alert.setContentText("You have been logged out");
                            alert.showAndWait();

                            primaryStage.setScene(loginScene);
                        });
                    } catch (IOException ex) {
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
                        dataOut.writeUTF("exit");
                        dataOut.writeUTF(currentUserLogin);
                        dataOut.flush();
                        socket.close();
                    } catch (IOException ex) {
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
                        dataOut.writeUTF("createLobby");
                        String lobbyName = createLobbyLayout.getLobbyNameField().getText();
                        dataOut.writeUTF(lobbyName);
                        dataOut.writeInt(Integer.parseInt(createLobbyLayout.getMaxPlayersField().getText()));
                        dataOut.writeUTF(currentUserLogin);
                        dataOut.flush();

                        boolean response = dataIn.readBoolean();
                        System.out.println("Response:" + response);
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

                        ArrayList<String> listOfUsers = new ArrayList<>();
                        currentLobbyName = lobbyName;
                        int listOfUsersSize = dataIn.readInt();
                        for (int i = 0; i < listOfUsersSize; i++) {
                            String user = dataIn.readUTF();
                            listOfUsers.add(user);
                            System.out.println("User: " + user);
                        }


                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText("Lobby created");
                            alert.setContentText("Lobby has been created");
                            alert.showAndWait();

                            lobbyLayout.clearUsers();
                            listOfUsers.forEach(lobbyLayout::addUser);

                            primaryStage.setScene(lobbyScene);
                        });

                        startLobbyListener(dataIn, lobbyLayout);

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            createLobbyThread.start();
        });

        // Create lobby scene - Back to main menu button
        createLobbyLayout.getBackToMainMenuButton().setOnAction(e -> {
            primaryStage.setScene(mainMenuScene);
        });

        // List lobbies scene - Join lobby
        listLobbiesLayout.getLobbiesList().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    String selectedLobbyName = listLobbiesLayout.getLobbiesList().getSelectionModel().getSelectedItem();
                    System.out.println("Selected lobby: " + selectedLobbyName);

                    if (selectedLobbyName == null) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("No lobby selected");
                            alert.setContentText("Selected lobby does not exist");
                            alert.showAndWait();
                        });
                        return;
                    }
                    Thread joinLobbyThread = new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try {
                                dataOut.writeUTF("joinLobby");
                                dataOut.writeUTF(selectedLobbyName);
                                dataOut.writeUTF(currentUserLogin);
                                dataOut.flush();
                                
                                boolean response = dataIn.readBoolean();
                                System.out.println("Response:" + response);

                                if (!response){
                                    Platform.runLater(() -> {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Error");
                                        alert.setHeaderText("Joining lobby failed");
                                        alert.setContentText("Lobby is full or game has already started");
                                        alert.showAndWait();
                                    });
                                    return;
                                }
                                ArrayList<String> listOfUsers = new ArrayList<>();
                                currentLobbyName = selectedLobbyName;
                                int listOfUsersSize = dataIn.readInt();
                                for (int i = 0; i < listOfUsersSize; i++) {
                                    String user = dataIn.readUTF();
                                    listOfUsers.add(user);
                                    System.out.println("User: " + user);
                                }

                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Success");
                                    alert.setHeaderText("Lobby joined");
                                    alert.setContentText("You have joined the lobby");
                                    alert.showAndWait();

                                    lobbyLayout.clearUsers();

                                    listOfUsers.forEach(lobbyLayout::addUser);

                                    primaryStage.setScene(lobbyScene);
                                });

                                startLobbyListener(dataIn, lobbyLayout);

                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    joinLobbyThread.start();
                }
            }

        });

        // List lobbies scene - Refresh button
        listLobbiesLayout.getRefreshLobbiesButton().setOnAction(e -> {
            Thread refreshThread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        dataOut.writeUTF("listLobbies");
                        dataOut.flush();

                        ArrayList<String> listOfLobbies = new ArrayList<>();
                        int listOfLobbiesSize = dataIn.readInt();
                        for (int i = 0; i < listOfLobbiesSize; i++) {
                            String lobby = dataIn.readUTF();
                            listOfLobbies.add(lobby);
                            System.out.println("Lobby: " + lobby);
                        }


                        Platform.runLater(() -> {
                            listLobbiesLayout.clearLobbies();
                            listOfLobbies.forEach(listLobbiesLayout::addLobby);

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText("Lobbies refreshed");
                            alert.setContentText("Lobbies have been refreshed");
                            alert.showAndWait();

                        });

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            refreshThread.start();
        });

        // List lobbies scene - Back to main menu button
        listLobbiesLayout.getBackToMainMenuFromListButton().setOnAction(e -> {
            primaryStage.setScene(mainMenuScene);
        });

        // Lobby scene - Start game button
        lobbyLayout.getStartGameButton().setOnAction(e -> {
            // to be implemented
        });

        // Lobby scene - Leave lobby button
        lobbyLayout.getLeaveLobbyButton().setOnAction(e -> {

            Thread leaveLobbyThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        dataOut.writeUTF("stopListener");
                        dataOut.writeUTF("leaveLobby");

                        dataOut.writeUTF(currentLobbyName);
                        dataOut.writeUTF(currentUserLogin);
                        System.out.println("\n");
                        System.out.println("Current lobby: " + currentLobbyName);
                        System.out.println("Leaving user: " + currentUserLogin);

                        boolean removedSuccess = dataIn.readBoolean();
                        if (!removedSuccess){
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("Leaving lobby failed");
                                alert.setContentText("Error occurred during leaving lobby, could not remove player");
                                alert.showAndWait();
                            });
                            startLobbyListener(dataIn, lobbyLayout);
                            return;
                        }
                        currentLobbyName = null;

                        Platform.runLater(() -> {
                            primaryStage.setScene(mainMenuScene);
                        });

                    } catch (IOException ex) {
                        startLobbyListener(dataIn, lobbyLayout);
                        ex.printStackTrace();
                    }
                }
            });
            leaveLobbyThread.start();
        });

        // When client shuts down unexpectedly logout user
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    dataOut.writeUTF("exit");
                    if(!dataIn.readBoolean()) return;
                    dataOut.writeUTF(currentUserLogin);
                    dataOut.flush();
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void startLobbyListener(DataInputStream dataIn, LobbyLayout lobbyLayout) {
        lobbyListener = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    while (true) {
                        String command = dataIn.readUTF();
                        System.out.println("Command received client: " + command);
                        if (command.equals("refreshLobbyUsers")) {
                            System.out.println("Refreshing lobby users");
                            ArrayList<String> listOfUsers = new ArrayList<>();
                            int listOfUsersSize = dataIn.readInt();
                            for (int i = 0; i < listOfUsersSize; i++) {
                                String user = dataIn.readUTF();
                                listOfUsers.add(user);
                                System.out.println("User: " + user);
                            }

                            Platform.runLater(() -> {
                                lobbyLayout.clearUsers();
                                listOfUsers.forEach(lobbyLayout::addUser);
                            });
                        } else if (command.equals("stopListener")) {
                            break;
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        lobbyListener.start();
    }
}
