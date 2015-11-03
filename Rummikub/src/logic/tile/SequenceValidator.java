package logic.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;
import static logic.tile.SequenceValidator.PrevCurrTileRes.*;

class SequenceValidator {

    private final ArrayList<Tile> sequence;
    private ListIterator<Tile> indexInSeq;
    private final Stack<JokerTile> jokers;
    private final LinkedList<Color> unusedColors;
    private Stack jokersToAdd;
    private boolean isStraight;

    enum PrevCurrTileRes {

        SAME_COLOR_FOLLOWING_VAL,
        SAME_COLOR_NOT_FOLLOWING_VAL,
        DIFF_COLOR_SAME_VAL,
        DIFF_COLOR_NOT_SAME_VAL
    }

    public SequenceValidator(ArrayList<Tile> sequence, Stack<JokerTile> jokers) {
        jokersToAdd = new Stack();
        this.sequence = sequence;
        this.jokers = jokers;
        unusedColors = new LinkedList<>();
        unusedColors.addAll(Arrays.asList(Color.values()));
        isStraight = false;
    }

    public void validate() throws Sequence.InvalidSequence {
        if (sequence.size() + jokers.size() < 3) {
            throw new Sequence.InvalidSequence();
        }
        validateTilesOneByOne();
    }

    private void validateTilesOneByOne() throws Sequence.InvalidSequence {
        indexInSeq = sequence.listIterator();
        Tile prevTile = indexInSeq.next();
        markColor(prevTile.color);
        while(indexInSeq.hasNext()) {
            Tile currTile = indexInSeq.next();
            prevTile = handleCurTile(prevTile, currTile);
        }
        handleUnusedJokers();
        sequence.addAll(jokersToAdd);
    }

    private Tile handleCurTile(Tile prevTile, Tile currTile)
            throws SequenceValidatorError, Sequence.InvalidSequence {
        switch (getPrevCurrTileRelations(prevTile, currTile)) {
            case SAME_COLOR_FOLLOWING_VAL:
                if (isColorMarked(currTile.color) == false) {
                    throw new Sequence.InvalidSequence();
                }
                isStraight = true;
                break;
            case SAME_COLOR_NOT_FOLLOWING_VAL:
                currTile = tryToUseJokerForStright(prevTile);
                isStraight = true;
                //Itarate back so we wont miss a tile.
                indexInSeq = sequence.listIterator(indexInSeq.previousIndex());
                break;
            case DIFF_COLOR_SAME_VAL:
                if (isStraight || isColorMarked(currTile.color)) {
                    throw new Sequence.InvalidSequence();
                }
                markColor(currTile.color);
                break;
            case DIFF_COLOR_NOT_SAME_VAL:
                throw new Sequence.InvalidSequence();
            default:
                throw new SequenceValidatorError();
        }
        return currTile;
    }

    private PrevCurrTileRes getPrevCurrTileRelations(Tile prevTile, Tile currTile) {
        if (prevTile.color == currTile.color) {
            if (prevTile.value + 1 == currTile.value) {
                return SAME_COLOR_FOLLOWING_VAL;
            } else {
                return SAME_COLOR_NOT_FOLLOWING_VAL;
            }
        } else if (prevTile.value == currTile.value) {
            return DIFF_COLOR_SAME_VAL;
        } else {
            return DIFF_COLOR_NOT_SAME_VAL;
        }
    }

    private Tile tryToUseJokerForStright(Tile tile) throws Sequence.InvalidSequence {
        if (jokers.isEmpty()) {
            throw new Sequence.InvalidSequence();
        }

        Tile joker = morphJokerToTile(tile.color, tile.value + 1);
        jokersToAdd.push(joker);
        return joker;
    }

    private void tryToUseJokerForSameValueSequence() {
        if (jokers.isEmpty() || unusedColors.isEmpty() || sequence.isEmpty()) {
            throw new Sequence.InvalidSequence();
        }

        Tile tile = morphJokerToTile(unusedColors.getFirst(), sequence.get(0).value);
        jokersToAdd.push(tile);
    }

    private JokerTile morphJokerToTile(Color color, int value) {
        JokerTile joker = jokers.pop();
        joker.setColor(color);
        joker.setValue(value);
        return joker;
    }

    private void handleUnusedJokers() {
        while (jokers.isEmpty() == false) {
            if (isStraight) {
                if (sequence.get(sequence.size() - 1).value != 13) {
                    //Add to tail of straight
                    Tile tilePrev = sequence.get(sequence.size() - 1);
                    sequence.add(morphJokerToTile(tilePrev.color, tilePrev.value + 1));
                } else {
                    //Add to head of straight
                    Tile tileNext = sequence.get(0);
                    sequence.add(0,morphJokerToTile(tileNext.color, tileNext.value - 1));
                }
            } else {
                tryToUseJokerForSameValueSequence();
            }
        }
    }

    //Return false if it has been used before
    private boolean isColorMarked(Color color) {
        return unusedColors.contains(color) == false;
    }

    private void markColor(Color color) {
        unusedColors.remove(color);
    }

    private static class SequenceValidatorError extends RuntimeException {
    }

}
