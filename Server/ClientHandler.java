package Server;


import java.io.EOFException;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import User.User;
import Lobby.Lobby;
import javafx.util.Pair;

public class ClientHandler implements Runnable {

    private static final Map<String, Command> commandMap = new HashMap<>();
    private int clientID;
    final DataInputStream dataIn;
    final DataOutputStream dataOut;
    Socket socket;
    boolean isloggedin;

    // constructor
    public ClientHandler(Socket socket, int clientID, DataInputStream objectIn, DataOutputStream objectOut) {
        this.dataIn      = objectIn;
        this.dataOut     = objectOut;
        this.clientID = clientID;
        this.socket        = socket;
        this.isloggedin    = true;
    }

    @Override
    public void run() {
        String clientCommand;
        initializeCommands();
        while (true) {
            try {
                // receive the string with the command from the client
                clientCommand = dataIn.readUTF();
                System.out.println("Command received: " + clientCommand);

                // map the command to the appropriate function
                Command command = commandMap.get(clientCommand);
                System.out.println("Command: " + command);

                if (command != null) {
                    // execute the command
                    command.execute(dataIn, dataOut, clientID);
                }
            } catch (SocketException | EOFException e) {
                System.out.println("Client " + clientID + " disconnected");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void initializeCommands() {
        commandMap.put("login", new LoginCommand());
        commandMap.put("register", new RegisterCommand());
        commandMap.put("createLobby", new CreateLobbyCommand());
        commandMap.put("joinLobby", new JoinLobbyCommand());
        commandMap.put("listLobbies", new ListLobbiesCommand());
        commandMap.put("logout", new LogoutCommand());
        commandMap.put("exit", new ExitCommand());
        commandMap.put("leaveLobby", new LeaveLobbyCommand());
        commandMap.put("startGame", new StartGameCommand());
        commandMap.put("stopLobbyListener", new StopLobbyListenerCommand());
        commandMap.put("rollDice", new RollDiceCommand());
        commandMap.put("endTurn", new EndTurnCommand());
    }

    public void notifyListenersRefreshLobbyUsers(Lobby lobby){
        try{
            System.out.println("Notifying listeners to refresh lobby users");
            ArrayList<String> listOfUsers = new ArrayList<>();
            lobby.getUsersArray().forEach(user -> listOfUsers.add(user.getNickname()));
            dataOut.writeUTF("refreshLobbyUsers");
            dataOut.writeInt(listOfUsers.size());
            for (String user : listOfUsers){
                dataOut.writeUTF(user);
            }
            dataOut.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyListenersStartGame(Lobby lobby){
        try{
            System.out.println("Notifying listeners to start game");
            ArrayList<String> listOfUsersNicknames = new ArrayList<>();
            lobby.getUsersArray().forEach(user -> listOfUsersNicknames.add(user.getNickname()));
            dataOut.writeUTF("startGame");
            dataOut.writeInt(listOfUsersNicknames.size());
            for (String user : listOfUsersNicknames){
                dataOut.writeUTF(user);
            }
            String currentPlayerLogin = lobby.getGameManager().getCurrentPlayerLogin();
            String currentPlayerNickname = lobby.getUsers().get(currentPlayerLogin).getNickname();
            dataOut.writeUTF(currentPlayerNickname);

            dataOut.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyListenersNextTurn(Lobby lobby){
        try{
            dataOut.writeUTF("nextTurn");
            String currentPlayerLogin = lobby.getGameManager().getCurrentPlayerLogin();
            String currentPlayerNickname = lobby.getUsers().get(currentPlayerLogin).getNickname();
            dataOut.writeUTF(currentPlayerNickname);
            dataOut.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyListenersPlayerMoved(Pair<Double, Double> newLocation, int currentPlayerID){
        try{
            dataOut.writeUTF("updatePlayerLocation");
            dataOut.writeDouble(newLocation.getKey());
            dataOut.writeDouble(newLocation.getValue());
            dataOut.writeInt(currentPlayerID);
            dataOut.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    interface Command {
        void execute(DataInputStream dataIn, DataOutputStream dataOut, int clientID) throws IOException;
    }

    static class LoginCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut, int clientID) throws IOException {
            String username = dataIn.readUTF();
            String password = dataIn.readUTF();
            System.out.println("Login attempt: " + username + ", " + password);
            System.out.println("Users before:");
            System.out.println(ServerMainNew.users);
            boolean success = ServerMainNew.users.loginUser(username, password);
            System.out.println("Users after:");
            System.out.println(ServerMainNew.users);
            if (!success) {
                System.out.println("Login failed");
                dataOut.writeBoolean(false);
                return;
            }
            dataOut.writeBoolean(true);
            dataOut.writeUTF(ServerMainNew.users.getUserByLogin(username).getNickname());
            dataOut.flush();
        }
    }

    static class RegisterCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut, int clientID) throws IOException {
            String username = dataIn.readUTF();
            String password = dataIn.readUTF();
            String nickname = dataIn.readUTF();
            System.out.println("Register attempt: " + username + ", " + password + ", " + nickname);
            String response = ServerMainNew.users.registerUser(username, password, nickname);
            dataOut.writeUTF(response);
            dataOut.flush();
        }
    }

    static class CreateLobbyCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut, int clientID) throws IOException {
            String lobbyName = dataIn.readUTF();
            int maxPlayers = dataIn.readInt();
            String ownerLogin = dataIn.readUTF();

            User owner = ServerMainNew.users.getUserByLogin(ownerLogin);

            boolean response = ServerMainNew.lobbies.createLobby(lobbyName, clientID, maxPlayers, owner);
            System.out.println("Lobby created: " + response);
            dataOut.writeBoolean(response);
            if (!response) return;

            Lobby createdLobby = ServerMainNew.lobbies.getLobbyByName(lobbyName);
            ArrayList<String> createdLobbyUsers = new ArrayList<>();

            createdLobby.getUsersArray().forEach(user -> createdLobbyUsers.add(user.getNickname()));
            dataOut.writeInt(createdLobbyUsers.size());
            for (String user : createdLobbyUsers){
                dataOut.writeUTF(user);
            }
            dataOut.flush();
        }
    }

    static class JoinLobbyCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut, int ClientID) throws IOException {
            String lobbyName = dataIn.readUTF();
            String joiningUserLogin = dataIn.readUTF();
            User joiningUser = ServerMainNew.users.getUserByLogin(joiningUserLogin);
            Lobby lobby = ServerMainNew.lobbies.getLobbyByName(lobbyName);
            if (lobby == null) {
                dataOut.writeBoolean(false);
                return;
            }
            boolean response = lobby.addUser(joiningUser);

            dataOut.writeBoolean(response);
            if (!response) return;
            lobby.getListenersIDs().forEach(listenerID -> ServerMainNew.notifyClientToRefreshUsers(listenerID, lobby));
            lobby.addListener(ClientID);

            ArrayList<String> joinedLobbyUsers = new ArrayList<>();
            lobby.getUsersArray().forEach(user -> joinedLobbyUsers.add(user.getNickname()));
            dataOut.writeInt(joinedLobbyUsers.size());
            for (String user : joinedLobbyUsers){
                dataOut.writeUTF(user);
            }
            dataOut.flush();
        }
    }

    static class ListLobbiesCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut, int ClientID) throws IOException {
            ArrayList<String> lobbies = ServerMainNew.lobbies.getLobbiesList();
            dataOut.writeInt(lobbies.size());
            for (String lobby : lobbies){
                dataOut.writeUTF(lobby);
            }
            dataOut.flush();
        }
    }

    static class LogoutCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut, int ClientID) throws IOException {
            String userLogin = dataIn.readUTF();
            User user = ServerMainNew.users.getUserByLogin(userLogin);
            boolean response = ServerMainNew.users.logoutUser(user);
            dataOut.writeBoolean(response);
        }
    }

    static class ExitCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut, int ClientID) throws IOException {
            String userLogin = dataIn.readUTF();
            User user = ServerMainNew.users.getUserByLogin(userLogin);
            if(user == null){
                return;
            }
            ServerMainNew.users.logoutUser(user);
            ServerMainNew.clients.remove(ClientID);
        }
    }

    static class LeaveLobbyCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut, int ClientID) throws IOException {

            String lobbyName = dataIn.readUTF();
            String leavingUserLogin = dataIn.readUTF();
            User leavingUser = ServerMainNew.users.getUserByLogin(leavingUserLogin);
            Lobby lobby = ServerMainNew.lobbies.getLobbyByName(lobbyName);

            System.out.println("\n");
            System.out.println("Current lobby:" + lobby.getLobbyName());
            System.out.println("Leaving user: " + leavingUser.getNickname());

            if (lobby == null) {
                System.out.println("Lobby not found");
                dataOut.writeBoolean(false);
                return;
            }
            if (!lobby.removePlayer(leavingUser)) {
                System.out.println("User not found in lobby");
                dataOut.writeBoolean(false);
                return;
            }
            lobby.removeListener(ClientID);
            lobby.getListenersIDs().forEach(listenerID -> ServerMainNew.notifyClientToRefreshUsers(listenerID, lobby));
            dataOut.writeBoolean(true);

            if (lobby.getUsersArray().isEmpty()){
                ServerMainNew.lobbies.removeLobby(lobbyName);
                System.out.println("Removed lobby: " + lobbyName);
            }
        }
    }

    public static class StartGameCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut, int ClientID) throws IOException {
            String lobbyName = dataIn.readUTF();
            String ownerLogin = dataIn.readUTF();
            User user = ServerMainNew.users.getUserByLogin(ownerLogin);
            Lobby lobby = ServerMainNew.lobbies.getLobbyByName(lobbyName);

            if (lobby == null) {
                dataOut.writeUTF("noLobby");
                return;
            }
            if (!lobby.getOwnerLogin().equals(user.getLogin())) {
                dataOut.writeUTF("noOwner");
                return;
            }
            if (lobby.getUsersArray().size() < 2) {
                dataOut.writeUTF("noPlayers");
                return;
            }

            boolean success  = lobby.startGame();

            if (!success){
                dataOut.writeUTF("noSuccess");
                return;
            }
            dataOut.writeUTF("success");

            dataOut.writeInt(lobby.getUsersArray().size());
            for (User u : lobby.getUsersArray()){
                dataOut.writeUTF(u.getNickname());
            }
            String currentPlayerLogin = lobby.getGameManager().getCurrentPlayerLogin();
            // get the current player's nickname
            String currentPlayerNickname = lobby.getUsers().get(currentPlayerLogin).getNickname();
            dataOut.writeUTF(currentPlayerNickname);

            ArrayList<Integer> listOfListeners = lobby.getListenersIDsCopy();
            listOfListeners.remove((Integer) ClientID);

            listOfListeners.forEach(listenerID -> ServerMainNew.notifyClientGameStarted(listenerID, lobby));
        }
    }

    public static class StopLobbyListenerCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut, int ClientID) throws IOException {
            dataOut.writeUTF("stopListener");
        }
    }

    public static class RollDiceCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut, int ClientID) throws IOException {
            String lobbyName = dataIn.readUTF();

            dataOut.writeUTF("rollDice");

            Lobby lobby = ServerMainNew.lobbies.getLobbyByName(lobbyName);
            Pair<Integer, Integer> currentRoll = lobby.getGameManager().getCurrentPlayer().rollDice();
            Pair<Double, Double> newLocation = lobby.getGameManager().getCurrentPlayer()
                    .moveAmountRolled(currentRoll.getKey() + currentRoll.getValue());

            int currentPlayerID = lobby.getGameManager().getCurrentPlayer().getPlayerID();
            boolean isInJail = lobby.getGameManager().getCurrentPlayer().isInJail();
            int newSpace = lobby.getGameManager().getCurrentPlayer().getCurrentSpace();
            dataOut.writeInt(currentRoll.getKey());
            dataOut.writeInt(currentRoll.getValue());
            dataOut.writeDouble(newLocation.getKey());
            dataOut.writeDouble(newLocation.getValue());
            dataOut.writeInt(currentPlayerID);
            dataOut.writeInt(newSpace);
            dataOut.writeBoolean(isInJail);
            dataOut.flush();

            ArrayList<Integer> listOfListeners = lobby.getListenersIDsCopy();

            listOfListeners.forEach(listenerID -> ServerMainNew.notifyClientPlayerMoved(listenerID, newLocation, currentPlayerID));
        }
    }

    public static class EndTurnCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut, int ClientID) throws IOException {
            String lobbyName = dataIn.readUTF();

            dataOut.writeUTF("endTurn");

            Lobby lobby = ServerMainNew.lobbies.getLobbyByName(lobbyName);

            if(!lobby.getGameManager().nextTurn()) {
                dataOut.writeBoolean(false);
                return;
            }
            dataOut.writeBoolean(true);

            String currentPlayerLogin = lobby.getGameManager().getCurrentPlayerLogin();
            String currentPlayerNickname = lobby.getUsers().get(currentPlayerLogin).getNickname();
            dataOut.writeUTF(currentPlayerNickname);

            ArrayList<Integer> listOfListeners = lobby.getListenersIDsCopy();

            listOfListeners.forEach(listenerID -> ServerMainNew.notifyClientNextTurn(listenerID, lobby));
        }
    }

    public int getClientID() {
        return clientID;
    }
}
