package logic;

import java.util.ArrayList;
import java.util.List;
import static logic.tile.Color.*;
import logic.tile.Sequence;
import logic.tile.Tile;
import static logic.tile.TileGenerator.generate;
import static logic.tile.TileGenerator.generateJoker;
import static logic.tile.TileGenerator.generateRndTiles;
import static org.junit.Assert.*;
import org.junit.Test;

public class BoardTest {

    private Board board = new Board();

    private static Sequence generateRndSequence(int count) throws Exception {
        return new Sequence(generateRndTiles(count));
    }

    private void addToBoard(Tile... tiles) {
        board.addSequence(new Sequence(tiles));
    }

    private boolean compareTwoSequences(Sequence first, Sequence second) {
        if (first.getSize() != second.getSize())
            return false;

        for (int i = 0; i < first.getSize(); i++) {
            if (first.getTile(i).equals(second.getTile(i)) == false)
                return false;
        }

        return true;
    }

    @Test
    public void boardCreateFewSequences() throws Exception {
        board.reset();
        board.addSequence(generateRndSequence(4));
        board.addSequence(generateRndSequence(3));
        board.addSequence(generateRndSequence(5));
        board.addSequence(generateRndSequence(6));
        board.addSequence(generateRndSequence(3));
    }

    @Test
    public void MoveTileToEndSeccessfuly() throws Exception {
        board.reset();
        MoveTileData data = new MoveTileData();
        data.setSourceSequenceIndex(0);
        data.setSourceSequencePosition(2);
        data.setTargetSequenceIndex(1);
        data.setTargetSequencePosition(3);

        addToBoard(generate(Blue, 1), generate(Red, 4), generate(Black, 5));
        addToBoard(generate(Blue, 7), generate(Yellow, 11), generate(Red, 6));

        board.moveTile(data);

        Sequence resSeq1 = new Sequence(generate(Blue, 1), generate(Red, 4));
        Sequence resSeq2 = new Sequence(generate(Blue, 7), generate(Yellow, 11), generate(Red, 6), generate(Black, 5));

        assertTrue(compareTwoSequences(board.getSequence(0), resSeq1));
        assertTrue(compareTwoSequences(board.getSequence(1), resSeq2));
    }

    @Test
    public void MoveTileToFrontSeccessfuly() throws Exception {
        board.reset();
        MoveTileData data = new MoveTileData();
        data.setSourceSequenceIndex(0);
        data.setSourceSequencePosition(2);
        data.setTargetSequenceIndex(1);
        data.setTargetSequencePosition(0);

        addToBoard(generate(Blue, 1), generate(Red, 4), generate(Black, 5));
        addToBoard(generate(Blue, 7), generate(Yellow, 11), generate(Red, 6));

        board.moveTile(data);

        Sequence resSeq1 = new Sequence(generate(Blue, 1), generate(Red, 4));
        Sequence resSeq2 = new Sequence(generate(Black, 5), generate(Blue, 7), generate(Yellow, 11), generate(Red, 6));

        assertTrue(compareTwoSequences(board.getSequence(0), resSeq1));
        assertTrue(compareTwoSequences(board.getSequence(1), resSeq2));
    }

    @Test
    public void MoveTileAndSplitSeccessfuly() throws Exception {
        board.reset();
        MoveTileData data = new MoveTileData();
        data.setSourceSequenceIndex(0);
        data.setSourceSequencePosition(2);
        data.setTargetSequenceIndex(1);
        data.setTargetSequencePosition(2);

        addToBoard(generate(Blue, 1), generate(Red, 4), generate(Black, 5));
        addToBoard(generate(Blue, 7), generate(Yellow, 11), generate(Red, 6), generate(Red, 7), generate(Red, 8));

        board.moveTile(data);

        Sequence resSeq1 = new Sequence(generate(Blue, 1), generate(Red, 4));
        Sequence resSeq2 = new Sequence(generate(Blue, 7), generate(Yellow, 11), generate(Black, 5));
        Sequence resSeq3 = new Sequence(generate(Red, 6), generate(Red, 7), generate(Red, 8));

        assertTrue(compareTwoSequences(board.getSequence(0), resSeq1));
        assertTrue(compareTwoSequences(board.getSequence(1), resSeq2));
        assertTrue(compareTwoSequences(board.getSequence(2), resSeq3));
    }

