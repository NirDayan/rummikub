package logic.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// This class might be redundent. need to consider removing it.
public class Sequence {

    private List<Tile> sequence;

    public Sequence(Tile... tiles) {
        List<Tile> allTiles = new ArrayList<>();
        allTiles.addAll(Arrays.asList(tiles));
        sequence = allTiles;
    }

    public Sequence(List<Tile> tiles) {
        sequence = tiles;
    }

    public boolean isValid() {
        SequenceValidator validator = new SequenceValidator(sequence);
        return validator.isValid();
    }

    public int getSize() {
        return sequence.size();
    }

    public int getValueSum() {
        if (isValid() == false) {
            return -1; // TODO: I dont like this kind of error code passing. 
        }// I think it should throw an exception. nir, what do you think?
        //TODO: figure out a way to check the sum
        int sum = 0;
        for (Tile tile : sequence) {
            sum += tile.getValue();
        }
        return sum;
    }

}
