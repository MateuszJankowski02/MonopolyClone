package Utilities;

import BoardSpaces.BoardSpaceProperty;

import java.util.ArrayList;

public class Player {
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
    private boolean hasRolled;
    private int doublesRolled;
    private int currentRoll;
    private boolean hasMoved;
    private boolean hisTurn;
    private ArrayList<BoardSpaceProperty> properties = new ArrayList<>();

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
        this.hasRolled = false;
        this.doublesRolled = 0;
        this.currentRoll = 0;
        this.hasMoved = false;
        this.hisTurn = id == 0;
    }


}