    @Test
    public void addTileSeccessfuly() throws Exception {
        board.reset();
        addToBoard(generate(Blue, 1), generate(Red, 4), generate(Black, 5));

        board.addTile(0, 3, generate(Black, 13));

        Sequence resSeq1 = new Sequence(generate(Blue, 1), generate(Red, 4), generate(Black, 5), generate(Black, 13));
        assertTrue(compareTwoSequences(board.getSequence(0), resSeq1));
    }

    @Test
    public void validateBoardSeccessfuly() throws Exception {
        board.reset();

        addToBoard(
                generate(Blue, 1),
                generate(Blue, 2),
                generate(Blue, 3),
                generate(Blue, 4)
        );

        addToBoard(
                generate(Red, 7),
                generate(Blue, 7),
                generate(Black, 7)
        );

        addToBoard(
                generate(Red, 1),
                generate(Red, 2),
                generateJoker(),
                generate(Red, 4)
        );

        addToBoard(
                generate(Black, 4),
                generate(Black, 5),
                generateJoker(),
                generate(Black, 7),
                generateJoker(),
                generate(Black, 9),
                generate(Black, 10));

        assertTrue(board.isValid());

    }

    @Test
    public void whenBoardIsInvalid_returnFasle() throws Exception {
        board.reset();

        addToBoard(
                generate(Blue, 1),
                generate(Blue, 2),
                generate(Blue, 3),
                generate(Blue, 4)
        );

        addToBoard(
                generate(Red, 7),
                generate(Blue, 7),
                generate(Red, 7) //This tile is invalid!!!
        );

        addToBoard(
                generate(Red, 1),
                generate(Red, 2),
                generateJoker(),
                generate(Red, 4)
        );

        addToBoard(
                generate(Black, 4),
                generate(Black, 5),
                generateJoker(),
                generate(Black, 7),
                generateJoker(),
                generate(Black, 9),
                generate(Black, 10));

        assertFalse(board.isValid());
    }

    @Test
    public void whenAddTilePositionIsOutOfBound_ReturnFalse() throws Exception {
        board.reset();

        addToBoard(
                generate(Blue, 1),
                generate(Red, 4),
                generate(Black, 5));

        assertFalse(board.addTile(0, 4, generate(Black, 13)));
    }

    @Test
    public void whenAddTileIndexIsOutOfBound_ReturnFalse() throws Exception {
        board.reset();

        addToBoard(
                generate(Blue, 1),
                generate(Red, 4),
                generate(Black, 5));

        assertFalse(board.addTile(1, 3, generate(Black, 13)));
    }

    @Test
    public void RestoreBoardSeccessfully() throws Exception {
        List<Sequence> sequences = new ArrayList<>();
        sequences.add(generateRndSequence(4));
        sequences.add(generateRndSequence(7));
        sequences.add(generateRndSequence(2));
        sequences.add(generateRndSequence(5));
        sequences.add(generateRndSequence(3));

        board.reset();
        for (Sequence sequence : sequences) {
            board.addSequence(sequence.clone());
        }
        board.storeBackup();
        
        board.addSequence(generateRndSequence(4));
        board.removeTile(0, 2);
        board.removeTile(1, 5);
        board.removeTile(0, 0);
        
        board.restoreFromBackup();
        assertEquals(board.getSequences().size(),sequences.size());
        for (int i = 0; i < sequences.size(); i++) {
            assertTrue(compareTwoSequences(board.getSequence(i), sequences.get(i)));
        }
    }

}
