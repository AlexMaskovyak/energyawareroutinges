package energyaware;

/**
 * Abstracted representation of Network level IDs for Node objects.
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 *
 * A node ID is a assigned to every node.  For the purposes of our project, a
 * node ID is simple a unique Integer value.
 */
public class NodeID {
	
	protected int ID;
	private static final int HASHFACTOR = 65521;
	
	/**
	 * Constructor.  Assigns this NodeID's value to the one specified. 
	 *
	 * @param pID Value to assign to this instance.
	 */
	public NodeID(int pID) {
		setID(pID);
	}
	
	/**
	 * Returns an integer representation of this NodeID.
	 * 
	 * @return An integer representation of the NodeID.
	 */
	public int getID() {
		return ID;
	}
	
	/**
	 * Sets the NodeID based upon the integer specified.
	 * 
	 * @param pID Base integer from which to create the NodeID.
	 */
	public void setID(int pID) {
		ID = Math.abs(pID);
	}
	
	/**
	 * Override of the default equals method.
	 */
	@Override
	public boolean equals(Object pObject) {
		if (!(pObject instanceof NodeID)) {
			return false;
		}
		
		return ((NodeID)pObject).ID == ID;
	}
	
	/**
	 * Override of the default hashcode method.
	 */
	@Override
	public int hashCode() {
		return (ID % NodeID.HASHFACTOR);
	}
	
	/**
	 * Override of the default tostring method.
	 */
	@Override
	public String toString() {
		return new Integer(ID).toString();
	}
}
