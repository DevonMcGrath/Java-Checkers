/* Name: NetworkPlayer
 * Author: Devon McGrath
 * Description: This class represents a network player, who may be on a
 * different host.
 */

package model;

/**
 * The {@code NetworkPlayer} class is a dummy player used so that the game
 * can be updated properly from the corresponding client.
 */
public class NetworkPlayer extends Player {

	@Override
	public boolean isHuman() {
		return false;
	}

	/**
	 * This method does not actually update the game state for network players
	 * as it is updated when their client sends the updated game.
	 */
	@Override
	public void updateGame(Game game) {}

}
