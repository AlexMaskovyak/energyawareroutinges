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
	public static final String DATA = "DATA";	// Represents normal data
	public static final String UNINIT = "NA";	// Represents an uninitialized message type
	public static final int NONE = -1;			// Used for default constructor values
	
	private String type;						// The type of this datagram
	private int source;							// The source address of this datagram
	private int destination;					// The destination address for this datagram
	private int rreqID;							// The semi-unique id value (Math.Randint)
	private Segment segment;					// The payload of this datagram
	private List<Integer> path;					// A list of Node IDs that represents the path for this datagram
	private List<Integer> batteryMetricValues;	// A list of battery metric values for storing new battery metrics that are encountered
	private List<Integer> transmissionValues;	// A minimum transmission value or distance cost
	
	/**
	 * Default constructor.
	 */
	public Datagram() {		
		this(Datagram.UNINIT,
				Datagram.NONE,
				Datagram.NONE);
	}
	
	/**
	 * Create a new datagram with type, source, and destination values.  Set
	 * up objects.
	 * @param pType
	 * @param pSource
	 * @param pDestination
	 */
	public Datagram ( String pType, int pSource, int pDestination ) {
		this ( pType, pSource, pDestination, new Segment(), new ArrayList<Integer>(), new ArrayList<Integer>());
	}
	
	/**
	 * Create a new datagram with type, source, destination and Segment values.
	 * Set up objects.
	 * 
	 * @param pType
	 * @param pSource
	 * @param pDestination
	 * @param pSegment
	 */
	public Datagram ( String pType, int pSource, int pDestination, Segment pSegment ) {
		this ( pType, pSource, pDestination, pSegment, new ArrayList<Integer>(), new ArrayList<Integer>() );
	}
	
	/**
	 * 
	 * @param pType
	 * @param pSource
	 * @param pDestination
	 * @param pSegment
	 * @param pPath
	 * @param pBatteryMetricValues
	 */
	public Datagram ( String pType, int pSource, int pDestination, Segment pSegment, List<Integer> pPath, List<Integer> pBatteryMetricValues ) {
		type = pType;
		source = pSource;
		destination = pDestination;
		segment = pSegment;
		path = pPath;
		batteryMetricValues = pBatteryMetricValues;
		transmissionValues = new ArrayList<Integer>();
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
		segment = pSegment;
		path = pPath;
		batteryMetricValues = new ArrayList<Integer>();
		batteryMetricValues.add(batteryMetric);
		transmissionValues = new ArrayList<Integer>();
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
	
	// Appends a node id for the path
	public void addToPath( int pNodeID ) {
		
		path.add( pNodeID );
	}
	
	/**
	 * Set the segment 
	 */
	public Segment getSegment() {
		return segment;
	}
	
	/**
	 * Get the segment
	 */
	public void setSegment( Segment pSegment ) {
		segment = pSegment;
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
	
	// Append a battery metric value
	public void addBatteryMetricValue( int pBatteryMetric ) {
		
		batteryMetricValues.add( pBatteryMetric );
	}
	/**
	 * Clears the battery metrics
	 */
	public void clearBatteryMetricValues(){
		batteryMetricValues.clear();
	}
	
	/**
	 * Takes a list and reverses the order of its elements.
	 * 
	 * @param incoming A list that needs to be reversed
	 * @return A list in the reverse order of the incoming
	 */
	public static ArrayList<Integer> reverse( ArrayList<Integer> incoming ) {
		// obtain size of list
		int size = incoming.size();
		
		// An empty incoming list will result in an empty outgoing list
		ArrayList<Integer> output = new ArrayList<Integer>( size );
    	
    	for( int i = 0; i < size; ++i) {
    		output.add(i, incoming.get(size - 1 - i));
    	}

    	/*for(int i : output) {
    		System.out.print(i + " ");
    	}
    	System.out.println();
    	System.out.println("reverse was run");
    	*/
    	return output;
	}
	
	public List<Integer> getTransmissionValues() {
		return transmissionValues;
	}
	
	public void setTransmissionValues( List<Integer> pValues ) {
		
		transmissionValues = pValues;
	}
	
	public void addTransmissionCost( int pTransmissionValue ) {
		
		transmissionValues.add( pTransmissionValue );
	}
	
	public int getRreqID() {
		return rreqID;
	}
	
	public void setRreqID( int pRreqID ) {
		rreqID = pRreqID;
	}
	
	public static void run() {}
}
