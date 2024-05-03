package BoardSpaces;

import java.util.ArrayList;

public class BoardSpaceUtility implements Board{
    private BoardSpace boardSpace;
    private int buyoutCost;
    private ArrayList<Integer> multiplier = new ArrayList<>();

    public BoardSpaceUtility(BoardSpace boardSpace, int buyoutCost, ArrayList<Integer> multiplier) {
        this.boardSpace = boardSpace;
        this.buyoutCost = buyoutCost;
        this.multiplier = multiplier;
    }
}
