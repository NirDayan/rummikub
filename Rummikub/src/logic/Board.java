/* Board interaction with Game should be:
 *   1. game move and add tiles in board
 *   2. game tell the board that player finished to edit tiles
 *   3. board throws exception if edit was not valid
 */
package logic;

import java.util.ArrayList;
import java.util.List;
import logic.tile.Sequence;
import logic.tile.Tile;

public class Board {

    private List<Sequence> sequencesArray;

    Board() {
        reset();
    }

    public void reset() {
        sequencesArray = new ArrayList<>();
    }
    
    public void moveTile(MoveTileData data) {
        Sequence sourceSeq, targetSeq;
        sourceSeq = sequencesArray.get(data.getSourceSequenceIndex());
        targetSeq = sequencesArray.get(data.getTargetSequenceIndex());
        if (sourceSeq != null && targetSeq != null) {
            Tile tileToMove = sourceSeq.removeTile(data.getSourceSequencePosition());
            targetSeq.addTile(data.getTargetSequencePosition(), tileToMove);
        }
    }

    public void addTile(int sequenceIndex, int indexInSequence, Tile tile) {
        if (sequenceIndex < sequencesArray.size() && sequenceIndex >= 0) {
            Sequence sequence = sequencesArray.get(sequenceIndex);
            sequence.addTile(indexInSequence, tile);
        }
    }

    public boolean isValid() {
        for (Sequence sequence : sequencesArray) {
            if(sequence.isValid() == false)
                return false;
        }
        return true;
    }

    public void addSequence(Sequence sequence) {
        if (sequence != null) {
            sequencesArray.add(sequence);
        }
    }

    public Sequence getSequence(int index) {
        Sequence res = null;
        
        if (index >=0 && index < sequencesArray.size()) {
            res = sequencesArray.get(index);
        }
        
        return res;
    }
    
    public List<Sequence> getSequences(){
        return sequencesArray;
    }
}
