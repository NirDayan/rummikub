package logic;

import java.util.ArrayList;
import logic.tile.Tile;

public abstract class Player {

    private final String name;
    private final int ID;
    private final ArrayList<Tile> tiles;
    private boolean isResign;

    Player(int ID, String name) {
        this.ID = ID;
        this.name = name;
        this.tiles = new ArrayList<>();
    }

    public void setIsResign(boolean isResign) {
        this.isResign = isResign;
    }

    public boolean getIsResign() {
        return isResign;
    }   
    
    public void addTile(Tile tile) {
        if (tile != null) 
            tiles.add(tile);
    }
    
    public abstract void play();
    
    public boolean isFinished() {
        return tiles.isEmpty();
    }
    
    
}
