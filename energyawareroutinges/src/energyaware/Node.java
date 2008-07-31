package energyaware;

/**
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 * @version July 2008
 * 
 * A Node represents a system.  Virtually a "computer".
 */
public class Node implements TrafficGenerator {

	private Agent agent;	// The agent "protocol" that handles communication processing at the node.
	
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
	 * Receive a datagram from another node on the network.
	 * @param pDatagram Datagram received.
	 */
	public void receiveDatagram(Datagram pDatagram) {
		
	}
	
	/**
	 * Returns the NodeID assigned to this Node.
	 * @return This Node's NodeID.
	 */
	public int getID() {
		return -1;
	}
}
