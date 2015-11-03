package logic.tile;

import static logic.tile.Color.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class SequenceTest {

    // Should Create Sequence Seccessfuly
    // ========================
    @Test
    public void sequenceCreatedSeccessfulyWithNoJokers() throws Exception {
        Sequence sequence = new Sequence(
                new Tile(Red, 10),
                new Tile(Red, 11),
                new Tile(Red, 12)
        );
        assertEquals(sequence.getSize(), 3);
        assertTrue(sequence.getValueSum() == 33);
        sequence = new Sequence(
                new Tile(Red, 5),
                new Tile(Blue, 5),
                new Tile(Black, 5),
                new Tile(Yellow, 5)
        );
        assertEquals(sequence.getSize(), 4);
        assertTrue(sequence.getValueSum() == 20);
    }

    @Test
    public void sequenceCreatedSeccessfulyWithOneJoker() throws Exception {
        Sequence sequence = new Sequence(
                new Tile(Black, 3),
                new JokerTile(),
                new Tile(Black, 5)
        );
        assertEquals(sequence.getSize(), 3);
        assertTrue(sequence.getValueSum() == 12);

        sequence = new Sequence(
                new Tile(Red, 10),
                new JokerTile(),
                new JokerTile()
        );
        assertEquals(sequence.getSize(), 3);
    }

    @Test
    public void createLongStraightWithJokersSeccessfuly() throws Exception {
        Sequence sequence = new Sequence(
                new Tile(Red, 1),
                new Tile(Red, 2),
                new Tile(Red, 3),
                new Tile(Red, 5),
                new Tile(Red, 6),
                new Tile(Red, 7),
                new Tile(Red, 8),
                new Tile(Red, 9),
                new Tile(Red, 10),
                new Tile(Red, 12),
                new Tile(Red, 13),
                new JokerTile(),
                new JokerTile()
        );
        assertEquals(sequence.getSize(), 13);
        assertTrue(sequence.getValueSum() == 91);
    }

    @Test
    public void addOneJokerFromBelow() throws Exception{
        Sequence sequence = new Sequence(
                new Tile(Black, 12),
                new Tile(Black, 13),
                new JokerTile()
        );
        assertTrue(sequence.getValueSum() == 36);
    }

    @Test
    public void addTwoJokerFromBelow() throws Exception {
        Sequence sequence = new Sequence(
                new Tile(Black, 12),
                new Tile(Black, 13),
                new JokerTile(), //10
                new JokerTile() //11
        );
        assertTrue(sequence.getValueSum() == 46);
    }

    @Test
    public void createSeqWith2GoodTilesAndOneJoker() throws Exception {
        Sequence sequence = new Sequence(
                new Tile(Red, 3),
                new Tile(Red, 4),
                new JokerTile()
        );
        assertEquals(sequence.getSize(), 3);
        sequence = new Sequence(
                new Tile(Red, 5),
                new Tile(Blue, 5),
                new Tile(Black, 5),
                new JokerTile()
        );
        assertEquals(sequence.getSize(), 4);
    }

    // Should Throw Exceptions
    // ========================
    @Test(expected = Sequence.InvalidSequenceException.class)
    public void whenInvalidSequenceCreated_ThrowException1() throws Exception {
        Sequence sequence = new Sequence(
                new Tile(Red, 10),
                new Tile(Blue, 11),
                new Tile(Red, 12)
        );

    }

    @Test(expected = Sequence.InvalidSequenceException.class)
    public void whenInvalidSequenceCreated_ThrowException2() throws Exception {
        Sequence sequence = new Sequence(
                new Tile(Yellow, 5),
                new Tile(Blue, 3),
                new Tile(Black, 8)
        );
    }

    @Test(expected = Sequence.InvalidSequenceException.class)
    public void FourOfASameKindWithJoker_ThrowException() throws Exception {
        Sequence sequence = new Sequence(
                new Tile(Red, 2),
                new Tile(Blue, 2),
                new Tile(Black, 2),
                new Tile(Yellow, 2),
                new JokerTile()
        );
    }

    @Test(expected = Sequence.InvalidSequenceException.class)
    public void BadStraightWithJoker_ThrowExc() throws Exception {
        Sequence sequence = new Sequence(
                new Tile(Red, 2),
                new Tile(Red, 3),
                new Tile(Red, 6),
                new JokerTile()
        );
    }
}
