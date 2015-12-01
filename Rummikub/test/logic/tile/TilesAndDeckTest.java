package logic.tile;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TilesAndDeckTest {

    private Deck deck;

    private boolean isTileValid(Tile t) {
        if (t.isJoker()) {
            return true;
        }
        return (t.getValue() <= Deck.HIGHEST_TILE_VALUE)
                && (t.getValue() >= Deck.LOWEST_TILE_VALUE);
    }

    @Before
    public void setUp() {
        deck = new Deck();
    }

    @Test
    public void CheckAllTilesExistInDeck() {
        int counter = 0;
        while (!deck.isEmpty()) {
            assertTrue(isTileValid(deck.pullTile()));
            counter++;
        }
        assertEquals(106, counter);
    }

    @Test
    public void pullingFromDeckSeccessfuly() {
        Tile tile = deck.pullTile();
        assertTrue(isTileValid(tile));
    }

    @Test
    public void pullAllTilesFromDeckSeccessfuly() {
        int pulled = 0;
        while (!deck.isEmpty()) {
            Tile tile = deck.pullTile();
            if (tile != null)
                pulled++;
        }
        assertEquals(pulled, 106);
        assertFalse(!deck.isEmpty());
    }

    @Test
    public void whenNoTilesInDeckAndPulling_throwUnderFlow() {
        while (!deck.isEmpty())
            deck.pullTile();

        assertNull(deck.pullTile());
    }
}
