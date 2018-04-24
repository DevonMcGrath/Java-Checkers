/* Name: NetworkWindow
 * Author: Devon McGrath
 * Description: This class is a window that contains connection settings for a
 * player.
 */

package ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The {@code NetworkWindow} class is used as a way to get input from the user
 * in making network connections between checkers clients. It specifies the
 * port that the client should listen on and the destination (remote) client's
 * host name or IP and port that it is listening on.
 * <p>
 * The network window can be provided with an action listener through
 * {@link #setActionListener(ActionListener)}. This action listener will get
 * invoked when either the "Listen" or "Connect" buttons are pressed. The
 * {@link ActionEvent} itself contains the network window as the source object
 * and the ID is either {@link #LISTEN_BUTTON} or {@link #CONNECT_BUTTON}
 * (depending on which button was clicked).
 * <p>
 * This class does not implement any network logic. It only provides an
 * interface to get the required network settings.
 */
public class NetworkWindow extends JFrame {

	private static final long serialVersionUID = -3680869784531557351L;
	
	/** The default width for the network window. */
	public static final int DEFAULT_WIDTH = 400;
	
	/** The default height for the network window. */
	public static final int DEFAULT_HEIGHT = 140;
	
	/** The default title for the network window. */
	public static final String DEFAULT_TITLE = "Configure Network";

	/** The ID sent to the action listener when the connect button is clicked. */
	public static final int CONNECT_BUTTON = 0;
	
	/** The ID sent to the action listener when the listen button is clicked. */
	public static final int LISTEN_BUTTON = 1;
	
	/** The text field for the source port. */
	private JTextField srcPort;
	
	/** The text field for the destination host name or IP. */
	private JTextField destHost;
	
	/** The text field for the destination port. */
	private JTextField destPort;
	
	/** The button that is used to indicate that the client should start
	 * listening on the port specified in {@link #srcPort}. */
	private JButton listen;
	
	/** The button that is used to indicate that the client should attempt to
	 * connect to the remote host/port specified in {@link #destHost} and
	 * {@link #destPort}. */
	private JButton connect;
	
	/** The panel containing all the components for this client's settings. */
	private JPanel src;
	
	/** The panel containing all the components for the remote client's
	 * settings. */
	private JPanel dest;
	
	/** The label to display the message on the window. */
	private JLabel msg;
	
	/** The action listener that is invoked when "Listen" or "Connect" is
	 * clicked. */
	private ActionListener actionListener;

	/**
	 * Creates a network window with all blank fields and no action listener.
	 * The action listener should be set through
	 * {@link #setActionListener(ActionListener)} in order to handle the
	 * network settings.
	 * 
	 * @see {@link #NetworkWindow(ActionListener)},
	 * {@link #NetworkWindow(ActionListener, int, String, int)}
	 */
	public NetworkWindow() {
		super(DEFAULT_TITLE);
		super.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		super.setLocationByPlatform(true);
		init();
	}
	
	/**
	 * Creates a network window with all blank fields and an action listener to
	 * receive events when buttons are clicked.
	 * 
	 * @param actionListener	the action listener to listen for events.
	 */
	public NetworkWindow(ActionListener actionListener) {
		this();
		this.actionListener = actionListener;
	}
	
	/**
	 * Creates a network window with all the fields already completed and an
	 * action listener to receive events when buttons are clicked.
	 * 
	 * @param actionListener	the action listener to listen for events.
	 * @param srcPort			the source port value.
	 * @param destHost			the destination host name or IP.
	 * @param destPort			the destination port value.
	 */
	public NetworkWindow(ActionListener actionListener, int srcPort,
			String destHost, int destPort) {
		this();
		this.actionListener = actionListener;
		setSourcePort(srcPort);
		setDestinationHost(destHost);
		setDestinationPort(destPort);
	}
	
	/** Initializes the components to display in the window. */
	private void init() {
		
		// Setup the components
		this.getContentPane().setLayout(new GridLayout(3, 1));
		this.srcPort = new JTextField(4);
		this.destHost = new JTextField(11);
		this.destHost.setText("127.0.0.1");
		this.destPort = new JTextField(4);
		this.listen = new JButton("Listen");
		this.listen.addActionListener(new ButtonListener());
		this.connect = new JButton("Connect");
		this.connect.addActionListener(new ButtonListener());
		this.src = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.dest = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.msg = new JLabel();
		this.src.add(new JLabel("Source port:"));
		this.src.add(srcPort);
		this.src.add(listen);
		this.dest.add(new JLabel("Destination host/port:"));
		this.dest.add(destHost);
		this.dest.add(destPort);
		this.dest.add(connect);
		setCanUpdateConnect(false);
		
		// Add tool tips
		this.srcPort.setToolTipText("Source port to listen for "
				+ "updates (1025 - 65535)");
		this.destPort.setToolTipText("Destination port to listen for "
				+ "updates (1025 - 65535)");
		this.destHost.setToolTipText("The destination host to send "
				+ "updates to (e.g. localhost)");

		createLayout(null);
	}
	
	/**
	 * Creates or updates the layout with an optional message.
	 * 
	 * @param msg	the message to display.
	 */
	private void createLayout(String msg) {
		
		this.getContentPane().removeAll();
		
		// Add the appropriate components
		this.getContentPane().add(src);
		this.getContentPane().add(dest);
		this.msg.setText(msg);
		this.getContentPane().add(this.msg);
		this.msg.setVisible(false);
		this.msg.setVisible(true);
	}
	
	/**
	 * Updates the state of the components required to update the port this
	 * client is listening on.
	 * 
	 * @param canUpdate	true if the listen components should be enabled.
	 */
	public void setCanUpdateListen(boolean canUpdate) {
		this.srcPort.setEnabled(canUpdate);
		this.listen.setEnabled(canUpdate);
	}
	
	/**
	 * Updates the state of the components required to make a remote
	 * connection to another checkers client.
	 * 
	 * @param canUpdate	true if the connect components should be enabled.
	 */
	public void setCanUpdateConnect(boolean canUpdate) {
		this.destHost.setEnabled(canUpdate);
		this.destPort.setEnabled(canUpdate);
		this.connect.setEnabled(canUpdate);
	}
	
	/**
	 * Gets the action listener that is invoked when either the "Listen" or
	 * "Connect" button is pressed.
	 * 
	 * @return the action listener for the buttons.
	 * @see {@link #setActionListener(ActionListener)}
	 */
	public ActionListener getActionListener() {
		return actionListener;
	}

	/**
	 * Sets the action listener that is invoked when either the "Listen" or
	 * "Connect" button is pressed.
	 * 
	 * @param actionListener	the action listener to receive button events.
	 * @see {@link #getActionListener()}
	 */
	public void setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}

	/**
	 * Gets the source port that the user entered in the corresponding text
	 * field. The source port is the port that this client will be listening
	 * on for connections from other remote clients.
	 * 
	 * @return the parsed source port text that the user entered.
	 * @see {@link #setSourcePort(int)}
	 */
	public int getSourcePort() {
		return parseField(srcPort);
	}
	
	/**
	 * Sets the source port entered in the corresponding text field. The source
	 * port is the port that this client will be listening on for connections
	 * from other remote clients.
	 * 
	 * @param port	the source port.
	 * @see {@link #getSourcePort()}
	 */
	public void setSourcePort(int port) {
		this.srcPort.setText("" + port);
	}
	
	/**
	 * Gets the destination host entered in the corresponding text field. The
	 * destination host is the IP or host name of the remote client to connect
	 * to.
	 * 
	 * @return the destination host text entered by the user.
	 * @see {@link #setDestinationHost(String)},
	 * {@link #getDestinationPort()}, {@link #setDestinationPort(int)}
	 */
	public String getDestinationHost() {
		return destHost.getText();
	}
	
	/**
	 * Sets the destination host text in the corresponding text field. The
	 * destination host is the IP or host name of the remote client to connect
	 * to.
	 * 
	 * @param host	the host name or IP of the destination host.
	 * @see {@link #getDestinationHost()}, {@link #getDestinationPort()},
	 * {@link #setDestinationPort(int)}
	 */
	public void setDestinationHost(String host) {
		this.destHost.setText(host);
	}
	
	/**
	 * Gets the destination port text entered in the corresponding text field.
	 * The destination port is the port on the remote client to connect to.
	 * 
	 * @return the parsed destination port text that the user entered.
	 * @see {@link #setDestinationPort(int)}, {@link #getDestinationHost()},
	 * {@link #setDestinationHost(String)}
	 */
	public int getDestinationPort() {
		return parseField(destPort);
	}
	
	/**
	 * Sets the destination port text in the corresponding text field.
	 * The destination port is the port on the remote client to connect to.
	 * 
	 * @param port	the destination port.
	 */
	public void setDestinationPort(int port) {
		this.destPort.setText("" + port);
	}
	
	/**
	 * Gets the message text being displayed on the window.
	 * 
	 * @return the message being displayed.
	 * @see {@link #setMessage(String)}
	 */
	public String getMessage() {
		return msg.getText();
	}
	
	/**
	 * Sets the message to display on the window and updates the user
	 * interface.
	 * 
	 * @param message	the message to display.
	 * @see {@link #getMessage()}
	 */
	public void setMessage(String message) {
		createLayout(message);
	}
	
	/**
	 * Attempts to parse the specified text field value to an integer.
	 * 
	 * @param tf	the text field to parse.
	 * @return the integer value parsed from the text field or 0 if an error
	 * occurred.
	 */
	private static int parseField(JTextField tf) {
		
		if (tf == null) {
			return 0;
		}
		
		// Try to parse the text input
		int val = 0;
		try {
			val = Integer.parseInt(tf.getText());
		} catch (NumberFormatException e) {}
		
		return val;
	}
	
	/**
	 * The {@code ButtonListener} class listens for button click events from
	 * any button in the window.
	 */
	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			if (actionListener != null) {
				JButton src = (JButton) e.getSource();
				ActionEvent event = null;
				if (src == listen) {
					event = new ActionEvent(NetworkWindow.this,
							LISTEN_BUTTON, null);
				} else {
					event = new ActionEvent(NetworkWindow.this,
							CONNECT_BUTTON, null);
				}
				actionListener.actionPerformed(event);
			}
		}
	}
}
