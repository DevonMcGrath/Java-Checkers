/* Name: Path
 * Author: Devon McGrath
 * Date: 01/01/2016
 * Description: This class generates the next best move for a player.
 */

package ai;

import checkers.Checker;
import checkers.Player;
import checkers.Validate;
import tools.Board;
import tools.Move;
import tools.Point;

public class Path {

	//Method to take a turn
	public static Move createMove(Board board, Player player) {

		//Get the move counts
		Point moveCounts = Validate.getMoveAndSkipCount(board, player);

		//Check if the player has to make a skip
		if (moveCounts.y > 0) { //Can skip
			return generateSkip(board, player, moveCounts.y);
		}
		else {
			return generateMove(board, player, moveCounts.x);
		}
		
	}
	
	//Method to make the next skip for a checker
	public static Point createNextSkip(Board board, Player player, Point start) {
		
		//Special cases
		if (board == null || player == null || start == null) {
			return null;
		}
		if (!Point.isInRange(start, board.getSize())) {
			return null;
		}
		
		//Create the weight array
		Checker checker = player.getCheckerAtPoint(start);
		if (checker == null) {
			return null;
		}
		WeightArray a = new WeightArray(checker);
		a = getSkipArray(board, player, a);
		if (!a.hasValidTurn()) {
			return null;
		}
		
		//Get the best move
		Move m = getMove(a.max(), true);
		
		//Return the end point
		return new Point(start.x + m.getDX(), start.y + m.getDY());
	}

	//Method will create a move
	private static Move generateMove(Board board, Player player,
			int size) {

		//Get the original board
		int[][] originalBoard = board.getBoardClone();

		//Create the array
		int found = 0;
		WeightArray[] weights = new WeightArray[size];
		Point[] checkers = (player.hasBlackCheckers()) ?
				board.getBlackCheckerPoints() : board.getWhiteCheckerPoints();
		for (int n = 0; n < checkers.length; n ++) {
			if (Validate.canMove(board, checkers[n])) {
				weights[found] = new WeightArray(
						player.getCheckerAtPoint(checkers[n]));
				found ++;
			}
		}

		//Loop through array to get the weights for each checker
		for (int n = 0; n < weights.length; n ++) {
			weights[n] = getMoveArray(board, player, weights[n]);
		}

		//Revert changes to the board
		board.revert(originalBoard);

		//Get the checker to create the move
		Point best = WeightArray.randMove(weights);
		Checker c = weights[best.x].getChecker();

		//Get the move
		Move m = getMove(best.y, false);
		
		return new Move(c.getPoint(), m.getDX(), m.getDY());
	}

	//Method will create a skip
	private static Move generateSkip(Board board, Player player,
			int size) {

		//Get the original board
		int[][] originalBoard = board.getBoardClone();

		//Create the array
		int found = 0;
		WeightArray[] weights = new WeightArray[size];
		Point[] checkers = (player.hasBlackCheckers()) ?
				board.getBlackCheckerPoints() : board.getWhiteCheckerPoints();
		for (int n = 0; n < checkers.length; n ++) {
			if (Validate.canSkip(board, checkers[n])) {
				weights[found] = new WeightArray(
						player.getCheckerAtPoint(checkers[n]));
				found ++;
			}
		}

		//Loop through array to get the weights for each checker
		for (int n = 0; n < weights.length; n ++) {
			weights[n] = getSkipArray(board, player, weights[n]);
		}

		//Revert changes to the board
		board.revert(originalBoard);

		//Get the checker to create the move
		Point best = WeightArray.randMove(weights);
		Checker c = weights[best.x].getChecker();

		//Get the move
		Move m = getMove(best.y, true);
				
		return new Move(c.getPoint(), m.getDX(), m.getDY());
	}

