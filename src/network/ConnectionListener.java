/* Name: ConnectionListener
 * Author: Devon McGrath
 * Description: This class acts as a server and listens for connections on the
 * specified port.
 */

package network;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The {@code ConnectionListener} class listens for connections on a specific
 * port. Once a connection is made, it will create an instance of
 * {@link ConnectionHandler} and run it on a new thread to handle the
 * connection. It will use the connection handler passed either through
 * {@link #ConnectionListener(int, ActionListener)} or
 * {@link #setConnectionHandler(ActionListener)}.
 * <p>
 * The action listener will be invoked with a {@code ConnectionHandler} object
 * that contains the listener that created it and the socket connection.
 */
public class ConnectionListener extends Thread {
	
	/** The socket that will listen for connections. */
	private ServerSocket serverSocket;
	
	/** The action listener that will be invoked when a connection is made. */
	private ActionListener connectionHandler;
	
	/**
	 * Creates a connection listener on a dynamically allocated port.
	 */
	public ConnectionListener() {
		this(0);
	}
	
	/**
	 * Creates a connection listener for the specified port.
	 * 
	 * @param port	the port to listen on.
	 */
	public ConnectionListener(int port) {
		setPort(port);
	}
	
	/**
	 * Creates a connection listener on the specified port with a connection
	 * handler.
	 * 
	 * @param port				the port to listen on.
	 * @param connectionHandler	the action listener to handle connections.
	 */
	public ConnectionListener(int port, ActionListener connectionHandler) {
		setPort(port);
		this.connectionHandler = connectionHandler;
	}
	
	/**
	 * Starts listening on the port from {@link #getPort()} on a new thread. To
	 * avoid creating a new thread, {@link #run()} can be called directly.
	 * 
	 * @see {@link #run()}, {@link #stopListening()}
	 */
	public void listen() {
		start();
	}

	/**
	 * Starts listening on the port from {@link #getPort()} but does so on the
	 * thread it was invoked from. To run the listener on a new thread,
	 * {@link #listen()} can be called directly.
	 * 
	 * @see {@link #listen()}, {@link #stopListening()}
	 */
	@Override
	public void run() {
		
		// Special cases
		if (serverSocket == null) {
			return;
		}
		if (serverSocket.isClosed()) {
			try {
				this.serverSocket = new ServerSocket(
						serverSocket.getLocalPort());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Listen for incoming attempts to connect to the server
		while (!serverSocket.isClosed()) {
			try {

				// Get the connection and handle it
				ConnectionHandler conn = new ConnectionHandler(
						this, serverSocket.accept());
				conn.start();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Tells the listener to stop listening for new connections.
	 * 
	 * @return true if the connection was torn down successfully.
	 * @see {@link #listen()}, {@link #run()}
	 */
	public boolean stopListening() {
		
		// Special cases
		if (serverSocket == null || serverSocket.isClosed()) {
			return true;
		}

		// Try to close the connection
		boolean err = false;
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			err = true;
		}

		return !err;
	}
	
	/**
	 * Gets the port the listener will listen on or is listening on.
	 * 
	 * @return the server socket port.
	 * @see {@link #setPort(int)}
	 */
	public int getPort() {
		return serverSocket.getLocalPort();
	}
	
	/**
	 * Sets the port for this listener to listen on. If the port is less than
	 * 1, the port will be dynamically allocated. The listener will be stopped
	 * if it is listening.
	 * 
	 * @param port	the new port to listen on.
	 * @see {@link #getPort()}
	 */
	public void setPort(int port) {
		
		// Stop the server, if it is running
		stopListening();
		
		// Create the new server socket (the server will need to be restarted)
		try {
			if (port < 0) {
				this.serverSocket = new ServerSocket(0);
			} else {
				this.serverSocket = new ServerSocket(port);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public ActionListener getConnectionHandler() {
		return connectionHandler;
	}

	public void setConnectionHandler(ActionListener connectionHandler) {
		this.connectionHandler = connectionHandler;
	}

	/**
	 * Reads all the data that was sent until either the connection is closed
	 * or the other client stops sending data.
	 * 
	 * @param socket	the connection that should be open.
	 * @return the data that was read or an empty string otherwise.
	 */
	public static String read(Socket socket) {

		if (socket == null) {
			return "";
		}

		// Read all the data from the stream
		String data = "";
		try {
			InputStream in = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = br.readLine()) != null) {
				data += line + "\n";
				if (!br.ready()) {break;}
			}
			if (!data.isEmpty()) {
				data = data.substring(0, data.length()-1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return data;
	}
	
	/**
	 * Checks to see if a specific port is available.
	 *
	 * @param port	the port to check for availability.
	 */
	public static boolean available(int port) {
		
		if (port < 0 || port > 65535) {
			return false;
		}

		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {}
			}
		}

		return false;
	}
}
