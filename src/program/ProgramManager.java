/* Name: ProgramManager
 * Author: Devon McGrath
 * Date: 12/28/2015
 * Description: This class handles all objects.
 */

package program;

import display.Display;
import display.Game;

public class ProgramManager {

	public static void main(String[] args) {
		
		//Create the display and game
		Display display = new Display(false, "Checkers Version 3.1.4", 500, 500);
		Game game = new Game(display);
		
		//Change the settings of objects
		display.setMinimumSize(300, 300);
		game.setColour(200, 200, 200);
		
		//Add components to the display
		display.add(game);
		
		//Make the display visible
		display.setVisible(true);
		
		//Start the game
		game.start();
	}
}
