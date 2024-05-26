package Utilities;

import BoardSpaces.BoardSpaceStreet;
import Login.User;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    private static final long serialVersionUID = 2L;
    private static int idCounter = 0;
    private int id;
    private User user;
    private int money;
    private int currentSpace;
    private String color;
    private boolean inJail;
    private int turnsInJail;
    private int jailCards;
    private boolean bankrupt;
    private boolean canRoll;
    private int doublesRolled;
    private int currentRoll;
    private boolean hisTurn;
    private ArrayList<BoardSpaceStreet> properties = new ArrayList<>();

    public Player(User user) {
        this.id = idCounter++;
        this.user = user;
        this.money = 1500;
        this.currentSpace = 0;
        switch (id) {
            case 0:
                this.color = "RED";
                break;
            case 1:
                this.color = "BLUE";
                break;
            case 2:
                this.color = "GREEN";
                break;
            case 3:
                this.color = "YELLOW";
                break;
            default:
                this.color = "BLACK";
        }
        this.inJail = false;
        this.turnsInJail = 0;
        this.jailCards = 0;
        this.bankrupt = false;
        this.canRoll = true;
        this.doublesRolled = 0;
        this.currentRoll = 0;
        this.hisTurn = id == 0;
    }

    private void addMoney(int amount) {
        money += amount;
    }

    public void rollDice() {
        if (!canRoll || !hisTurn) return;
        int die1 = (int) (Math.random() * 6) + 1;
        int die2 = (int) (Math.random() * 6) + 1;
        currentRoll = die1 + die2;
        if (die1 == die2) {
            doublesRolled++;
        } else {
            canRoll = false;
        }

        if (doublesRolled == 3) {
            goToJail();
        } else {
            moveAmountRolled(currentRoll);
        }

        if(inJail && die1 == die2){
            inJail = false;
            turnsInJail = 0;
        }
        if (!inJail) {
            moveAmountRolled(currentRoll);
        }else{
            turnsInJail++;
            if(turnsInJail == 3){
                addMoney(-50);
                inJail = false;
                turnsInJail = 0;
            }
        }
    }

    private void goToJail() {
        inJail = true;
        currentSpace = 10;
        doublesRolled = 0;
    }

    private void moveAmountRolled(int diceRoll) {
        int nextSpace = currentSpace + diceRoll;
        if (nextSpace > 40) {
            currentSpace %= diceRoll;
            addMoney(200);
        }else {
            currentSpace = nextSpace;
        }
    }

    public void endTurn() {
        hisTurn = false;
        canRoll = true;
    }

    public User getUser() {
        return user;
    }

    public String getName(){
        return user.getName();
    }

    public int getMoney() {
        return money;
    }

    public int getCurrentSpace() {
        return currentSpace;
    }

    public String getColor() {
        return color;
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

    public boolean canRoll() {
        return canRoll;
    }

    public int getDoublesRolled() {
        return doublesRolled;
    }

    public int getCurrentRoll() {
        return currentRoll;
    }


}