	//Method to generate a weight array for a checker that can move
	private static WeightArray getMoveArray(Board board, Player player, WeightArray a) {

		Checker c = a.getChecker();
		int x = c.getX(), y = c.getY();
		int id = c.getID();
		boolean isKing = Board.isKing(id);
		boolean isBlack = Board.isBlackChecker(id);

		/* Indexes in array are:
		 * [0] - Up left
		 * [1] - Up right
		 * [2] - Down left
		 * [3] - Down right
		 */

		//Set invalid moves
		Point p1 = new Point(x-1, y-1);
		Point p2 = new Point(x+1, y-1);
		Point p3 = new Point(x-1, y+1);
		Point p4 = new Point(x+1, y+1);
		if (x == 0) { //Against left wall
			a.setWeight(0, WeightArray.W_INVALID);
			a.setWeight(2, WeightArray.W_INVALID);
			
			if (!board.isEmpty(p2)) {
				a.setWeight(1, WeightArray.W_INVALID);
			}
			if (!board.isEmpty(p4)) {
				a.setWeight(3, WeightArray.W_INVALID);
			}
		}
		else if (x == board.getWidth() - 1) { //Against right wall
			a.setWeight(1, WeightArray.W_INVALID);
			a.setWeight(3, WeightArray.W_INVALID);
			
			if (!board.isEmpty(p1)) {
				a.setWeight(0, WeightArray.W_INVALID);
			}
			if (!board.isEmpty(p3)) {
				a.setWeight(2, WeightArray.W_INVALID);
			}
		}
		if (y == 0) { //Against top wall
			a.setWeight(0, WeightArray.W_INVALID);
			a.setWeight(1, WeightArray.W_INVALID);
			
			if (!board.isEmpty(p3)) {
				a.setWeight(2, WeightArray.W_INVALID);
			}
			if (!board.isEmpty(p4)) {
				a.setWeight(3, WeightArray.W_INVALID);
			}
		}
		else if (y == board.getWidth() - 1) { //Against bottom wall
			a.setWeight(2, WeightArray.W_INVALID);
			a.setWeight(3, WeightArray.W_INVALID);
			
			if (!board.isEmpty(p1)) {
				a.setWeight(0, WeightArray.W_INVALID);
			}
			if (!board.isEmpty(p2)) {
				a.setWeight(1, WeightArray.W_INVALID);
			}
		}

		//More specific scenarios
		if (isBlack && !isKing) { //Regular black checker
			a.setWeight(0, WeightArray.W_INVALID);
			a.setWeight(1, WeightArray.W_INVALID);
		}
		else if (!isBlack && !isKing) { //Regular white checker
			a.setWeight(2, WeightArray.W_INVALID);
			a.setWeight(3, WeightArray.W_INVALID);
		}
		if ((x > 0 && x < board.getWidth() - 1)
				&& (y > 0 && y < board.getHeight() - 1)) {

			if (!board.isEmpty(p1)) {
				a.setWeight(0, WeightArray.W_INVALID);
			}
			if (!board.isEmpty(p2)) {
				a.setWeight(1, WeightArray.W_INVALID);
			}
			if (!board.isEmpty(p3)) {
				a.setWeight(2, WeightArray.W_INVALID);
			}
			if (!board.isEmpty(p4)) {
				a.setWeight(3, WeightArray.W_INVALID);
			}
		}
		/* END OF INVALID MOVE TESTING */

		Point start = new Point(x, y);
		boolean isSafe = Validate.isSafe(board, start);
		boolean allSafe = (!isSafe) ? false :
			Validate.playerCanSkip(board, player.getEnemy());

		//Try all valid moves
		if (a.isValid(0)) {
			a.increment(0, fakeMove(board, player,
					start, p1, isSafe, allSafe));
		}
		if (a.isValid(1)) {
			a.increment(1, fakeMove(board, player,
					start, p2, isSafe, allSafe));
		}
		if (a.isValid(2)) {
			a.increment(2, fakeMove(board, player,
					start, p3, isSafe, allSafe));
		}
		if (a.isValid(3)) {
			a.increment(3, fakeMove(board, player,
					start, p4, isSafe, allSafe));
		}

		//Return the result
		return a;
	}

