/* Name: CheckersNetworkHandler
 * Author: Devon McGrath
 * Description: This class handles connections between two clients. It receives
 * connections and sends responses.
 */

package network;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import model.NetworkPlayer;
import ui.CheckerBoard;
import ui.CheckersWindow;
import ui.NetworkWindow;
import ui.OptionPanel;

/**
 * The {@code CheckersNetworkHandler} class handles incoming connections from
 * remote checkers clients. It decides whether connections should be accepted
 * and perform an action on this client. It sends two responses: accepted and
 * denied. Each response starts with the corresponding string
 * {@link #RESPONSE_ACCEPTED} or {@link #RESPONSE_DENIED}.
 */
public class CheckersNetworkHandler implements ActionListener {
	
	/** The minimum number of characters in the session ID. */
	private static final int MIN_SID_LENGTH = 16;
	
	/** The max number of characters in the session ID. */
	private static final int MAX_SID_LENGTH = 64;

	/** The start of a response that was accepted. */
	public static final String RESPONSE_ACCEPTED = "ACCEPTED";

	/** The start of a response that was denied. */
	public static final String RESPONSE_DENIED = "DENIED";

	/** The flag indicating if this handler is handling a connection to player
	 * 1 or not. */
	private boolean isPlayer1;

	/** The checkers window. */
	private CheckersWindow window;

	/** The checker board from the checkers window. */
	private CheckerBoard board;
	
	/** The option panel in the checkers window. */
	private OptionPanel opts;

	public CheckersNetworkHandler(boolean isPlayer1, CheckersWindow window,
			CheckerBoard board, OptionPanel opts) {
		this.isPlayer1 = isPlayer1;
		this.window = window;
		this.board = board;
		this.opts = opts;
	}

	/**
	 * Handles a new connection from the {@link ConnectionListener}.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// Invalid event
		if (e == null || !(e.getSource() instanceof ConnectionHandler)) {
			return;
		}

		// Get the data from the connection
		ConnectionHandler handler = (ConnectionHandler) e.getSource();
		String data = ConnectionListener.read(handler.getSocket());
		data = data.replace("\r\n", "\n");
		
		// Unable to handle
		if (window == null || board == null || opts == null) {
			sendResponse(handler, "Client error: invalid network handler.");
			return;
		}
		
		Session s1 = window.getSession1(), s2 = window.getSession2();

		// Determine if a valid user
		String[] lines = data.split("\n");
		String cmd = lines[0].split(" ")[0].toUpperCase();
		String sid = lines.length > 1? lines[1] : "";
		String response = "";
		boolean match = false;
		if (isPlayer1) {
			match = sid.equals(s1.getSid());
		} else {
			match = sid.equals(s2.getSid());
		}

		// A connected client wants to update the board
		if (cmd.equals(Command.COMMAND_UPDATE)) {
			String newState = (match && lines.length > 2? lines[2] : "");
			response = handleUpdate(newState);
		}
		
		// A client wants to connect to this one
		else if (cmd.equals(Command.COMMAND_CONNECT)) {
			
			// Get the port that was passed (in the SID field)
			int port = -1;
			try {
				port = Integer.parseInt(sid);
			} catch (NumberFormatException err) {}
			
			// Determine if the client attempting to connect is player 1
			String isP1 = (lines.length > 2? lines[2] : "");
			boolean remotePlayer1 = isP1.startsWith("1");
			
			// Handle the connect request
			response = handleConnect(handler.getSocket(), port, remotePlayer1);
		}
		
		// A connected client wants the current game state
		else if (cmd.equals(Command.COMMAND_GET)) {

			// Send the board if there was a SID match
			if (match) {
				response = RESPONSE_ACCEPTED + "\n"
						+ board.getGame().getGameState();
			} else {
				response = RESPONSE_DENIED;
			}
		}
		
		// A connected client wants to disconnect
		else if (cmd.equals(Command.COMMAND_DISCONNECT)) {
			
			// Disconnect if SID match
			if (match) {
				response = RESPONSE_ACCEPTED + "\nClient has been disconnected.";
				if (isPlayer1) {
					s1.setSid(null);
					this.opts.getNetworkWindow1().setCanUpdateConnect(true);
				} else {
					s2.setSid(null);
					this.opts.getNetworkWindow2().setCanUpdateConnect(true);
				}
			} else {
				response = RESPONSE_DENIED + "\nError: cannot disconnect if not connected.";
			}
		}
		
		// Invalid command
		else {
			response = RESPONSE_DENIED + "\nJava Checkers - unknown "
					+ "command '" + cmd + "'";
		}

		// Send the response to whoever connected
		sendResponse(handler, response);
	}
	
	/**
	 * Handles the update command from a connected client. The update commands
	 * is used by the other connected client to update the game state after a
	 * move was made. If both players on this client are network players, then
	 * the state if forwarded to the other player (effectively making this
	 * client a router).
	 * 
	 * @param newState
	 * @return
	 */
	private String handleUpdate(String newState) {
		
		// New state is invalid 
		if (newState.isEmpty()) {
			return RESPONSE_DENIED;
		}

		// Update the current client's game state
		this.board.setGameState(false, newState, null);
		if (!board.getCurrentPlayer().isHuman()) {
			board.update();
		}

		// Check if both players are network players
		// If so, forward the game state (i.e. this client acts as a router)
		if (isPlayer1 &&
				board.getPlayer2() instanceof NetworkPlayer) {
			board.sendGameState(window.getSession2());
		} else if (!isPlayer1 &&
				board.getPlayer1() instanceof NetworkPlayer) {
			board.sendGameState(window.getSession1());
		}

		return RESPONSE_ACCEPTED;
	}
	
