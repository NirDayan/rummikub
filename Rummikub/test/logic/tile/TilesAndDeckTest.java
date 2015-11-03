package logic.tile;

import logic.tile.Deck;
import logic.tile.JokerTile;
import logic.tile.Tile;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Lior
 */
public class TilesAndDeckTest {
 
    private Deck deck;

    @Before
    public void setUp() {
        deck = new Deck();
    }

    @Test
    public void CheckAllCardsExitInDeck() {
        int counter = 0;
        while (deck.hasMore()) {
            assertTrue(isTileValid(deck.pullTile()));
            counter++;
        }
        assertEquals(106, counter);
    }

    private boolean isTileValid(Tile t) {
        if (t.getValue() == JokerTile.JOKER_INITIAL_VALUE) {
            return true;
        }
        return (t.getValue() < 14)
                && (t.getValue() > 0);
    }
    
    @Test
    public void PullingFromDeckSeccessfuly() {
        Tile tile = deck.pullTile();
        assertTrue(isTileValid(tile));
    }
    
    @Test
    public void PullAllTilesFromDeckSeccessfuly() {
        int pulled =0;
        Deck testDeck = new Deck();
        while (testDeck.hasMore()){
            Tile tile = testDeck.pullTile();
            if (tile != null)
                pulled++;
        }
        assertEquals(pulled,106);
        assertFalse(testDeck.hasMore());
    }
    
}
