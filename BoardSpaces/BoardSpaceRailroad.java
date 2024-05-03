package BoardSpaces;

import java.util.ArrayList;

public class BoardSpaceRailroad implements Board{
    private BoardSpace boardSpace;
    private int buyoutCost;
    private ArrayList<Integer> rentCost = new ArrayList<>();

    public BoardSpaceRailroad(BoardSpace boardSpace, int buyoutCost, ArrayList<Integer> rentCost) {
        this.boardSpace = boardSpace;
        this.buyoutCost = buyoutCost;
        this.rentCost = rentCost;
    }
}