	/**
	 * Checks if the client connect request can be satisfied. A connection
	 * request can be satisfied for the player if there is no connected client
	 * and the client is the correct player (e.g. player 1 can't connect to
	 * player 1 on this client as the game would not be able to be played).
	 * 
	 * @param s				the socket that the remote client used to connect.
	 * @param port			the port that the remote client sent in the request.
	 * @param remotePlayer1	the flag indicating if the remote player is player 1.
	 * @return the resulting response to send to the remote client.
	 */
	private String handleConnect(Socket s, int port, boolean remotePlayer1) {

		// Check if there is someone already connected
		Session s1 = window.getSession1(), s2 = window.getSession2();
		String sid1 = s1.getSid();
		String sid2 = s2.getSid();
		if ((isPlayer1 && sid1 != null && !sid1.isEmpty()) ||
				(!isPlayer1 && sid2 != null && !sid2.isEmpty())) {
			return RESPONSE_DENIED + "\nError: user already connected.";
		}
		
		// Check that it is a valid connection
		if (!(isPlayer1 ^ remotePlayer1)) {
			return RESPONSE_DENIED + "\nError: the other client is already "
					+ "player " + (remotePlayer1? "1." : "2.");
		}
		String host = s.getInetAddress().getHostAddress();
		if (host.equals("127.0.0.1")) {
			if ((isPlayer1 && port == s2.getSourcePort()) ||
					(!isPlayer1 && port == s1.getSourcePort())) {
				return RESPONSE_DENIED + "\nError: the client cannot connect "
						+ "to itself.";
			}
		}

		// Update the connection
		String sid = generateSessionID();
		Session session = isPlayer1? s1 : s2;
		NetworkWindow win = (isPlayer1?
				opts.getNetworkWindow1() : opts.getNetworkWindow2());
		session.setSid(sid);
		session.setDestinationHost(host);
		session.setDestinationPort(port);

		// Update the UI
		win.setDestinationHost(host);
		win.setDestinationPort(port);
		win.setCanUpdateConnect(false);
		win.setMessage("  Connected to " + host + ":" + port + ".");

		return RESPONSE_ACCEPTED + "\n" + sid + "\nSuccessfully connected.";
	}

	/**
	 * Sends a response to the connection handler's connection, if it is not
	 * closed.
	 * 
	 * @param handler	the connection handler to send the response to.
	 * @param response	the response data to send.
	 */
	private static void sendResponse(ConnectionHandler handler,
			String response) {

		// Trivial cases
		if (handler == null) {
			return;
		}
		Socket s = handler.getSocket();
		if (s == null || s.isClosed()) {
			return;
		}
		if (response == null) {
			response = "";
		}

		// Write the response and close the connection
		try (OutputStream os = s.getOutputStream()) {
			os.write(response.getBytes());
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			// Close the socket
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Generates a session ID of a random length with random characters. The
	 * session ID itself is guaranteed to be at least {@value #MIN_SID_LENGTH}
	 * characters long, but not longer than {@value #MAX_SID_LENGTH}.
	 * 
	 * @return a randomly generated SID.
	 */
	private static String generateSessionID() {

		// Generate a string of random length
		String sid = "";
		int chars = (int) ((MAX_SID_LENGTH - MIN_SID_LENGTH) * Math.random())
				+ MIN_SID_LENGTH;
		for (int i = 0; i < chars; i ++) {
			
			// Generate a character in a random range
			int t = (int) (4 * Math.random());
			int min = 32, max = 48;
			if (t == 1) {
				min = 48;
				max = 65;
			} else if (t == 2) {
				min = 65;
				max = 97;
			} else if (t == 3) {
				min = 97;
				max = 125;
			}
			char randChar = (char) ((Math.random() * (max - min)) + min);
			sid += randChar;
		}

		return sid;
	}
}
