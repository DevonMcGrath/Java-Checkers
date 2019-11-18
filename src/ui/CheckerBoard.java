/* Name: CheckerBoard
 * Author: Devon McGrath
 * Description: This class is the graphical user interface representation of
 * a checkers game. It is responsible for drawing the checker board and
 * allowing moves to be made. It does not provide a method to allow the user to
 * change settings of the game or restart it.
 */

package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.Timer;

import logic.MoveGenerator;
import model.Board;
import model.Game;
import model.HumanPlayer;
import model.NetworkPlayer;
import model.Player;
import network.Command;
import network.Session;

/**
 * The {@code CheckerBoard} class is a graphical user interface component that
 * is capable of drawing any checkers game state. It also handles player turns.
 * For human players, this means interacting with and selecting tiles on the
 * checker board. For non-human players, this means using the logic implemented
 * by the specified player object itself is used.
 */
public class CheckerBoard extends JButton {

	private static final long serialVersionUID = -6014690893709316364L;
	
	/** The amount of milliseconds before a computer player takes a move. */
	private static final int TIMER_DELAY = 1000;
	
	/** The number of pixels of padding between this component's border and the
	 * actual checker board that is drawn. */
	private static final int PADDING = 16;

	/** The game of checkers that is being played on this component. */
	private Game game;
	
	/** The window containing this checker board UI component. */
	private CheckersWindow window;
	
	/** The player in control of the black checkers. */
	private Player player1;
	
	/** The player in control of the white checkers. */
	private Player player2;
	
	/** The last point that the current player selected on the checker board. */
	private Point selected;
	
	/** The flag to determine the colour of the selected tile. If the selection
	 * is valid, a green colour is used to highlight the tile. Otherwise, a red
	 * colour is used. */
	private boolean selectionValid;
	
	/** The colour of the light tiles (by default, this is white). */
	private Color lightTile;

	/** The colour of the dark tiles (by default, this is black). */
	private Color darkTile;
	
	/** A convenience flag to check if the game is over. */
	private boolean isGameOver;
	
	/** The timer to control how fast a computer player makes a move. */
	private Timer timer;
	
	public CheckerBoard(CheckersWindow window) {
		this(window, new Game(), null, null);
	}
	
	public CheckerBoard(CheckersWindow window, Game game,
			Player player1, Player player2) {
		
		// Setup the component
		super.setBorderPainted(false);
		super.setFocusPainted(false);
		super.setContentAreaFilled(false);
		super.setBackground(Color.LIGHT_GRAY);
		this.addActionListener(new ClickListener());
		
		// Setup the game
		this.game = (game == null)? new Game() : game;
		this.lightTile = Color.WHITE;
		this.darkTile = Color.BLACK;
		this.window = window;
		setPlayer1(player1);
		setPlayer2(player2);
	}
	
	/**
	 * Checks if the game is over and redraws the component graphics.
	 */
	public void update() {
		runPlayer();
		this.isGameOver = game.isGameOver();
		repaint();
	}
	
