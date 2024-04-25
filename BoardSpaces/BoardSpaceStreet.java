package BoardSpaces;

import Utilities.Player;

import java.util.ArrayList;

public class BoardSpaceStreet implements Board{
    private BoardSpace boardSpace;
    private StreetSet streetSet;
    private int houseCost;
    private int buyoutCost;
    private ArrayList<Integer> rent = new ArrayList<>(); // 0 = base rent, 1-4 = rent with 1-4 houses, 5 = rent with hotel
    private int houseCount;
    private Player owner;

    public BoardSpaceStreet(BoardSpace boardSpace, StreetSet streetSet, int buyoutCost, int houseCost, ArrayList<Integer> rent) {
        this.boardSpace = boardSpace;
        this.streetSet = streetSet;
        this.buyoutCost = buyoutCost;
        this.houseCost = houseCost;
        this.rent = rent;
        this.houseCount = 0;
        this.owner = null;
    }
}
