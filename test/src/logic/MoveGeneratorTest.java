package logic;

import model.Board;
import model.Move;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MoveGeneratorTest {

    @Test
    void getMoves() {
        //Test the get moves function
        Board board = new Board();
        Point start = new Point(0,0);
        MoveGenerator mg = new MoveGenerator();
        List<Point> points = mg.getMoves(board, start);
        //System.out.println(points.toString());
        assertEquals(points.size(), 0);

        start = new Point(0, 5);
        points = mg.getMoves(board, start);
        //System.out.println(points.toString());
        assertEquals(points.size(), 1);
    }

//    @Test
//    void getSkips() {
//        //Test the get skips function
//        Board board = new Board();
//        Point start = new Point(0,0);
//        MoveGenerator mg = new MoveGenerator();
//        List<Point> points = mg.getSkips(board, start);
//        System.out.println(points.toString());
//
//        //move 20 to 13
//        Point s = new Point(0, 5);
//        Point e = new Point(1, 4);
//        Move m = new Move(s, e);
//
//        s = new Point(1, 4);
//        e = new Point(2, 3);
//        m = new Move(s, e);
//
//        //check 9 for jumps/skips
//        start = new Point(2, 2);
//        points = mg.getSkips(board, start);
//        System.out.println(points.toString());
//    }
//
//    @Test
//    void isValidSkip() {
//    }

    @Test
    void addPoints() {
        //Test the add points function
        Board board = new Board();
        MoveGenerator mg = new MoveGenerator();

        Point p1 = new Point(0,0);
        Point p2 = new Point(1,1);
        List<Point> points = new ArrayList<Point>();
        points.add(p1);
        points.add(p2);

        Point toAdd = new Point(6,6);
        mg.addPoints(points, toAdd, Board.BLACK_CHECKER, 1);
        assertEquals(points.size(), 4);

        toAdd = new Point(5,5);
        mg.addPoints(points, toAdd, Board.WHITE_CHECKER, 2);
        assertEquals(points.size(), 6);
    }
}