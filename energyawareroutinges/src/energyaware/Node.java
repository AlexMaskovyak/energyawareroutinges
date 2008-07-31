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
	
	
	/**
	 * Receive a datagram from another node on the network.
	 * @param pDatagram Datagram received.
	 */
	public void receiveFrame(Frame pFrame) {
		
	}
}
