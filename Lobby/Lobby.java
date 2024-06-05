package Lobby;

import Server.ServerMainNew;
import User.User;
import Server.GameManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class Lobby implements Serializable {
    private static final long serialVersionUID = 1332257598815134L;
    private ArrayList<User> users;
    private ArrayList<Integer> listenersIDs;
    private User owner;
    private String lobbyName;
    private int maxUsers;
    private GameManager gameManager;
    private boolean gameStarted;

    public Lobby(User owner, int listenerID, String lobbyName, int maxUsers) {
        this.owner = owner;
        this.lobbyName = lobbyName;
        this.maxUsers = maxUsers;
        this.users = new ArrayList<>();
        this.listenersIDs = new ArrayList<>();
        this.gameStarted = false;
        this.gameManager = null;
        users.add(owner);
        listenersIDs.add(listenerID);
    }

    public boolean addUser(User user, int listener) {
        if (users.size() < maxUsers) {
            users.add(user);
            return true;
        } else {
            return false;
        }
    }

    public void addListener(int listener) {
        listenersIDs.add(listener);
    }

    public boolean removePlayer(User user) {
        boolean removed = users.remove(user);
        if (removed && user.equals(owner)) {
            changeOwner();
        }
        return removed;
    }

    public void removeListener(int listener) {
        listenersIDs.remove(listener);
    }

    public void notifyListeners() {
        for (int listenerID : listenersIDs) {
            ServerMainNew.notifyClient(listenerID, this);
        }
    }

    private void changeOwner() {
        if (users.isEmpty()) {
            owner = null; // No owner if there are no players
        } else {
            owner = users.getFirst(); // Set the next player as the new owner
        }
    }

    public boolean isEmpty() {
        return users.isEmpty();
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public User getOwner() {
        return owner;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    static public class Lobbies {
        public HashMap<String, Lobby> lobbies = new HashMap<>();

        public Lobbies(){};

        public boolean createLobby(String lobbyName, int listenerID, int maxPlayers, User owner) {
            if (lobbies.containsKey(lobbyName)) {
                return false;
            }
            lobbies.put(lobbyName, new Lobby(owner, listenerID, lobbyName, maxPlayers));
            return true;
        }

        public boolean createLobby(String lobbyName, Lobby lobby) {
            if (lobbies.containsKey(lobbyName)) {
                return false;
            }
            lobbies.put(lobbyName, lobby);
            return true;
        }

        public Lobby getLobbyByName(String lobbyName) {
            return lobbies.get(lobbyName);
        }

        public void removeLobby(String lobbyName) {
            lobbies.remove(lobbyName);
        }

        public ArrayList<String> getLobbiesList() {
            return new ArrayList<>(lobbies.keySet());
        }


    }
}
