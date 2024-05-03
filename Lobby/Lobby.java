package Lobby;

import Login.User;
import java.util.ArrayList;

public class Lobby {
    private ArrayList<User> players;
    private User owner;
    private String lobbyName;
    private int maxPlayers;

    public Lobby(User owner, String lobbyName, int maxPlayers) {
        this.owner = owner;
        this.lobbyName = lobbyName;
        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>();
        this.players.add(owner); // The owner is the first player in the lobby
    }

    public boolean addPlayer(User player) {
        if (players.size() < maxPlayers) {
            players.add(player);
            return true;
        } else {
            return false;
        }
    }

    public void removePlayer(User player) {
        players.remove(player);
    }

    public boolean isFull() {
        return players.size() == maxPlayers;
    }

    // getters and setters
    public ArrayList<User> getPlayers() {
        return players;
    }

    public User getOwner() {
        return owner;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}