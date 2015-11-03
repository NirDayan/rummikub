package logic.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class Sequence {

    private final ArrayList<Tile> dumbTilesSequence;
    private final Stack<JokerTile> jokerTiles;

    public Sequence(Tile... tiles) throws InvalidSequence {
        dumbTilesSequence = new ArrayList<>();
        jokerTiles = new Stack<>();
        //TODO: there might be a problem if the sequnce fails and the jokers are changed
        initTiles(tiles);
        sapateJokersAndDumbTiles(tiles);
        validate();
    }

    public final void validate() throws Sequence.InvalidSequence {
        Collections.sort(dumbTilesSequence);
        SequenceValidator validator = new SequenceValidator(dumbTilesSequence, jokerTiles);
        validator.validate();
    }

    public int getSize() {
        return dumbTilesSequence.size();
    }

    public int getValueSum() {
        int sum = 0;
        for (Tile tile : dumbTilesSequence) {
            sum += tile.getValue();
        }
        return sum;
    }

    private void initTiles(Tile... tiles) {
        for (Tile tile : tiles) {
            tile.initialize();
        }
    }

    private void sapateJokersAndDumbTiles(Tile[] tiles) {
        for (Tile tile : tiles) {
            if (tile.getValue() == JokerTile.JOKER_INITIAL_VALUE) {
                jokerTiles.push((JokerTile) tile);
            } else {
                dumbTilesSequence.add(tile);
            }
        }
    }

    public static class InvalidSequence extends RuntimeException {
    }
}