	//Method to generate a weight array for a checker that can move
	private static WeightArray getSkipArray(Board board, Player player, WeightArray a) {

		Checker c = a.getChecker();
		int x = c.getX(), y = c.getY();
		int id = c.getID();
		boolean isKing = Board.isKing(id);
		boolean isBlack = Board.isBlackChecker(id);

		/* Indexes in array are:
		 * [0] - Up left
		 * [1] - Up right
		 * [2] - Down left
		 * [3] - Down right
		 */

		//Set invalid moves
		Point p1 = new Point(x-1, y-1);
		Point p1END = new Point(x-2, y-2);
		Point p2 = new Point(x+1, y-1);
		Point p2END = new Point(x+2, y-2);
		Point p3 = new Point(x-1, y+1);
		Point p3END = new Point(x-2, y+2);
		Point p4 = new Point(x+1, y+1);
		Point p4END = new Point(x+2, y+2);
		if (x <= 1) { //Close to left wall
			a.setWeight(0, WeightArray.W_INVALID);
			a.setWeight(2, WeightArray.W_INVALID);
			
			if (!board.isEmpty(p2END) || Validate.isFriend(board.ID(p2), id)
					|| board.isEmpty(p2)) {
				a.setWeight(1, WeightArray.W_INVALID);
			}
			if (!board.isEmpty(p4END) || Validate.isFriend(board.ID(p4), id)
					|| board.isEmpty(p4)) {
				a.setWeight(3, WeightArray.W_INVALID);
			}
		}
		else if (x >= board.getWidth() - 2) { //Close to right wall
			a.setWeight(1, WeightArray.W_INVALID);
			a.setWeight(3, WeightArray.W_INVALID);
			
			if (!board.isEmpty(p1END) || Validate.isFriend(board.ID(p1), id)
					|| board.isEmpty(p1)) {
				a.setWeight(0, WeightArray.W_INVALID);
			}
			if (!board.isEmpty(p3END) || Validate.isFriend(board.ID(p3), id)
					|| board.isEmpty(p3)) {
				a.setWeight(2, WeightArray.W_INVALID);
			}
		}
		if (y <= 1) { //Close to top wall
			a.setWeight(0, WeightArray.W_INVALID);
			a.setWeight(1, WeightArray.W_INVALID);
			
			if (!board.isEmpty(p3END) || Validate.isFriend(board.ID(p3), id)
					|| board.isEmpty(p3)) {
				a.setWeight(2, WeightArray.W_INVALID);
			}
			if (!board.isEmpty(p4END) || Validate.isFriend(board.ID(p4), id)
					|| board.isEmpty(p4)) {
				a.setWeight(3, WeightArray.W_INVALID);
			}
		}
		else if (y >= board.getWidth() - 2) { //Close to bottom wall
			a.setWeight(2, WeightArray.W_INVALID);
			a.setWeight(3, WeightArray.W_INVALID);
			
			if (!board.isEmpty(p1END) || Validate.isFriend(board.ID(p1), id)
					|| board.isEmpty(p1)) {
				a.setWeight(0, WeightArray.W_INVALID);
			}
			if (!board.isEmpty(p2END) || Validate.isFriend(board.ID(p2), id)
					|| board.isEmpty(p2)) {
				a.setWeight(1, WeightArray.W_INVALID);
			}
		}

		//More specific scenarios
		if (isBlack && !isKing) { //Regular black checker
			a.setWeight(0, WeightArray.W_INVALID);
			a.setWeight(1, WeightArray.W_INVALID);
		}
		else if (!isBlack && !isKing) { //Regular white checker
			a.setWeight(2, WeightArray.W_INVALID);
			a.setWeight(3, WeightArray.W_INVALID);
		}
		if ((x > 1 && x < board.getWidth() - 2)
				&& (y > 1 && y < board.getHeight() - 2)) {

			if (!board.isEmpty(p1END) || Validate.isFriend(board.ID(p1), id)
					|| board.isEmpty(p1)) {
				a.setWeight(0, WeightArray.W_INVALID);
			}
			if (!board.isEmpty(p2END) || Validate.isFriend(board.ID(p2), id)
					|| board.isEmpty(p2)) {
				a.setWeight(1, WeightArray.W_INVALID);
			}
			if (!board.isEmpty(p3END) || Validate.isFriend(board.ID(p3), id)
					|| board.isEmpty(p3)) {
				a.setWeight(2, WeightArray.W_INVALID);
			}
			if (!board.isEmpty(p4END) || Validate.isFriend(board.ID(p4), id)
					|| board.isEmpty(p4)) {
				a.setWeight(3, WeightArray.W_INVALID);
			}
		}
		/* END OF INVALID MOVE TESTING */

		Point start = new Point(x, y);
		boolean isSafe = Validate.isSafe(board, start);
		boolean allSafe = (!isSafe) ? false :
			Validate.playerCanSkip(board, player.getEnemy());

		//Try all valid moves
		if (a.isValid(0)) {
			a.increment(0, fakeSkip(board, player, start, p1END, isSafe, allSafe));
		}
		if (a.isValid(1)) {
			a.increment(1, fakeSkip(board, player, start, p2END, isSafe, allSafe));
		}
		if (a.isValid(2)) {
			a.increment(2, fakeSkip(board, player, start, p3END, isSafe, allSafe));
		}
		if (a.isValid(3)) {
			a.increment(3, fakeSkip(board, player, start, p4END, isSafe, allSafe));
		}

		//Return the result
		return a;
	}

