package logic.tile;

import java.util.List;
import java.util.ListIterator;

class SequenceValidator {

    private List<Tile> tileList;
    private Color sequnceColor;
    private int valueIndex;
    private ListIterator<Tile> iterator;

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
        initIteratorAndValueIndex(tileList.listIterator(), -1);
        List unusedColors = Color.getColorsList();
        int jokerCounter = 0;
        while (iterator.hasNext()) {
            Tile tile = iterator.next();

            if (tile.isJoker()) {
                jokerCounter++;
            } else if (valueIndex == -1) {
                setValueIndexAndSeqColor(tile);
                unusedColors.remove(tile.getColor());
            } else if (!isTileSameValueAndUnusedColor(unusedColors, tile)) {
                return false;
            } else {
                unusedColors.remove(tile.getColor());
            }
        }
        return unusedColors.size() >= jokerCounter;
    }

    private boolean validateValuesAscending() {
        initIteratorAndValueIndex(tileList.listIterator(), -1);
        while (iterator.hasNext()) {
            Tile tile = iterator.next();

            if (!isTileInStraightOrder(tile)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateValuesDescending() {
        initIteratorAndValueIndex(tileList.listIterator(tileList.size()), -1);
        while (iterator.hasPrevious()) {
            Tile tile = iterator.previous();

            if (!isTileInStraightOrder(tile)) {
                return false;
            }
        }
        return true;
    }

    private void initIteratorAndValueIndex(ListIterator<Tile> iterator, int valueIndex) {
        this.iterator = iterator;
        this.valueIndex = valueIndex;
    }

    private boolean isTileInStraightOrder(Tile tile) {
        if (!(tile.isJoker())) {
            if (!isNormalTileInStraight(tile)) {
                return false;
            }
        } else if (valueIndex != -1) {
            valueIndex++;
        }
        return true;
    }

    private boolean isNormalTileInStraight(Tile tile) {
        if (valueIndex == -1) {
            setValueIndexAndSeqColor(tile);
            return true;
        }
        if (tile.getColor() != sequnceColor
                || tile.getValue() != valueIndex + 1) {
            return false;
        }
        valueIndex++;
        return true;
    }

    private void setValueIndexAndSeqColor(Tile tile) {
        //init value Index and sequenceColor
        valueIndex = tile.getValue();
        sequnceColor = tile.getColor();
    }

    private boolean isTileSameValueAndUnusedColor(List unusedColors, Tile tile) {
        return tile.getValue() == valueIndex
                && unusedColors.contains(tile.getColor());
    }
}
