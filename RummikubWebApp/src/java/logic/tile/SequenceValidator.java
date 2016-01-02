package logic.tile;

import java.util.List;
import java.util.ListIterator;

class SequenceValidator {

    private List<Tile> tileList;
    private Color sequnceColor;
    private int currValue;
    private ListIterator<Tile> iterator;
    private int jokerCounter;

    public SequenceValidator(List tileList) {
        this.tileList = tileList;
    }

    public boolean isValid() {
        if (tileList.size() < 3) {
            return false;
        }
        return validateSameValues()
                || validateValuesAscending()
                || validateValuesDescending();
    }

    private boolean validateSameValues() {
        initIteratorAndCurrValue(tileList.listIterator(), -1);
        List unusedColors = Color.getColorsList();
        jokerCounter = 0;
        while (iterator.hasNext()) {
            Tile tile = iterator.next();

            if (tile.isJoker()) {
                jokerCounter++;
            }
            else if (currValue == -1) {
                setCurrValueAndSeqColor(tile);
                unusedColors.remove(tile.getColor());
            }
            else if (!isTileSameValueAndUnusedColor(unusedColors, tile)) {
                return false;
            }
            else {
                unusedColors.remove(tile.getColor());
            }
        }
        return unusedColors.size() >= jokerCounter;
    }

    private boolean validateValuesAscending() {
        initIteratorAndCurrValue(tileList.listIterator(), -1);
        jokerCounter = 0;

        while (iterator.hasNext()) {
            Tile tile = iterator.next();

            if (!isTileInStraightOrder(tile)) {
                return false;
            }
        }
        return true;
    }

    //Same logic as Ascending by iterating the sequecne backwards.
    private boolean validateValuesDescending() {
        initIteratorAndCurrValue(tileList.listIterator(tileList.size()), -1);
        jokerCounter = 0;

        while (iterator.hasPrevious()) {
            Tile tile = iterator.previous();

            if (!isTileInStraightOrder(tile)) {
                return false;
            }
        }
        return true;
    }

    private void initIteratorAndCurrValue(ListIterator<Tile> iterator, int valueIndex) {
        this.iterator = iterator;
        this.currValue = valueIndex;
    }

    private boolean isTileInStraightOrder(Tile tile) {
        if (!(tile.isJoker())) {
            if (!isNormalTileInStraight(tile)) {
                return false;
            }
        }
        else {
            jokerCounter++;
            if (currValue != -1) {
                if (currValue == 13) {
                    return false;
                }
                currValue++;
            }
        }
        return true;
    }

    private boolean isNormalTileInStraight(Tile tile) {
        if (currValue == -1) {
            setCurrValueAndSeqColor(tile);
            return true;
        }
        else if (currValue == 1 && jokerCounter > 0) {
            return false;
        }
        else if (tile.getColor() != sequnceColor
                || tile.getValue() != currValue + 1) {
            return false;
        }
        else {
            currValue++;
            return true;
        }
    }

    private void setCurrValueAndSeqColor(Tile tile) {
        //init value Index and sequenceColor
        currValue = tile.getValue();
        sequnceColor = tile.getColor();
    }

    private boolean isTileSameValueAndUnusedColor(List unusedColors, Tile tile) {
        return tile.getValue() == currValue
                && unusedColors.contains(tile.getColor());
    }
}
