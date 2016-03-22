package display;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.Timer;

import tools.Board;
import tools.Point;
import tools.Time;
import checkers.Player;
import checkers.Validate;

public class Game extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2489934033113131282L;

	private static final int DELAY_LIMIT_MILLIS = 3000;

	private Display display;
	private Timer timer; //Timer for how fast the AI can update
	private Time time;
	private boolean showTime;
	private boolean p1IsBlack = true;
	private boolean p1IsAI = false, p2IsAI = true;
	private boolean isP1Turn;
	private boolean isValidBox;
	private boolean isGameOver;
	private boolean moveMade;
	private int delay;
	private Point last;
	private Point current;
	private Board board;
	private Player p1;
	private Player p2;

	//Basic constructor
	public Game(Display display) {

		this.display = display;
		this.showTime = true;
		this.p1IsBlack = true;
		this.p1IsAI = false;
		this.p2IsAI = true;

		//Set the event listeners
		this.addMouseListener(new MouseEvents());
		this.display.addKeyListener(new KeyEvents());

		//Set the timer
		this.timer = new Timer(1000, this);

		//Initialize
		reset();
	}

	//Constructor with parameters
	public Game(Display display, boolean p1HasBlackCheckers,
			boolean p1IsAI, boolean p2IsAI) {

		this.display = display;
		this.showTime = true;
		this.p1IsBlack = p1HasBlackCheckers;
		this.p1IsAI = p1IsAI;
		this.p2IsAI = p2IsAI;

		//Set the event listeners
		this.addMouseListener(new MouseEvents());
		this.display.addKeyListener(new KeyEvents());

		//Set the timer
		this.timer = new Timer(1000, this);

		//Initialize
		reset();
	}

	//Timer event method
	public void actionPerformed(ActionEvent e) {

		//Get the current player
		Player current = getCurrentPlayer();

		//Let the player take the turn
		if (current.hasAI()) {
			boolean canMoveAgain = current.takeTurn();
			if (!canMoveAgain) {
				this.isP1Turn = !isP1Turn;
			}
			checkGameState();
		}

		//Time related updates
		time.increment(timer.getDelay());
		if (moveMade) {
			delay += timer.getDelay();
		}
		if (moveMade && delay >= DELAY_LIMIT_MILLIS) {
			delay = 0;
			changeTurn();
		}

		//Graphics
		repaint();
	}

	//Override graphics method
	public void paint(Graphics g1) {
		super.paint(g1);

		//More advances graphics
		Graphics2D g = (Graphics2D)(g1);

		//Variables
		Point c = new Point(getWidth(), getHeight());
		Point start = new Point(25,25);
		int boxSize = board.calulateBoxSize(start, c);
		start = board.calculateBoarderSize(boxSize, c);
		Point boardSize = new Point(board.getWidth() * boxSize,
				board.getHeight() * boxSize);

		//Draw the board
		g.setColor(createColour(30,30,30,120));
		g.fillRect(start.x + 4, start.y + 4,
				boardSize.x + 2, boardSize.y + 2);
		g.setColor(Color.BLACK);
		g.fillRect(start.x - 1, start.y - 1, boardSize.x + 2, boardSize.y + 2);
		g.setColor(Color.WHITE);
		for (int i = 0; i < board.getWidth(); i ++) {
			for (int j = 0; j < board.getHeight(); j ++) {
				if (i%2 == j%2) {
					g.fill3DRect(start.x + boxSize*i, start.y + boxSize*j,
							boxSize, boxSize, true);
				}
			}
		}

		//Draw the selected square
		if (current.x >= 0 && current.y >= 0 && !playerIsAI()) {

			if (isValidBox) {
				g.setColor(Color.GREEN);
			}
			else {
				g.setColor(Color.RED);
			}
			g.fill3DRect(start.x + boxSize*current.x,
					start.y + boxSize*current.y,
					boxSize, boxSize, true);
		}

		//Draw checkers
		int[][] boardClone = board.getBoardClone();
		for (int i = 0; i < board.getWidth(); i ++) {
			for (int j = 0; j < board.getHeight(); j ++) {
				if (i%2 != j%2 && boardClone[i][j] != Board.ID_EMPTY) {
					//g.setColor(Color.CYAN);
					//g.fill3DRect(start.x + boxSize*i, start.y + boxSize*j,
					//	boxSize, boxSize, true);

					int id = boardClone[i][j];

					//It is a white checker
					if (id == Board.ID_WHITE ||
							id == Board.ID_WHITE_KING) {

						g.setColor(Color.WHITE);
						g.fillOval(start.x + 2 + boxSize * i,
								start.y + 2 + boxSize * j,
								boxSize - 6, boxSize - 6);
					}

					//It is a black checker
					else if (id == Board.ID_BLACK ||
							id == Board.ID_BLACK_KING) {

						g.setColor(Color.BLACK);
						g.fillOval(start.x + 2 + boxSize * i,
								start.y + 2 + boxSize * j,
								boxSize - 7, boxSize - 7);
						g.setColor(Color.WHITE);
						g.drawOval(start.x + 2 + boxSize * i,
								start.y + 2 + boxSize * j,
								boxSize - 6, boxSize - 6);
					}

					//If it's a king
					if (id == Board.ID_WHITE_KING ||
							id == Board.ID_BLACK_KING) {
						g.setColor(Color.ORANGE);
						for (int n = 0; n < 2; n ++) {
							g.drawOval(start.x + 2 + n*2 + boxSize*i,
									start.y + 2 + n*2 + boxSize*j,
									boxSize - 6 - n*4, boxSize - 6 - n*4);
						}
					}
				}
			}
		}

		//Draw the time
		Font font = new Font("Arial", Font.BOLD, 20);
		g.setFont(font);
		if (showTime) {
			g.setColor(Color.BLUE);
			String s = time.toString();
			g.drawString(s, (float)(getWidth()/2 - 4.1*s.length()),
					getHeight() - 5);
		}
		
		//Draw reset key
		font = new Font("Arial", Font.PLAIN, 12);
		g.setFont(font);
		g.setColor(Color.BLACK);
		g.drawString("r - reset", 10, getHeight()-5);
		
	}

	//Checks if the game is over
	private void checkGameState() {

		this.isGameOver = Validate.isGameOver(board, p1, p2);

		//Stop the timer if the game is over
		if (isGameOver) {
			timer.stop();
		}
	}

	//Method to start the game
	public void start() {
		if (timer != null) {
			this.timer.start();
		}
	}

	//Method to stop/pause the game
	public void stop() {
		if (timer != null) {
			this.timer.stop();
		}
	}

	//Resets the game
	public void reset() {

		this.isP1Turn = p1IsBlack;
		this.isValidBox = false;
		this.isGameOver = false;
		this.moveMade = false;
		this.delay = 0;
		this.last = new Point(-1, -1);
		this.current = new Point(-1, -1);

		//Create objects
		this.board = new Board();
		this.p1 = new Player(board, p1IsBlack, p1IsAI);
		this.p2 = new Player(board, !p1IsBlack, p2IsAI);
		this.p1.setEnemy(p2);
		this.p2.setEnemy(p1);

		//Set the time
		this.time = new Time();
	}

	//Returns true if the game timer is running
	public boolean isRunning() {
		if (timer != null) {
			return timer.isRunning();
		}
		else {
			return false;
		}
	}

	//Toggles the game state
	public void toggleGameState() {

		//Only change game state if timer is initialized
		if (timer != null) {
			if (isRunning()) {
				stop();
			}
			else {
				start();
			}
		}
	}

	//Returns the time
	public Time getTime() {
		return time;
	}

	//Toggles the display of time
	public void toggleTime() {
		this.showTime = !showTime;
	}

	//Toggles AI for player 1
	public void toggleP1AI() {

		this.p1IsAI = !p1IsAI;
		this.p1.setAI(p1IsAI);
	}

	//Toggles AI for player 2
	public void toggleP2AI() {

		this.p2IsAI = !p2IsAI;
		this.p2.setAI(p2IsAI);
	}

	//Method sets the colour of the component
	public void setColour(int r, int g, int b) {
		this.setBackground(createColour(r, g, b, 255));
	}

	//Method to create a colour
	private Color createColour(int r, int g, int b, int alpha) {

		//Check if values need to be adjusted
		r  = adjust(r, 0, 255);
		g = adjust(g, 0, 255);
		b = adjust(b, 0, 255);
		alpha = adjust(alpha, 0, 255);

		//Return the colour with the appropriate values
		return new Color(r, g, b, alpha);
	}

	//Method to keep a value in-between two limits
	private int adjust(int value, int min, int max) {

		//Adjust value if necessary
		if (value < min) {
			value = min;
		}
		else if (value > max) {
			value = max;
		}

		return value;
	}

	//Checks if the current player has AI enabled
	private boolean playerIsAI() {

		//If player 1's turn
		if (isP1Turn) {
			return p1.hasAI();
		}

		//Player 2's turn
		return p2.hasAI();
	}

	//Sets the last selected box
	private void setSelected(Point box) {

		//Update points
		this.last = current;
		this.current = box;

		//Check if the selected square is valid
		if (isP1Turn) {
			this.isValidBox = Validate.isValid(board, p1, box);
		}
		else {
			this.isValidBox = Validate.isValid(board, p2, box);
		}

		//Call the move method
		move();
	}

	//Method that makes a move for a player with no AI
	private void move() {

		//Only try to move if the player has no AI
		if (!playerIsAI()) {

			boolean moveMade = getCurrentPlayer().takeTurn(last, current);

			//Move was made, update values
			if (moveMade) {

				if (Math.abs(Point.calculateDY(current, last)) == 1 || 
						!Validate.canSkip(board, getCurrentPlayer()
								.getCheckerAtPoint(current))) {
					changeTurn();
				}
				else { //Can make another skip
					this.isValidBox = true;
					this.moveMade = true;
					this.delay = 0;
				}

				//Check if the game is over
				checkGameState();
			}
		}
	}

	//Called when a non-AI makes a move and the turn changes
	private void changeTurn() {

		//Reset variables
		this.last = new Point(-1, -1);
		this.current = new Point(-1, -1);
		this.isValidBox = false;
		this.isP1Turn = !isP1Turn;
		this.moveMade = false;
	}

	//Gets the current player
	private Player getCurrentPlayer() {

		//P1's turn
		if (isP1Turn) {
			return p1;
		}

		//P2's turn
		return p2;
	}

	//Class to respond to mouse events
	private class MouseEvents extends MouseAdapter {

		//Method invoked when the mouse was clicked
		public void mouseClicked(MouseEvent e) {

			//Only check if game is not over and it's running
			if (!isGameOver && isRunning()) {

				Point square = getSelectedSquare(new Point(e.getX(), e.getY()));

				//Check if the click valid
				if (square != null) {

					setSelected(square);

					//Update graphics
					repaint();
				}
			}
		}

		//Returns the selected square on the grid
		private Point getSelectedSquare(Point click) {

			Point c = new Point(getWidth(), getHeight());
			Point start = new Point(25,25);
			int boxSize = board.calulateBoxSize(start, c);
			start = board.calculateBoarderSize(boxSize, c);

			//Do some calculations and checks
			Point box = new Point((click.x - start.x), (click.y - start.y));
			if (box.x < 0 || box.y < 0) { //Click was not on the board
				return null;
			}

			//Adjust according to the box size
			box = new Point(box.x / boxSize, box.y / boxSize);

			//Return the box if it's within range and on a black tile
			if (Point.isInRange(box, board.getSize())
					&& box.x % 2 != box.y % 2) {
				return box;
			}

			//Invalid click
			return null;
		}
	}
	
	//Class to respond to key events
	private class KeyEvents extends KeyAdapter {
		
		public void keyPressed(KeyEvent e) {

			int key = e.getKeyCode();
			
			//Check for reset
			if (key == KeyEvent.VK_R) {
				reset();
			}
		}
	}
}
