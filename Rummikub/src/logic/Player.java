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

    public boolean isResign() {
        return isResign;
    }   
    
    public void addTile(Tile tile) {
        if (tile != null) 
            tiles.add(tile);
    }
        
    public boolean isFinished() {
        return tiles.isEmpty();
    }

    public String getName() {
        return name;
    }
    
    public int getID() {
        return ID;
    }
    
    public void reset() {
        tiles.clear();
        isResign = false;
    }
    
    public ArrayList<Tile> getTiles() {
        return tiles;
    }
    
    Tile removeTile(int index) {
        if (index < tiles.size() && index >=0) {
            return tiles.remove(index);
        }
        
        return null;
    }
}
