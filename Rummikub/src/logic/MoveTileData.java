package logic;

// TODO: need to find a better location for this object
public class MoveTileData {

    private int playerID;
    private int sourceSequenceIndex;
    private int sourceSequencePosition;
    private int targetSequenceIndex;
    private int targetSequencePosition;

    public MoveTileData(int playerID, int sourceSequenceIndex, int sourceSequencePosition, int targetSequenceIndex, int targetSequencePosition) {
        this.playerID = playerID;
        this.sourceSequenceIndex = sourceSequenceIndex;
        this.sourceSequencePosition = sourceSequencePosition;
        this.targetSequenceIndex = targetSequenceIndex;
        this.targetSequencePosition = targetSequencePosition;
    }

    public MoveTileData() {
    }
    
    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public int getSourceSequenceIndex() {
        return sourceSequenceIndex;
    }

    public void setSourceSequenceIndex(int sourceSequenceIndex) {
        this.sourceSequenceIndex = sourceSequenceIndex;
    }

    public int getSourceSequencePosition() {
        return sourceSequencePosition;
    }

    public void setSourceSequencePosition(int sourceSequencePosition) {
        this.sourceSequencePosition = sourceSequencePosition;
    }

    public int getTargetSequenceIndex() {
        return targetSequenceIndex;
    }

    public void setTargetSequenceIndex(int targetSequenceIndex) {
        this.targetSequenceIndex = targetSequenceIndex;
    }

    public int getTargetSequencePosition() {
        return targetSequencePosition;
    }

    public void setTargetSequencePosition(int targetSequencePosition) {
        this.targetSequencePosition = targetSequencePosition;
    }
}
