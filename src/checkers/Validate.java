package checkers;

import java.util.ArrayList;
import java.util.List;

import tools.Board;
import tools.Move;
import tools.Point;

public class Validate {

	//Returns true if point selected is valid
	public static boolean isValid(Board board, Player player, Point p) {

		//Return the other method's result
		return isValid(board, player, player.getCheckerAtPoint(p));
	}

	//Returns true if checker selected is valid
	public static boolean isValid(Board board, Player player, Checker checker) {

		//Do tests to make sure it is valid
		if (checker == null) { //There is no checker at the point
			return false;
		}
		int id = checker.getID();
		if (id == Board.ID_BLACK_DEAD || //Checker is eliminated
				id == Board.ID_WHITE_DEAD) {
			return false;
		}

		//Player has black checkers
		if (player.hasBlackCheckers()) {

			if (id == Board.ID_WHITE || id == Board.ID_WHITE_KING) {
				return false;
			}
		}

		//Player has white checkers
		else {

			if (id == Board.ID_BLACK || id == Board.ID_BLACK_KING) {
				return false;
			}
		}

		//Checker cannot move or skip
		if (!canMove(board, checker) &&
				!canSkip(board, checker)) {
			return false;
		}

		//A checker can skip; current checker can't
		if (!canSkip(board, checker) &&
				playerCanSkip(board, player)) {
			return false;
		}

		//Passed all tests, must be valid
		return true;
	}

	//Returns true if the board is empty at point p
	public static boolean isEmpty(Board board, Point p) {

		//Special cases
		if (board == null || p == null) {
			return false;
		}
		if (!Point.isInRange(p, board.getSize())) {
			return false;
		}

		//Get the ID at p
		return (board.getBoard()[p.x][p.y] == Board.ID_EMPTY);
	}

	//Checks if the move made was valid
	public static boolean isValidTurn(Board board, Player player,
			Point start, Point end) {

		//If the turn is a move
		if (Math.abs(Point.calculateDY(start, end)) == 1) {
			return isValidMove(board, player, start, end);
		}

		//Otherwise
		return isValidSkip(board, player, start, end);
	}

	//Returns true if valid move
	public static boolean isValidMove(Board board, Player player,
			Point start, Point end) {

		//Do the basic tests to check if valid
		if (!passesBasicTests(board, player, start, end)) {
			return false;
		}

		//Check if the distance is correct
		if (Math.abs(Point.calculateDY(start, end)) != 1) {
			return false;
		}

		//Player can skip
		if (playerCanSkip(board, player)) {
			return false;
		}

		//Passed all tests, must be valid
		return true;
	}

	//Returns true if the move was a valid skip
	public static boolean isValidSkip(Board board, Player player,
			Point start, Point end) {

		//Do the basic tests to check if valid
		if (!passesBasicTests(board, player, start, end)) {
			return false;
		}

		//Check if the distance is correct
		if (Math.abs(Point.calculateDY(start, end)) != 2) {
			return false;
		}

		//Check to see if the checker skipped has the right id
		int id = board.ID(start);
		Point middle = Point.middle(start, end);
		if (!isEnemy(board.getBoard(), middle, id) ||
				isEmpty(board, middle)) { //Not the correct id
			return false;
		}


		//Passed all tests, must be valid
		return true;
	}

	//Method to get whether or not an ID at a point is the other player
	public static boolean isEnemy(int[][] board, Point p, int idFriend) {

		return isEnemy(board[p.x][p.y], idFriend);
	}

	//Method to tests if two IDs are enemies
	public static boolean isEnemy(int idTest, int idFriend) {

		//Special case
		if (idTest == Board.ID_EMPTY) {
			return false;
		}

		return (isWhiteChecker(idTest)
				!= isWhiteChecker(idFriend));
	}
	
	//Method to test if two IDs are friends
	public static boolean isFriend(int idTest, int idFriend) {
		
		//Friend is a black checker
		if (Board.isBlackChecker(idFriend)) {
			return Board.isBlackChecker(idTest);
		}
		
		//Friend is a white checker
		else if (Board.isWhiteChecker(idFriend)) {
			return Board.isWhiteChecker(idTest);
		}
		
		//Empty or invalid
		return false;
	}

