package logic.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    public static final int LOWEST_TILE_VALUE = 1;
    public static final int HIGHEST_TILE_VALUE = 13;
    private final List<Tile> deck;

    public Deck() {
        deck = new ArrayList<>();
        reset();
    }

    public Tile pullTile() {
        if (deck.isEmpty() == true)
            throw new DeckUnderflow();
        return deck.remove(0);
    }

    // Returns null if not exist in deck
    public Tile pullTile(Color color, int value) {
        Tile tileToPull = new Tile(color, value);
        if (deck.isEmpty() == true || deck.contains(tileToPull) == false)
            return null;
        deck.remove(tileToPull);
        return tileToPull;
    }

    public boolean isEmpty() {
        return deck.isEmpty();
    }

    public void reset() {
        deck.clear();
        createAllTilesInDeck();
        Collections.shuffle(deck);
    }

    private void createAllTilesInDeck() {
        //Iterate all tile types and add 2 of each type
        for (Color color : Color.values()) {
            for (int value = LOWEST_TILE_VALUE; value <= HIGHEST_TILE_VALUE; value++) {
                Tile tile = new Tile(color, value);
                deck.add(tile);
                deck.add(tile);
            }
        }
        Tile joker = new Tile(Color.Black, Tile.JOKER_VALUE);
        deck.add(joker);
        deck.add(joker);
    }

    public static class DeckUnderflow extends RuntimeException {
    }
}
