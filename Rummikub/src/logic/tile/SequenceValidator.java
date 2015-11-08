package logic.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;
import logic.tile.Sequence.InvalidSequenceException;
import static logic.tile.SequenceValidator.PrevCurrTileRes.*;

class SequenceValidator {

    private List<Tile> validatedSequence;
    private List<Tile> originalSequence;
    private ListIterator<Tile> indexInSeq;
    private Stack<JokerTile> jokers;
    private LinkedList<Color> unusedColors;
    private Stack<Tile> jokersToAdd;
    private boolean isStraight;


    enum PrevCurrTileRes {

        SAME_COLOR_FOLLOWING_VAL,
        SAME_COLOR_NOT_FOLLOWING_VAL,
        DIFF_COLOR_SAME_VAL,
        DIFF_COLOR_NOT_SAME_VAL
    }

    public SequenceValidator(List tileList) {
        initilaizations(tileList);
        initTiles(tileList);
        separateJokersAndDumbTiles(tileList);
        Collections.sort(validatedSequence);
    }

    private void initilaizations(List tileList) {
        jokersToAdd = new Stack<>();
        unusedColors = new LinkedList<>();
        validatedSequence = new ArrayList<>();
        originalSequence = tileList;
        jokers = new Stack<>();
        unusedColors.addAll(Arrays.asList(Color.values()));
        isStraight = false;
    }

    private static void initTiles(List<Tile> tiles) {
        for (Tile tile : tiles) {
            tile.initialize();
        }
    }

    private void separateJokersAndDumbTiles(List<Tile> tiles) {
        for (Tile tile : tiles) {
            if (tile.getValue() == JokerTile.JOKER_INITIAL_VALUE) {
                jokers.push((JokerTile) tile);
            } else {
                validatedSequence.add(tile);
            }
        }
    }

    public void validate() throws InvalidSequenceException {
        if (validatedSequence.size() + jokers.size() < 3) {
            throw new InvalidSequenceException();
        }
        createValidatedSequence();
        compareOriginalAndValidatedSequences();
    }

    private void createValidatedSequence() throws InvalidSequenceException {
        indexInSeq = validatedSequence.listIterator();
        Tile prevTile = indexInSeq.next();
        markColor(prevTile.color);
        while (indexInSeq.hasNext()) {
            Tile currTile = indexInSeq.next();
            prevTile = handleCurTile(prevTile, currTile);
        }
        addJokersToValidatedSequence();
    }
    
    private void compareOriginalAndValidatedSequences() throws InvalidSequenceException {
        ListIterator<Tile> origIt = originalSequence.listIterator(),
                            validatedIt = validatedSequence.listIterator();
        while(origIt.hasNext()){
            if(origIt.next().equals(validatedIt.next())== false){
                throw new InvalidSequenceException();
            }
        }
    }

    private Tile handleCurTile(Tile prevTile, Tile currTile)
            throws SequenceValidatorException, InvalidSequenceException {
        switch (getPrevCurrTileRelations(prevTile, currTile)) {
            case SAME_COLOR_FOLLOWING_VAL:
                if (isColorMarked(currTile.color) == false) {
                    throw new InvalidSequenceException();
                }
                isStraight = true;
                break;
            case SAME_COLOR_NOT_FOLLOWING_VAL:
                currTile = tryToUseJokerForStright(prevTile);
                isStraight = true;
                //Itarate back so we wont miss a tile.
                indexInSeq = validatedSequence.listIterator(indexInSeq.previousIndex());
                break;
            case DIFF_COLOR_SAME_VAL:
                if (isStraight || isColorMarked(currTile.color)) {
                    throw new InvalidSequenceException();
                }
                markColor(currTile.color);
                break;
            case DIFF_COLOR_NOT_SAME_VAL:
                throw new InvalidSequenceException();
            default:
                throw new SequenceValidatorException();
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
    
    private void addJokersToValidatedSequence() throws InvalidSequenceException {
        handleUnusedJokers();
        validatedSequence.addAll(jokersToAdd);
        Collections.sort(validatedSequence);
    }

    //Return false if it has been used before
    private boolean isColorMarked(Color color) {
        return unusedColors.contains(color) == false;
    }

    private void markColor(Color color) {
        unusedColors.remove(color);
    }

    private Tile tryToUseJokerForStright(Tile tile) throws InvalidSequenceException {
        if (jokers.isEmpty()) {
            throw new InvalidSequenceException();
        }

        Tile joker = morphJokerToTile(tile.color, tile.value + 1);
        jokersToAdd.push(joker);
        return joker;
    }

    private void tryToUseJokerForSameValueSequence() throws InvalidSequenceException {
        if (jokers.isEmpty() || unusedColors.isEmpty() || validatedSequence.isEmpty()) {
            throw new InvalidSequenceException();
        }

        Tile tile = morphJokerToTile(unusedColors.removeFirst(), validatedSequence.get(0).value);
        jokersToAdd.push(tile);
    }

    private JokerTile morphJokerToTile(Color color, int value) {
        JokerTile joker = jokers.pop();
        joker.setColor(color);
        joker.setValue(value);
        return joker;
    }

    private void handleUnusedJokers() throws InvalidSequenceException {
        while (jokers.isEmpty() == false) {
            if (isStraight) {
                if (validatedSequence.get(validatedSequence.size() - 1).value != 13) {
                    //Add to tail of straight
                    Tile tilePrev = validatedSequence.get(validatedSequence.size() - 1);
                    validatedSequence.add(morphJokerToTile(tilePrev.color, tilePrev.value + 1));
                } else {
                    //Add to head of straight
                    Tile tileNext = validatedSequence.get(0);
                    validatedSequence.add(0, morphJokerToTile(tileNext.color, tileNext.value - 1));
                }
            } else {
                tryToUseJokerForSameValueSequence();
            }
        }
    }

    private static class SequenceValidatorException extends RuntimeException {
    }
}
