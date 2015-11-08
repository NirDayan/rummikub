package logic.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// This class might be redundent. need to consider removing it.
public class Sequence {

    private List<Tile> sequence;

    public Sequence(Tile... tiles) throws InvalidSequenceException {
        List<Tile> allTiles = new ArrayList<>();
        allTiles.addAll(Arrays.asList(tiles));
        init(allTiles);
    }

    public Sequence(List<Tile> tiles) throws InvalidSequenceException {
        init(tiles);
    }

    private void init(List<Tile> tiles) throws InvalidSequenceException {
        sequence = tiles;
    }

    public void validate() throws Sequence.InvalidSequenceException {
        SequenceValidator validator = new SequenceValidator(sequence);
        validator.validate();
    }
    
    public List toList() {
        return sequence;
    }

    public int getSize() {
        return sequence.size();
    }

    public int getValueSum() throws InvalidSequenceException {
        validate();
        int sum = 0;
        for (Tile tile : sequence) {
            sum += tile.getValue();
        }
        return sum;
    }

    public static class InvalidSequenceException extends Exception {
    }
}
