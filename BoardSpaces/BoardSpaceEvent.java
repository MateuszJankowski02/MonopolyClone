package BoardSpaces;

public class BoardSpaceEvent implements Board{
    private BoardSpace boardSpace;
    private SpaceEventType spaceEventType;

    public BoardSpaceEvent(BoardSpace boardSpace, SpaceEventType spaceEventType) {
        this.boardSpace = boardSpace;
        this.spaceEventType = spaceEventType;
    }
}
