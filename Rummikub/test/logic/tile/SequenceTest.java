package logic.tile;

import static logic.tile.Color.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class SequenceTest {

    private Sequence sequence;

    // Should Create Sequence Seccessfuly
    // ========================
    @Test
    public void sequenceCreatedSeccessfulyWithNoJokers() throws Exception {
        sequence = new Sequence(
                new Tile(Red, 10),
                new Tile(Red, 11),
                new Tile(Red, 12)
        );
        assertTrue(sequence.isValid());
        assertEquals(sequence.getSize(), 3);
        assertTrue(sequence.getValueSum() == 33);
        sequence = new Sequence(
                new Tile(Red, 5),
                new Tile(Blue, 5),
                new Tile(Black, 5),
                new Tile(Yellow, 5)
        );
        assertTrue(sequence.isValid());
        assertEquals(sequence.getSize(), 4);
        assertTrue(sequence.getValueSum() == 20);
    }

    @Test
    public void sequenceCreatedSeccessfulyWithOneJoker() throws Exception {
        sequence = new Sequence(
                new Tile(Black, 3),
                new Tile(Black, 0),
                new Tile(Black, 5)
        );
        assertTrue(sequence.isValid());
        assertEquals(sequence.getSize(), 3);
        assertTrue(sequence.getValueSum() == 12);

        sequence = new Sequence(
                new Tile(Red, 10),
                new Tile(Black, 0),
                new Tile(Black, 0)
        );
        assertTrue(sequence.isValid());
        assertEquals(sequence.getSize(), 3);
    }

    @Test
    public void createLongStraightWithJokersSeccessfuly() throws Exception {
        sequence = new Sequence(
                new Tile(Red, 1),
                new Tile(Red, 2),
                new Tile(Red, 3),
                new Tile(Black, 0),
                new Tile(Red, 5),
                new Tile(Red, 6),
                new Tile(Red, 7),
                new Tile(Red, 8),
                new Tile(Red, 9),
                new Tile(Red, 10),
                new Tile(Black, 0),
                new Tile(Red, 12),
                new Tile(Red, 13)
        );
        assertTrue(sequence.isValid());
        assertEquals(sequence.getSize(), 13);
        assertTrue(sequence.getValueSum() == 91);
    }

    @Test
    public void addOneJokerFromBelow() throws Exception {
        sequence = new Sequence(
                new Tile(Black, 0),
                new Tile(Black, 12),
                new Tile(Black, 13)
        );
        assertTrue(sequence.isValid());
//        assertTrue(sequence.getValueSum() == 36);
    }

    @Test
    public void addTwoJokerFromBelow() throws Exception {
        sequence = new Sequence(
                new Tile(Black, 0), //10
                new Tile(Black, 0), //11
                new Tile(Black, 12),
                new Tile(Black, 13)
        );
        assertTrue(sequence.isValid());
//        assertTrue(sequence.getValueSum() == 46);
    }

    @Test
    public void createSeqWith2GoodTilesAndOneJoker() throws Exception {
        sequence = new Sequence(
                new Tile(Red, 3),
                new Tile(Red, 4),
                new Tile(Black, 0)
        );
        assertTrue(sequence.isValid());
        assertEquals(sequence.getSize(), 3);
        assertTrue(sequence.getValueSum() == 12);
        sequence = new Sequence(
                new Tile(Red, 5),
                new Tile(Blue, 5),
                new Tile(Black, 5),
                new Tile(Black, 0)
        );
        assertTrue(sequence.isValid());
        assertEquals(sequence.getSize(), 4);
        assertTrue(sequence.getValueSum() == 20);
    }

    @Test
    public void createDescendingSequence() throws Exception {
        sequence = new Sequence(
                new Tile(Red, 6),
                new Tile(Red, 5),
                new Tile(Black, 0)
        );
        assertTrue(sequence.isValid());
        assertEquals(sequence.getSize(), 3);
        assertTrue(sequence.getValueSum() == 15);
        sequence = new Sequence(
                new Tile(Blue, 5),
                new Tile(Black, 0),
                new Tile(Blue, 3),
                new Tile(Black, 0)
        );
        assertTrue(sequence.isValid());
        assertEquals(sequence.getSize(), 4);
        assertTrue(sequence.getValueSum() == 14);
    }

    // Should Return False
    // ========================
    @Test
    public void whenInvalidSequenceCreated_ReturnFalse1() throws Exception {
        sequence = new Sequence(
                new Tile(Red, 10),
                new Tile(Blue, 11),
                new Tile(Red, 12)
        );
        assertFalse(sequence.isValid());
    }

    @Test
    public void whenInvalidSequenceCreated_ReturnFalse2() throws Exception {
        sequence = new Sequence(
                new Tile(Yellow, 5),
                new Tile(Blue, 3),
                new Tile(Black, 8)
        );
        assertFalse(sequence.isValid());
    }

    @Test
    public void FourOfASameKindWithJoker_ReturnFalse() throws Exception {
        sequence = new Sequence(
                new Tile(Red, 2),
                new Tile(Blue, 2),
                new Tile(Black, 2),
                new Tile(Yellow, 2),
                new Tile(Black, 0)
        );
        assertFalse(sequence.isValid());
    }

    @Test
    public void BadStraightWithJoker_ReturnFalse() throws Exception {
        sequence = new Sequence(
                new Tile(Red, 2),
                new Tile(Red, 3),
                new Tile(Red, 6),
                new Tile(Black, 0)
        );
        assertFalse(sequence.isValid());
    }

    @Test
    public void BadDescendingStraightWithJoker_ReturnFalse() throws Exception {
        sequence = new Sequence(
                new Tile(Black, 0),
                new Tile(Red, 9),
                new Tile(Red, 8),
                new Tile(Red, 6)
        );
        assertFalse(sequence.isValid());
    }

    @Test
    public void BadDescendingStraightWithJoker_ReturnFalse2() throws Exception {
        sequence = new Sequence(
                new Tile(Red, 3),
                new Tile(Red, 2),
                new Tile(Red, 1),
                new Tile(Black, 0)
        );
        assertFalse(sequence.isValid());
    }
    
        @Test
    public void BadAscStraightWithJoker_ReturnFalse() throws Exception {
        sequence = new Sequence(
                new Tile(Red, 12),
                new Tile(Red, 13),
                new Tile(Black, 0)
        );
        assertFalse(sequence.isValid());
    }
}
