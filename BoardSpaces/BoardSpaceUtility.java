package BoardSpaces;

import java.util.ArrayList;

public class BoardSpaceUtility {
    private String name;
    private int buyoutCost;
    private ArrayList<Integer> multiplier = new ArrayList<>();

    public BoardSpaceUtility(String name, int buyoutCost, ArrayList<Integer> multiplier) {
        this.name = name;
        this.buyoutCost = buyoutCost;
        this.multiplier = multiplier;
    }
}
