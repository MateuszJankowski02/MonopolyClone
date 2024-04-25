package Server;

import BoardSpaces.*;
import Utilities.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class GameManager {
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Board> boardSpaces = new ArrayList<>();
    private ArrayList<StreetSet> streetSets = new ArrayList<>();
    private int currentPlayerIndex = 0;

    public GameManager(ArrayList<Player> players) {
        if (players.size() < 2 || players.size() > 4) throw new IllegalArgumentException("Invalid number of players");
        this.players = players;
    }

    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    private void populateStreets() {
        streetSets.add(new StreetSet("Brown", 2, 50));
        streetSets.add(new StreetSet("Light Blue", 3, 50));
        streetSets.add(new StreetSet("Purple", 3, 100));
        streetSets.add(new StreetSet("Orange", 3, 100));
        streetSets.add(new StreetSet("Red", 3, 150));
        streetSets.add(new StreetSet("Yellow", 3, 150));
        streetSets.add(new StreetSet("Green", 3, 200));
        streetSets.add(new StreetSet("Dark Blue", 2, 200));
    }

    private void populateSpaces() {
        // Space 0
        boardSpaces.add(new BoardSpaceEvent(
                new BoardSpace(1, "Go", SpaceType.EVENT),
                SpaceEventType.GO
        ));
        // Space 1
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(2, "Mediterranean Avenue", SpaceType.STREET),
                streetSets.get(0),
                60,
                streetSets.get(0).getHouseCost(),
                new ArrayList<>(Arrays.asList(2, 10, 30, 90, 160, 250))
        ));
        // Space 2
        boardSpaces.add(new BoardSpaceEvent(
                new BoardSpace(3, "Community Chest", SpaceType.EVENT),
                SpaceEventType.CHEST
        ));
        // Space 3
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(4, "Baltic Avenue", SpaceType.STREET),
                streetSets.get(0),
                60,
                streetSets.get(0).getHouseCost(),
                new ArrayList<>(Arrays.asList(4, 20, 60, 180, 320, 450))
        ));
        // Space 4
        boardSpaces.add(new BoardSpaceEvent(
                new BoardSpace(5, "Income Tax", SpaceType.EVENT),
                SpaceEventType.INCOME_TAX
        ));
        // Space 5
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(6, "Reading Railroad", SpaceType.STREET),
                streetSets.get(0),
                200,
                streetSets.get(0).getHouseCost(),
                new ArrayList<>(Arrays.asList(25, 50, 100, 200))
        ));


    }

    public void populateBoard() {
        // Populate boardSpaces with all the spaces
    }
    public void startGame() {

    }
}
