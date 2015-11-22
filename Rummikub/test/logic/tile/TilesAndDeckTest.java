package logic.tile;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TilesAndDeckTest {

    private Deck deck;

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

    private boolean isTileValid(Tile t) {
        if (t.isJoker()) {
            return true;
        }
        return (t.getValue() <= Deck.HIGHEST_TILE_VALUE)
                && (t.getValue() >= Deck.LOWEST_TILE_VALUE);
    }

    @Test
    public void pullingFromDeckSeccessfuly() {
        Tile tile = deck.pullTile();
        assertTrue(isTileValid(tile));
    }

    @Test
    public void pullAllTilesFromDeckSeccessfuly() {
        int pulled = 0;
        Deck testDeck = new Deck();
        while (!testDeck.isEmpty()) {
            Tile tile = testDeck.pullTile();
            if (tile != null)
                pulled++;
        }
        assertEquals(pulled, 106);
        assertFalse(!testDeck.isEmpty());
    }

    @Test
    public void whenNoTilesInDeckAndPulling_throwUnderFlow() {
        Deck testDeck = new Deck();
        while (!testDeck.isEmpty())
            testDeck.pullTile();

        assertNull(testDeck.pullTile());
    }

}
