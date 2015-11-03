package logic.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

public class Sequence {

    private ArrayList<Tile> sequence;
    private Stack<JokerTile> jokerTiles;

    public Sequence(Tile... tiles) throws InvalidSequenceException {
        Collection<Tile> allTiles = new ArrayList<>();
        allTiles.addAll(Arrays.asList(tiles));
        init(allTiles);
    }

    public Sequence(Collection<Tile> tiles) throws InvalidSequenceException {
        init(tiles);
    }

    //TODO: there might be a problem if the sequnce fails and the jokers are changed
    private void init(Collection<Tile> tiles) throws InvalidSequenceException {
        sequence = new ArrayList<>();
        jokerTiles = new Stack<>();
        initTiles(tiles);
        separateJokersAndDumbTiles(tiles);
        validate();
    }

    private void initTiles(Collection<Tile> tiles) {
        for (Tile tile : tiles) {
            tile.initialize();
        }
    }

    private void separateJokersAndDumbTiles(Collection<Tile> tiles) {
        for (Tile tile : tiles) {
            if (tile.getValue() == JokerTile.JOKER_INITIAL_VALUE) {
                jokerTiles.push((JokerTile) tile);
            } else {
                sequence.add(tile);
            }
        }
    }

    public final void validate() throws Sequence.InvalidSequenceException {
        Collections.sort(sequence);
        SequenceValidator validator = new SequenceValidator(sequence, jokerTiles);
        validator.validate();
    }

    public int getSize() {
        return sequence.size();
    }

    public int getValueSum() {
        int sum = 0;
        for (Tile tile : sequence) {
            sum += tile.getValue();
        }
        return sum;
    }

    public static class InvalidSequenceException extends Exception {
    }
}