	private void runPlayer() {
		
		// Nothing to do
		Player player = getCurrentPlayer();
		if (player == null || player.isHuman() ||
				player instanceof NetworkPlayer) {
			return;
		}
		
		// Set a timer to run
		this.timer = new Timer(TIMER_DELAY, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getCurrentPlayer().updateGame(game);
				timer.stop();
				updateNetwork();
				update();
			}
		});
		this.timer.start();
	}
	
	public void updateNetwork() {
		
		// Get the relevant sessions to send to
		List<Session> sessions = new ArrayList<>();
		if (player1 instanceof NetworkPlayer) {
			sessions.add(window.getSession1());
		}
		if (player2 instanceof NetworkPlayer) {
			sessions.add(window.getSession2());
		}
		
		// Send the game update
		for (Session s : sessions) {
			sendGameState(s);
		}
	}
	
	public synchronized boolean setGameState(boolean testValue,
			String newState, String expected) {
		
		// Test the value if requested
		if (testValue && !game.getGameState().equals(expected)) {
			return false;
		}
		
		// Update the game state
		this.game.setGameState(newState);
		repaint();
		
		return true;
	}
	
	public void sendGameState(Session s) {

		if (s == null) {
			return;
		}
		
		// Create the command and send it
		Command update = new Command(Command.COMMAND_UPDATE,
				s.getSid(), game.getGameState());
		String host = s.getDestinationHost();
		int port = s.getDestinationPort();
		update.send(host, port);
	}
	
	/**
	 * Draws the current checkers game state.
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		Game game = this.game.copy();
		
		// Perform calculations
		final int BOX_PADDING = 4;
		final int W = getWidth(), H = getHeight();
		final int DIM = W < H? W : H, BOX_SIZE = (DIM - 2 * PADDING) / 8;
		final int OFFSET_X = (W - BOX_SIZE * 8) / 2;
		final int OFFSET_Y = (H - BOX_SIZE * 8) / 2;
		final int CHECKER_SIZE = Math.max(0, BOX_SIZE - 2 * BOX_PADDING);
		
		// Draw checker board
		g.setColor(Color.BLACK);
		g.drawRect(OFFSET_X - 1, OFFSET_Y - 1, BOX_SIZE * 8 + 1, BOX_SIZE * 8 + 1);
		g.setColor(lightTile);
		g.fillRect(OFFSET_X, OFFSET_Y, BOX_SIZE * 8, BOX_SIZE * 8);
		g.setColor(darkTile);
		for (int y = 0; y < 8; y ++) {
			for (int x = (y + 1) % 2; x < 8; x += 2) {
				g.fillRect(OFFSET_X + x * BOX_SIZE, OFFSET_Y + y * BOX_SIZE,
						BOX_SIZE, BOX_SIZE);
			}
		}
		
		// Highlight the selected tile if valid
		if (Board.isValidPoint(selected)) {
			g.setColor(selectionValid? Color.GREEN : Color.RED);
			g.fillRect(OFFSET_X + selected.x * BOX_SIZE,
					OFFSET_Y + selected.y * BOX_SIZE,
					BOX_SIZE, BOX_SIZE);
		}
		
		// Draw the checkers
		Board b = game.getBoard();
		for (int y = 0; y < 8; y ++) {
			int cy = OFFSET_Y + y * BOX_SIZE + BOX_PADDING;
			for (int x = (y + 1) % 2; x < 8; x += 2) {
				int id = b.get(x, y);
				
				// Empty, just skip
				if (id == Board.EMPTY) {
					continue;
				}
				
				int cx = OFFSET_X + x * BOX_SIZE + BOX_PADDING;
				
				// Black checker
				if (id == Board.BLACK_CHECKER) {
					g.setColor(Color.DARK_GRAY);
					g.fillOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.LIGHT_GRAY);
					g.drawOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.BLACK);
					g.fillOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.LIGHT_GRAY);
					g.drawOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
				}
				
				// Black king
				else if (id == Board.BLACK_KING) {
					g.setColor(Color.DARK_GRAY);
					g.fillOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.LIGHT_GRAY);
					g.drawOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.DARK_GRAY);
					g.fillOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.LIGHT_GRAY);
					g.drawOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.BLACK);
					g.fillOval(cx - 1, cy - 2, CHECKER_SIZE, CHECKER_SIZE);
				}
				
				// White checker
				else if (id == Board.WHITE_CHECKER) {
					g.setColor(Color.LIGHT_GRAY);
					g.fillOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.DARK_GRAY);
					g.drawOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.WHITE);
					g.fillOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.DARK_GRAY);
					g.drawOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
				}
				
				// White king
				else if (id == Board.WHITE_KING) {
					g.setColor(Color.LIGHT_GRAY);
					g.fillOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.DARK_GRAY);
					g.drawOval(cx + 1, cy + 2, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.LIGHT_GRAY);
					g.fillOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.DARK_GRAY);
					g.drawOval(cx, cy, CHECKER_SIZE, CHECKER_SIZE);
					g.setColor(Color.WHITE);
					g.fillOval(cx - 1, cy - 2, CHECKER_SIZE, CHECKER_SIZE);
				}
				
				// Any king (add some extra highlights)
				if (id == Board.BLACK_KING || id == Board.WHITE_KING) {
					g.setColor(new Color(255, 240,0));
					g.drawOval(cx - 1, cy - 2, CHECKER_SIZE, CHECKER_SIZE);
					g.drawOval(cx + 1, cy, CHECKER_SIZE - 4, CHECKER_SIZE - 4);
				}
			}
		}
		
		// Draw the player turn sign
		String msg = game.isP1Turn()? "Player 1's turn" : "Player 2's turn";
		int width = g.getFontMetrics().stringWidth(msg);
		Color back = game.isP1Turn()? Color.BLACK : Color.WHITE;
		Color front = game.isP1Turn()? Color.WHITE : Color.BLACK;
		g.setColor(back);
		g.fillRect(W / 2 - width / 2 - 5, OFFSET_Y + 8 * BOX_SIZE + 2,
				width + 10, 15);
		g.setColor(front);
		g.drawString(msg, W / 2 - width / 2, OFFSET_Y + 8 * BOX_SIZE + 2 + 11);
		
		// Draw a game over sign
		if (isGameOver) {
			g.setFont(new Font("Arial", Font.BOLD, 20));
			msg = "Game Over!";
			width = g.getFontMetrics().stringWidth(msg);
			g.setColor(new Color(240, 240, 255));
			g.fillRoundRect(W / 2 - width / 2 - 5,
					OFFSET_Y + BOX_SIZE * 4 - 16,
					width + 10, 30, 10, 10);
			g.setColor(Color.RED);
			g.drawString(msg, W / 2 - width / 2, OFFSET_Y + BOX_SIZE * 4 + 7);
		}
	}
	
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = (game == null)? new Game() : game;
	}

	public CheckersWindow getWindow() {
		return window;
	}

	public void setWindow(CheckersWindow window) {
		this.window = window;
	}

	public Player getPlayer1() {
		return player1;
	}

	public void setPlayer1(Player player1) {
		this.player1 = (player1 == null)? new HumanPlayer() : player1;
		if (game.isP1Turn() && !this.player1.isHuman()) {
			this.selected = null;
		}
	}

	public Player getPlayer2() {
		return player2;
	}

	public void setPlayer2(Player player2) {
		this.player2 = (player2 == null)? new HumanPlayer() : player2;
		if (!game.isP1Turn() && !this.player2.isHuman()) {
			this.selected = null;
		}
	}
	
	public Player getCurrentPlayer() {
		return game.isP1Turn()? player1 : player2;
	}

	public Color getLightTile() {
		return lightTile;
	}

	public void setLightTile(Color lightTile) {
		this.lightTile = (lightTile == null)? Color.WHITE : lightTile;
	}

	public Color getDarkTile() {
		return darkTile;
	}

	public void setDarkTile(Color darkTile) {
		this.darkTile = (darkTile == null)? Color.BLACK : darkTile;
	}

	/**
	 * Handles a click on this component at the specified point. If the current
	 * player is not human, this method does nothing. Otherwise, the selected
	 * point is updated and a move is attempted if the last click and this one
	 * both are on black tiles.
	 * 
	 * @param x	the x-coordinate of the click on this component.
	 * @param y	the y-coordinate of the click on this component.
	 */
	private void handleClick(int x, int y) {
		
		// The game is over or the current player isn't human
		if (isGameOver || !getCurrentPlayer().isHuman()) {
			return;
		}
		
		Game copy = game.copy();
		
		// Determine what square (if any) was selected
		final int W = getWidth(), H = getHeight();
		final int DIM = W < H? W : H, BOX_SIZE = (DIM - 2 * PADDING) / 8;
		final int OFFSET_X = (W - BOX_SIZE * 8) / 2;
		final int OFFSET_Y = (H - BOX_SIZE * 8) / 2;
		x = (x - OFFSET_X) / BOX_SIZE;
		y = (y - OFFSET_Y) / BOX_SIZE;
		Point sel = new Point(x, y);
		
		// Determine if a move should be attempted
		if (Board.isValidPoint(sel) && Board.isValidPoint(selected)) {
			boolean change = copy.isP1Turn();
			String expected = copy.getGameState();
			boolean move = copy.move(selected, sel);
			boolean updated = (move?
					setGameState(true, copy.getGameState(), expected) : false);
			if (updated) {
				updateNetwork();
			}
			change = (copy.isP1Turn() != change);
			this.selected = change? null : sel;
		} else {
			this.selected = sel;
		}
		
		// Check if the selection is valid
		this.selectionValid = isValidSelection(
				copy.getBoard(), copy.isP1Turn(), selected);
		
		update();
	}
	
	/**
	 * Checks if a selected point is valid in the context of the current
	 * player's turn.
	 * 
	 * @param b			the current board.
	 * @param isP1Turn	the flag indicating if it is player 1's turn.
	 * @param selected	the point to test.
	 * @return true if and only if the selected point is a checker that would
	 * be allowed to make a move in the current turn.
	 */
	private boolean isValidSelection(Board b, boolean isP1Turn, Point selected) {

		// Trivial cases
		int i = Board.toIndex(selected), id = b.get(i);
		if (id == Board.EMPTY || id == Board.INVALID) { // no checker here
			return false;
		} else if(isP1Turn ^ (id == Board.BLACK_CHECKER ||
				id == Board.BLACK_KING)) { // wrong checker
			return false;
		} else if (!MoveGenerator.getSkips(b, i).isEmpty()) { // skip available
			return true;
		} else if (MoveGenerator.getMoves(b, i).isEmpty()) { // no moves
			return false;
		}
		
		// Determine if there is a skip available for another checker
		List<Point> points = b.find(
				isP1Turn? Board.BLACK_CHECKER : Board.WHITE_CHECKER);
		points.addAll(b.find(
				isP1Turn? Board.BLACK_KING : Board.WHITE_KING));
		for (Point p : points) {
			int checker = Board.toIndex(p);
			if (checker == i) {
				continue;
			}
			if (!MoveGenerator.getSkips(b, checker).isEmpty()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * The {@code ClickListener} class is responsible for responding to click
	 * events on the checker board component. It uses the coordinates of the
	 * mouse relative to the location of the checker board component.
	 */
	private class ClickListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// Get the new mouse coordinates and handle the click
			Point m = CheckerBoard.this.getMousePosition();
			if (m != null) {
				handleClick(m.x, m.y);
			}
		}
	}
}
