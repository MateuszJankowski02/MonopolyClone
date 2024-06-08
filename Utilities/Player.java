package Utilities;

import BoardSpaces.BoardSpaceStreet;
import User.User;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;

public class Player {
    private static int idCounter = 0;
    private int money;
    private int currentSpace;
    private boolean inJail;
    private int turnsInJail;
    private int jailCards;
    private boolean bankrupt;
    private int doublesRolled;
    private int currentRoll;
    private boolean hasRolled;
    private ArrayList<BoardSpaceStreet> properties = new ArrayList<>();

    public Player() {
        this.money = 1500;
        this.currentSpace = 0;
        this.inJail = false;
        this.turnsInJail = 0;
        this.jailCards = 0;
        this.bankrupt = false;
        this.doublesRolled = 0;
        this.currentRoll = 0;
        this.hasRolled = false;
    }

    private void addMoney(int amount) {
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

        moveAmountRolled(die1 + die2);

        return currentRoll;
    }

    private void goToJail() {
        inJail = true;
        currentSpace = 10;
        doublesRolled = 0;
    }

    private void moveAmountRolled(int diceRoll) {
        int nextSpace = currentSpace + diceRoll;
        if (nextSpace >= 40) {
            addMoney(200);
            currentSpace = nextSpace - 39;
        }else {
            currentSpace = nextSpace;
        }
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
}
