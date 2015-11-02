package logic;

import java.util.ArrayList;

public class Player {
    
    private String name;
    private int ID;
    ArrayList<Tile> tiles;
    
    Player(int ID, String name) {
        this.ID = ID;
        this.name = name;
        this.tiles = new ArrayList<Tile>();
    }
    
    public void addTile(Tile tile) {
        if (tile != null)
            tiles.add(tile);
    }
}
