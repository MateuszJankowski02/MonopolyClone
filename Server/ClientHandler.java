package Server;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import User.User;
import Lobby.Lobby;

public class ClientHandler implements Runnable {

    private static final Map<String, Command> commandMap = new HashMap<>();
    private int clientID;
    final ObjectInputStream objectIn;
    final ObjectOutputStream objectOut;
    Socket socket;
    boolean isloggedin;

    // constructor
    public ClientHandler(Socket socket, int clientID, ObjectInputStream objectIn, ObjectOutputStream objectOut) {
        this.objectIn      = objectIn;
        this.objectOut     = objectOut;
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
                clientCommand = (String) objectIn.readObject();
                System.out.println("Command received: " + clientCommand);

                // map the command to the appropriate function
                Command command = commandMap.get(clientCommand);
                System.out.println("Command: " + command);

                if (command != null) {
                    // execute the command
                    objectOut.writeObject(true);
                    command.execute(objectIn, objectOut, clientID);
                } else {
                    // if the command is not recognized, send an error message to the client
                    objectOut.writeObject(false);
                }
            } catch (SocketException e) {
                System.out.println("Client " + clientID + " disconnected");
                break;
            } catch (IOException | ClassNotFoundException e) {
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
    }

    public void notifyListenersRefreshLobbyUsers(Lobby lobby){
        try{
            objectOut.writeObject("refreshLobbyUsers");
            objectOut.writeObject(lobby);
            objectOut.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    interface Command {
        void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut, int clientID) throws IOException, ClassNotFoundException;
    }

    static class LoginCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut, int clientID) throws IOException, ClassNotFoundException {
            String username = (String) objectIn.readObject();
            String password = (String) objectIn.readObject();
            System.out.println("Login attempt: " + username + ", " + password);
            System.out.println("Users before:");
            System.out.println(ServerMainNew.users);
            User user = ServerMainNew.users.loginUser(username, password);
            System.out.println("Users after:");
            System.out.println(ServerMainNew.users);
            if (user == null) {
                System.out.println("Login failed");
                objectOut.writeObject(false);
                return;
            }
            objectOut.writeObject(true);
            objectOut.writeObject(user);
            objectOut.flush();
        }
    }

    static class RegisterCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut, int clientID) throws IOException, ClassNotFoundException {
            String username = (String) objectIn.readObject();
            String password = (String) objectIn.readObject();
            String nickname = (String) objectIn.readObject();
            System.out.println("Register attempt: " + username + ", " + password + ", " + nickname);
            String response = ServerMainNew.users.registerUser(username, password, nickname);
            objectOut.writeObject(response);
            objectOut.flush();
        }
    }

    static class CreateLobbyCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut, int clientID) throws IOException, ClassNotFoundException {
            String lobbyName = (String) objectIn.readObject();
            Integer maxPlayers = (Integer) objectIn.readObject();
            User owner = (User) objectIn.readObject();

            boolean response = ServerMainNew.lobbies.createLobby(lobbyName, clientID, maxPlayers, owner);
            System.out.println("Lobby created: " + response);
            objectOut.writeObject(response);
            if (!response) return;
            objectOut.writeObject(ServerMainNew.lobbies.getLobbyByName(lobbyName)); // typecast to Lobby
            objectOut.flush();
        }
    }

    static class JoinLobbyCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut, int ClientID) throws IOException, ClassNotFoundException {

        }
    }

    static class ListLobbiesCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut, int ClientID) throws IOException {
            objectOut.writeObject(ServerMainNew.lobbies.getLobbiesList());
            objectOut.flush();
        }
    }

    static class LogoutCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut, int ClientID) throws IOException, ClassNotFoundException {
            User user = (User) objectIn.readObject();
            boolean response = ServerMainNew.users.logoutUser(user);
            objectOut.writeObject(response);
        }
    }

    static class ExitCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut, int ClientID) throws IOException, ClassNotFoundException {
            User user = (User) objectIn.readObject();
            if(user == null){
                return;
            }
            ServerMainNew.users.logoutUser(user);
        }
    }

    static class GetLobbyPlayersCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut, int ClientID) throws IOException, ClassNotFoundException {

        }
    }

    static class LeaveLobbyCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut, int ClientID) throws IOException, ClassNotFoundException {

        }
    }

    public static class StartGameCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut, int ClientID) throws IOException, ClassNotFoundException {

        }
    }

    public static class CheckGameStateCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut, int ClientID) throws IOException, ClassNotFoundException {

        }
    }

    public int getClientID() {
        return clientID;
    }
}
