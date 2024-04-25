package BoardSpaces;

import java.util.ArrayList;

public class BoardSpaceRailroad {
    private String name;
    private int buyoutCost;
    private ArrayList<Integer> rentCost = new ArrayList<>();

    public BoardSpaceRailroad(String name, int buyoutCost, ArrayList<Integer> rentCost) {
        this.name = name;
        this.buyoutCost = buyoutCost;
        this.rentCost = rentCost;
    }
}
