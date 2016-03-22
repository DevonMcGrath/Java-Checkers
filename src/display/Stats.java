package display;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Stats extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4258465582378564228L;
	
	private Game game;
	private boolean isP1;
	
	//Constructor
	public Stats(Game game, boolean isP1) {
	
		this.game = game;
		this.isP1 = isP1;
		
		//Set the background colour
		this.setBackground(this.game.getBackground());
	}
	
	//Graphics
	public void paint(Graphics g) {
		super.paint(g);
		
		Font big = new Font("Arial", Font.BOLD, 36);
		
		//Draw player titles
		g.setFont(big);
		g.setColor(Color.BLACK);
		if (isP1) {
			g.drawString("Player 1", 10, 20);
		}
		else {
			g.drawString("Player 2", 10, 20);
		}
	}
}
