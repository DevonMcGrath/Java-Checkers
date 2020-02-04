package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    @Test
    void restart() {
        Game g = new Game();

        g.move(5,2);
        g.restart();

        assertEquals(true, g.isP1Turn());
    }

    @Test
    void copy() {
        Game g1 = new Game();

        g1.move(8, 12);
        g1.setP1Turn(true);

        Game g2 = new Game();

        g2.move(9,13);
        g2.setP1Turn(false);

        g2 = g1.copy();

        assertEquals(g1.getBoard().toString(), g2.getBoard().toString());
    }

    @Test
    void move() {
        Game g1 = new Game();

        g1.move(5,2);

        assertEquals(false, MoveLogic.isValidMove(g1, 5, 2));

        assertEquals(true, MoveLogic.isValidMove(g1, 8, 12));

        g1.move(8,12);

        assertEquals(false, g1.isP1Turn());

        Game g2 = new Game();

        assertNotEquals(g2.getBoard().toString(), g1.getBoard().toString() );
    }

}
