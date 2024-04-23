package BoardSpaces;

import java.util.ArrayList;

public class BoardSpaceProperty {
    private BoardSpace boardSpace;
    private Street street;
    private int houseCost;
    private ArrayList<Integer> rent = new ArrayList<>(); // 0 = base rent, 1-4 = rent with 1-4 houses, 5 = rent with hotel

    public BoardSpaceProperty(BoardSpace boardSpace) {
        this.boardSpace = boardSpace;
    }
}
