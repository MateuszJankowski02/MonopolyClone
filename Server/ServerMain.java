package Server;

import Login.Login;
import Login.Register;
import Login.User;
import Lobby.Lobby;
import Utilities.Player;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
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
        try (ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream())) {

            while (true) {
                String commandKey = (String) objectIn.readObject();
                Command command = commandMap.get(commandKey);
                if (command != null) {
                    command.execute(objectIn, objectOut);
                } else {
                    objectOut.writeObject("Unknown command");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
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
        commandMap.put("getLobbyPlayers", new GetLobbyPlayersCommand());
        commandMap.put("leaveLobby", new LeaveLobbyCommand());
        commandMap.put("startGame", new StartGameCommand());
        commandMap.put("checkGameState", new CheckGameStateCommand());
    }

    interface Command {
        void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut) throws IOException, ClassNotFoundException;
    }

    static class LoginCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut) throws IOException, ClassNotFoundException {
            String username = (String) objectIn.readObject();
            String password = (String) objectIn.readObject();
            System.out.println("User " + username + " is trying to log in with password " + password);

            usersLock.readLock().lock();
            try {
                if (Login.loginUser(username, password) != null) {
                    System.out.println("User " + username + " logged in successfully");
                    objectOut.writeObject(true);
                } else {
                    System.out.println("User " + username + " failed to log in");
                    objectOut.writeObject(false);
                }
            } finally {
                usersLock.readLock().unlock();
            }
        }
    }

    static class RegisterCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut) throws IOException, ClassNotFoundException {
            String nicknameInput = (String) objectIn.readObject();
            String loginInput = (String) objectIn.readObject();
            String passwordInput = (String) objectIn.readObject();

            usersLock.writeLock().lock();
            try {
                if (Register.checkLogin(loginInput)) {
                    objectOut.writeObject("Login already exists");
                } else {
                    Register.registerUser(nicknameInput, loginInput, passwordInput);
                    objectOut.writeObject("Registration successful");
                    users.refresh();
                }
            } finally {
                usersLock.writeLock().unlock();
            }
        }
    }

    static class CreateLobbyCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut) throws IOException, ClassNotFoundException {
            String lobbyNameCreate = (String) objectIn.readObject();
            System.out.println("lobbyNameCreate: " + lobbyNameCreate);
            int maxPlayers = (int) objectIn.readObject();
            if (maxPlayers < 2 || maxPlayers > 4) {
                objectOut.writeObject("Invalid number of players");
                objectOut.writeObject(false);
                return;
            }
            User owner = users.getUserById((int) objectIn.readObject());
            lobbiesLock.writeLock().lock();
            try {
                Lobby lobbyCreate = new Lobby(owner, lobbyNameCreate, maxPlayers);
                lobbies.put(lobbyNameCreate, lobbyCreate);
                objectOut.writeObject("Lobby created successfully");
                objectOut.writeObject(true);
            } finally {
                lobbiesLock.writeLock().unlock();
            }
        }
    }

    static class JoinLobbyCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut) throws IOException, ClassNotFoundException {
            String lobbyNameJoin = (String) objectIn.readObject();
            User player = users.getUserById((int) objectIn.readObject());
            lobbiesLock.writeLock().lock();
            try {
                Lobby lobbyJoin = lobbies.get(lobbyNameJoin);
                if (lobbyJoin == null) {
                    objectOut.writeObject("Lobby does not exist");
                    objectOut.writeObject(false);
                } else if (lobbyJoin.getPlayers().size() < lobbyJoin.getMaxPlayers()) {
                    lobbyJoin.addPlayer(player);
                    objectOut.writeObject("Joined lobby");
                    objectOut.writeObject(true);
                } else {
                    objectOut.writeObject("Lobby is full");
                    objectOut.writeObject(false);
                }
            } finally {
                lobbiesLock.writeLock().unlock();
            }
        }
    }

    static class ListLobbiesCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut) throws IOException {
            lobbiesLock.readLock().lock();
            try {
                objectOut.writeObject(lobbies.size());
                for (String lobbyName : lobbies.keySet()) {
                    Lobby lobby = lobbies.get(lobbyName);
                    objectOut.writeObject(lobby.getLobbyName());
                    objectOut.writeObject(String.valueOf(lobby.getPlayers().size()));
                    objectOut.writeObject(String.valueOf(lobby.getMaxPlayers()));
                    objectOut.writeObject(lobby.getOwner().getNickname());
                }
            } finally {
                lobbiesLock.readLock().unlock();
            }
        }
    }

    static class GetLobbyPlayersCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut) throws IOException, ClassNotFoundException {
            String lobbyName = (String) objectIn.readObject();
            lobbiesLock.readLock().lock();
            try {
                Lobby lobby = lobbies.get(lobbyName);
                if (lobby != null) {
                    objectOut.writeObject(lobby.getPlayers().size());
                    for (User player : lobby.getPlayers()) {
                        objectOut.writeObject(player.getNickname());
                    }
                } else {
                    objectOut.writeObject(0);
                }
            } finally {
                lobbiesLock.readLock().unlock();
            }
        }
    }

    static class LeaveLobbyCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut) throws IOException, ClassNotFoundException {
            int userId = (int) objectIn.readObject();
            usersLock.readLock().lock();
            try {
                User user = users.getUserById(userId);
                if (user != null) {
                    lobbiesLock.writeLock().lock();
                    try {
                        for (String lobbyName : lobbies.keySet()) {
                            Lobby lobby = lobbies.get(lobbyName);
                            if (lobby.removePlayer(user)) {
                                if (lobby.isEmpty()) {
                                    lobbies.remove(lobbyName);
                                    objectOut.writeObject("Left lobby and lobby deleted");
                                } else {
                                    objectOut.writeObject("Left lobby successfully");
                                }
                                objectOut.writeObject(true);
                                return;
                            }
                        }
                        objectOut.writeObject("User not in any lobby");
                        objectOut.writeObject(false);
                    } finally {
                        lobbiesLock.writeLock().unlock();
                    }
                } else {
                    objectOut.writeObject("User not found");
                    objectOut.writeObject(false);
                }
            } finally {
                usersLock.readLock().unlock();
            }
        }
    }

    public static class StartGameCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut) throws IOException, ClassNotFoundException {
            String lobbyName = (String) objectIn.readObject();
            User player = users.getUserById((int) objectIn.readObject());

            lobbiesLock.writeLock().lock();
            try {
                Lobby lobby = lobbies.get(lobbyName);
                if (lobby == null) {
                    objectOut.writeObject("Lobby does not exist");
                    objectOut.writeObject(false);
                } else if (!lobby.getOwner().equals(player)) {
                    objectOut.writeObject("Only the lobby owner can start the game");
                    objectOut.writeObject(false);
                } else if (lobby.getPlayers().size() < 2) {
                    objectOut.writeObject("Not enough players to start the game");
                    objectOut.writeObject(false);
                } else {
                    ArrayList<Player> players = new ArrayList<>();
                    for (User user : lobby.getPlayers()) {
                        players.add(new Player(user));
                    }
                    GameManager gameManager = new GameManager(players);  // Assuming players is already initialized

                    // Set gameStarted flag to true
                    lobby.setGameStarted(true);
                    lobby.setGameManager(gameManager);

                    objectOut.writeObject("Game started successfully");
                    objectOut.writeObject(true);
                    objectOut.writeObject(gameManager);
                }
            } finally {
                lobbiesLock.writeLock().unlock();
            }
        }
    }

    public static class CheckGameStateCommand implements Command {
        @Override
        public void execute(ObjectInputStream objectIn, ObjectOutputStream objectOut) throws IOException, ClassNotFoundException {
            String lobbyName = (String) objectIn.readObject();
            lobbiesLock.readLock().lock();
            try {
                Lobby lobby = lobbies.get(lobbyName);
                if (lobby != null) {
                    objectOut.writeObject(lobby.isGameStarted());
                    if (lobby.isGameStarted()) {
                        objectOut.writeObject(lobby.getGameManager());
                    }
                } else {
                    objectOut.writeObject(false);
                }
            } finally {
                lobbiesLock.readLock().unlock();
            }
        }
    }
}
