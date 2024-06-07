package Server;


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
        while (true)
        {
            try
            {
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
            } catch (SocketException e) {
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
        commandMap.put("getLobbyPlayers", new GetLobbyPlayersCommand());
        commandMap.put("leaveLobby", new LeaveLobbyCommand());
        commandMap.put("startGame", new StartGameCommand());
        commandMap.put("checkGameState", new CheckGameStateCommand());
        commandMap.put("stopListener", new StopListenerCommand());
    }

    public void notifyListenersRefreshLobbyUsers(Lobby lobby){
        try{
            System.out.println("Notifying listeners to refresh lobby users");
            ArrayList<String> listOfUsers = new ArrayList<>();
            lobby.getUsers().forEach(user -> listOfUsers.add(user.getNickname()));
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

    public void notifyListenersStartGame(){
        try{
            dataOut.writeUTF("startGame");
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

            createdLobby.getUsers().forEach(user -> createdLobbyUsers.add(user.getNickname()));
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
            lobby.notifyListeners();
            lobby.addListener(ClientID);

            ArrayList<String> joinedLobbyUsers = new ArrayList<>();
            lobby.getUsers().forEach(user -> joinedLobbyUsers.add(user.getNickname()));
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
        }
    }

    static class GetLobbyPlayersCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut, int ClientID) throws IOException {

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
            lobby.notifyListeners();
            dataOut.writeBoolean(true);

            if (lobby.getUsers().isEmpty()){
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

            boolean success  = lobby.startGame();
        }
    }

    public static class CheckGameStateCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut, int ClientID) throws IOException {

        }
    }

    public static class StopListenerCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut, int ClientID) throws IOException {
            dataOut.writeUTF("stopListener");
        }
    }

    public int getClientID() {
        return clientID;
    }
}
