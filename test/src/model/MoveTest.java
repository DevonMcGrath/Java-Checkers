package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveTest {
    /**
     * test to check if two moves are equal
     */
    @Test
    void TestMoveConstruction() {
        Move m = new Move(9, 12);
        Move m1 = new Move(9, 13);
        assertNotEquals(m, m1);
    }

    /**
     * check if it gets the correct start index
     */
    @Test
    void getStartIndex() {
        Move m = new Move(1, 3);
        assertEquals(m.getStartIndex(), 1);
        assertNotEquals(m.getStartIndex(), 3);

    }

    /**
     * check by setting a new start index
     */
    @Test
    void setStartIndex() {
        Move m = new Move(9, 13);
        assertEquals(m.getStartIndex(), 9);
        m.setStartIndex(10);
        assertEquals(m.getStartIndex(), 10);
        assertNotEquals(m.getStartIndex(), 9);


    }

    /**
     * check the end index of the move
     */
    @Test
    void getEndIndex() {
        Move m = new Move(9, 12);
        assertEquals(m.getEndIndex(), 12);
        assertNotEquals(m.getEndIndex(), 11);
    }

    /**
     * check by setting a new end index
     */
    @Test
    void setEndIndex() {

        Move m = new Move(9, 13);
        assertEquals(m.getEndIndex(), 13);

        m.setEndIndex(10);
        assertEquals(m.getEndIndex(), 10);
        assertNotEquals(m.getEndIndex(), 13);
    }
}