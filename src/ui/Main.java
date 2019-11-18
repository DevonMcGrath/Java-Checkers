/* Name: Main
 * Author: Devon McGrath
 * Description: This class contains the main method to create the GUI and
 * start the checkers game.
 * ============================================================================
 * JAVA CHECKERS
 * -------------
 * Author: Devon McGrath (https://github.com/DevonMcGrath)
 * Created: 2018-04-14
 * Description: This program is a simple implementation of the standard
 * checkers game, with standard rules, in Java.
 * 
 * RULES
 * >>>>>>>>>>>>>
 * 1.	Checkers can only move diagonally, on dark tiles.
 * 
 * 2.	Normal checkers can only move forward diagonally (for black checkers,
 * 		this is down and for white checkers, this is up).
 * 
 * 3.	A checker becomes a king when it reaches the opponents end and cannot
 * 		move forward anymore.
 * 
 * 4.	Once a checker becomes a king, the player's turn is over.
 * 
 * 5.	After a checker/king moves one space diagonally, the player's turn is
 * 		over.
 * 
 * 6.	If an opponent's checker/king can be skipped, it must be skipped.
 * 
 * 7.	If after a skip, the same checker can skip again, it must. Otherwise,
 * 		the turn is over.
 * 
 * 8.	The game is over if a player either has no more checkers or cannot make
 * 		a move on their turn.
 * 
 * 9.	The player with the black checkers moves first.
 */

package ui;

import javax.swing.UIManager;

public class Main {

	public static void main(String[] args) {
		
		//Set the look and feel to the OS look and feel
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Create a window to display the checkers game
		CheckersWindow window = new CheckersWindow();
		window.setDefaultCloseOperation(CheckersWindow.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
}
