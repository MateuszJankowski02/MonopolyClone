package Lobby;

import Server.ServerMainNew;
import User.User;
import Server.GameManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class Lobby implements Serializable {
    private static final long serialVersionUID = 1332257598815134L;
    private HashMap<String, User> users;
    private ArrayList<Integer> listenersIDs;
    private User owner;
    private String lobbyName;
    private int maxUsers;
    private GameManager gameManager;
    private boolean isGameStarted;

    public Lobby(User owner, int listenerID, String lobbyName, int maxUsers) {
        this.owner = owner;
        this.lobbyName = lobbyName;
        this.maxUsers = maxUsers;
        this.users = new HashMap<String, User>();
        this.listenersIDs = new ArrayList<>();
        this.gameManager = null;
        this.isGameStarted = false;
        users.put(owner.getLogin(), owner);
        listenersIDs.add(listenerID);
    }

    public boolean startGame(){
        ArrayList<String> usersLogins = new ArrayList<>(users.keySet());
        try {
            gameManager = new GameManager(usersLogins);
            isGameStarted = true;
            return true;
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean addUser(User user) {
        if (users.size() < maxUsers) {
            users.put(user.getLogin(), user);
            return true;
        } else {
            return false;
        }
    }

    public void addListener(int listener) {
        listenersIDs.add(listener);
    }

    public boolean removePlayer(User user) {
        User removedUser = users.remove(user.getLogin());
        if (removedUser == null) return false;

        if (user.getLogin().equals(owner.getLogin())) {
            changeOwner();
        }
        return true;
    }

    public void removeListener(int listener) {
        listenersIDs.remove((Integer) listener);
    }

    public ArrayList<Integer> getListenersIDs() {
        return listenersIDs;
    }

    public ArrayList<Integer> getListenersIDsCopy() {
        return new ArrayList<>(listenersIDs);
    }

    private void changeOwner() {
        if (users.isEmpty()) {
            owner = null; // No owner if there are no players
        } else {
            // get the first element in hashmap
            owner = users.get(users.keySet().iterator().next());
        }
    }

    public boolean isEmpty() {
        return users.isEmpty();
    }

    public ArrayList<User> getUsersArray() {
        return new ArrayList<>(users.values());
    }

    public HashMap<String, User> getUsers() {
        return users;
    }

    public String getOwnerLogin() {
        return owner.getLogin();
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    @Override
    public String toString() {
        return "Name: " + lobbyName + ", Owner: " + owner.getNickname() + ", Players: " + users.size() + "/" + maxUsers + ", Game started: " + isGameStarted;
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
            ArrayList<String> lobbiesList = new ArrayList<>();
            for (Lobby lobby : lobbies.values()) {
                lobbiesList.add(lobby.toString());
            }
            return lobbiesList;
        }
    }
}
