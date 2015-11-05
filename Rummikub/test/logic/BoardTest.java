package logic;

import static logic.tile.Color.*;
import logic.tile.Sequence;
import logic.tile.Tile;
import static logic.tile.TileGenerator.generate;
import static logic.tile.TileGenerator.generateJoker;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class BoardTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board();
    }

    @Test
    public void boardCreateSequenceSeccessfuly() throws Exception {
        int index = -1;
        index = board.createSequence(generate(4));
        assertTrue(index >= 0);
    }

    @Test
    public void boardCreateFewSequences() throws Exception {
        board = new Board();
        board.createSequence(generate(4));
        board.createSequence(generate(3));
        board.createSequence(generate(5));
        board.createSequence(generate(6));
        int index = board.createSequence(generate(2));
        assertEquals(index, 4);
    }

    @Test
    public void MoveTileSeccessfuly() throws Exception {
        board = new Board();
        MoveTileData data = new MoveTileData();
        data.setSourceSequenceIndex(0);
        data.setSourceSequencePosition(2);
        data.setTargetSequenceIndex(1);
        data.setTargetSequencePosition(1);

        board.createSequence(
                new Tile[]{generate(Blue, 1),
                    generate(Red, 4),
                    generate(Black, 5)});

        board.createSequence(
                new Tile[]{generate(Blue, 7),
                    generate(Yellow, 11),
                    generate(Red, 6)});

        board.moveTile(data);

        Tile[] resSeq1 = {generate(Blue, 1), generate(Red, 4)};
        Tile[] resSeq2 = {generate(Blue, 7), generate(Black, 5), generate(Yellow, 11), generate(Red, 6)};

        assertEquals(board.getSequence(0).size(), 2);
        assertEquals(board.getSequence(1).size(), 4);
        assertArrayEquals(board.getSequence(0).toArray(), resSeq1);
        assertArrayEquals(board.getSequence(1).toArray(), resSeq2);
    }

    @Test
    public void addTileSeccessfuly() throws Exception {
        board = new Board();
        AddTileData data = new AddTileData();
        data.setSequenceIndex(0);
        data.setSequencePosition(3);
        data.setTile(generate(Black, 13));

        board.createSequence(
                new Tile[]{generate(Blue, 1),
                    generate(Red, 4),
                    generate(Black, 5)});

        board.addTile(data);

        Tile[] resSeq1 = {generate(Blue, 1), generate(Red, 4), generate(Black, 5), generate(Black, 13)};
        assertArrayEquals(board.getSequence(0).toArray(), resSeq1);
    }

    @Test
    public void finishTurnSeccessfuly() throws Exception {
        board = new Board();

        board.createSequence(
                new Tile[]{
                    generate(Blue, 1),
                    generate(Blue, 2),
                    generate(Blue, 3),
                    generate(Blue, 4)
                });

        board.createSequence(
                new Tile[]{
                    generate(Red, 7),
                    generate(Blue, 7),
                    generate(Black, 7)
                });

        board.createSequence(
                new Tile[]{
                    generate(Red, 1),
                    generate(Red, 2),
                    generateJoker(),
                    generate(Red, 4)
                });

        board.createSequence(
                new Tile[]{
                    generate(Black, 4),
                    generate(Black, 5),
                    generateJoker(),
                    generate(Black, 7),
                    generateJoker(),
                    generate(Black, 9),
                    generate(Black, 10),});

        board.finishTurn();

    }

    @Test (expected = Sequence.InvalidSequenceException.class)
    public void whenBoardIsInvalid_throwException() throws Exception {
        board = new Board();

        board.createSequence(
                new Tile[]{
                    generate(Blue, 1),
                    generate(Blue, 2),
                    generate(Blue, 3),
                    generate(Blue, 4)
                });

        board.createSequence(
                new Tile[]{
                    generate(Red, 7),
                    generate(Blue, 7),
                    generate(Red, 7) //This tile is invalid!!!
                });

        board.createSequence(
                new Tile[]{
                    generate(Red, 1),
                    generate(Red, 2),
                    generateJoker(),
                    generate(Red, 4)
                });

        board.createSequence(
                new Tile[]{
                    generate(Black, 4),
                    generate(Black, 5),
                    generateJoker(),
                    generate(Black, 7),
                    generateJoker(),
                    generate(Black, 9),
                    generate(Black, 10),});

        board.finishTurn();
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void whenAddTilePositionIsOutOfBound_ThrowException() throws Exception {
        board = new Board();
        AddTileData data = new AddTileData();
        data.setSequenceIndex(0);
        data.setSequencePosition(4);//Invalid Position
        data.setTile(generate(Black, 13));

        board.createSequence(
                new Tile[]{generate(Blue, 1),
                    generate(Red, 4),
                    generate(Black, 5)});

        board.addTile(data);
    }
    
     @Test(expected = Board.sequenceNotFoundException.class)
    public void whenAddTileIndexIsOutOfBound_ThrowException() throws Exception {
        board = new Board();
        AddTileData data = new AddTileData();
        data.setSequenceIndex(1);//Invalid Index!!
        data.setSequencePosition(3);
        data.setTile(generate(Black, 13));

        board.createSequence(
                new Tile[]{generate(Blue, 1),
                    generate(Red, 4),
                    generate(Black, 5)});

        board.addTile(data);
    }
}