	//Method to get the move from the an integer
	private static Move getMove(int index, boolean canSkip) {

		//Out of range
		if (index < 0 || index >= 4) {
			return null;
		}

		//The player can skip
		if (canSkip) {
			if (index == 0) {
				return Move.S_UP_LEFT;
			}
			else if (index == 1) {
				return Move.S_UP_RIGHT;
			}
			if (index == 2) {
				return Move.S_DOWN_LEFT;
			}
			else {
				return Move.S_DOWN_RIGHT;
			}
		}

		//The player can only move
		else {
			if (index == 0) {
				return Move.M_UP_LEFT;
			}
			else if (index == 1) {
				return Move.M_UP_RIGHT;
			}
			if (index == 2) {
				return Move.M_DOWN_LEFT;
			}
			else {
				return Move.M_DOWN_RIGHT;
			}
		}
	}

	//Method to take a fake turn; returns the weight
	private static float fakeMove(Board board, Player player,
			Point start, Point end, boolean isSafe, boolean allSafe) {

		int[][] original = board.getBoardClone();
		float weight = 0;

		//Make the move
		int id = original[start.x][start.y];
		board.updateChecker(start, end);

		//Perform checks and calculate weight
		boolean isSafeUpdated = Validate.isSafe(board, end); //safe?
		if (Board.isKing(board.getBoard()[end.x][end.y]) && //Becomes a king
				!Board.isKing(id)) {
			weight += WeightArray.W_BECOMES_K;
		}
		if (!Validate.canMove(board, end)) { //stuck?
			weight += WeightArray.W_GETS_STUCK;
		}
		if (Validate.playerCanSkip(board, player)) { //Can skip on next move?
			weight += WeightArray.W_SKIP_ON_NEXT_MOVE;
		}
		if (allSafe && Validate.playerCanSkip(
				board, player.getEnemy())) { //Putting another checker in danger?
			weight += WeightArray.W_ENEMY_SKIP_AFTER;
		}
		if (player.getEnemy().remaining() //Close to the end of the game
				<= player.getEnemy().initialCheckerCount()/3) {
			
			//Get enemy checkers
			Point[] enemyCheckers = player.getEnemy().getCheckerPoints();
			
			//Add the negative change in distance to the weight
			if (enemyCheckers != null) {
				for (Point p : enemyCheckers) {
					weight -= Point.calculateChangeInDist(start, end, p);
				}
			}
		}

		//Add the result from the 'safe test'
		weight += safeTest(isSafe, isSafeUpdated);
		
		//Revert changes
		board.revert(original);
		
		//Return the calculated weight
		return weight;
	}

	//Method to take a fake skip; returns the weight
	private static float fakeSkip(Board board, Player player,
			Point start, Point end, boolean isSafe, boolean allSafe) {

		float weight = fakeMove(board, player,
				start, end, isSafe, allSafe); //Get basic weight
		int[][] original = board.getBoardClone();

		//TODO implement method (remaining weights, multiple skips)

		//Revert changes
		board.revert(original);

		return weight;
	}

	//Method returns the appropriate weight based on safe/unsafe conditions
	private static float safeTest(boolean safe1, boolean safe2) {

		//Return the appropriate weight
		if (safe1 && safe2) {
			return WeightArray.W_S_S;
		}
		if (safe1 && !safe2) {
			return WeightArray.W_S_US;
		}
		if (!safe1 && safe2) {
			return WeightArray.W_US_S;
		}
		return WeightArray.W_US_US;
	}

}
