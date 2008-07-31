package energyaware;

/**
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 * @version July 2008
 * 
 * A Node represents a system.  Virtually a "computer".
 */
public class Node implements TrafficGenerator {

	private Agent agent;	// The agent "protocol" that handles communication processing at the node.
	private int ID;
	private Network network;
	
	/**
	 * Default constructor creates a new Node
	 */
	public Node() {
		
		agent = new Agent();
	}
	
	/**
	 * Gets the agent working at this node.
	 * 
	 * @return The agent.
	 */
	public Agent getAgent() {
		
		return agent;
	}
	
	/**
	 * Set the agent to work at this node.
	 * 
	 * @param pAgent An agent.
	 */
	public void setAgent( Agent pAgent ) {
		
		agent = pAgent;
	}
	
	
	/**
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
	public void receiveFrame(Frame pFrame) {
		agent.receiveDatagram(pFrame.getDatagram());
	}
	
	/**
	 * Send the frame to the network.
	 * @param pFrame
	 */
	public void sendFrame(Frame pFrame) {
		network.broadcast(this, pFrame, 10);
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