	//Returns true if the ID given is a white checker
	public static boolean isWhiteChecker(int id) {
		return (id == Board.ID_WHITE_DEAD ||
				id == Board.ID_WHITE ||
				id == Board.ID_WHITE_KING);
	}

	//Method to check if a checker can move
	public static boolean canMove(Board board,
			Point start) {

		if (start.x < 0 || start.y < 0) { //Eliminated checker
			return false;
		}
		
		boolean isWhiteChecker = isWhiteChecker(board.ID(start));
		boolean isKing = Board.isKing(board.ID(start));

		//If checker is white
		if (isWhiteChecker) {

			//Test points
			Point end = Point.createNewPoint(start, Move.M_UP_LEFT);
			if (isEmpty(board,end)) {
				return true;
			}
			end = Point.createNewPoint(start, Move.M_UP_RIGHT);
			if (isEmpty(board,end)) {
				return true;
			}

			//Checker is a king
			if (isKing) {
				end = Point.createNewPoint(start, Move.M_DOWN_LEFT);
				if (isEmpty(board,end)) {
					return true;
				}
				end = Point.createNewPoint(start, Move.M_DOWN_RIGHT);
				if (isEmpty(board,end)) {
					return true;
				}
			}
		}

		//Checker is black
		else {

			//Test points
			Point end = Point.createNewPoint(start, Move.M_DOWN_LEFT);
			if (isEmpty(board,end)) {
				return true;
			}
			end = Point.createNewPoint(start, Move.M_DOWN_RIGHT);
			if (isEmpty(board,end)) {
				return true;
			}

			//Checker is a king
			if (isKing) {
				end = Point.createNewPoint(start, Move.M_UP_LEFT);
				if (isEmpty(board,end)) {
					return true;
				}
				end = Point.createNewPoint(start, Move.M_UP_RIGHT);
				if (isEmpty(board,end)) {
					return true;
				}
			}
		}

		//Testing failed
		return false;
	}

	//Method to check if a checker can move
	public static boolean canMove(Board board,
			Checker checker) {
		return canMove(board, checker.getPoint());
	}

	//Method to check if a checker can skip
	public static boolean canSkip(Board board, Point start) {

		if (board == null || start == null) {
			return false;
		}
		if (start.x < 0 || start.y < 0) { //Eliminated checker
			return false;
		}

		boolean isWhiteChecker = isWhiteChecker(board.ID(start));
		boolean isKing = Board.isKing(board.ID(start));

		//If checker is white
		if (isWhiteChecker) {

			//Test points
			Point end = Point.createNewPoint(start, Move.S_UP_LEFT);
			if (cheapSkipTest(board, start, end)) {
				return true;
			}
			end = Point.createNewPoint(start, Move.S_UP_RIGHT);
			if (cheapSkipTest(board, start, end)) {
				return true;
			}

			//Checker is a king
			if (isKing) {
				end = Point.createNewPoint(start, Move.S_DOWN_LEFT);
				if (cheapSkipTest(board, start, end)) {
					return true;
				}
				end = Point.createNewPoint(start, Move.S_DOWN_RIGHT);
				if (cheapSkipTest(board, start, end)) {
					return true;
				}
			}
		}

		//Checker is black
		else {

			//Test points
			Point end = Point.createNewPoint(start, Move.S_DOWN_LEFT);
			if (cheapSkipTest(board, start, end)) {
				return true;
			}
			end = Point.createNewPoint(start, Move.S_DOWN_RIGHT);
			if (cheapSkipTest(board, start, end)){
				return true;
			}

			//Checker is a king
			if (isKing) {
				end = Point.createNewPoint(start, Move.S_UP_LEFT);
				if (cheapSkipTest(board, start, end)) {
					return true;
				}
				end = Point.createNewPoint(start, Move.S_UP_RIGHT);
				if (cheapSkipTest(board, start, end)) {
					return true;
				}
			}
		}

		//Testing failed
		return false;
	}

	//Method to check if a checker can skip
	public static boolean canSkip(Board board,
			Checker checker) {

		if (board == null || checker == null) {
			return false;
		}

		return canSkip(board, checker.getPoint());
	}

