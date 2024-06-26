package Client;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.util.Pair;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;


public class ClientMainNew extends Application {

    final static String HOST = "localhost";
    final static int PORT = 8080;
    private Thread lobbyListener;
    private String currentUserLogin = null;
    private String currentUserNickname = null;
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
        Scene loginScene = new Scene(loginLayout, 500, 300);

        // Register scene
        RegisterLayout registerLayout = new RegisterLayout();
        Scene registerScene = new Scene(registerLayout, 500, 300);

        // Main menu scene
        MainMenuLayout mainMenuLayout = new MainMenuLayout();
        Scene mainMenuScene = new Scene(mainMenuLayout, 500, 300);

        // Create lobby scene
        CreateLobbyLayout createLobbyLayout = new CreateLobbyLayout();
        Scene createLobbyScene = new Scene(createLobbyLayout, 500, 300);

        // List lobbies scene
        ListLobbiesLayout listLobbiesLayout = new ListLobbiesLayout();
        Scene listLobbiesScene = new Scene(listLobbiesLayout, 500, 300);

        // Lobby scene
        LobbyLayout lobbyLayout = new LobbyLayout();
        Scene lobbyScene = new Scene(lobbyLayout, 500, 300);

        // Game scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/monopoly.fxml"));
        AnchorPane root = loader.load();
        GameController gameController = loader.getController();
        Scene gameScene = new Scene(root);

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Monopoly Game");
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
                        currentUserNickname = dataIn.readUTF();
                        System.out.println("Login successful, welcome " + currentUserLogin + ".");
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText("Login successful");
                            alert.setContentText("Welcome, " + currentUserLogin);
                            alert.showAndWait();
                            primaryStage.setTitle("Monopoly Game - " + currentUserLogin);
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
            if(currentUserLogin == null || currentUserNickname == null){
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
                        currentUserNickname = null;

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

            if(!createLobbyLayout.getMaxPlayersField().getText().matches("[0-9]+")){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid character");
                alert.setContentText("Please enter a valid number of players");
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

                        startLobbyListener(dataIn, dataOut, lobbyLayout, primaryStage, gameScene, gameController);

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
                    String selectedLobbyName = listLobbiesLayout.getLobbiesList().getSelectionModel().getSelectedItem().substring(
                            listLobbiesLayout.getLobbiesList().getSelectionModel().getSelectedItem().indexOf(":") + 2,
                            listLobbiesLayout.getLobbiesList().getSelectionModel().getSelectedItem().indexOf(",")
                    );
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

                                startLobbyListener(dataIn, dataOut, lobbyLayout, primaryStage, gameScene, gameController);

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
            Thread startGameThread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        dataOut.writeUTF("stopLobbyListener");
                        dataOut.writeUTF("startGame");
                        dataOut.writeUTF(currentLobbyName);
                        dataOut.writeUTF(currentUserLogin);
                        dataOut.flush();

                        String response = dataIn.readUTF();

                        switch (response){
                            case "noLobby":
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Error");
                                    alert.setHeaderText("Game start failed");
                                    alert.setContentText("Lobby does not exist");
                                    alert.show();
                                    startLobbyListener(dataIn, dataOut, lobbyLayout, primaryStage, gameScene, gameController);
                                });
                                return;
                            case "noOwner":
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Error");
                                    alert.setHeaderText("Game start failed");
                                    alert.setContentText("You are not the owner of the lobby");
                                    alert.show();
                                    startLobbyListener(dataIn, dataOut, lobbyLayout, primaryStage, gameScene, gameController);
                                });
                                return;
                            case "noPlayers":
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Error");
                                    alert.setHeaderText("Game start failed");
                                    alert.setContentText("Not enough players in the lobby");
                                    alert.show();
                                    startLobbyListener(dataIn, dataOut, lobbyLayout, primaryStage, gameScene, gameController);
                                });
                                return;
                            case "noSuccess":
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Error");
                                    alert.setHeaderText("Game start failed");
                                    alert.setContentText("Error occurred during game start");
                                    alert.show();
                                    startLobbyListener(dataIn, dataOut, lobbyLayout, primaryStage, gameScene, gameController);
                                });
                                return;
                            case "success":
                                break;
                            default:
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Error");
                                    alert.setHeaderText("Game start failed");
                                    alert.setContentText("Unknown error occurred");
                                    alert.show();
                                    startLobbyListener(dataIn, dataOut, lobbyLayout, primaryStage, gameScene, gameController);
                                });
                                return;
                        }

                        int amountOfPlayers = dataIn.readInt();
                        ArrayList<String> userNicknames = new ArrayList<>();
                        for (int i = 0; i < amountOfPlayers; i++) {
                            userNicknames.add(dataIn.readUTF());
                        }
                        String currentPlayer = dataIn.readUTF();

                        initializeGameController(gameController, userNicknames, currentPlayer, dataOut, dataIn);
                        startGameListener(dataIn, gameController, primaryStage);

                        Platform.runLater(() -> {
                            primaryStage.setResizable(false);
                            primaryStage.setScene(gameScene);
                        });

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            startGameThread.start();
        });

        // Lobby scene - Leave lobby button
        lobbyLayout.getLeaveLobbyButton().setOnAction(e -> {

            Thread leaveLobbyThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        dataOut.writeUTF("stopLobbyListener");
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
                            startLobbyListener(dataIn, dataOut, lobbyLayout, primaryStage, gameScene, gameController);
                            return;
                        }
                        currentLobbyName = null;

                        Platform.runLater(() -> {
                            primaryStage.setScene(mainMenuScene);
                        });

                    } catch (IOException ex) {
                        startLobbyListener(dataIn, dataOut, lobbyLayout, primaryStage, gameScene, gameController);
                        ex.printStackTrace();
                    }
                }
            });
            leaveLobbyThread.start();
        });

        // Logout client on program termination
        primaryStage.setOnCloseRequest(e -> {
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
        });
    }

    public void startLobbyListener(DataInputStream dataIn, DataOutputStream dataOut, LobbyLayout lobbyLayout,
                                   Stage primaryStage, Scene gameScene, GameController gameController) {
        lobbyListener = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    while (true) {
                        String command = dataIn.readUTF();
                        System.out.println("Command received client lobby listener: " + command);
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
                        } else if(command.equals("startGame")) {
                            System.out.println("Starting game");
                            int amountOfPlayers = dataIn.readInt();
                            ArrayList<String> userNicknames = new ArrayList<>();
                            for (int i = 0; i < amountOfPlayers; i++) {
                                userNicknames.add(dataIn.readUTF());
                            }
                            String currentPlayer = dataIn.readUTF();

                            initializeGameController(gameController, userNicknames, currentPlayer, dataOut, dataIn);
                            startGameListener(dataIn, gameController, primaryStage);

                            Platform.runLater(() -> {
                                primaryStage.setResizable(false);
                                primaryStage.setScene(gameScene);
                            });
                            break;

                        }else if (command.equals("stopListener")) {
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

    public void startGameListener(DataInputStream dataIn, GameController gameController, Stage primaryStage){
        Thread gameListener = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    label:
                    while (true) {
                        String command = dataIn.readUTF();
                        System.out.println("Command received client game listener: " + command);
                        switch (command) {
                            case "updateCurrentPlayer":
                                String newCurrentPlayer = dataIn.readUTF();
                                Platform.runLater(() -> {
                                    gameController.setCurrentPlayerLabelValue(newCurrentPlayer);
                                    if (newCurrentPlayer.equals(currentUserNickname)) {
                                        gameController.getRollDiceButton().setDisable(false);
                                        gameController.getEndTurnButton().setDisable(false);
                                    } else {
                                        gameController.getRollDiceButton().setDisable(true);
                                        gameController.getEndTurnButton().setDisable(true);
                                    }
                                });
                                break;
                            case "updatePlayerLocation":
                                Pair<Double, Double> playerPieceLocation = new Pair<>(dataIn.readDouble(), dataIn.readDouble());
                                double locationX = playerPieceLocation.getKey();
                                double locationY = playerPieceLocation.getValue();
                                int playerID = dataIn.readInt();

                                Platform.runLater(() -> {
                                    switch (playerID) {
                                        case 0:
                                            gameController.getPlayerOnePiece().setLayoutX(locationX);
                                            gameController.getPlayerOnePiece().setLayoutY(locationY);
                                            break;
                                        case 1:
                                            gameController.getPlayerTwoPiece().setLayoutX(locationX);
                                            gameController.getPlayerTwoPiece().setLayoutY(locationY);
                                            break;
                                        case 2:
                                            gameController.getPlayerThreePiece().setLayoutX(locationX);
                                            gameController.getPlayerThreePiece().setLayoutY(locationY);
                                            break;
                                        case 3:
                                            gameController.getPlayerFourPiece().setLayoutX(locationX);
                                            gameController.getPlayerFourPiece().setLayoutY(locationY);
                                            break;
                                    }
                                });
                                break;
                            case "refreshMoney":
                                ArrayList<Integer> money = new ArrayList<>();
                                int moneySize = dataIn.readInt();
                                for (int i = 0; i < moneySize; i++) {
                                    money.add(dataIn.readInt());
                                }
                                Platform.runLater(() -> {
                                    switch (moneySize) {
                                        case 2:
                                            gameController.setPlayerOneCashValue(money.get(0));
                                            gameController.setPlayerTwoCashValue(money.get(1));
                                            break;
                                        case 3:
                                            gameController.setPlayerOneCashValue(money.get(0));
                                            gameController.setPlayerTwoCashValue(money.get(1));
                                            gameController.setPlayerThreeCashValue(money.get(2));
                                            break;
                                        case 4:
                                            gameController.setPlayerOneCashValue(money.get(0));
                                            gameController.setPlayerTwoCashValue(money.get(1));
                                            gameController.setPlayerThreeCashValue(money.get(2));
                                            gameController.setPlayerFourCashValue(money.get(3));
                                            break;
                                    }
                                });
                                break;
                            case "endTurn":
                                boolean response = dataIn.readBoolean();
                                if (!response) {
                                    Platform.runLater(() -> {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Error");
                                        alert.setHeaderText("End turn failed");
                                        alert.setContentText("You still have rolls left, please roll the dice first");
                                        alert.show();
                                    });
                                    continue;
                                }
                                String nextPlayerEndTurn = dataIn.readUTF();
                                Platform.runLater(() -> {
                                    gameController.getRollDiceButton().setDisable(true);
                                    gameController.getEndTurnButton().setDisable(true);
                                    gameController.setCurrentPlayerLabelValue(nextPlayerEndTurn);
                                });
                                break;
                            case "nextTurn":
                                String nextPlayerNextTurn = dataIn.readUTF();
                                Platform.runLater(() -> {
                                    gameController.setCurrentPlayerLabelValue(nextPlayerNextTurn);
                                    if (nextPlayerNextTurn.equals(currentUserNickname)) {
                                        gameController.getRollDiceButton().setDisable(false);
                                        gameController.getEndTurnButton().setDisable(false);
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Next turn");
                                        alert.setHeaderText("It's your turn");
                                        alert.show();
                                    } else {
                                        gameController.getRollDiceButton().setDisable(true);
                                        gameController.getEndTurnButton().setDisable(true);
                                    }
                                });
                                break;
                            case "rollDice":
                                Pair<Integer, Integer> currentRoll = new Pair<>(dataIn.readInt(), dataIn.readInt());
                                int dice1 = currentRoll.getKey();
                                int dice2 = currentRoll.getValue();
                                System.out.println("Dice 1: " + dice1 + " Dice 2: " + dice2);
                                Pair<Double, Double> playerPieceLocation2 = new Pair<>(dataIn.readDouble(), dataIn.readDouble());
                                double locationX2 = playerPieceLocation2.getKey();
                                double locationY2 = playerPieceLocation2.getValue();
                                System.out.println("Location X: " + locationX2 + " Location Y: " + locationY2);
                                int playerID2 = dataIn.readInt();
                                int currentPlayerPosition = dataIn.readInt();
                                System.out.println("Current player position: " + currentPlayerPosition);
                                boolean isInJail = dataIn.readBoolean();



                                Platform.runLater(() -> {
                                    gameController.setCurrentRollAmountLabelValue(currentRoll.getKey() + currentRoll.getValue());
                                    gameController.getRollDiceButton().setDisable(!Objects.equals
                                            (currentRoll.getKey(), currentRoll.getValue()));
                                    switch (playerID2) {
                                        case 0:
                                            gameController.getPlayerOnePiece().setLayoutX(locationX2);
                                            gameController.getPlayerOnePiece().setLayoutY(locationY2);
                                            break;
                                        case 1:
                                            gameController.getPlayerTwoPiece().setLayoutX(locationX2);
                                            gameController.getPlayerTwoPiece().setLayoutY(locationY2);
                                            break;
                                        case 2:
                                            gameController.getPlayerThreePiece().setLayoutX(locationX2);
                                            gameController.getPlayerThreePiece().setLayoutY(locationY2);
                                            break;
                                        case 3:
                                            gameController.getPlayerFourPiece().setLayoutX(locationX2);
                                            gameController.getPlayerFourPiece().setLayoutY(locationY2);
                                            break;
                                    }
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Dice roll");
                                    if(!isInJail){
                                    alert.setHeaderText("You rolled: " + dice1 + " and " + dice2 +
                                            ". Moved to position: " + currentPlayerPosition);
                                    }else{
                                        alert.setHeaderText("You rolled: " + dice1 + " and " + dice2 +
                                                ". You are in jail");
                                    }
                                    alert.show();
                                });

                                eventHandlerClient(dataIn);

                                break;
                            case "stopGameListener":
                                break label;
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        gameListener.start();
    }

    public void eventHandlerClient(DataInputStream dataIn){
        try{
            String eventName = dataIn.readUTF();
            System.out.println("Event name: " + eventName);

            switch (eventName){
                case "buyStreet":
                    int value = dataIn.readInt();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Buy street");
                        alert.setHeaderText("You bought a street");
                        alert.setContentText("You bought a street for: " + value);
                        alert.show();
                    });
                    break;
                case "noBuyStreet":
                    Platform.runLater(() -> {
                        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                        alert2.setTitle("Not enough money");
                        alert2.setHeaderText("You don't have enough money to buy this street");
                        alert2.setContentText("You need more money to buy this street");
                        alert2.show();
                    });
                    break;
                case "buyUtility":
                    int utilityValue = dataIn.readInt();
                    Platform.runLater(() -> {
                        Alert alert3 = new Alert(Alert.AlertType.INFORMATION);
                        alert3.setTitle("Buy utility");
                        alert3.setHeaderText("You bought a utility");
                        alert3.setContentText("You bought a utility for: " + utilityValue);
                        alert3.show();
                    });
                    break;
                case "noBuyUtility":
                    Platform.runLater(() -> {
                        Alert alert4 = new Alert(Alert.AlertType.INFORMATION);
                        alert4.setTitle("Not enough money");
                        alert4.setHeaderText("You don't have enough money to buy this utility");
                        alert4.setContentText("You need more money to buy this utility");
                        alert4.show();
                    });
                    break;
                case "bankrupt":
                    Platform.runLater(() -> {
                        Alert alert3 = new Alert(Alert.AlertType.INFORMATION);
                        alert3.setTitle("Bankrupt");
                        alert3.setHeaderText("You are bankrupt");
                        alert3.setContentText("You are bankrupt, you lost the game");
                        alert3.show();
                    });
                    break;
                case "payRent":
                    int rent = dataIn.readInt();
                    Platform.runLater(() -> {
                        Alert alert4 = new Alert(Alert.AlertType.INFORMATION);
                        alert4.setTitle("Pay rent");
                        alert4.setHeaderText("You have to pay rent");
                        alert4.setContentText("You have to pay rent: " + rent + "$");
                        alert4.show();
                    });
                    break;
                case "ownProperty":
                    Platform.runLater(() -> {
                        Alert alert5 = new Alert(Alert.AlertType.INFORMATION);
                        alert5.setTitle("Own property");
                        alert5.setHeaderText("You own this property");
                        alert5.setContentText("You own this property, you don't have to pay rent");
                        alert5.show();
                    });
                    break;
                case "buyRailroad":
                    int railroadValue = dataIn.readInt();
                    Platform.runLater(() -> {
                        Alert alert6 = new Alert(Alert.AlertType.INFORMATION);
                        alert6.setTitle("Buy railroad");
                        alert6.setHeaderText("You bought a railroad");
                        alert6.setContentText("You bought a railroad for: " + railroadValue);
                        alert6.show();
                    });
                    break;
                case "noBuyRailroad":
                    Platform.runLater(() -> {
                        Alert alert7 = new Alert(Alert.AlertType.INFORMATION);
                        alert7.setTitle("Not enough money");
                        alert7.setHeaderText("You don't have enough money to buy this railroad");
                        alert7.setContentText("You need more money to buy this railroad");
                        alert7.show();
                    });
                    break;
                case "payChest":
                    int chestValuePay = dataIn.readInt();
                    Platform.runLater(() -> {
                        Alert alert8 = new Alert(Alert.AlertType.INFORMATION);
                        alert8.setTitle("Chest");
                        alert8.setHeaderText("You've got scammed");
                        alert8.setContentText("You bought a chest for" + chestValuePay + "$ and there was nothing in it");
                        alert8.show();
                    });
                    break;
                case "receiveChest":
                    int chestValue = dataIn.readInt();
                    Platform.runLater(() -> {
                        Alert alert9 = new Alert(Alert.AlertType.INFORMATION);
                        alert9.setTitle("Chest");
                        alert9.setHeaderText("You received a chest");
                        alert9.setContentText("You received a chest with " + chestValue + "$ in it");
                        alert9.show();
                    });
                    break;
                case "incomeTax":
                    int incomeTaxValue = dataIn.readInt();
                    Platform.runLater(() -> {
                        Alert alert9 = new Alert(Alert.AlertType.INFORMATION);
                        alert9.setTitle("Income tax");
                        alert9.setHeaderText("You have to pay income tax");
                        alert9.setContentText("You have to pay: " + incomeTaxValue + "$");
                        alert9.show();
                    });
                    break;
                case "luxuryTax":
                    int luxuryTaxValue = dataIn.readInt();
                    Platform.runLater(() -> {
                        Alert alert10 = new Alert(Alert.AlertType.INFORMATION);
                        alert10.setTitle("Luxury tax");
                        alert10.setHeaderText("You have to pay luxury tax");
                        alert10.setContentText("You have to pay: " + luxuryTaxValue + "$");
                        alert10.show();
                    });
                    break;
                case "goToJail":
                    Platform.runLater(() -> {
                        Alert alert11 = new Alert(Alert.AlertType.INFORMATION);
                        alert11.setTitle("Go to jail");
                        alert11.setHeaderText("You have to go to jail");
                        alert11.setContentText("You have to go to jail");
                        alert11.show();
                    });
                    break;
                case "payChance":
                    int chanceValuePay = dataIn.readInt();
                    Platform.runLater(() -> {
                        Alert alert12 = new Alert(Alert.AlertType.INFORMATION);
                        alert12.setTitle("Chance");
                        alert12.setHeaderText("You've lost your wallet");
                        alert12.setContentText("You lost your wallet with " + chanceValuePay + "$ in it");
                        alert12.show();
                    });
                    break;
                case "receiveChance":
                    int chanceValue = dataIn.readInt();
                    Platform.runLater(() -> {
                        Alert alert13 = new Alert(Alert.AlertType.INFORMATION);
                        alert13.setTitle("Chance");
                        alert13.setHeaderText("You've found money");
                        alert13.setContentText("While walking you found " + chanceValue + "$ on the ground");
                        alert13.show();
                    });
                    break;
                default:
                    System.out.println("Unknown event");
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void initializeGameController(GameController gameController, ArrayList<String> userNicknames,
                                         String currentPlayer, DataOutputStream dataOut, DataInputStream dataIn){

        if (!currentPlayer.equals(currentUserNickname)){
            gameController.getRollDiceButton().setDisable(true);
            gameController.getEndTurnButton().setDisable(true);
        }

        gameController.setPlayerLabels(userNicknames);
        gameController.setCurrentPlayerLabelValue(currentPlayer);

        gameController.getRollDiceButton().setOnAction(e -> {
            Thread rollDiceThread = new Thread(new Runnable(){
                @Override
                public void run(){
                    try {
                        dataOut.writeUTF("rollDice");
                        dataOut.writeUTF(currentLobbyName);
                        dataOut.flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            rollDiceThread.start();
        });

        gameController.getEndTurnButton().setOnAction(e -> {
            Thread endTurnThread = new Thread(new Runnable(){
                @Override
                public void run(){
                    try {
                        dataOut.writeUTF("endTurn");
                        dataOut.writeUTF(currentLobbyName);
                        dataOut.flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            endTurnThread.start();
        });
    }
}
