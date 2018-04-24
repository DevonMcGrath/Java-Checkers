/* Name: Command
 * Author: Devon McGrath
 * Description: This class represents a command that can be sent to another
 * checkers window.
 */

package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * The {@code Command} class is used to represent a command to send from one
 * checkers client to another. It defines the standard commands to send.
 */
public class Command {

	/** The command to send the current game state to the connected client.
	 * Note: a matching SID is required for the game to be updated. */
	public static final String COMMAND_UPDATE = "UPDATE";
	
	/** The command to try to connect to another checkers client. Note: this
	 * command requires two additional lines: 1) the remote port that should be
	 * connected to, and 2) either "1" or "2" indicating if the remote client
	 * is connecting as player 1 or 2. */
	public static final String COMMAND_CONNECT = "CONNECT";
	
	/** The command to disconnect from the remote client (e.g. when the client
	 * stops the program). Note: this command requires one additional line of
	 * the matching SID. */
	public static final String COMMAND_DISCONNECT = "DISCONNECT";
	
	/** The command to get the game state from a remote client. Note: a
	 * matching SID is required for the game state to be sent. */
	public static final String COMMAND_GET = "GET-STATE";
	
	/** The command to issue. */
	private String command;
	
	/** The data on the following lines. */
	private String[] data;
	
	/**
	 * Constructs a command with the data.
	 * 
	 * @param command	the command to send.
	 * @param data		the data to send (where each element is a line).
	 */
	public Command(String command, String... data) {
		this.command = command;
		this.data = data;
	}
	
	/**
	 * Sends the command and the data to the specified host and port. It then
	 * reads and returns the response from the other host.
	 * 
	 * @param host	the remote host (e.g. 127.0.0.1).
	 * @param port	the port to connect to.
	 * @return the response from the host or an empty string if an error
	 * occurred.
	 * @see {@link #getOutput()}
	 */
	public String send(String host, int port) {
		
		String data = getOutput(), response = "";
		try {
			
			// Write the response
			Socket s = new Socket(host, port);
			PrintWriter writer = new PrintWriter(s.getOutputStream());
			writer.println(data);
			writer.flush();

			// Get the response
			BufferedReader br = new BufferedReader(new InputStreamReader(
					s.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				response += line + "\n";
			}
			if (!response.isEmpty()) {
				response = response.substring(0, response.length() - 1);
			}
			s.close();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
	/**
	 * Gets the output that will be sent for this command and is the
	 * combination of the command as the first line and each line in the
	 * data up until the first null value (or the end of the data).
	 * 
	 * @return the output from this command.
	 */
	public String getOutput() {
		
		String out = command;
		
		// Add the lines until the first null value
		int n = data == null? 0 : data.length;
		for (int i = 0; i < n; i ++) {
			if (data[i] == null) {
				break;
			}
			out += "\n" + data[i];
		}
		
		return out;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String[] getData() {
		return data;
	}

	public void setData(String[] data) {
		this.data = data;
	}
}
