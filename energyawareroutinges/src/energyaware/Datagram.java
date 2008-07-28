package energyaware;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 * @version July 2008
 * 
 * A datagram is the data encapsulation used to hold a segment and is exchanged between
 * the Network and Datalink layers.  A datagram has 5 fields for type, source address,
 * destination address, path list and battery metric list.
 */
public class Datagram {
	
	public String type;							// The type of this datagram
	public NodeID source;						// The source address of this datagram
	public NodeID destination;					// The destination address for this datagram
	public List<NodeID> path;					// A list of Node IDs that represents the path for this datagram
	private List<Integer> batteryMetricValues;	// A list of battery metric values for storing new battery metrics that are encountered
	
	/**
	 * Default constructor.
	 */
	public Datagram() {
		
		type = "None";
		source = "None";
		destination = "None";
		path = new ArrayList<String>();
		batteryMetricValues = new ArrayList<Integer>();
	}
	
	/**
	 * Creates a new Datagram from the the specified parameters.
	 * 
	 * @param pType The Datagram's type.
	 * @param pSource The Datagram's source address.
	 * @param pDestination The Datagram's destination address.
	 * @param pPath The Datagram's specified path to follow.
	 */
	public Datagram( String pType, String pSource, String pDestination, List<String> pPath ) {
		
		type = pType;
		source = pSource;
		destination = pDestination;
		path = pPath;
		batteryMetricValues = new ArrayList<Integer>( pPath.size() );
	}
	
	/**
	 * Sets the datagram's type.
	 * 
	 * @param pType The datagram's type.
	 */
	public void setType( String pType ) {
		
		type = pType;
	}
	
	/**
	 * Gets the datagram's type.
	 * 
	 * @return The datagram's type.
	 */
	public String getType() {
		
		return type;
	}
}
