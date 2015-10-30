package logic;

import java.util.ArrayList;
import java.util.Collections;

public class TilesDeck {

    public static final int LOWEST_TILE_VALUE = 1;
    public static final int HIGHEST_TILE_VALUE = 13;
    private ArrayList<Tile> deck;

    public TilesDeck() {
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
        deck.add(new Tile(Color.Joker));
        deck.add(new Tile(Color.Joker));
    }
    

}
