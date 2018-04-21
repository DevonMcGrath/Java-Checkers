/* Name: OptionPanel
 * Author: Devon McGrath
 * Description: This class is a user interface to interact with a checkers
 * game window.
 */

package ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.ComputerPlayer;
import model.HumanPlayer;
import model.Player;

/**
 * The {@code OptionPanel} class provides a user interface component to control
 * options for the game of checkers being played in the window.
 */
public class OptionPanel extends JPanel {

	private static final long serialVersionUID = -4763875452164030755L;

	/** The checkers window to update when an option is changed. */
	private CheckersWindow window;
	
	/** The button that when clicked, restarts the game. */
	private JButton restartBtn;
	
	/** The combo box that changes what type of player player 1 is. */
	private JComboBox<String> player1Opts;

	/** The combo box that changes what type of player player 2 is. */
	private JComboBox<String> player2Opts;
	
	/**
	 * Creates a new option panel for the specified checkers window.
	 * 
	 * @param window	the window with the game of checkers to update.
	 */
	public OptionPanel(CheckersWindow window) {
		super(new GridLayout(3, 1));
		
		this.window = window;
		
		// Initialize the components
		final String[] playerTypeOpts = {"Human", "Computer"};
		this.restartBtn = new JButton("Restart");
		this.player1Opts = new JComboBox<>(playerTypeOpts);
		this.player2Opts = new JComboBox<>(playerTypeOpts);
		this.restartBtn.addActionListener(new OptionListener());
		this.player1Opts.addActionListener(new OptionListener());
		this.player2Opts.addActionListener(new OptionListener());
		JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel middle = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		top.add(restartBtn);
		middle.add(new JLabel("(black) Player 1: "));
		middle.add(player1Opts);
		bottom.add(new JLabel("(white) Player 2: "));
		bottom.add(player2Opts);
		this.add(top);
		this.add(middle);
		this.add(bottom);
	}

	public CheckersWindow getWindow() {
		return window;
	}

	public void setWindow(CheckersWindow window) {
		this.window = window;
	}
	
	/**
	 * Gets a new instance of the type of player selected for the specified
	 * combo box.
	 * 
	 * @param playerOpts	the combo box with the player options.
	 * @return a new instance of a {@link model.Player} object that corresponds
	 * with the type of player selected.
	 */
	private static Player getPlayer(JComboBox<String> playerOpts) {
		
		Player player = new HumanPlayer();
		if (playerOpts == null) {
			return player;
		}
		
		// Determine the type
		String type = "" + playerOpts.getSelectedItem();
		if (type.equals("Computer")) {
			player = new ComputerPlayer();
		}
		
		return player;
	}
	
	/**
	 * The {@code OptionListener} class responds to the components within the
	 * option panel when they are clicked/updated.
	 */
	private class OptionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			// No window to update
			if (window == null) {
				return;
			}
			
			Object src = e.getSource();

			// Handle the user action
			if (src == restartBtn) {
				window.restart();
			} else if (src == player1Opts) {
				window.setPlayer1(getPlayer(player1Opts));
			} else if (src == player2Opts) {
				window.setPlayer2(getPlayer(player2Opts));
			}
		}
	}
}
