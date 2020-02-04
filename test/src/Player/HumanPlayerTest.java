package Player;

import model.ComputerPlayer;
import model.Game;
import model.HumanPlayer;
import model.Player;

import org.junit.Test;
import org.junit.runner.Computer;

import static org.junit.Assert.*;


public class HumanPlayerTest {
    @Test
    public void testIsComputer() {
        // Human Player
        Player h = new HumanPlayer();
        assertEquals(true, h.isHuman());

        // Computer player turned into human player
        Player c = new ComputerPlayer();
        c = new HumanPlayer();
        assertEquals(true, c.isHuman());
    }

    @Test
    public void testUpdateGame() {
        // Human against human
        Game g = new Game();
        Player h1 = new HumanPlayer();
        Player h2 = new HumanPlayer();
        assertEquals(true, g.isP1Turn());
        h1.updateGame(g);   // does nothing
        g.move(8, 12);    // We have to move our human h1
        assertEquals(false, g.isP1Turn());
        h2.updateGame(g);   //does nothing
        g.move(22, 18);     // We have to move our human h2
        assertEquals(true, g.isP1Turn());

        // Human against computer
        Game g2 = new Game();
        Player h3 = new HumanPlayer();
        Player c4 = new ComputerPlayer();
        assertEquals(true, g2.isP1Turn());
        h3.updateGame(g2);   // does nothing
        g2.move(8, 12);    // We have to move our human h3
        assertEquals(false, g2.isP1Turn());
        c4.updateGame(g2);
        assertEquals(true, g2.isP1Turn());
    }

    @Test
    public void testToString() {
        Player h = new HumanPlayer();
        assertEquals("HumanPlayer[isHuman=true]", h.toString());
    }


}
