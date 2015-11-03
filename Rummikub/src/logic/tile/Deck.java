package logic.tile;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    public static final int LOWEST_TILE_VALUE = 1;
    public static final int HIGHEST_TILE_VALUE = 13;
    private final ArrayList<Tile> deck;

    public Deck() {
        deck = new ArrayList<>();
        createAllTilesInDeck();
        Collections.shuffle(deck);
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

    public boolean hasMore() {
        return (deck.isEmpty() == false);
    }

    public Tile pullTile() {
        return deck.remove(0);
    }

}
