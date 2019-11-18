/* Name: HumanPlayer
 * Author: Devon McGrath
 * Description: This class represents a human player (i.e. a user) that can
 * interact with the system.
 */

package model;

/**
 * The {@code HumanPlayer} class represents a user of the checkers game that
 * can update the game by clicking on tiles on the board.
 */
public class HumanPlayer extends Player {

	@Override
	public boolean isHuman() {
		return true;
	}

	/**
	 * Performs no updates on the game. As human players can interact with the
	 * user interface to update the game.
	 */
	@Override
	public void updateGame(Game game) {}

}
