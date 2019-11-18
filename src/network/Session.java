/* Name: Session
 * Author: Devon McGrath
 * Description: This class represents a session between two clients.
 */

package network;

/**
 * The {@code Session} class represents a session between this client and a
 * remote checkers client. It contains the important connection information
 * that is used to pass messages and accept messages.
 */
public class Session {

	/** The listener handling the connection on this client. */
	private ConnectionListener listener;
	
	/** The session ID used for correspondence between the two clients. */
	private String sid;
	
	/** The destination host name or IP. */
	private String destinationHost;
	
	/** The destination port. */
	private int destinationPort;

	public Session(ConnectionListener listener, String sid,
			String destinationHost, int destinationPort) {
		this.listener = listener;
		this.sid = sid;
		this.destinationHost = destinationHost;
		this.destinationPort = destinationPort;
	}
	
	public Session(String sid, int sourcePort,
			String destinationHost, int destinationPort) {
		this.listener = new ConnectionListener(sourcePort);
		this.sid = sid;
		this.destinationHost = destinationHost;
		this.destinationPort = destinationPort;
	}

	public ConnectionListener getListener() {
		return listener;
	}

	public void setListener(ConnectionListener listener) {
		this.listener = listener;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getDestinationHost() {
		return destinationHost;
	}

	public void setDestinationHost(String destinationHost) {
		this.destinationHost = destinationHost;
	}

	public int getDestinationPort() {
		return destinationPort;
	}

	public void setDestinationPort(int destinationPort) {
		this.destinationPort = destinationPort;
	}
	
	public int getSourcePort() {
		return (listener == null? -1 : listener.getPort());
	}
	
	public void setSourcePort(int sourcePort) {
		if (listener != null) {
			this.listener.setPort(sourcePort);
		}
	}
}
