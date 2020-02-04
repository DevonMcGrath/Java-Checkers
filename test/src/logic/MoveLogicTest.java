package logic;

import model.Board;
import model.Game;
import java.awt.Point;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveLogicTest {

    @Test
    void isValidMove() {
        Game g = new Game();
        //test first move
        boolean valid = MoveLogic.isValidMove(g, 8, 12);
        assertTrue(valid);

        boolean valid4 = MoveLogic.isValidMove(g, 10, 14);
        assertTrue(valid4);

        //test invalid move
        boolean valid1 = MoveLogic.isValidMove(g, 8, 4);
        assertFalse(valid1);

        boolean valid7 = MoveLogic.isValidMove(g, 26, 22);
        assertFalse(valid7);

        //no piece to move
        boolean valid3 = MoveLogic.isValidMove(g, 14, 18);
        assertFalse(valid3);

        boolean valid2 = MoveLogic.isValidMove(g, 13, 21);
        assertFalse(valid1);

        //test move to a spot with a piece
        boolean valid5 = MoveLogic.isValidMove(g, 4, 8);
        assertFalse(valid5);
    }

    @Test
    void testIsValidMove() {

        //test if there is any board
        Board b = null;
        boolean isP1Turn = true;
        boolean valid = MoveLogic.isValidMove(b, isP1Turn, 8, 12, 33);
        assertFalse(valid);

        //test if the move is valie
        Board b1 = new Board();
        isP1Turn = true;
        valid = MoveLogic.isValidMove(b1, isP1Turn, 8, 12, 33);
        assertTrue(valid);


        //case when it is not p1's turn
        Board b2 = new Board();
        isP1Turn = false;
        valid = MoveLogic.isValidMove(b2, isP1Turn, 8, 12, 33);
        assertFalse(valid);

        //case when the move is invalid (moving to invalid spot)
        Board b3 = new Board();
        isP1Turn = true;
        valid = MoveLogic.isValidMove(b3, isP1Turn, 8, 9, 33);
        assertFalse(valid);


    }

}