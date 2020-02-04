package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    @Test
    void copy() {
        Board b1 = new Board();

        b1.set(12, 5);

        Board b2 = b1.copy();

        assertEquals(b1.toString(), b2.toString());
    }

    @Test
    void reset() {
        Board b1 = new Board();
        b1.set(12, 5);

        b1.reset();

        Board b2 = new Board();

        assertEquals(b1.toString(), b2.toString());
    }

    @Test
    void isValidIndex() {
        Board b1 = new Board();
        assertEquals(true, b1.isValidIndex(5));
        assertNotEquals(true, b1.isValidIndex(33));
    }

}