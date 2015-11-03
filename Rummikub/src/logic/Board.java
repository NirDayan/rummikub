/* Board interaction with Game should be:
 *   1. game move and add tiles in board
 *   2. game tell the board that player finished to edit tiles
 *   3. board throws exception if edit was not valid
 */
package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import logic.tile.Sequence.InvalidSequenceException;
import logic.tile.Tile;

public class Board {

    private Map<Integer, List<Tile>> sequencesMap;
    private Integer sequnceIndex;

    Board() {
        sequencesMap = new HashMap<>();
        sequnceIndex = 0;
    }

    public void moveTile(MoveTileData data) throws sequenceNotFoundException {
        List<Tile> sourceSeq, targetSeq;
        sourceSeq = sequencesMap.get(data.getSourceSequenceIndex());
        targetSeq = sequencesMap.get(data.getTargetSequenceIndex());
        if(sourceSeq == null || targetSeq == null)
            throw new sequenceNotFoundException();
        Tile tileToMove = sourceSeq.remove(data.getSourceSequencePosition());
        targetSeq.add(data.getTargetSequencePosition(), tileToMove);
    }

    public void addTile(AddTileData data) throws sequenceNotFoundException {
        List<Tile> sequence;
        sequence = sequencesMap.get(data.getSequenceIndex());
        if (sequence == null) {
            throw new sequenceNotFoundException();
        }
        sequence.add(data.getSequencePosition(), data.getTile());
    }

    public void finishTurn() throws InvalidSequenceException {

    }

    public int createSequence(Tile[] tiles) {
        List<Tile> tilesList = new ArrayList<>();
        tilesList.addAll(Arrays.asList(tiles));
        int newIndex = generateNewIndex();
        sequencesMap.put(newIndex, tilesList);
        return newIndex;
    }

    private int generateNewIndex() {
        return sequnceIndex++;
    }

    public static class sequenceNotFoundException extends Exception {
    }
}
