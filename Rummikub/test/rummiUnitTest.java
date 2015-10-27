import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import rummikub.GameDetails;
import rummikub.Rummikub;

public class rummiUnitTest {
    private Rummikub rummikub;
    
    @Before
    public void setUp() {
        rummikub = new Rummikub();
        rummikub.createGame("Game1", 2, 1);
    }
    
    @Test
    public void GameWasCreatedSuccessfully(){
        GameDetails gameDetails = rummikub.getGameDetails("Game1");
        assertEquals(2,gameDetails.getComputerPlayersNum());
        assertEquals(1,gameDetails.getHumenPlayersNum());
        assertEquals("Game1", gameDetails.getGameName());
    }
    
}
