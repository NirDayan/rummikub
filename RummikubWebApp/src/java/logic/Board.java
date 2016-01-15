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
    //This member is required when the user performs his step and the board is not valid,
    //So we need to restore the board to the last valid state
    private List<Sequence> sequencesArrayBackup;

    Board() {
        reset();
    }

    public void reset() {
        sequencesArray = new ArrayList<>();
    }

    public boolean moveTile(MoveTileData data) {
        int sourceIndex = data.getSourceSequenceIndex();
        int sourcePosition = data.getSourceSequencePosition();
        int targetIndex = data.getTargetSequenceIndex();
        int targetPosition = data.getTargetSequencePosition();
        
        if (isMoveValid(data)) {
            if (isSequenceContainsOneTileOnly(sourceIndex) && sourceIndex < targetIndex) {
                //This sequence will be removed after "removeTile" operation
                //So the target sequence will be (targetIndex - 1)
                targetIndex = targetIndex - 1;
            }
            //After removing tile from the sequence, if the target position is bigger than the source position,
            //We need to reduce targetPosition by 1
            if (sourceIndex == targetIndex && sourcePosition < targetPosition) {
                targetPosition = targetPosition - 1;
            }
            Tile tile = removeTile(sourceIndex, sourcePosition);
            return addTile(targetIndex, targetPosition, tile);
        }

        return false;
    }

    public Tile removeTile(int sequenceIndex, int indexInSequence) {
        Sequence sequence;
        Tile tile = null;
        if (isMovePositionValid(sequenceIndex, indexInSequence)) {
            sequence = sequencesArray.get(sequenceIndex);
            tile = sequence.removeTile(indexInSequence);
            if (sequence.getSize() == 0) {
                sequencesArray.remove(sequenceIndex);
            }
        }

        return tile;
    }

    public boolean addTile(int sequenceIndex, int indexInSequence, Tile tile) {
        if (sequencesArray.size() == 0) {
            sequencesArray.add(new Sequence(tile));
            return true;
        }
        else if (tile != null && sequenceIndex < sequencesArray.size() && sequenceIndex >= 0) {
            Sequence sequence = sequencesArray.get(sequenceIndex);
            if (indexInSequence == 0) {//add at the beginning of the sequence
                return sequence.addTile(0, tile);
            }
            else if (indexInSequence == sequence.getSize()) {//add at the end of the sequence
                return sequence.addTile(sequence.getSize(), tile);
            }
            else {//Split flow
                return split(sequenceIndex, indexInSequence, tile);
            }
        }

        return false;
    }

    public boolean isValid() {
        for (Sequence sequence : sequencesArray) {
            if (sequence.isValid() == false)
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

        if (index >= 0 && index < sequencesArray.size()) {
            res = sequencesArray.get(index);
        }

        return res;
    }

    public List<Sequence> getSequences() {
        return sequencesArray;
    }

    public boolean isTargetValid(int sequenceIndex, int sequencePosition) {
        if (sequenceIndex == 0 && sequencesArray.size() == 0)
            return true;
        if (sequenceIndex < sequencesArray.size()
                && sequencePosition <= sequencesArray.get(sequenceIndex).getSize()
                && sequenceIndex >= 0 && sequencePosition >= 0)
            return true;

        return false;
    }

    private boolean isMovePositionValid(int sequenceIndex, int sequencePosition) {
        boolean isSequenceExist = sequenceIndex < sequencesArray.size();
        boolean isPositionValid;

        if (!isSequenceExist)
            return false;

        isPositionValid = (sequencePosition == 0)
                || (sequencePosition == sequencesArray.get(sequenceIndex).getSize()-1);

        return isPositionValid;
    }

    public boolean isMoveValid(MoveTileData data) {
        int sourceSeqIndex = data.getSourceSequenceIndex();
        int sourceSeqPosition = data.getSourceSequencePosition();
        int targetSeqIndex = data.getTargetSequenceIndex();
        int targetSeqPosition = data.getTargetSequencePosition();

        return isMovePositionValid(sourceSeqIndex, sourceSeqPosition)
                && isTargetValid(targetSeqIndex, targetSeqPosition);
    }

    private boolean split(int sequenceIndex, int indexInSequence, Tile tile) {
        if (!isTargetValid(sequenceIndex, indexInSequence))
            return false;

        Sequence sequence = sequencesArray.get(sequenceIndex);
        Sequence newSequence = sequence.split(indexInSequence);
        if (newSequence == null)
            return false;
        sequence.addTile(indexInSequence, tile);
        sequencesArray.add(newSequence);

        return true;
    }

    public void storeBackup() {
        sequencesArrayBackup = new ArrayList<>(sequencesArray.size());
        for (Sequence sequence : sequencesArray) {
            sequencesArrayBackup.add(sequence.clone());
        }
    }
    
    public void restoreFromBackup() {
        if (sequencesArrayBackup != null) {
            sequencesArray = sequencesArrayBackup;
        }
    }
    
    public Tile getTile(int sequenceIndex, int sequencePosition) {
        if (sequenceIndex < 0 || sequenceIndex > sequencesArray.size() - 1) {
            return null;            
        }
        return sequencesArray.get(sequenceIndex).getTile(sequencePosition);
    }

    private boolean isSequenceContainsOneTileOnly(int sourceSequenceIndex) {
        return sequencesArray.get(sourceSequenceIndex).getSize() == 1;
    }
}
