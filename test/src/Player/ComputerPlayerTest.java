package Player;

import logic.MoveLogic;
import model.ComputerPlayer;
import model.Game;
import model.HumanPlayer;
import model.Player;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComputerPlayerTest {
    @Test
    public void testIsHuman(){
        // Computer Player
        Player c = new ComputerPlayer();
        assertEquals(false, c.isHuman());

        // Human become a Computer player
        Player h = new HumanPlayer();
        h = new ComputerPlayer();
        assertEquals(false, c.isHuman());

    }

    @Test
    public void testToString(){
        Player c = new ComputerPlayer();
        assertEquals("ComputerPlayer[isHuman=false]", c.toString());
    }

    @Test
    public void testUpdateGame(){
        Game g = new Game();
        Player c1 = new ComputerPlayer();
        Player c2 = new ComputerPlayer();
        assertEquals(true,g.isP1Turn());
        c1.updateGame(g);
        assertEquals(false, g.isP1Turn());
        c2.updateGame(g);
        assertEquals(true,g.isP1Turn());

        Game g2 = new Game();
        Player c3 = new ComputerPlayer();
        Player h4 = new HumanPlayer();
        assertEquals(true,g2.isP1Turn());
        c3.updateGame(g2);
        assertEquals(false, g2.isP1Turn());
        h4.updateGame(g2);  // Since this does nothing
        g2.move(20, 16);    // We have to move our human
        assertEquals(true, g2.isP1Turn());
    }
}
