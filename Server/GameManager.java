package Server;

import BoardSpaces.*;
import Utilities.Player;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class GameManager {

    /*
    start game ->
    notify listeners that game started ->
    start new GameManager for chosen lobby ->
    create 'game scene' for all clients in the lobby that gets data from thats lobbys GameManager
     */

    private static int gameID = 0;
    private HashMap<String, Player> players;
    private ArrayList<Board> boardSpaces;
    private ArrayList<StreetSet> streetSets;
    private String currentPlayerLogin;
    private Player currentPlayer;

    public GameManager(ArrayList<String> usersLogins) {
        this.players = new HashMap<>();
        this.boardSpaces = new ArrayList<>();
        this.streetSets = new ArrayList<>();

        for(String login : usersLogins){
            players.put(login, new Player(usersLogins.size()));
        }
        if (players.size() < 2 || players.size() > 4) throw new IllegalArgumentException("Invalid number of players");

        //random number from 0 to players.size() - 1
        int randomPlayer = (int) (Math.random() * players.size());

        currentPlayerLogin = usersLogins.get(randomPlayer);
        currentPlayer = players.get(currentPlayerLogin);

        gameID++;
    }

    public boolean nextTurn() {
        if (!currentPlayer.hasRolled()) return false;

        ArrayList<String> playerLogins = new ArrayList<>(players.keySet());
        int currentIndex = playerLogins.indexOf(currentPlayerLogin);
        int nextIndex = (currentIndex + 1) % playerLogins.size();

        currentPlayerLogin = playerLogins.get(nextIndex);
        currentPlayer = players.get(currentPlayerLogin);

        System.out.println("Current player: " + currentPlayerLogin);
        return true;
    }

    public Pair<String, Integer> handleSpaceEvent(int currentSpaceID, Player player) {
        if (boardSpaces.get(player.getCurrentSpace()).getType() == SpaceType.STREET ||
            boardSpaces.get(player.getCurrentSpace()).getType() == SpaceType.RAILROAD ||
            boardSpaces.get(player.getCurrentSpace()).getType() == SpaceType.UTILITY) {

            return handlePropertyEvent(currentSpaceID, player);
        } else {
            return handleEvent(currentSpaceID, player);
        }
    }

    public Pair<String, Integer> handlePropertyEvent(int currentSpaceID, Player player) {
        if (boardSpaces.get(currentSpaceID).getType() == SpaceType.STREET) {
            BoardSpaceStreet street = (BoardSpaceStreet) boardSpaces.get(currentSpaceID);
            if (street.getOwner() == null) {
                if (player.getMoney() >= street.getBuyoutCost()) {
                    player.buyProperty(currentSpaceID, street.getBuyoutCost());
                    street.setOwner(player);
                    return new Pair<>("buyStreet", street.getBuyoutCost());
                } else {
                    return new Pair<>("noBuyStreet", 0);
                }
            } else if (street.getOwner() != player) {
                int rent = street.getRent();
                player.addMoney(-rent);
                street.getOwner().addMoney(rent);
                if (player.getMoney() < 0) {
                    return new Pair<>("bankrupt", 0);
                }
                return new Pair<>("payRent", rent);
            } else {
                return new Pair<>("ownProperty", 0);
            }
        } else if (boardSpaces.get(currentSpaceID).getType() == SpaceType.RAILROAD) {
            BoardSpaceRailroad railroad = (BoardSpaceRailroad) boardSpaces.get(currentSpaceID);
            if (railroad.getOwner() == null) {
                if (player.getMoney() >= railroad.getBuyoutCost()) {
                    player.buyProperty(currentSpaceID, railroad.getBuyoutCost());
                    railroad.setOwner(player);
                    return new Pair<>("buyRailroad", railroad.getBuyoutCost());
                } else {
                    return new Pair<>("noBuyRailroad", 0);
                }
            } else if (railroad.getOwner() != player) {
                int rent = railroad.getRent();
                player.addMoney(-rent);
                railroad.getOwner().addMoney(rent);
                if (player.getMoney() < 0) {
                    return new Pair<>("bankrupt", 0);
                }
                return new Pair<>("payRent", rent);
            } else {
                return new Pair<>("ownProperty", 0);
            }
        } else {
            BoardSpaceUtility utility = (BoardSpaceUtility) boardSpaces.get(currentSpaceID);
            if (utility.getOwner() == null) {
                if (player.getMoney() >= utility.getBuyoutCost()) {
                    player.buyProperty(currentSpaceID, utility.getBuyoutCost());
                    utility.setOwner(player);
                    return new Pair<>("buyUtility", utility.getBuyoutCost());
                } else {
                    return new Pair<>("noBuyUtility", 0);
                }
            } else if (utility.getOwner() != player) {
                int rent = utility.getRent(player.getCurrentRoll(), 0);
                player.addMoney(-rent);
                utility.getOwner().addMoney(rent);
                if (player.getMoney() < 0) {
                    return new Pair<>("bankrupt", 0);
                }
                return new Pair<>("payRent", rent);
            } else {
                return new Pair<>("ownProperty", 0);
            }
        }
    }

    public Pair<String, Integer> handleEvent(int currentSpaceID, Player player) {
        if (boardSpaces.get(currentSpaceID).getType() == SpaceType.EVENT) {
            BoardSpaceEvent event = (BoardSpaceEvent) boardSpaces.get(currentSpaceID);
            switch (event.getSpaceEventType()) {
                case CHEST:
                    return new Pair<>("chest", 0);
                case INCOME_TAX:
                    player.addMoney(-60);
                    return new Pair<>("incomeTax", 60);
                case CHANCE:
                    return new Pair<>("chance", 0);
                case GO_TO_JAIL:
                    player.goToJail();
                    return new Pair<>("goToJail", 0);
                case LUXURY_TAX:
                    player.addMoney(-100);
                    return new Pair<>("luxuryTax", 100);
                default:
                    return new Pair<>("error", 0);
            }
        } else {
            return new Pair<>("error", 0);
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerLogin);
    }

    public String getCurrentPlayerLogin() {
        return currentPlayerLogin;
    }

    public HashMap<String, Player> getPlayers() {
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
        boardSpaces.add(new BoardSpaceRailroad(
                new BoardSpace(6, "Reading Railroad", SpaceType.RAILROAD),
                200,
                new ArrayList<>(Arrays.asList(25, 50, 100, 200))
        ));
        // Space 6
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(7, "Oriental Avenue", SpaceType.STREET),
                streetSets.get(1),
                100,
                streetSets.get(1).getHouseCost(),
                new ArrayList<>(Arrays.asList(6, 30, 90, 270, 400, 550))
        ));
        // Space 7
        boardSpaces.add(new BoardSpaceEvent(
                new BoardSpace(8, "Chance", SpaceType.EVENT),
                SpaceEventType.CHANCE
        ));
        // Space 8
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(9, "Vermont Avenue", SpaceType.STREET),
                streetSets.get(1),
                100,
                streetSets.get(1).getHouseCost(),
                new ArrayList<>(Arrays.asList(6, 30, 90, 270, 400, 550))
        ));
        // Space 9
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(10, "Connecticut Avenue", SpaceType.STREET),
                streetSets.get(1),
                120,
                streetSets.get(1).getHouseCost(),
                new ArrayList<>(Arrays.asList(8, 40, 100, 300, 450, 600))
        ));
        // Space 10
        boardSpaces.add(new BoardSpaceEvent(
                new BoardSpace(11, "Jail", SpaceType.EVENT),
                SpaceEventType.JAIL
        ));
        // Space 11
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(12, "St. Charles Place", SpaceType.STREET),
                streetSets.get(2),
                140,
                streetSets.get(2).getHouseCost(),
                new ArrayList<>(Arrays.asList(10, 50, 150, 450, 625, 750))
        ));
        // Space 12
        boardSpaces.add(new BoardSpaceUtility(
                new BoardSpace(13, "Electric Company", SpaceType.UTILITY),
                150,
                new ArrayList<>(Arrays.asList(4, 10))
        ));
        // Space 13
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(14, "States Avenue", SpaceType.STREET),
                streetSets.get(2),
                140,
                streetSets.get(2).getHouseCost(),
                new ArrayList<>(Arrays.asList(10, 50, 150, 450, 625, 750))
        ));
        // Space 14
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(15, "Virginia Avenue", SpaceType.STREET),
                streetSets.get(2),
                160,
                streetSets.get(2).getHouseCost(),
                new ArrayList<>(Arrays.asList(12, 60, 180, 500, 700, 900))
        ));
        // Space 15
        boardSpaces.add(new BoardSpaceRailroad(
                new BoardSpace(16, "Pennsylvania Railroad", SpaceType.RAILROAD),
                200,
                new ArrayList<>(Arrays.asList(25, 50, 100, 200))
        ));
        // Space 16
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(17, "St. James Place", SpaceType.STREET),
                streetSets.get(3),
                180,
                streetSets.get(3).getHouseCost(),
                new ArrayList<>(Arrays.asList(14, 70, 200, 550, 750, 950))
        ));
        // Space 17
        boardSpaces.add(new BoardSpaceEvent(
                new BoardSpace(18, "Community Chest", SpaceType.EVENT),
                SpaceEventType.CHEST
        ));
        // Space 18
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(19, "Tennessee Avenue", SpaceType.STREET),
                streetSets.get(3),
                180,
                streetSets.get(3).getHouseCost(),
                new ArrayList<>(Arrays.asList(14, 70, 200, 550, 750, 950))
        ));
        // Space 19
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(20, "New York Avenue", SpaceType.STREET),
                streetSets.get(3),
                200,
                streetSets.get(3).getHouseCost(),
                new ArrayList<>(Arrays.asList(16, 80, 220, 600, 800, 1000))
        ));
        // Space 20
        boardSpaces.add(new BoardSpaceEvent(
                new BoardSpace(21, "Free Parking", SpaceType.EVENT),
                SpaceEventType.FREE_PARKING
        ));
        // Space 21
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(22, "Kentucky Avenue", SpaceType.STREET),
                streetSets.get(4),
                220,
                streetSets.get(4).getHouseCost(),
                new ArrayList<>(Arrays.asList(18, 90, 250, 700, 875, 1050))
        ));
        // Space 22
        boardSpaces.add(new BoardSpaceEvent(
                new BoardSpace(23, "Chance", SpaceType.EVENT),
                SpaceEventType.CHANCE
        ));
        // Space 23
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(24, "Indiana Avenue", SpaceType.STREET),
                streetSets.get(4),
                220,
                streetSets.get(4).getHouseCost(),
                new ArrayList<>(Arrays.asList(18, 90, 250, 700, 875, 1050))
        ));
        // Space 24
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(25, "Illinois Avenue", SpaceType.STREET),
                streetSets.get(4),
                240,
                streetSets.get(4).getHouseCost(),
                new ArrayList<>(Arrays.asList(20, 100, 300, 750, 925, 1100))
        ));
        // Space 25
        boardSpaces.add(new BoardSpaceRailroad(
                new BoardSpace(26, "B. & O. Railroad", SpaceType.RAILROAD),
                200,
                new ArrayList<>(Arrays.asList(25, 50, 100, 200))
        ));
        // Space 26
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(27, "Atlantic Avenue", SpaceType.STREET),
                streetSets.get(5),
                260,
                streetSets.get(5).getHouseCost(),
                new ArrayList<>(Arrays.asList(22, 110, 330, 800, 975, 1150))
        ));
        // Space 27
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(28, "Ventnor Avenue", SpaceType.STREET),
                streetSets.get(5),
                260,
                streetSets.get(5).getHouseCost(),
                new ArrayList<>(Arrays.asList(22, 110, 330, 800, 975, 1150))
        ));
        // Space 28
        boardSpaces.add(new BoardSpaceUtility(
                new BoardSpace(29, "Water Works", SpaceType.UTILITY),
                150,
                new ArrayList<>(Arrays.asList(4, 10))
        ));
        // Space 29
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(30, "Marvin Gardens", SpaceType.STREET),
                streetSets.get(5),
                280,
                streetSets.get(5).getHouseCost(),
                new ArrayList<>(Arrays.asList(24, 120, 360, 850, 1025, 1200))
        ));
        // Space 30
        boardSpaces.add(new BoardSpaceEvent(
                new BoardSpace(31, "Go to Jail", SpaceType.EVENT),
                SpaceEventType.GO_TO_JAIL
        ));
        // Space 31
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(32, "Pacific Avenue", SpaceType.STREET),
                streetSets.get(6),
                300,
                streetSets.get(6).getHouseCost(),
                new ArrayList<>(Arrays.asList(26, 130, 390, 900, 1100, 1275))
        ));
        // Space 32
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(33, "North Carolina Avenue", SpaceType.STREET),
                streetSets.get(6),
                300,
                streetSets.get(6).getHouseCost(),
                new ArrayList<>(Arrays.asList(26, 130, 390, 900, 1100, 1275))
        ));
        // Space 33
        boardSpaces.add(new BoardSpaceEvent(
                new BoardSpace(34, "Community Chest", SpaceType.EVENT),
                SpaceEventType.CHEST
        ));
        // Space 34
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(35, "Pennsylvania Avenue", SpaceType.STREET),
                streetSets.get(6),
                320,
                streetSets.get(6).getHouseCost(),
                new ArrayList<>(Arrays.asList(28, 150, 450, 1000, 1200, 1400))
        ));
        // Space 35
        boardSpaces.add(new BoardSpaceRailroad(
                new BoardSpace(36, "Short Line", SpaceType.RAILROAD),
                200,
                new ArrayList<>(Arrays.asList(25, 50, 100, 200))
        ));
        // Space 36
        boardSpaces.add(new BoardSpaceEvent(
                new BoardSpace(37, "Luxury Tax", SpaceType.EVENT),
                SpaceEventType.LUXURY_TAX
        ));
        // Space 37
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(38, "Park Place", SpaceType.STREET),
                streetSets.get(7),
                350,
                streetSets.get(7).getHouseCost(),
                new ArrayList<>(Arrays.asList(35, 175, 500, 1100, 1300, 1500))
        ));
        // Space 38
        boardSpaces.add(new BoardSpaceEvent(
                new BoardSpace(39, "Chance", SpaceType.EVENT),
                SpaceEventType.CHANCE
        ));
        // Space 39
        boardSpaces.add(new BoardSpaceStreet(
                new BoardSpace(40, "Boardwalk", SpaceType.STREET),
                streetSets.get(7),
                400,
                streetSets.get(7).getHouseCost(),
                new ArrayList<>(Arrays.asList(50, 200, 600, 1400, 1700, 2000))
        ));
    }

    public void startGame() {
        populateStreets();
        populateSpaces();
    }

    public int getGameID() {
        return gameID;
    }

}
