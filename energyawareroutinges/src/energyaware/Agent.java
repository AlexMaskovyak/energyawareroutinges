package energyaware;

import jess.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 * @version July 2008
 * 
 * An agent is the Java side of the communication processing protocol.
 */
public class Agent{

	private Rete engine;				// JESS side of communication protocol
	private WorkingMemoryMarker marker;	// JESS usage
	private Node node;					// A reference to our node
//  private Database database;
	private PathTable pathTable;
	private Map<Integer,Integer> batteryMetrics;

	public int getBatteryMetrics( int pNodeID ){
		return batteryMetrics.get( pNodeID );
	}
	
	public void updateBatteryMetric( int pNodeID, int pMetricValue ){
		batteryMetrics.put( pNodeID, pMetricValue );
	}
	
	/**
	 * Default agent constructor.
	 */
	public Agent() {

//		Database aDatabase
		batteryMetrics = new HashMap<Integer,Integer>();
		pathTable = new PathTable();
		
		try {

			// Create a Jess rule engine
			engine = new Rete();
			engine.reset();

			// Load the pricing rules
			engine.batch("rules.clp");
			engine.run();
			
			// Load the catalog data into working memory
//			database = aDatabase;
//			engine.addAll(database.getCatalogItems());

			// Mark end of catalog data for later
//			marker = engine.mark();
			
		} catch (JessException e) {
			e.printStackTrace();
		}
	}

	
	/// agent has to receive messages from node
	/// agent has to send message to node
	
	/// agent has to receive frames from node
	/// agent has to send frames from the node
	
	/**
	 * Obtain a segment from up in the transport layer.  This segment is to be
	 * passed to JESS.
	 * 
	 * @param pMessage Segment from the application layer.
	 * @param pDestinationNodeID Destination to receive this message 
	 */
	public void receiveMessage(Message pMessage, int pDestinationNodeID) {
		
		// encapsulate into segment for transport layer 
		Segment segment = new Segment(pMessage, pDestinationNodeID);
		
		try {
			engine.add(segment);	// Pass segment to JESS for processing
			engine.run();
		}
		catch( JessException e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Takes a segment and simulates the transport layer.  This layer's operations 
	 * are simulated completely by this method.  It strips out the message from
	 * the segment.
	 * 
	 * @param pSegment to the transport layer.
	 */
	public void sendMessage(Segment pSegment) {
		
		Message message = pSegment.getMessage();
		node.receiveMessage(message);				// Pass message to the Node
	}
	
	/**
	 * Sends the datagram down the stack to this node's datalink layer.  This
	 * layer's operations are simulated by this method with the help of 
	 * "network".
	 * 
	 * @param pDatagram Datagram to send out.
	 * @param pTransmissionDistance Distance to send this packet which
	 * 			influences energy expenditure.
	 */
	public void sendDatagram(Datagram pDatagram, int pTransmissionDistance) {
		
		// encapsulate into frame for the datalink operation
		Frame frame = new Frame(pDatagram);
		
		// send it to the node
		node.sendFrame(frame, pTransmissionDistance);
	}
	
	
	/**
	 * Allow our Java Agent & JESS object to send data via the Node
	 * 
	 * @param pNode The node "we" reside in.
	 */
	public void setNode( Node pNode ) throws JessException {
		
		node = pNode;
		engine.add( this );
		engine.run();
	}
	
	
	/**
	 * Obtain a datagram from down in the datalink layer.  This datagram is 
	 * passed to JESS.
	 * @param pDatagram Datagram received from the datalink layer. 
	 */
	public void receiveDatagram(Datagram pDatagram, int pTransmissionCost ) {
		
		try {
			if( pDatagram.getType().equals( "RREQ" ) ) {
			
				pDatagram.addTransmissionCost( pTransmissionCost );
			}
		
			engine.add(pDatagram);
			engine.run();
			
			for (Iterator it = engine.listFacts(); it.hasNext();) {
				System.out.println(it.next());
			}
//		for( Iterator it = engine.listDefglobals(); it.hasNext(); ) {
//			System.out.println( it.next() );
//		}
		}
		catch( JessException e ) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Yep you guessed it.  Get the ID
	 * 
	 * @return Take a guess
	 */
	public int getID() {
		
		return node.getID();
	}
	
	
	/**
	 * 
	 * 
	 * @param pId
	 */
	public void setID( int pId ) {
		
	}
	
	public int getBatteryMetric() {
		
		return BatteryMetric.calculateBatteryMetric(
				node.getBattery() );
	}
	
	public Rete getEngine() {
		return engine;
	}
	
	private PathTable pathtable;
	
	public void updatePathTable( ArrayList<Integer> pPath ) {
		
	}
	
	public void addPath( ArrayList<Integer> pPath ){
		pathTable.addPath(pPath);
	}
	
	private class PathTable {
		
		private Map<Integer,PathSet> pathSetMap;
		
		public PathTable() {
			pathSetMap = new HashMap<Integer,PathSet>();
		}
		
		public ArrayList<Integer> getPath() {
			return null;
		}
		
		public void addPath( ArrayList<Integer> pPath ) {
			int dest = pPath.get(pPath.size()-1);
			PathSet pathSet = pathSetMap.get(dest);
			if ( pathSet != null ){	//destination already exists in map
				pathSet.addPath( pPath );
			} 
			else {				// destination does not exist yest
				PathSet tempSet = new PathSet();
				tempSet.addPath(pPath);
				pathSetMap.put( dest, tempSet );
			}
		} 
		
		private class PathSet {
			
			private Set <ArrayList<Integer>> paths;
			
			public PathSet() {
				paths = new HashSet<ArrayList<Integer>>();
			}
			
			public void addPath(ArrayList<Integer> pPath){
				paths.add(pPath);
			}
		}
	}
}
