package Lobby;

import Login.User;
import java.util.ArrayList;

public class Lobby {
    private ArrayList<User> players;
    private User owner;
    private String lobbyName;
    private int maxPlayers;
    private boolean gameStarted;

    public Lobby(User owner, String lobbyName, int maxPlayers) {
        this.owner = owner;
        this.lobbyName = lobbyName;
        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>();
        this.players.add(owner);
        this.gameStarted = false;
    }

    public boolean addPlayer(User player) {
        if (players.size() < maxPlayers) {
            players.add(player);
            return true;
        } else {
            return false;
        }
    }

    public boolean removePlayer(User player) {
        boolean removed = players.remove(player);
        if (removed && player.equals(owner)) {
            changeOwner();
        }
        return removed;
    }

    private void changeOwner() {
        if (players.isEmpty()) {
            owner = null; // No owner if there are no players
        } else {
            owner = players.getFirst(); // Set the next player as the new owner
        }
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

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

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
}
