package Server;

import Login.Login;
import Login.Register;
import Login.User;
import Lobby.Lobby;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServerMain {
    public static User.Users users = new User.Users();
    static HashMap<String, Lobby> lobbies = new HashMap<>();
    private static final ReentrantReadWriteLock lobbiesLock = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock usersLock = new ReentrantReadWriteLock();
    private static final Map<String, Command> commandMap = new HashMap<>();

    public static void main(String[] args) {
        initializeCommands();
        try (ServerSocket server = new ServerSocket(8080)) {
            System.out.println("Server started on port 8080");
            while (true) {
                Socket socket = server.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (DataInputStream dataIn = new DataInputStream(socket.getInputStream());
             DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream())) {

            while (true) {
                String commandKey = dataIn.readUTF();
                Command command = commandMap.get(commandKey);
                if (command != null) {
                    command.execute(dataIn, dataOut);
                } else {
                    dataOut.writeUTF("Unknown command");
                }
            }
        } catch (IOException e) {
            System.err.println("Client handler exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeCommands() {
        commandMap.put("login", new LoginCommand());
        commandMap.put("register", new RegisterCommand());
        commandMap.put("createLobby", new CreateLobbyCommand());
        commandMap.put("joinLobby", new JoinLobbyCommand());
        commandMap.put("listLobbies", new ListLobbiesCommand());
    }

    interface Command {
        void execute(DataInputStream dataIn, DataOutputStream dataOut) throws IOException;
    }

    static class LoginCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut) throws IOException {
            String username = dataIn.readUTF();
            String password = dataIn.readUTF();
            System.out.println("User " + username + " is trying to log in with password " + password);

            usersLock.readLock().lock();
            try {
                if (Login.loginUser(username, password) != null) {
                    System.out.println("User " + username + " logged in successfully");
                    dataOut.writeBoolean(true);
                } else {
                    System.out.println("User " + username + " failed to log in");
                    dataOut.writeBoolean(false);
                }
            } finally {
                usersLock.readLock().unlock();
            }
        }
    }

    static class RegisterCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut) throws IOException {
            String nicknameInput = dataIn.readUTF();
            String loginInput = dataIn.readUTF();
            String passwordInput = dataIn.readUTF();
            Register register = new Register();

            usersLock.writeLock().lock();
            try {
                if (register.checkLogin(loginInput)) {
                    dataOut.writeUTF("Login already exists");
                } else {
                    register.registerUser(nicknameInput, loginInput, passwordInput);
                    dataOut.writeUTF("Registration successful");
                    users.refresh();
                }
            } finally {
                usersLock.writeLock().unlock();
            }
        }
    }

    static class CreateLobbyCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut) throws IOException {
            String lobbyNameCreate = dataIn.readUTF();
            int maxPlayers = dataIn.readInt();
            if (maxPlayers < 2 || maxPlayers > 4) {
                dataOut.writeUTF("Invalid number of players");
                dataOut.writeBoolean(false);
                return;
            }
            User owner = users.getUserById(dataIn.readInt());
            lobbiesLock.writeLock().lock();
            try {
                Lobby lobbyCreate = new Lobby(owner, lobbyNameCreate, maxPlayers);
                lobbies.put(lobbyNameCreate, lobbyCreate);
                dataOut.writeUTF("Lobby created successfully");
                dataOut.writeBoolean(true);
            } finally {
                lobbiesLock.writeLock().unlock();
            }
        }
    }

    static class JoinLobbyCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut) throws IOException {
            String lobbyNameJoin = dataIn.readUTF();
            User player = users.getUserById(dataIn.readInt());
            lobbiesLock.writeLock().lock();
            try {
                Lobby lobbyJoin = lobbies.get(lobbyNameJoin);
                if (lobbyJoin == null) {
                    dataOut.writeUTF("Lobby does not exist");
                    dataOut.writeBoolean(false);
                } else if (lobbyJoin.getPlayers().size() < lobbyJoin.getMaxPlayers()) {
                    lobbyJoin.addPlayer(player);
                    dataOut.writeUTF("Joined lobby");
                    dataOut.writeBoolean(true);
                } else {
                    dataOut.writeUTF("Lobby is full");
                    dataOut.writeBoolean(false);
                }
            } finally {
                lobbiesLock.writeLock().unlock();
            }
        }
    }


    static class ListLobbiesCommand implements Command {
        @Override
        public void execute(DataInputStream dataIn, DataOutputStream dataOut) throws IOException {
            lobbiesLock.readLock().lock();
            try {
                dataOut.writeInt(lobbies.size());
                for (String lobbyName : lobbies.keySet()) {
                    Lobby lobby = lobbies.get(lobbyName);
                    dataOut.writeUTF(lobby.getLobbyName());
                    dataOut.writeUTF(String.valueOf(lobby.getPlayers().size()));
                    dataOut.writeUTF(String.valueOf(lobby.getMaxPlayers()));
                    dataOut.writeUTF(lobby.getOwner().getNickname());
                }
            } finally {
                lobbiesLock.readLock().unlock();
            }
        }
    }
}
