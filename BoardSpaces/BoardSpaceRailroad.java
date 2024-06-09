package BoardSpaces;

import Utilities.Player;

import java.util.ArrayList;

public class BoardSpaceRailroad implements Board{
    private BoardSpace boardSpace;
    private int buyoutCost;
    private ArrayList<Integer> rentCost = new ArrayList<>();

    private Player owner;

    public BoardSpaceRailroad(BoardSpace boardSpace, int buyoutCost, ArrayList<Integer> rentCost) {
        this.boardSpace = boardSpace;
        this.buyoutCost = buyoutCost;
        this.rentCost = rentCost;
    }

    public int getSpaceID() {
        return boardSpace.getSpaceID();
    }

    public String getName() {
        return boardSpace.getName();
    }

    public SpaceType getType() {
        return boardSpace.getType();
    }

    public int getBuyoutCost() {
        return buyoutCost;
    }

    public int getRentCost(int numOwned) {
        return rentCost.get(numOwned);
    }

    public int getNumOwned() {
        return rentCost.size();
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getRent() {
        return rentCost.getFirst();
    }

}
