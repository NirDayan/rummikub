package logic.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    public static final int LOWEST_TILE_VALUE = 1;
    public static final int HIGHEST_TILE_VALUE = 13;
    public static final int INITIAL_TILES_IN_DECK_NUM = 106;
    private final List<Tile> deck;

    public Deck() {
        deck = new ArrayList<>();
        createAllTilesInDeck();
        Collections.shuffle(deck);
    }

    public Tile pullTile() {
        if (deck.isEmpty() == true)
            throw new DeckUnderflow();
        return deck.remove(0);
    }
    
    public boolean hasMore() {
        return (deck.isEmpty() == false);
    }

    private void createAllTilesInDeck() {
        //Iterate all tile types and add 2 of each type
        for (Color color : Color.values()) {
            for (int value = LOWEST_TILE_VALUE; value <= HIGHEST_TILE_VALUE; value++) {
                deck.add(new Tile(color, value));
                deck.add(new Tile(color, value));
            }
        }
        deck.add(new JokerTile());
        deck.add(new JokerTile());
    }

    public static class DeckUnderflow extends RuntimeException {
    }
}
