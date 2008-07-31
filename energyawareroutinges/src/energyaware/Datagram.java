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
	
	public static final String RREQ = "RREQ";	// Represents a RREQ message type
	public static final String RREP = "RREP";	// Represents a RREP message type
	public static final String RERR = "RERR";	// Represents a RRER message type
	public static final String UNINIT = "NA";	// Represents an uninitialized message type
	public static final int NONE = -1;			// Used for default constructor values
	
	private String type;						// The type of this datagram
	private int source;							// The source address of this datagram
	private int destination;					// The destination address for this datagram
	private Segment segment;					// The paylod of this datagram
	private List<Integer> path;					// A list of Node IDs that represents the path for this datagram
	private List<Integer> batteryMetricValues;	// A list of battery metric values for storing new battery metrics that are encountered
	
	/**
	 * Default constructor.
	 */
	public Datagram() {
		
		type = Datagram.UNINIT;
		source = NONE;
		destination = NONE;
		segment = new Segment();
		path = new ArrayList<Integer>();
		batteryMetricValues = new ArrayList<Integer>();
	}
	
	/**
	 * Creates a new Datagram from the the specified parameters.
	 * 
	 * @param pType The Datagram's type.
	 * @param pSource The Datagram's source address.
	 * @param pSegment The payload for this datagram.
	 * @param pDestination The Datagram's destination address.
	 * @param pPath The Datagram's specified path to follow.
	 * @param pBatteryMetric The battery metric for this node.
	 */
	public Datagram( String pType, int pSource, int pDestination, Segment pSegment, List<Integer> pPath, int batteryMetric ) {
		type = pType;
		source = pSource;
		destination = pDestination;
		path = pPath;
		batteryMetricValues = new ArrayList<Integer>();
		batteryMetricValues.add(batteryMetric);
	}
	
	/**
	 * Get the type field.
	 * 
	 * @return The type
	 */
	public String getType() {
		
		return type;
	}
	
	/**
	 * Set the type field.
	 * 
	 * @param pType A type.
	 */
	public void setType( String pType ) {
		
		type = pType;
	}
	
	/**
	 * Get the source field
	 *
	 * @return The Source
	 */
	public int getSource() {
	
		return source;
	}
	
	/**
	 * Set the source id.
	 * 
	 * @param pSource A source id.
	 */
	public void setSource( int pSource ) {
		
		source = pSource;
	}
	
	/**
	 * Get the destination node id.
	 * 
	 * @return The destination node id.
	 */
	public int getDestination() {
		
		return destination;
	}
	
	/**
	 * Set the destination node id.
	 * 
	 * @param pDestination A destination node id.
	 */
	public void setDestination( int pDestination ) {
		
		destination = pDestination;
	}
	
	/**
	 * Get the path of node ids.
	 * 
	 * @return The path.
	 */
	public List<Integer> getPath() {
		
		return path;
	}
	
	/**
	 * Set a new path of node ids.
	 * 
	 * @param pPath A path of node ids.
	 */
	public void setPath( List<Integer> pPath ) {
	
		path = pPath;
	}
	/**
	 * Get the list of battery metrics.
	 * 
	 * @return The list of battery metrics.
	 */
	public List<Integer> getBatteryMetricValues() {
		
		return batteryMetricValues;
	}
	
	/**
	 * Set the list of battery metrics.
	 * 
	 * @param pBatteryMetricValues The battery metric values.
	 */
	public void setBatteryMetricValues( List<Integer> pBatteryMetricValues ) {
		
		batteryMetricValues = pBatteryMetricValues;
	}
	
	/**
	 * Takes a list and reverses the order of its elements.
	 * 
	 * @param incoming A list that needs to be reversed
	 * @return A list in the reverse order of the incoming
	 */
	public static List<Integer> reverse( ArrayList<Integer> incoming ) {
		// obtain size of list
		int size = incoming.size();
		
		// An empty incoming list will result in an empty outgoing list
		ArrayList<Integer> output = new ArrayList<Integer>( size );
    	
    	for( int i = 0; i < size; ++i) {
    		output.add(i, incoming.get(size - 1 - i));
    	}

    	for(int i : output) {
    		System.out.print(i + " ");
    	}
    	System.out.println();
    	System.out.println("reverse was run");
    	
    	return output;
	}

	public static void run() {}
}
