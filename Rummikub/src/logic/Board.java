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

    public boolean moveTile(MoveTileData data) {
        if (isMoveValid(data)) {
            Tile tile = removeTile(data.getSourceSequenceIndex(), data.getSourceSequencePosition());
            return addTile(data.getTargetSequenceIndex(), data.getTargetSequencePosition(), tile);
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
            else if (indexInSequence == sequence.getSize()) {//edd at the end of the sequence
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
                || (sequencePosition == sequencesArray.get(sequenceIndex).getSize());

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
}
