package logic;

import java.util.ArrayList;
import java.util.List;
import static logic.tile.Color.*;
import logic.tile.Sequence;
import logic.tile.Tile;
import static logic.tile.TileGenerator.generate;
import static logic.tile.TileGenerator.generateJoker;
import org.junit.Test;
import static org.junit.Assert.*;

public class ComputerAITest {

    private ComputerAI comp;

    public ComputerAITest() {
        comp = new ComputerAI();
    }

    @Test
    public void getSetSeccessfully() throws Exception {
        List<Tile> tiles = new ArrayList<>();

        tiles.add(generate(Yellow, 6));
        tiles.add(generate(Black, 3));
        tiles.add(generate(Red, 4)); // 1
        tiles.add(generate(Blue, 6));
        tiles.add(generate(Black, 4)); // 2
        tiles.add(generate(Blue, 7));
        tiles.add(generate(Red, 10));
        tiles.add(generate(Red, 5));
        tiles.add(generate(Black, 2));
        tiles.add(generate(Blue, 4)); // 3
        tiles.add(generate(Yellow, 4)); //4 
        tiles.add(generate(Blue, 9));

        List<Tile> result = comp.getRelevantTiles(tiles);
        assertNotNull(result);
        Sequence seq = new Sequence(result);
        assertTrue(seq.isValid());
        assertEquals(seq.getValueSum(), 4*4);
    }

    @Test
    public void getSequenceSeccessfully() throws Exception {
        List<Tile> tiles = new ArrayList<>();

        tiles.add(generate(Yellow, 6));
        tiles.add(generate(Black, 3));
        tiles.add(generate(Red, 4));
        tiles.add(generate(Blue, 6));//1
        tiles.add(generate(Black, 4));
        tiles.add(generate(Blue, 7));//2
        tiles.add(generate(Red, 10));
        tiles.add(generate(Red, 5));
        tiles.add(generate(Blue, 9));//4
        tiles.add(generate(Black, 2));
        tiles.add(generate(Blue, 8)); //3
        tiles.add(generate(Red, 4));

        List<Tile> result = comp.getRelevantTiles(tiles);
        assertNotNull(result);
        Sequence seq = new Sequence(result);
        assertTrue(seq.isValid());
        assertEquals(result.size(), 4);
        assertEquals(seq.getValueSum(), 6+7+8+9);
    }

    @Test
    public void getSequenceSeccessfully2() throws Exception {
        List<Tile> tiles = new ArrayList<>();

        tiles.add(generate(Yellow, 6));
        tiles.add(generate(Black, 3));
        tiles.add(generate(Red, 4));
        tiles.add(generate(Blue, 6));
        tiles.add(generate(Black, 4));
        tiles.add(generate(Red, 7));
        tiles.add(generate(Red, 10));
        tiles.add(generate(Red, 5));
        tiles.add(generate(Blue, 9));
        tiles.add(generate(Black, 2));
        tiles.add(generate(Blue, 8));
        tiles.add(generate(Yellow, 4));

        List<Tile> result = comp.getRelevantTiles(tiles);
        assertNotNull(result);
        Sequence seq = new Sequence(result);
        assertTrue(seq.isValid());
        assertEquals(result.size(), 3);
        assertEquals(seq.getValueSum(), 4+4+4);
    }

    @Test
    public void getSequenceSeccessfullyWithJoker() throws Exception {
        List<Tile> tiles = new ArrayList<>();

        tiles.add(generate(Yellow, 6));
        tiles.add(generate(Black, 3));
        tiles.add(generate(Red, 5));
        tiles.add(generate(Blue, 12));
        tiles.add(generateJoker());
        tiles.add(generate(Red, 7));
        tiles.add(generate(Red, 10));
        tiles.add(generate(Red, 13));
        tiles.add(generate(Blue, 9));
        tiles.add(generate(Black, 2));
        tiles.add(generate(Blue, 1));
        tiles.add(generate(Red, 5));

        List<Tile> result = comp.getRelevantTiles(tiles);
        assertNotNull(result);
        Sequence seq = new Sequence(result);
        assertTrue(seq.isValid());
        assertEquals(result.size(), 3);
        assertEquals(seq.getValueSum(), 5+6+7);
    }

    @Test
    public void noReleventTilesFound() throws Exception {
        List<Tile> tiles = new ArrayList<>();

        tiles.add(generate(Yellow, 6));
        tiles.add(generate(Black, 5));
        tiles.add(generate(Red, 4));
        tiles.add(generate(Blue, 12));
        tiles.add(generateJoker());
        tiles.add(generate(Red, 7));
        tiles.add(generate(Red, 10));
        tiles.add(generate(Red, 13));
        tiles.add(generate(Blue, 9));
        tiles.add(generate(Black, 2));
        tiles.add(generate(Blue, 1));
        tiles.add(generate(Red, 4));

        List<Tile> result = comp.getRelevantTiles(tiles);
        assertNull(result);
    }
    
        @Test
    public void getSequenceSeccessfullyWithTwoJoker() throws Exception {
        List<Tile> tiles = new ArrayList<>();

        tiles.add(generate(Yellow, 6));
        tiles.add(generate(Black, 5));
        tiles.add(generate(Red, 4));
        tiles.add(generate(Blue, 12));
        tiles.add(generateJoker());
        tiles.add(generate(Red, 7));
        tiles.add(generate(Red, 10));
        tiles.add(generate(Red, 13));
        tiles.add(generateJoker());
        tiles.add(generate(Black, 2));
        tiles.add(generate(Blue, 1));
        tiles.add(generate(Red, 4));

        List<Tile> result = comp.getRelevantTiles(tiles);
        assertNotNull(result);
        Sequence seq = new Sequence(result);
        assertTrue(seq.isValid());
        assertEquals(result.size(), 4);
        assertEquals(seq.getValueSum(), 10+11+12+13);
    }

}
