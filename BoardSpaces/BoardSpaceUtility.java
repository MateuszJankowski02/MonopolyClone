package BoardSpaces;

import Utilities.Player;

import java.util.ArrayList;

public class BoardSpaceUtility implements Board{
    private BoardSpace boardSpace;
    private int buyoutCost;
    private ArrayList<Integer> multiplier = new ArrayList<>();
    private Player owner;

    public BoardSpaceUtility(BoardSpace boardSpace, int buyoutCost, ArrayList<Integer> multiplier) {
        this.boardSpace = boardSpace;
        this.buyoutCost = buyoutCost;
        this.multiplier = multiplier;
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

    public int getMultiplier() {
        return multiplier.getFirst();
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getRent(int diceRoll, int numOwned) {
        return multiplier.get(numOwned) * diceRoll;
    }

}
