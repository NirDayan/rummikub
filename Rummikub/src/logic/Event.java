package logic;

public class Event {
    
    private int ID;
    private int timeoutCount;
    private final String playerName;
    private Tile[] tiles;
    private final eventType eventType;
    private int sourceSequenceIndex;
    private int sourceSequencePosition;
    private int targetSequenceIndex;
    private int targetSequencePosition;
    
    public enum eventType {
        GAME_START,
        GAME_OVER,
        GAME_WINNER,
        PLAYER_TURN,
        PLAYER_FINISHED,
        PLAYER_RESIGNED,
        SEQUENCE_CREATED,
        TILE_ADDED,
        TILE_MOVED,
        REVERT
    };
    
    public Event(eventType eventType, String playerName) {
        this.eventType = eventType;
        this.playerName = playerName;
    }
    
    public int getEventID() {
        return ID;
    }
    
    public void setEventID(int ID) {
        this.ID = ID;
    }
    
    public int getTimeoutCount() {// will be 0 in case no timer is active
        return timeoutCount;
    }
    
    public void setTimeoutCount(int timeoutCount) {
        this.timeoutCount = timeoutCount;
    }
    
    public eventType getEventType() {
        return eventType;
    }
    
    public String getPlayerName() {//The player to which this event is related to
        return playerName;
    }
        
    public Tile[] getTiles() {
        return tiles;
    }
    
    public void setTiles(Tile[] tiles) {
        this.tiles = tiles;
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
