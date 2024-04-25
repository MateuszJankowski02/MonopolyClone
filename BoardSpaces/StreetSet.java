package BoardSpaces;

public class StreetSet {
    private String name;
    private int amountOfProperties;
    private int houseCost;
    public StreetSet(String name, int amountOfProperties, int houseCost) {
        this.name = name;
        this.amountOfProperties = amountOfProperties;
        this.houseCost = houseCost;
    }
    public String getName() {
        return name;
    }
    public int getAmountOfProperties() {
        return amountOfProperties;
    }
    public int getHouseCost() {
        return houseCost;
    }
}
