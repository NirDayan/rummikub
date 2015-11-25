package logic.persistency;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import logic.Game;
import logic.GameDetails;
import logic.PlayerDetails;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;

public class PersistencyTest {
    private static final String TEST_FILES_PATH = "Rummikub\\test\\logic\\resources\\";
    private static final File testsFolder = new File(TEST_FILES_PATH);
    private static FileDetails pathToSave;

    @BeforeClass
    public static void beforeClass() {
        pathToSave = new FileDetails(testsFolder.getAbsolutePath(), "GeneratedBySave.xml", true);
    }

    @Test
    public void LoadSeccessfully() throws Exception {
        FileDetails fileDetails = new FileDetails(testsFolder.getAbsolutePath(), "test1.xml", true);
        Game game = GamePersistency.load(fileDetails);
        assertNotNull(game);
        assertEquals(game.getPlayers().size(), 4);
    }

    @Test(expected = Exception.class)
    public void LoadInvalidFile() throws Exception {
        FileDetails fileDetails = new FileDetails(testsFolder.getAbsolutePath(), "test2.xml", true);
        GamePersistency.load(fileDetails);
    }

    @Test(expected = GamePersistency.PersistencyException.class)
    public void LoadFileWithLogicalErrors() throws Exception {
        FileDetails fileDetails = new FileDetails(testsFolder.getAbsolutePath(), "test3.xml", true);
        GamePersistency.load(fileDetails);
    }

    @Test
    public void SaveFileSeccessfully() throws Exception {
        List<PlayerDetails> playersDetails = new ArrayList<>();
        playersDetails.add(new PlayerDetails(0, "Test Player 1", true));
        playersDetails.add(new PlayerDetails(0, "Test Player 2", true));
        playersDetails.add(new PlayerDetails(0, "Test Player 3", false));

        GameDetails gameDetails = new GameDetails("TestName", playersDetails, new FileDetails(null, null, true));
        Game game = new Game(gameDetails);
        game.reset();

        GamePersistency.save(pathToSave, game);
    }

    @Test
    public void LoadSeccessfuly_CheckPlacedFirstSeq() throws Exception {
        FileDetails fileDetails = new FileDetails(testsFolder.getAbsolutePath(), "test1.xml", true);
        Game game = GamePersistency.load(fileDetails);
        assertNotNull(game);
        assertTrue(game.getCurrentPlayer().isFirstStep());
    }

    @AfterClass
    public static void afterClass() {
        File fileToDelete = new File(pathToSave.getFullPath());
        assertTrue(fileToDelete.delete());
    }

}
