package energyaware;

/**
 * Abstracted representation of Network level IDs for Node objects.
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 *
 */
public class NodeID {
	
	protected int ID;  // internal representation of the NodeID
	
	private static final int HASHFACTOR = 65521;

	
	/**
	 * Constructor.  Assigns this NodeID's value to the one specified. 
	 * @param pID Value to assign to this instance.
	 */
	public NodeID(int pID) {
		setID(pID);
	}
	
	/**
	 * Returns an integer representation of this NodeID.
	 * @return An integer representation of the NodeID.
	 */
	public int getID() {
		return ID;
	}
	
	/**
	 * Sets the NodeID based upon the integer specified.
	 * @param pID Base integer from which to create the NodeID.
	 */
	public void setID(int pID) {
		ID = Math.abs(pID);
	}
	
	@Override
	public boolean equals(Object pObject) {
		if (!(pObject instanceof NodeID)) {
			return false;
		}
		
		return ((NodeID)pObject).ID == ID;
	}
	
	@Override
	public int hashCode() {
		return (ID % NodeID.HASHFACTOR);
	}
	
	@Override
	public String toString() {
		return new Integer(ID).toString();
	}
}