	//Method to get the total possible move and skip count
	//Returns the total number of checkers that can move and skip
	public static Point getMoveAndSkipCount(Board board, Player player) {

		//Special case
		if (board == null || player == null) {
			return null;
		}

		int moveCount = 0;
		int skipCount = 0;

		//Get the checkers
		Checker[] checkers = player.getCheckers();

		//Loop through each checker and test
		for (Checker c : checkers) {
			if (!c.isDead()) {

				//Test if the checker can move
				if (canMove(board, c)) {
					moveCount ++;
				}

				//Test if the checker can skip
				if (canSkip(board, c)) {
					skipCount ++;
				}
			}
		}

		//Return a point with both counts
		return new Point(moveCount, skipCount);
	}

	//Checks if a checker can be skipped
	public static boolean isSafe(Board board, Point p) {
	
		//Check if the checker is against a wall
		int x = p.x;
		int y = p.y;
		if (x == 0 || x == board.getWidth() - 1) {
			return true;
		}
		if (y == 0 || y == board.getHeight() - 1) {
			return true;
		}

		//Get the board
		int[][] b = board.getBoard();

		/* Spaces look like: (c is the checker)
		 * 1   2
		 *   c
		 * 3   4
		 */
		Point p1 = new Point(x-1, y-1);
		Point p2 = new Point(x+1, y-1);
		Point p3 = new Point(x-1, y+1);
		Point p4 = new Point(x+1, y+1);
		int empty = Board.ID_EMPTY;
		int id = b[x][y];
		int id1 = b[x-1][y-1], id2 = b[x+1][y-1]; //Up left/right
		int id3 = b[x-1][y+1], id4 = b[x+1][y+1]; //Down left/right
		boolean areEnemies = true;

		//Check if there are enemies
		if (Board.isBlackChecker(id)) {
			areEnemies = (Board.isWhiteChecker(id1) ||
					Board.isWhiteChecker(id2) ||
					Board.isWhiteChecker(id3) ||
					Board.isWhiteChecker(id4));
		}
		else {
			areEnemies = (Board.isBlackChecker(id1) ||
					Board.isBlackChecker(id2) ||
					Board.isBlackChecker(id3) ||
					Board.isBlackChecker(id4));
		}

		//No enemies
		if (!areEnemies) {
			return true;
		}

		//Check if the four spaces around are full
		if (id1 != empty && id2 != empty &&
				id3 != empty && id4 != empty) {
			return true;
		}

		//Two friendly checkers are behind/in-front of the checker
		if ((isFriend(id1, id) && isFriend(id2, id)) ||
				(isFriend(id3, id) && isFriend(id4, id))) {
			return true;
		}

		//id is a black checker
		if (Board.isBlackChecker(id)) {

			//Check for skips
			if (cheapSkipTest(board, p3, p2) ||
					cheapSkipTest(board, p4, p1)) {
				return false;
			}
			
			//Kings
			if ((cheapSkipTest(board, p2, p3) && Board.isKing(id2)) ||
					(cheapSkipTest(board, p1, p4) && Board.isKing(id1))) {
				return false;
			}
		}

		//id is a white checker
		else {
			
			//Check for skips
			if (cheapSkipTest(board, p2, p3) ||
					cheapSkipTest(board, p1, p4)) {
				return false;
			}
			
			//Kings
			if ((cheapSkipTest(board, p3, p2) && Board.isKing(id3)) ||
					(cheapSkipTest(board, p4, p1) && Board.isKing(id4))) {
				return false;
			}
		}

		//Failed all tests
		return true;
	}

	//Checks if a checker can be skipped
	public static boolean isSafe(Board board, Checker checker) {
		return isSafe(board, checker.getPoint());
	}

	//Returns an array of points with checkers that can be skipped
	public static Point[] getUnsafeCheckerPoints(Board board) {

		List<Point> unsafe = new ArrayList<Point>();

		//Get all the checker points
		Point[] checkers = Point.combine(board.getBlackCheckerPoints(),
				board.getWhiteCheckerPoints());

		//Add the checkers that aren't safe to the list
		for (Point p : checkers) {
			if (!isSafe(board, p)) {
				unsafe.add(p);
			}
		}

		return Point.toArray(unsafe);
	}

