package logic.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// This class might be redundent. need to consider removing it.
public class Sequence {

    private List<Tile> tiles;

    public Sequence(Tile... tiles) {
        List<Tile> allTiles = new ArrayList<>();
        allTiles.addAll(Arrays.asList(tiles));
        this.tiles = allTiles;
    }

    public Sequence(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public boolean isValid() {
        SequenceValidator validator = new SequenceValidator(tiles);
        return validator.isValid();
    }

    public int getSize() {
        return tiles.size();
    }

    public int getValueSum() {
        if (isValid() == false) {
            return -1; // TODO: I dont like this kind of error code passing. 
        }// I think it should throw an exception. nir, what do you think?
        //TODO: figure out a way to check the sum
        int sum = 0;
        for (Tile tile : tiles) {
            sum += tile.getValue();
        }
        return sum;
    }
    
    public boolean addTile(int index, Tile tile) {
        if (tile != null && (index <= tiles.size() && index >= 0)) {
            tiles.add(index, tile);
            
            return true;
        }
        return false;
    }
    
    public Tile removeTile(int index) {
        Tile tile = null;
        if (index < tiles.size() && index >= 0) {
            tile = tiles.remove(index);
        }
        
        return tile;
    }
    
    public Tile getTile(int index) {
        Tile tile = null;
        if (index < tiles.size() && index >= 0) {
            tile = tiles.get(index);
        }
        
        return tile;
    }
    
    public Sequence split(int index) {
        Sequence newSequence = null;
        
        if (index < tiles.size() && index >= 1) {
            newSequence = new Sequence(new ArrayList<>(tiles.subList(index, tiles.size())));
            tiles = new ArrayList<>(tiles.subList(0, index));
        }
        
        return newSequence;
    }
}
