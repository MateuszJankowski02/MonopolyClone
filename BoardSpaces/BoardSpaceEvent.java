package BoardSpaces;

public class BoardSpaceEvent implements Board{
    private BoardSpace boardSpace;
    private SpaceEventType spaceEventType;

    public BoardSpaceEvent(BoardSpace boardSpace, SpaceEventType spaceEventType) {
        this.boardSpace = boardSpace;
        this.spaceEventType = spaceEventType;
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

    public SpaceEventType getSpaceEventType() {
        return spaceEventType;
    }
}
