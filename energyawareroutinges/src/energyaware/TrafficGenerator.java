package energyaware;

/**
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 * @version July 2008
 * 
 * Traffic Generator are held by node objects. They interacts with JESS,
 * providing JESS with segments to send out and receiving segments meant for
 * this node.
 */
public interface TrafficGenerator {

	public Agent agent = null;
	
	/**
	 * Gets the agent working at this node.
	 * 
	 * @return The agent.
	 */
	public Agent getAgent();
	
	/**
	 * Set the agent to work at this node.
	 * 
	 * @param pAgent An agent.
	 */
	public void setAgent( Agent pAgent );
}
