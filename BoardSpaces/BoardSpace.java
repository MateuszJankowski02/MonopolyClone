package BoardSpaces;

public class BoardSpace implements Board{
    private int spaceID; // game board has 40 spaces
    private String name;
    private SpaceType type;

    public BoardSpace(int spaceID, String name, SpaceType type) {
        this.spaceID = spaceID;
        this.name = name;
        this.type = type;
    }

    public int getSpaceID() {
        return spaceID;
    }

    public String getName() {
        return name;
    }

    public SpaceType getType() {
        return type;
    }

}
