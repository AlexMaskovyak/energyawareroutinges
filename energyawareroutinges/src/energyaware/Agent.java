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
	private Map<Integer, Integer> batteryMetrics;
	private Map<NodePair, Integer> transmissionCosts;
	
	
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
	
	
	public Rete getEngine() {
		return engine;
	}
	
	
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
	
	////
	////
	//// Battery related information storage and maintenance.
	////
	////
	
	
	/**
	 * Obtain the node's battery metric.
	 * @return
	 */
	public int getBatteryMetric() {
		
		return BatteryMetric.calculateBatteryMetric(
				node.getBattery() );
	}

	/**
	 * Obtain the battery metric for a given node.
	 * @param pNodeID Node for which to obtain a battery metric.
	 * @return Battery metric for the specified node.
	 */
	public int getBatteryMetrics( int pNodeID ){
		return batteryMetrics.get( pNodeID );
	}
	
	/**
	 * Update the battery metric for a given node id. 
	 * @param pNodeID Node ID for which to store the specified battery metric.
	 * @param pMetricValue Battery metric value to associated with the Node ID.
	 */
	public void updateBatteryMetric( int pNodeID, int pMetricValue ){
		batteryMetrics.put( pNodeID, pMetricValue );
	}
	
	/**
	 * Updates our Map of nodes to battery metrics.  
	 * @param pNodeIDs
	 * @param pMetricValues
	 */
	public void updateBatteryMetrics( 
			ArrayList<Integer> pNodeIDs,
			ArrayList<Integer> pMetricValues ) {
		
		int metricSize = pMetricValues.size();
		
		// ensure that we don't have more metric values than nodes
		if ( metricSize > pNodeIDs.size() ) {
			return;
		}
		
		// pull each pair of metric values and pair
		for (int i = 0; i < metricSize; ++i) {
			int id = pNodeIDs.get( i );
			int metric = pMetricValues.get( i );
			updateBatteryMetric( id, metric );
		}
	}
	
	
	
	////
	////
	//// Path related storage and maintenance.
	////
	////
	
	/**
	 * Add the provided path to the list of paths.
	 * @param pPath Path to add to the list of paths.
	 */
	public void updatePathTable( ArrayList<Integer> pPath ){
		pathTable.addPath(pPath);
	}
	
	/**
	 * Determine whether we currently have the path.
	 * @param pPath
	 * @return
	 */
	public boolean hasPath( ArrayList<Integer> pPath ) {
		return pathTable.hasPath( pPath );
	}
	
	/**
	 * Holds an association: destinations have a pathset of paths which can be
	 * used to route on the network.
	 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
	 *
	 */
	private class PathTable {
		
		private Map<Integer,PathSet> pathSetMap;
		
		/**
		 * Default constructor.
		 */
		public PathTable() {
			pathSetMap = new HashMap<Integer,PathSet>();
		}
		
		/**
		 * Obtains a set of paths that end at a given destination.
		 * @return PathSet of paths to a given destination.
		 */
		public PathSet getPathSet( int pDestination ) {
			return pathSetMap.get( pDestination );
		}
		
		/**
		 * Determine whether the given path is stored in our pathtable.
		 * @param pPath Path whose occupancy is in question.
		 * @return True if we have the path stored, false otherwise.
		 */
		public boolean hasPath( ArrayList<Integer> pPath ) {
			int dest = pPath.get( pPath.size()-1 );
			
			return pathSetMap.get( dest ).hasPath(pPath);
		}
		
		/**
		 * Add an additional path to a set of paths associated with a given
		 * destination node id.
		 * @param pPath Path to a given destination, this path will be added to
		 * 			the pathset for that destination.
		 */
		public void addPath( ArrayList<Integer> pPath ) {
			int dest = pPath.get( pPath.size()-1 );
			PathSet pathSet = pathSetMap.get( dest );
			
			if ( pathSet != null ){	//destination already exists in map
				pathSet.addPath( pPath );
			} 
			else {				// destination does not exist yest
				PathSet tempSet = new PathSet();
				tempSet.addPath( pPath );
				pathSetMap.put( dest, tempSet );
			}
		} 
		
		/**
		 * Removes path to a destination device.
		 * @param pPath Path to remove.
		 */
		public void removePath( ArrayList<Integer> pPath ) {
			int dest = pPath.get( pPath.size()-1 );
			PathSet pathSet = pathSetMap.get( dest );
			
			if ( pathSet != null ) {
				pathSet.removePath( pPath );
			}
		}
		
		/**
		 * Simple wrapper for an array-list of a path.
		 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
		 *
		 */
		public class PathSet {
			
			private Set <ArrayList<Integer>> paths;
			
			/**
			 * Default constructor.
			 */
			public PathSet() {
				paths = new HashSet<ArrayList<Integer>>();
			}
			
			/**
			 * Adds the arraylist as a path to our pathset.
			 * @param pPath Path to add to this set.
			 */
			public void addPath( ArrayList<Integer> pPath ){
				paths.add( pPath );
			}
			
			/**
			 * 
			 * @param pPath
			 * @return
			 */
			public boolean hasPath( ArrayList<Integer> pPath ) {
				return paths.contains(pPath);
			}
			
			/**
			 * Removes the specified arraylist path from our pathset.
			 * @param pPath Path to remove from this set.
			 */
			public void removePath( ArrayList<Integer> pPath ) {
				paths.remove( pPath );
			}
		}
	}
	
	
	////
	////
	//// Transmission cost related storage and maintenance.
	//// 
	////
	
	/**
	 * 
	 */
	public void updateTransmissionCost( NodePair pNodePair, int pCost ) {
		transmissionCosts.put( pNodePair, pCost );
	}
	
	/**
	 * Update the transmission hop cost table.
	 * @param pNodeIDs
	 * @param pTransmissionCosts
	 */
	public void updateTransmissionCosts( 
			ArrayList<Integer> pNodeIDs,
			ArrayList<Integer> pTransmissionCosts) {
		
		// parameter sanity check
		int transSize = pTransmissionCosts.size();
		if ( transSize > pNodeIDs.size() - 1 ) {
			return;
		}
		
		// pull out node pairs (hops) and associate a cost with them
		for (int i = 0; i < transSize; ++i) {
			int cost = pTransmissionCosts.get( i );
			int nodeA = pNodeIDs.get( i );
			int nodeB = pNodeIDs.get( i + 1 );
			NodePair nodePair = new NodePair( nodeA, nodeB );
			
			updateTransmissionCost( nodePair, cost );
		}
	}
	
	
	/**
	 * Designed to aid in hop transmission cost storage.
	 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
	 *
	 */
	private class NodePair {
		
		private int nodeA;
		private int nodeB;
		
		/**
		 * Constructor.
		 * @param pNodeA
		 * @param pNodeB
		 */
		public NodePair( int pNodeA, int pNodeB ) {
			nodeA = Math.min( pNodeA, pNodeB );
			nodeA = Math.max( pNodeA, pNodeB );
		}
	}
}
