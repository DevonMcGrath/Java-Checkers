package checkers;

import ai.Path;
import tools.Board;
import tools.Move;
import tools.Point;

public class Player {

	private Player enemy;
	private Checker[] checkers;
	private Board board;
	private boolean isBlack;
	private boolean isAIEnabled;
	private int checkerCount;

	//Constructor - no number of checkers parameter
	public Player(Board board, boolean isBlack, boolean hasAI) {
		this.board = board;
		this.isBlack = isBlack;
		this.isAIEnabled = hasAI;
		this.checkerCount = 12;
		initializeCheckers();
	}

	//Constructor - number of checkers parameter included
	public Player(Board board, boolean isBlack, boolean hasAI, int checkerCount) {
		this.board = board;
		this.isBlack = isBlack;
		this.isAIEnabled = hasAI;
		this.checkerCount = checkerCount;
		initializeCheckers();
	}

	//Initializes the checker locations
	private void initializeCheckers() {

		this.checkers = new Checker[checkerCount];
		int[][] gameBoard = board.getBoard();

		/* Black is at the top of the board
		 * White is at the bottom of the board
		 */

		//If player has black checkers
		if (isBlack) {

			//Loop through board and create checkers
			int count = 0;
			for (int j = 0; j < board.getHeight(); j ++) {
				for (int i = 0; i < board.getWidth(); i ++) {
					if (gameBoard[i][j] == Board.ID_EMPTY
							&& count < checkerCount) {
						this.checkers[count] = new Checker(i, j,
								Board.ID_BLACK);
						this.board.place(new Point(i, j), Board.ID_BLACK);
						count ++;
					}
				}
			}
		}

		//Player has white checkers
		else {

			//Loop through board and create checkers
			int count = 0;
			for (int j = board.getHeight()-1; j >= 0; j --) {
				for (int i = 0; i < board.getWidth(); i ++) {
					if (gameBoard[i][j] == Board.ID_EMPTY
							&& count < checkerCount) {
						this.checkers[count] = new Checker(i, j,
								Board.ID_WHITE);
						this.board.place(new Point(i, j), Board.ID_WHITE);
						count ++;
					}
				}
			}
		}
	}

	//Method to take a turn (for AI); returns true if a move was made
	//and the AI can make another move in the same turn
	//and another can be made in the same turn
	public boolean takeTurn() {

		if (remaining() > 0 && enemy.remaining() > 0) {
			
			Move move = Path.createMove(board, this);
			Point start = move.getStart();
			Point end = move.createEndPoint();

			boolean moveMade = takeTurn(start, end);
			
			//If no move was made trick the system
			if (!moveMade) {
				return true;
			}

			//Check if the AI can skip again
			boolean canMoveAgain = false;
			if (Math.abs(Point.calculateDY(start, end)) == 2) {
				canMoveAgain = Validate.canSkip(
						board, getCheckerAtPoint(end));
			}

			return (moveMade && canMoveAgain);
		}
		
		//No checkers remaining for at least one player
		return false;
	}

	//Method to let the player take a turn; returns true if a move was made
	public boolean takeTurn(Point start, Point end) {

		//Only make the move if it's valid
		if (Validate.isValidTurn(board, this, start, end)) {

			//Get the checker at the start location
			int index = indexOfChecker(start);

			//Index invalid
			if (index < 0) {
				return false;
			}

			//Make the move
			move(index, start, end);
			return true;
		}

		//Move wasn't valid
		return false;
	}

	//Gets the index of a checker at point
	public int indexOfChecker(Point p) {

		//Special cases
		if (p == null || checkers == null) {
			return -1;
		}
		if (!Point.isInRange(p, board.getSize())) {
			return -1;
		}

		//Loop through the array of checkers
		for (int n = 0; n < checkers.length; n ++) {
			if (Point.isEqual(p, checkers[n].getPoint())) {
				return n;
			}
		}

		//Checker wasn't found
		return -1;
	}

	//Method returns the player's checker at a point, null if there is no
	//checker that exists in that location
	public Checker getCheckerAtPoint(Point p) {

		//Get the index of the checker
		int index = indexOfChecker(p);

		//If it's valid
		if (index != -1) {
			return checkers[index];
		}

		//Return null if it wasn't found
		return null;
	}

	//Method called when a checker is eliminated from the game
	public void eliminate(Point p) {

		//Only continue if p is defined
		if (p != null) {

			//Loop through the array of checkers
			for (int n = 0; n < checkerCount; n ++) {

				if (Point.isEqual(p, checkers[n].getPoint())) {
					this.board.place(p, Board.ID_EMPTY);
					this.checkers[n].eliminate();
				}
			}
		}
	}

	//Method to get how many checkers remain
	public int remaining() {

		int remaining = 0;

		//Go through the array of checkers
		for (Checker c : checkers) {
			if (!c.isDead()) {
				remaining ++;
			}
		}

		return remaining;
	}

	//Returns the initial amount of checkers
	public int initialCheckerCount() {
		return checkerCount;
	}

	//Method to move a checker
	private void move(int index, Point start, Point end) {

		//Update the checker location
		this.checkers[index].setLocation(end);

		//Update IDs
		if (isBlack && end.y == board.getHeight() - 1) {
			this.checkers[index].setID(Board.ID_BLACK_KING);
		}
		else if (!isBlack && end.y == 0) {
			this.checkers[index].setID(Board.ID_WHITE_KING);
		}
		board.place(start, Board.ID_EMPTY);
		board.place(end, checkers[index].getID());

		//Remove the enemy's checker if it was a skip
		if (Math.abs(Point.calculateDY(start, end)) == 2) {

			enemy.eliminate(Point.middle(start, end));
		}
	}

	//Returns true if all the checkers are eliminated
	public boolean hasCheckers() {

		//Loop through the array of checkers
		for (Checker c : checkers) {
			if (!c.isDead()) {
				return true;
			}
		}

		//Couldn't find a checker that wasn't eliminated
		return false;
	}

	//Returns the player's checkers
	public Checker[] getCheckers() {
		return checkers;
	}

	public Point[] getCheckerPoints() {
		if (isBlack) {
			return board.getBlackCheckerPoints();
		}
		return board.getWhiteCheckerPoints();
	}

	public boolean hasBlackCheckers() {
		return isBlack;
	}
	
	public void setAI(boolean hasAI) {
		this.isAIEnabled = hasAI;
	}

	public boolean hasAI() {
		return isAIEnabled;
	}

	//Sets the enemy player
	public void setEnemy(Player enemy) {
		this.enemy = enemy;
	}

	//Returns the enemy player
	public Player getEnemy() {
		return enemy;
	}
}
