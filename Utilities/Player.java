package Utilities;

import BoardSpaces.BoardSpaceStreet;
import User.User;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Player {
    private static int idCounter = 0;
    private int playerID;
    private int money;
    private int currentSpace;
    private boolean inJail;
    private int turnsInJail;
    private int jailCards;
    private boolean bankrupt;
    private int doublesRolled;
    private int currentRoll;
    private boolean hasRolled;
    private HashMap<Integer, Pair<Double,Double>> playerPieceLocations = new HashMap<>();
    private ArrayList<Integer> properties = new ArrayList<>();

    public Player(int amountOfPlayers) {
        this.money = 1500;
        this.currentSpace = 0;
        this.inJail = false;
        this.turnsInJail = 0;
        this.jailCards = 0;
        this.bankrupt = false;
        this.doublesRolled = 0;
        this.currentRoll = 0;
        this.hasRolled = false;
        if (idCounter >= amountOfPlayers) {
            idCounter = 0;
        }
        playerID = idCounter;
        idCounter++;

        playerPieceLocations = initializePlayerLocations(playerID);
    }

    public void addMoney(int amount) {
        money += amount;
    }

    public Pair<Integer, Integer> rollDice() {
        int die1 = (int) (Math.random() * 6) + 1;
        int die2 = (int) (Math.random() * 6) + 1;
        Pair<Integer, Integer> currentRoll = new Pair<>(die1, die2);

        if(inJail){
            if(die1 == die2){
                inJail = false;
                turnsInJail = 0;
                moveAmountRolled(die1 + die2);
                return currentRoll;
            }
            if (turnsInJail == 3) {
                addMoney(-50);
                inJail = false;
                turnsInJail = 0;
                moveAmountRolled(die1 + die2);
                return currentRoll;
            }
            turnsInJail++;
            return currentRoll;
        }

        if (die1 == die2) {
            doublesRolled++;
        } else {
            hasRolled = true;
            doublesRolled = 0;
        }

        if (doublesRolled >= 3) {
            goToJail();
            return currentRoll;
        }

        return currentRoll;
    }

    public void goToJail() {
        inJail = true;
        currentSpace = 10;
        doublesRolled = 0;
    }

    public Pair<Double, Double> moveAmountRolled(int diceRoll) {
        int nextSpace = currentSpace + diceRoll;
        if (nextSpace >= 40) {
            addMoney(200);
            currentSpace = nextSpace - 39;
        }else {
            currentSpace = nextSpace;
        }
        System.out.println("Player " + playerID + " moved to space " + currentSpace);
        System.out.println("Player coordinates: " + playerPieceLocations.get(currentSpace));
        return playerPieceLocations.get(currentSpace);
    }

    public void buyProperty(int propertyID, int propertyCost) {
        addMoney(-propertyCost);
        properties.add(propertyID);
    }

    public int getMoney() {
        return money;
    }

    public int getCurrentSpace() {
        return currentSpace;
    }

    public boolean isInJail() {
        return inJail;
    }

    public int getTurnsInJail() {
        return turnsInJail;
    }

    public int getJailCards() {
        return jailCards;
    }

    public boolean isBankrupt() {
        return bankrupt;
    }

    public int getDoublesRolled() {
        return doublesRolled;
    }

    public int getCurrentRoll() {
        return currentRoll;
    }

    public boolean hasRolled() {
        return hasRolled;
    }

    public void setHasRolled(boolean hasRolled) {
        this.hasRolled = hasRolled;
    }
    public int getPlayerID() {
        return playerID;
    }

    private HashMap<Integer, Pair<Double, Double>> initializePlayerLocations(int playerID) {
        HashMap<Integer, Pair<Double, Double>> playerPieceLocations = new HashMap<>();
        switch (playerID) {
            case 0:
                playerPieceLocations.put(0, new Pair<>(790.0, 790.0));
                for(int i = 0; i < 9; i++){
                    playerPieceLocations.put(i+1, new Pair<>(715.0 - (74.5*i), 815.0));
                }
                playerPieceLocations.put(10, new Pair<>(15.0, 790.0));
                for(int i = 0; i < 9; i++){
                    playerPieceLocations.put(i+11, new Pair<>(65.0, 715.0 - (74.5*i)));
                }
                playerPieceLocations.put(20, new Pair<>(15.0, 15.0));
                for(int i = 0; i < 9; i++){
                    playerPieceLocations.put(i+21, new Pair<>(119.0 + (74.5*i), 40.0));
                }
                playerPieceLocations.put(30, new Pair<>(790.0, 15.0));
                for(int i = 0; i < 9; i++){
                    playerPieceLocations.put(i+31, new Pair<>(815.0, 165.0 + (74.5*i)));
                }
                break;
            case 1:
                playerPieceLocations.put(1, new Pair<>(865.0, 790.0));
                for(int i = 0; i < 9; i++){
                    playerPieceLocations.put(i+1, new Pair<>(763.0 - (74.5*i), 815.0));
                }
                playerPieceLocations.put(10, new Pair<>(91.0, 790.0));
                for(int i = 0; i < 9; i++){
                    playerPieceLocations.put(i+11, new Pair<>(65.0, 763.0 - (74.5*i)));
                }
                playerPieceLocations.put(20, new Pair<>(91.0, 15.0));
                for(int i = 0; i < 9; i++){
                    playerPieceLocations.put(i+21, new Pair<>(165.0 + (74.5*i), 40.0));
                }
                playerPieceLocations.put(30, new Pair<>(865.0, 15.0));
                for(int i = 0; i < 9; i++){
                    playerPieceLocations.put(i+31, new Pair<>(815.0, 119.0 + (74.5*i)));
                }
                break;
            case 2:
                playerPieceLocations.put(2, new Pair<>(790.0, 865.0));
                for(int i = 0; i < 9; i++){
                    playerPieceLocations.put(i+1, new Pair<>(715.0 - (74.5*i), 850.0));
                }
                playerPieceLocations.put(10, new Pair<>(15.0, 865.0));
                for(int i = 0; i < 9; i++){
                    playerPieceLocations.put(i+11, new Pair<>(30.0, 715.0 - (74.5*i)));
                }
                playerPieceLocations.put(20, new Pair<>(15.0, 91.0));
                for(int i = 0; i < 9; i++){
                    playerPieceLocations.put(i+21, new Pair<>(119.0 + (74.5*i), 75.0));
                }
                playerPieceLocations.put(30, new Pair<>(790.0, 91.0));
                for(int i = 0; i < 9; i++){
                    playerPieceLocations.put(i+31, new Pair<>(855.0, 165.0 + (74.5*i)));
                }
                break;
            case 3:
                playerPieceLocations.put(3, new Pair<>(865.0, 865.0));
                for(int i = 0; i < 9; i++){
                    playerPieceLocations.put(i+1, new Pair<>(763.0 - (74.5*i), 850.0));
                }
                playerPieceLocations.put(10, new Pair<>(91.0, 865.0));
                for(int i = 0; i < 9; i++){
                    playerPieceLocations.put(i+11, new Pair<>(30.0, 763.0 - (74.5*i)));
                }
                playerPieceLocations.put(20, new Pair<>(91.0, 91.0));
                for(int i = 0; i < 9; i++){
                    playerPieceLocations.put(i+21, new Pair<>(165.0 + (74.5*i), 75.0));
                }
                playerPieceLocations.put(30, new Pair<>(865.0, 91.0));
                for(int i = 0; i < 9; i++){
                    playerPieceLocations.put(i+31, new Pair<>(855.0, 119.0 + (74.5*i)));
                }
                break;
        }
        return playerPieceLocations;
    }
}