	//Method to check if the player can move or skip
	public static boolean playerCanGo(Board board, Player player) {

		//Special case
		if (board == null || player == null) {
			return false;
		}

		//Get the checkers
		Point[] checkers;
		if (player.hasBlackCheckers()) {
			checkers = board.getBlackCheckerPoints();
		}
		else {
			checkers = board.getWhiteCheckerPoints();
		}
		
		//Check that checkers were found
		if (checkers == null) {
			return false;
		}

		//Loop through each checker and test
		for (Point p : checkers) {
			
			//Test if the checker can move
			if (canMove(board, p)) {
				return true;
			}

			//Test if the checker can skip
			if (canSkip(board, p)) {
				return true;
			}
		}

		//Testing failed
		return false;
	}

	//Method to check if the player can skip a piece
	public static boolean playerCanSkip(Board board, Player player) {

		//Special case
		if (board == null || player == null) {
			return false;
		}

		//Get the checkers
		Point[] checkers;
		if (player.hasBlackCheckers()) {
			checkers = board.getBlackCheckerPoints();
		}
		else {
			checkers = board.getWhiteCheckerPoints();
		}
		if (checkers == null) {
			return false;
		}

		//Loop through each checker and test
		for (Point p : checkers) {

			//Test if the checker can skip
			if (canSkip(board, p)) {
				return true;
			}
		}

		//Testing failed
		return false;
	}

	//Method to check if the game is over
	public static boolean isGameOver(Board board, Player p1, Player p2) {

		if (!p1.hasCheckers() || !p2.hasCheckers()) {
			return true; //A player has no checkers
		}
		if (!playerCanGo(board, p1) || !playerCanGo(board, p2)) {
			return true; //A player can't move
		}

		//Passed all tests
		return false;
	}

	//Checks if a move can even be considered a skip
	public static boolean isSkip(Point start, Point end) {
		return (Math.abs(Point.calculateDX(start, end)) == 2 &&
				Math.abs(Point.calculateDY(start, end)) == 2);
	}

	//Method to check basic tests
	private static boolean passesBasicTests(Board board, Player player,
			Point start, Point end) {

		//Null pointers
		if (board == null || player == null ||
				start == null || end == null) {
			return false;
		}

		Checker checker = player.getCheckerAtPoint(start);

		//Check if there exists a checker at the start
		if (checker == null) {
			return false;
		}

		int id = checker.getID();
		int dx = Point.calculateDX(start, end);
		int dy = Point.calculateDY(start, end);

		//Basic tests
		if (Math.abs(dx) > 2 || Math.abs(dy) > 2) { //Wrong distance
			return false;
		}
		if (Math.abs(dx) != Math.abs(dy)) { //Not a diagonal move
			return false;
		}
		if (Point.isEqual(start, end)) { //Didn't move
			return false;
		}
		if (!isEmpty(board, end)) { //Another checker is in the end goal
			return false;
		}
		if (end.x < 0 || end.y < 0) { //Not within the board
			return false;
		}
		if (end.x >= board.getWidth() || end.y >= board.getHeight()) {
			return false;
		}
		if (start.x < 0 || start.y < 0) { //Not within the board
			return false;
		}
		if (start.x >= board.getWidth() || start.y >= board.getHeight()) {
			return false;
		}

		//Player has black checkers
		if (player.hasBlackCheckers()) {

			//If DY is negative and not a king, invalid
			if (id == Board.ID_BLACK &&
					Point.calculateDY(start, end) < 0) {
				return false;
			}
		}

		//Player has white checkers
		else {

			//If DY is positive and not a king, invalid
			if (id == Board.ID_WHITE &&
					Point.calculateDY(start, end) > 0) {
				return false;
			}
		}

		//Passed all tests
		return true;
	}

	//Method that can quickly return a result for a valid skip
	private static boolean cheapSkipTest(Board board,
			Point start, Point end) {
		
		//Basic checks
		if (board.isEmpty(start)) { //Empty at start
			return false;
		}

		//Return empty in end and enemy in middle
		return board.isEmpty(end) && isEnemy(
				board.ID(Point.middle(start, end)), board.ID(start));
	}
}
