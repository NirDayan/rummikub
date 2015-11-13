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
    
    public Tile removeTile(int tileIndex) {
        Tile tileToRemove = null;
        if (tileIndex < tiles.size() && tileIndex >= 0) {
            tileToRemove = tiles.get(tileIndex);
            tiles.remove(tileToRemove);
        }
        
        return tileToRemove;
    }
    
    public boolean addTile(int index, Tile tile) {
        if (tile != null && (index < tiles.size() && index >= 0)) {
            tiles.add(index, tile);
            
            return true;
        }
        return false;
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
        List<Tile> tilesNewSeq;
        
        if (index < tiles.size() - 1 && index >= 1) {
            tilesNewSeq = new ArrayList<Tile>();
            for (int i = index; i < tiles.size(); i++) {
                tilesNewSeq.add(tiles.remove(i));
            }
            newSequence = new Sequence(tilesNewSeq);
        }
        
        return newSequence;
    }
}
