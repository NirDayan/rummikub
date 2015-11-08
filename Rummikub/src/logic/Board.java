/* Board interaction with Game should be:
 *   1. game move and add tiles in board
 *   2. game tell the board that player finished to edit tiles
 *   3. board throws exception if edit was not valid
 */
package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import logic.tile.Sequence;
import logic.tile.Sequence.InvalidSequenceException;
import logic.tile.Tile;

public class Board {

    private List<List<Tile>> sequencesArray;

    Board() {
        reset();
    }

    public void reset() {
        sequencesArray = new ArrayList<>();
    }
    
    public void moveTile(MoveTileData data) throws sequenceNotFoundException {
        List<Tile> sourceSeq, targetSeq;
        sourceSeq = sequencesArray.get(data.getSourceSequenceIndex());
        targetSeq = sequencesArray.get(data.getTargetSequenceIndex());
        if (sourceSeq == null || targetSeq == null) {
            throw new sequenceNotFoundException();
        }
        Tile tileToMove = sourceSeq.remove(data.getSourceSequencePosition());
        targetSeq.add(data.getTargetSequencePosition(), tileToMove);
    }

    public void addTile(AddTileData data) throws sequenceNotFoundException {
        List<Tile> sequence;
        try{
        sequence = sequencesArray.get(data.getSequenceIndex());
        }
        catch(IndexOutOfBoundsException e){
            throw new sequenceNotFoundException();
        }
        sequence.add(data.getSequencePosition(), data.getTile());
    }

    public void finishTurn() throws InvalidSequenceException {
        for (List list : sequencesArray) {
            new Sequence(list).validate();
        }
    }

    public void createSequence(Tile[] tiles) {
        List<Tile> tilesList = new ArrayList<>();
        tilesList.addAll(Arrays.asList(tiles));
        sequencesArray.add(tilesList);
    }

    public List getSequence(int index){
        return sequencesArray.get(index);
    }
    
    public List<List<Tile>> getBoard(){
        return sequencesArray;
    }

    public static class sequenceNotFoundException extends Exception {
    }
}
