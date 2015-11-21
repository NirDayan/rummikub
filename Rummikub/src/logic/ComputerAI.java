package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import logic.tile.Color;
import logic.tile.Tile;
import static logic.tile.Tile.JOKER_VALUE;

public class ComputerAI {

    private int jokerCounter;

    //Fields solely for findSequence method
    private int numOfUsedJokers;
    private int currVal;
    private ArrayList<Tile> maxSequenceTiles;
    private ArrayList<Tile> currSequence;
    private List<Tile> playerTiles;

    public List<Tile> getRelevantTiles(List<Tile> playerTiles) {
        this.playerTiles = playerTiles;
        jokerCounter = getJokersNumber();
        List<Tile> setResultTiles = findSet();
        List<Tile> sequenceResultTiles = findSequence();
        if ((setResultTiles != null) && (sequenceResultTiles != null)) {
            if (setResultTiles.size() > sequenceResultTiles.size()) {
                return setResultTiles;
            }
            else {
                return sequenceResultTiles;
            }
        }
        if (setResultTiles == null) {
            return sequenceResultTiles;
        }
        return setResultTiles;

    }

    private int getJokersNumber() {
        int counter = 0;
        for (Tile tile : playerTiles) {
            if (tile.isJoker()) {
                counter++;
            }
        }
        return counter;
    }

    private ArrayList<Tile> findSet() {
        ArrayList<Tile> set = new ArrayList<>();
        ArrayList<Tile> maxSet = new ArrayList<>();
        for (int i = 1; i < 14; i++) {
            for (Tile tile : playerTiles) {
                if (tile.getValue() == i) {
                    if (!set.contains(tile)) {
                        set.add(tile);
                    }
                }
            }
            if (set.size() + jokerCounter > 2) {
                if (set.size() > maxSet.size()) {
                    maxSet = (ArrayList<Tile>) set.clone();
                }
            }
            set.clear();
        }
        if (maxSet.size() + jokerCounter > 2) {
            Collections.sort(playerTiles);
            for (int j = 0; j < jokerCounter; j++) {
                maxSet.add(playerTiles.get(j));
            }
            return maxSet;
        }
        return null;
    }

    // Returns Sequecne Or null if not found.
    private ArrayList<Tile> findSequence() {
        Collections.sort(playerTiles);
        currSequence = new ArrayList<>();
        maxSequenceTiles = new ArrayList<>();
        numOfUsedJokers = 0;
        ArrayList<Tile> currColorTiles;

        for (Color color : Color.values()) {
            currColorTiles = getTilesByColor(playerTiles, color);
            resetCurrSequence();
            currVal = 0;
            for (Tile tile : currColorTiles) {
                if (currSequence.isEmpty() && tile.getValue() != JOKER_VALUE) {
                    addToCurrSequence(tile);
                }
                else if (currVal + 1 == tile.getValue()) {
                    addToCurrSequence(tile);
                }
                else if ((jokerCounter - numOfUsedJokers) == 1) {
                    // Try To Add next next to the sequence
                    if (currVal + 2 == tile.getValue()) {
                        currSequence.add(playerTiles.get(0));
                        addToCurrSequence(tile);
                        numOfUsedJokers++;
                    }
                    else {
                        saveToMaxSequnce();
                        resetCurrSequence();
                        addToCurrSequence(tile);
                    }
                }
                else if ((jokerCounter - numOfUsedJokers) == 2) {
                    // Try To Add next next to the sequence
                    if (currVal + 2 == tile.getValue()) {
                        currSequence.add(playerTiles.get(0));
                        addToCurrSequence(tile);
                        numOfUsedJokers++;
                    }
                    else if (currVal + 3 == tile.getValue()) {
                        currSequence.add(playerTiles.get(0));
                        currSequence.add(playerTiles.get(1));
                        addToCurrSequence(tile);
                        numOfUsedJokers += 2;
                    }
                } //end of sequence
                else {
                    saveToMaxSequnce();
                    resetCurrSequence();
                    addToCurrSequence(tile);
                }
            } // end for Current Color
            saveToMaxSequnce();
        }// end for All Colors
        if (maxSequenceTiles.size() > 2) {
            return maxSequenceTiles;
        }
        return null;
    }

    private void addToCurrSequence(Tile tile) {
        currSequence.add(tile);
        currVal = tile.getValue();
    }

    private ArrayList<Tile> getTilesByColor(List<Tile> tiles, Color color) {
        ArrayList<Tile> currColorTiles = new ArrayList<>();
        for (Tile currTile : tiles) {
            if (currTile.getColor().equals(color)) {
                if (currTile.getValue() > 0) {
                    if (!currColorTiles.contains(currTile)) {
                        currColorTiles.add(currTile);
                    }
                }
            }
        }
        return currColorTiles;
    }

    private void saveToMaxSequnce() {
        //If any unused jokers left, count them in.
        if (currSequence.size() + (jokerCounter - numOfUsedJokers) >= 3) {
            for (int joker = 0; joker < jokerCounter - numOfUsedJokers; joker++) {
                currSequence.add(playerTiles.get(0));
            }
        }
        
        if (currSequence.size() > 2 && currSequence.size() > maxSequenceTiles.size()) {
            maxSequenceTiles = (ArrayList<Tile>) currSequence.clone();
        }
    }

    private void resetCurrSequence() {
        numOfUsedJokers = 0;
        currSequence.clear();
    }
}
