package energyaware;

import jess.JessException;

/**
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 * @version July 2008
 * 
 * A Node represents a system.  Virtually a "computer".
 */
public class Node {

	private Agent agent;		// The agent "protocol" that handles communication processing at the node.
	private Battery battery;	// controls how long we can transmit and receive messages
	private int ID;
	private Network network;	// our connection to the network
	
	private Frame lastFrameSent; // holds the last frame sent
	
	public static final int STARTINGBATTERYLEVEL = 100;
	
	/**
	 * Default constructor creates a new Node
	 */
	private Node() {
		this( (int)(100 * Math.random()) );
	}
	
	/**
	 * Constructor that takes parameters in case you never saw Java before
	 */
	private Node( int pId ) {
		ID = pId;
		battery = new Battery( STARTINGBATTERYLEVEL, STARTINGBATTERYLEVEL );
	}
	
	public static Node getInstance( int pId ) {
		
		Node node = null;
		
		try {
			node = new Node( pId );
			Agent agent = new Agent();
		
			node.setAgent( agent );
			agent.setNode( node );
		}
		catch( JessException e ) {}
		
		return node;
	}
	
	/**
	 * Gets the agent working at this node.
	 * @return The agent.
	 */
	public Agent getAgent() {
		return agent;
	}
	
	/**
	 * Set the agent to work at this node.  Also ensures that this agent has an
	 * updated reference to this node.
	 * @param pAgent An agent.
	 */
	public void setAgent( Agent pAgent ) {
		agent = pAgent;
		try {
			agent.setNode( this );
		}
		catch ( JessException e ) {
			
		}
	}
	
	
	/**
	 * Get the battery installed in this node.
	 * @return Battery installed in this node.
	 */
	public Battery getBattery() {
		return battery;
	}
	
	/**
	 * Install a battery into this node.
	 * @param pBattery Battery to install into this node.
	 */
	public void setBattery(Battery pBattery) {
		battery = pBattery;
	}
	
	/**
	 * 
	 * Returns this Node's network ID.
	 * @return This Node's assigned network ID.
	 */
	public int getID() {
		return ID;
	}
	
	/**
	 * Sets this Node's network ID.
	 * @param pID Network ID to assign to this Node.
	 */
	public void setID(int pID) {
		ID = pID;
	}
	
	/**
	 * Retrieves the last frame that was sent by this node.
	 * @return Last frame sent by this node, null otherwise.
	 */
	public Frame getLastFrameSent() {
		return lastFrameSent;
	}
	
	/**
	 * Get a reference to the network to which this Node belongs.
	 * @return Reference to the network.
	 */
	public Network getNetwork() {
		return network;
	}
	
	/**
	 * Set the reference to the network to which this Node belongs.
	 * @param pNetwork Reference to network with which we belong/communicate.
	 */
	public void setNetwork(Network pNetwork) {
		network = pNetwork;
	}
	
	
	
	public void run() {
	}
	
	public void stop() {
		
	}
	
	
	/// hooks for the datalink layer and the network object
	/**
	 * Receive a from another node on the network.  This simulates the datalink
	 * layer.
	 * @param pDatagram Datagram received.
	 */
	public void receiveFrame(Frame pFrame, int pTransmissionDistance) {
		agent.receiveDatagram(pFrame.getDatagram(), pTransmissionDistance );
	}
	
	/**
	 * Send the frame to the network.
	 * @param pFrame Frame to broadcast onto the network.
	 * @param pTransmissionDistance Transmission distance which affects the 
	 * 			amount of power we use to send this frame.
	 */
	public void sendFrame(Frame pFrame, int pTransmissionDistance) {
		lastFrameSent = pFrame;
		
		/*** THIS NEEDS TO REDUCE OUR BATTERY LEVEL ***/
		if (network != null) {
			network.broadcast(this, pFrame, pTransmissionDistance);
		}
		System.out.println("Sending frame...");
	}
	
	
	/**
	 * Receive a message from the stack and pass to the "application."
	 * @param pMessage Message with a message we basically ignore.
	 */
	public void receiveMessage(Message pMessage) {
		// NO-OP, we don't care about getting the message since we don't really
		// have any sort of application to send it to.
	}
	
	/**
	 * Send a message into the Agent to send out.
	 * @param pMessage message to send
	 * @param pDestinationID id of destination
	 */
	public void sendMessage(Message pMessage, int pDestinationNodeID) {
		agent.receiveMessage(pMessage, pDestinationNodeID);
	}
}
