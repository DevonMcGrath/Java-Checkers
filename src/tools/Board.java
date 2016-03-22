package tools;

public class Board {

	//IDs for the board
	public static final int ID_EMPTY = 0;
	public static final int ID_INVALID = -1;
	public static final int ID_WHITE_DEAD = 10;
	public static final int ID_WHITE = 11;
	public static final int ID_WHITE_KING = 12;
	public static final int ID_BLACK_DEAD = 20;
	public static final int ID_BLACK = 21;
	public static final int ID_BLACK_KING = 22;

	private int width, height;
	private int[][] board;

	//Constructor - no parameters
	public Board() {
		this.width = 8;
		this.height = 8;
		initialize();
	}

	//Constructor - size as parameters
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		initialize();
	}

	//Method to initialize the board
	private void initialize() {

		this.board = new int[width][height];

		//Place the appropriate IDs in each location
		for (int i = 0; i < width; i ++) {
			for (int j = 0; j < height; j ++) {
				if (i % 2 == j % 2) {
					board[i][j] = ID_INVALID;
				}
				else {
					board[i][j] = ID_EMPTY;
				}
			}
		}
	}

	//Method reverts changes (assumes parameter is an old version of board)
	public void revert(int[][] board) {

		//Copy all values over
		for (int i = 0; i < width; i ++) {
			for (int j = 0; j < height; j ++) {
				this.board[i][j] = board[i][j];
			}
		}
	}

	//Places 'id' in the board
	public void place(Point p, int id) {

		if (p != null) {
			if (Point.isInRange(p, getSize())) {
				board[p.x][p.y] = id;
			}
		}
	}

	//Gets the id at a point
	public int ID(Point p) {

		//Special cases
		if (p == null) {
			return ID_INVALID;
		}
		if (p.x < 0 || p.x >= width) {
			return ID_INVALID;
		}
		if (p.y < 0 || p.y >= height) {
			return ID_INVALID;
		}

		return board[p.x][p.y];
	}

	//Method to calculate each box size based on how many squares
	//are on the board, offset, and component size
	public int calulateBoxSize(Point boarderSize, Point compSize) {

		boolean yIsBetter = (compSize.y - boarderSize.y*2) / height
				>= (compSize.x - boarderSize.x*2) / width;

				return (yIsBetter) ?
						(compSize.x - boarderSize.x*2) / width :
							(compSize.y - boarderSize.y*2) / height;
	}

	//Calculates the new boarder size
	public Point calculateBoarderSize(int boxSize, Point compSize) {

		//Calculate the individual axis
		int x = (compSize.x - boxSize * width) / 2;
		int y = (compSize.y - boxSize * height) /2;

		//Return the new point
		return new Point(x, y);
	}

	//Counts the number of times id appears in the board
	public int count(int id) {

		//Count the number of times id appears
		int count = 0;
		for (int i = 0; i < width; i ++) {
			for (int j = 0; j < height; j ++) {
				if (board[i][j] == id) {
					count ++;
				}
			}
		}

		//Return the result
		return count;
	}

	//Updates the board if a checker moved
	public void updateChecker(Point start, Point end) {

		int id = board[start.x][start.y];
		boolean isKing = isKing(id);
		boolean isBlack = isBlackChecker(id);

		//Transfer values
		board[start.x][start.y] = ID_EMPTY;
		if (!isKing && isBlack && end.y == height - 1) {
			board[end.x][end.y] = ID_BLACK_KING;
		}
		else if (!isKing && !isBlack && end.y == 0) {
			board[end.x][end.y] = ID_WHITE_KING;
		}
		else {
			board[end.x][end.y] = id;
		}

		//Check if the checker skipped
		if (Math.abs(Point.calculateDY(start, end)) == 2) {
			Point middle = Point.middle(start, end);
			board[middle.x][middle.y] = ID_EMPTY;
		}
	}

	//Returns true if the board is empty at (x,y)
	public boolean isEmpty(int x, int y) {
		return (board[x][y] == ID_EMPTY);
	}

	//Returns true if the board is empty at p
	public boolean isEmpty(Point p) {

		//Special cases
		if (board == null || p == null) {
			return false;
		}
		if (!Point.isInRange(p, this.getSize())) {
			return false;
		}
		
		return (board[p.x][p.y] == ID_EMPTY);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Point getSize() {
		return new Point(width, height);
	}

	public int[][] getBoard() {
		return board;
	}

	public int[][] getBoardClone() {

		//Copy the board
		int[][] clone = new int[width][height];
		for (int i = 0; i < width; i ++) {
			for (int j = 0; j < width; j ++) {
				clone[i][j] = board[i][j];
			}
		}

		//Return the cloned array
		return clone;
	}

	//Gets the point locations of id in the board
	public Point[] get(int id) {

		int count = count(id);

		//No 'id's exist
		if (count < 1) {
			return null;
		}

		Point[] array = new Point[count];

		//Loop through and find points
		int found = 0;
		for (int i = 0; i < width; i ++) {
			for (int j = 0; j < height; j ++) {
				if (board[i][j] == id) {
					array[found] = new Point(i, j);
					found ++;
				}
			}
		}

		return array;

	}

	//Returns an array of points with black checker locations
	public Point[] getBlackCheckerPoints() {
		return Point.combine(get(ID_BLACK), get(ID_BLACK_KING));
	}

	//Returns an array of points with black checker locations
	public Point[] getWhiteCheckerPoints() {
		return Point.combine(get(ID_WHITE), get(ID_WHITE_KING));
	}

	//Method to check if an id is a white checker
	public static boolean isWhiteChecker(int id) {
		return (id == ID_WHITE_DEAD ||
				id == ID_WHITE ||
				id == ID_WHITE_KING);
	}

	//Method to check if an id is a white checker
	public static boolean isBlackChecker(int id) {
		return (id == ID_BLACK_DEAD ||
				id == ID_BLACK ||
				id == ID_BLACK_KING);
	}

	//Checks if the checker if a king
	public static boolean isKing(int id) {
		return (id == Board.ID_BLACK_KING ||
				id == Board.ID_WHITE_KING);
	}
}
