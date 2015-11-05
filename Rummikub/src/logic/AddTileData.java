package logic;

// TODO: need to find a better location for this object
import logic.tile.Tile;

public class AddTileData {

    private int playerID;
    private Tile tile;
    private int sequenceIndex;
    private int sequencePosition;

    public AddTileData(int playerID, Tile tile, int sequenceIndex, int sequencePosition) {
        this.playerID = playerID;
        this.tile = tile;
        this.sequenceIndex = sequenceIndex;
        this.sequencePosition = sequencePosition;
    }

    public AddTileData() {
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public int getSequenceIndex() {
        return sequenceIndex;
    }

    public void setSequenceIndex(int sequenceIndex) {
        this.sequenceIndex = sequenceIndex;
    }

    public int getSequencePosition() {
        return sequencePosition;
    }

    public void setSequencePosition(int sequencePosition) {
        this.sequencePosition = sequencePosition;
    }
}
