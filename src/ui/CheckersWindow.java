/* Name: CheckersWindow
 * Author: Devon McGrath
 * Description: This class is a window that is used to play a game of checkers.
 * It also contains a component to change the game options.
 */

package ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import model.Player;
import network.CheckersNetworkHandler;
import network.ConnectionListener;
import network.Session;

/**
 * The {@code CheckersWindow} class is responsible for managing a window. This
 * window contains a game of checkers and also options to change the settings
 * of the game with an {@link OptionPanel}.
 */
public class CheckersWindow extends JFrame {

	private static final long serialVersionUID = 8782122389400590079L;
	
	/** The default width for the checkers window. */
	public static final int DEFAULT_WIDTH = 500;
	
	/** The default height for the checkers window. */
	public static final int DEFAULT_HEIGHT = 600;
	
	/** The default title for the checkers window. */
	public static final String DEFAULT_TITLE = "Java Checkers";
	
	/** The checker board component playing the updatable game. */
	private CheckerBoard board;
	
	private OptionPanel opts;
	
	private Session session1;
	
	private Session session2;
	
	public CheckersWindow() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_TITLE);
	}
	
	public CheckersWindow(Player player1, Player player2) {
		this();
		setPlayer1(player1);
		setPlayer2(player2);
	}
	
	public CheckersWindow(int width, int height, String title) {
		
		// Setup the window
		super(title);
		super.setSize(width, height);
		super.setLocationByPlatform(true);
		
		// Setup the components
		JPanel layout = new JPanel(new BorderLayout());
		this.board = new CheckerBoard(this);
		this.opts = new OptionPanel(this);
		layout.add(board, BorderLayout.CENTER);
		layout.add(opts, BorderLayout.SOUTH);
		this.add(layout);
		
		// Setup the network listeners
		CheckersNetworkHandler session1Handler, session2Handler;
		session1Handler = new CheckersNetworkHandler(true, this, board, opts);
		session2Handler = new CheckersNetworkHandler(false, this, board, opts);
		this.session1 = new Session(new ConnectionListener(
				0, session1Handler), null, null, -1);
		this.session2 = new Session(new ConnectionListener(
				0, session2Handler), null, null, -1);
	}
	
	public CheckerBoard getBoard() {
		return board;
	}

	/**
	 * Updates the type of player that is being used for player 1.
	 * 
	 * @param player1	the new player instance to control player 1.
	 */
	public void setPlayer1(Player player1) {
		this.board.setPlayer1(player1);
		this.board.update();
	}
	
	/**
	 * Updates the type of player that is being used for player 2.
	 * 
	 * @param player2	the new player instance to control player 2.
	 */
	public void setPlayer2(Player player2) {
		this.board.setPlayer2(player2);
		this.board.update();
	}
	
	/**
	 * Resets the game of checkers in the window.
	 */
	public void restart() {
		this.board.getGame().restart();
		this.board.update();
	}
	
	public void setGameState(String state) {
		this.board.getGame().setGameState(state);
	}
	
	public Session getSession1() {
		return session1;
	}

	public Session getSession2() {
		return session2;
	}
}
