/* Name: ConnectionHandler
 * Author: Devon McGrath
 * Description: This class is responsible for handling a connection to the
 * ConnectionListener.
 */

package network;

import java.awt.event.ActionEvent;
import java.net.Socket;

/**
 * The {@code ConnectionHandler} class handles a connection to an instance of
 * the {@link ConnectionListener} class. Once created, it will be run on a new
 * thread immediately after the connection is made and invokes the action
 * listener from the {@code ConnectionListener} class (if one is specified).
 */
public class ConnectionHandler extends Thread {

	/** The connection listener that created this handler. */
	private ConnectionListener listener;
	
	/** The connection from the remote client to this one. */
	private Socket socket;
	
	/**
	 * Creates a connection handler that is capable of handling an incoming
	 * connection.
	 * 
	 * @param listener	the listener to which the connection was made.
	 * @param socket	the actual connection between the two processes.
	 */
	public ConnectionHandler(ConnectionListener listener, Socket socket) {
		this.listener = listener;
		this.socket = socket;
	}
	
	/**
	 * Runs the action listener handler from the {@link ConnectionListener}
	 * instance that the connection was made to. If the action listener was not
	 * specified, then this method does nothing.
	 * <p>
	 * Note: this method should be called using {@link #start()} and not called
	 * directly to allow it to run on a new thread.
	 */
	@Override
	public void run() {
		
		if (listener == null) {
			return;
		}
		
		// Send the event to the handler
		ActionEvent e = new ActionEvent(this, 0, "CONNECTION ACCEPT");
		if (listener.getConnectionHandler() != null) {
			this.listener.getConnectionHandler().actionPerformed(e);
		}
	}

	/**
	 * Gets the listener associated with the connection.
	 * 
	 * @return the connection listener.
	 */
	public ConnectionListener getListener() {
		return listener;
	}

	/**
	 * Gets the socket connection between this process and the remote host's
	 * process.
	 * 
	 * @return the connection.
	 */
	public Socket getSocket() {
		return socket;
	}
}
