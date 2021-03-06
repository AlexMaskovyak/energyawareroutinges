package energyaware;

import jess.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.ListIterator;
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
	private PathTable pathTable;
	private Map<Integer, Integer> batteryMetrics;
	private Map<NodePair, Integer> transmissionCosts;
	private Map<Integer,Integer> rreqIDs;
	private Segment lastSegmentReceived;
	public static final int NOTCONNECTED = 1000;
	
	/**
	 * Default agent constructor.
	 */
	public Agent() {

		batteryMetrics = new HashMap<Integer,Integer>();
		pathTable = new PathTable();
		transmissionCosts = new HashMap<NodePair,Integer>();
		rreqIDs = new HashMap<Integer,Integer>();
		
		try {

			// Create a Jess rule engine
			engine = new Rete();
			engine.reset();

			// Load the pricing rules
			engine.batch("rules.clp");
			engine.run();
			
		}
		catch (JessException e) {
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
		lastSegmentReceived = segment;
		
		try {
			engine.add(segment);	// Pass segment to JESS for processing
			engine.run();
		}
		catch( JessException e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Takes a segment and simulates the transport layer.  This layer's
	 * operations are simulated completely by this method.  It strips out the
	 * message from the segment.
	 * 
	 * @param pSegment to the transport layer.
	 */
	public void sendMessage(Segment pSegment) {
		lastSegmentReceived = pSegment;
		
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
	 * Agents cannot set the Node ID.
	 * @param pId ID to ignore.
	 */
	public void setID( int pId ) {}
	
	
	
	/**
	 * Retrieves the last segment this agent received and forwarded up the 
	 * stack to the Node/User space.
	 * @return Last segment received/processed, null if no segment has yet been
	 * 			received/processed.
	 */
	public Segment getLastSegmentReceived() {
		return lastSegmentReceived;
	}
	
	
	////
	////
	//// Battery related information storage and maintenance.
	////
	////
	
	
	/**
	 * Obtain the node's battery metric.
	 * 
	 * @return The battery metric.
	 */
	public int getBatteryMetric() {
		
		return BatteryMetric.calculateBatteryMetric(
				node.getBattery() );
	}

	/**
	 * Obtain the battery metric for a given node.
	 * 
	 * @param pNodeID Node for which to obtain a battery metric.
	 * @return Battery metric for the specified node.
	 */
	public int getBatteryMetrics( int pNodeID ){
		return batteryMetrics.get( pNodeID );
	}
	
	/**
	 * Update the battery metric for a given node id.
	 * 
	 * @param pNodeID Node ID for which to store the specified battery metric.
	 * @param pMetricValue Battery metric value to associated with the Node ID.
	 */
	public void updateBatteryMetric( int pNodeID, int pMetricValue ){
		batteryMetrics.put( pNodeID, pMetricValue );
	}
	
	/**
	 * Updates our Map of nodes to battery metrics. 
	 *  
	 * @param pNodeIDs The Node to update the metric for.
	 * @param pMetricValues The node's metric.
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
	
	/**
	 * Evaluates the best path to a given destination node.
	 * 
	 * @param pDestination The destination node.
	 * @return The path path.
	 */
	public List<Integer> getBestPath( int pDestination ) {
		
		Iterator<ArrayList<Integer>> it = pathTable.getPathSet(
				pDestination ).paths.iterator();
		List<Integer> bestPath = null, comparePath = null;	// Path options
		int bestCost, compareCost;	// Keep track of the cost of each path
		
		// Determine if we even have one path to give
		if( it.hasNext() ) {
			
			bestPath = it.next();
			bestCost = getCost( bestPath.iterator() );
			
			// Determine if we have more than 1 path to evaluate
			while( it.hasNext() ) {
				
				comparePath = it.next();
				compareCost = getCost( comparePath.iterator() );

				// See if another path has a lower cost
				if( compareCost < bestCost ) {
					bestPath = comparePath;
					bestCost = compareCost;
				}
			}
		}
		
		return bestPath;
	}
	
	/**
	 * Calculate the cost of a given path.
	 * 
	 * @param pIt An iterator for a node list (path).
	 * @return The path's cost.
	 */
	private int getCost( Iterator<Integer> pIt ) {
		
		int value, start, next;
		try {
			value = 0;
			start = pIt.next();
			
			while( pIt.hasNext() ) {
				next = pIt.next();
				
				// Battery Metric * Transmission Cost
				value += getBatteryMetrics(next) * getTransmissionCost(
						start, next);
				start = next;
			}
		}
		catch( Exception e ) { // When we don't have a cost between nodes
			value = Integer.MAX_VALUE;
		}
		
		// All paths must have a cost associated otherwise we use Infinity
		if( value == 0 )
			value = Integer.MAX_VALUE;
		
		return value;
	}

	/**
	 * Determines if a given RREQ ID is currently in the set of ID's that this
	 * Agent is aware of.
	 * 
	 * @param pID The id we would like to check against.
	 * @return True if the key is not in our set.
	 */
	public boolean isNovelRREQID( int pID ) {
		
		return !rreqIDs.containsKey( pID );
	}
	
	/**
	 * Adds a RREQ ID to the list of ID's we have already seen.  Note: You can
	 * always add a key even if it already exists.
	 * 
	 * @param pID The ID we would like to add.
	 */
	public void addRREQID( int pID ) {
		
		rreqIDs.put(pID, pID);
	}
	
	/**
	 * Remove a RREQ ID from the list of IDs that we have already seen.
	 * 
	 * @param pID The RREQ ID to remove.
	 */
	public void removeRREQID( int pID ) {
		rreqIDs.remove(pID);
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
	@SuppressWarnings("unchecked")
	public void updatePathTable( ArrayList<Integer> pPath ){
		
		updatePathTable( pPath, node.getID() );
	}
	
	/**
	 * Add the provided path to the list of paths from a "source".
	 * If the source is not us, then we have learned of paths from another
	 * node's datagram while we were eavesdropping on other 
	 * 
	 * @param pPath The path to update.
	 * @param pSource The source that was suppose to receive the path.
	 */
	public void updatePathTable( ArrayList<Integer> pPath, int pSource ) {
		int us = getID();
		int ourIndex = Agent.getIndexOfID( us, pPath );
		
		// determines whether we should prepend our id in cases where
		// we are eavesdropping, see below
		boolean eavesdrop = false; 
		
		// we aren't a part of this path, we are eavesdropping.
		// we know this if the index returned is out of bounds,
		//
		// this means that our new start point is actually from the
		// source--we will manually prepend ourselves to the 
		// head of the lists generated in this way
		if( ourIndex == -1 ) {
			System.out.println ( "Source is : " + pSource );
			ourIndex = Agent.getIndexOfID( pSource, pPath );
			eavesdrop = true;
		}
		
		// create forward paths
		
		// add ourselves to the base list
		ArrayList<Integer> newPath = new ArrayList<Integer>();		
		newPath.add( us );

		// add the source if we are eavesdropping
		if (eavesdrop) {
			newPath.add( pSource );
		}
		
		int size = pPath.size();
		// iterate through all possible destinations
		for (int i = ourIndex + 1; i < size; ++i) {
			newPath.add( pPath.get( i ) );
			pathTable.addPath( newPath );
			newPath = (ArrayList<Integer>)newPath.clone();
		}
		
		// create backward paths
		
		// reverse this list
		pPath = Datagram.reverse( pPath );
		ourIndex = size - ourIndex - 1; //Agent.getIndexOfID( us, pPath );
		
		// add ourselves to the base list
		newPath = new ArrayList<Integer>();		
		newPath.add( us );

		// add the source if we are eavesdropping, additionally add this path
		if (eavesdrop) {
			newPath.add( pSource );
			pathTable.addPath( newPath );
			newPath = (ArrayList<Integer>)newPath.clone();
		}

		// iterate through all possible destinations
		for (int i = ourIndex + 1; i < size; ++i) {
			newPath.add( pPath.get( i ) );
			pathTable.addPath( newPath );
			newPath = (ArrayList<Integer>)newPath.clone();
		}
		
		// We are either the beginning, somewhere in the middle or the end
		/*if( pPath.size() > 1 ) { // At least two nodes needed to be a path
			
			// Find our position in the list
			ListIterator<Integer> it = pPath.listIterator();
			
			// Add all forward looking paths
			ArrayList<Integer> newPath = new ArrayList<Integer>();
			
			// Find our id in the list
			while( it.hasNext() ) {
				if( pSource == it.next() ) {	// Are we the intended source?
					if( pSource == us ) {
						newPath.add(us);
						break;
					}
					else {	// We assume a path to the intended source
						newPath.add( us );
						newPath.add( pSource );
						pathTable.addPath( (ArrayList<Integer>)
								newPath.clone() );
						break;
					}
				}
			}
			
			// Build sub paths from our ID to the end of the path list
			while( it.hasNext() ) {
				newPath.add( it.next() );	// Append a new destination
				pathTable.addPath( (ArrayList<Integer>) newPath.clone() );
			}
			
			// Move iterator back to our ID in the list
			while( it.hasPrevious() ) {
				if( pSource == it.previous() ) {// Are we the intended source?
					if( pSource == us ) {
						newPath.add(us);
						break;
					}
					else {	// We assume a path to the intended source
						newPath.add( us );
						newPath.add( pSource );
						pathTable.addPath( newPath );
						break;
					}
				}
			}
			
			// Build all reverse sub paths
			while( it.hasPrevious() ) {
				newPath.add( it.previous() );
				pathTable.addPath( (ArrayList<Integer>) newPath.clone() );
			}
		
		}*/	
	}
	
	/**
	 * Returns the index of the specified ID in the path.
	 * @param pId ID whose index we are attempting to locate in the path
	 * @param pPath Path which may or may not contain the ID.
	 * @return The index of the ID in the path, -1 if the ID could not
	 * 			be located.
	 */
	public static int getIndexOfID( int pId, List<Integer> pPath ) {
		
		int size = pPath.size();
		
		// if the path is empty or null, escape early
		if ( pPath == null || size == 0) {
			return -1;
		}
		
		int ourPosition = -1;
		for (int i = 0; i < size; ++i) {
			if ( pPath.get( i ) == pId ) {
				ourPosition = i;
				break;
			}
		}
		
		return ourPosition;
	}
	
	/**
	 * Determines whether we have any paths stored to reach the specified
	 * destination.
	 * 
	 * @param pDestination
	 * @return True if we have a path to the provided destination, false 
	 * 			otherwise.
	 */
	public boolean hasPath( int pDestination ) {
		return pathTable.hasPath( pDestination );
	}
	
	/**
	 * Determine whether we currently have the path specified.
	 * 
	 * @param pPath A path to compare against our current paths.
	 * @return True if the path already exists.
	 */
	public boolean hasPath( ArrayList<Integer> pPath ) {
		return pathTable.hasPath( pPath );
	}
	
	/**
	 * Removes a path from our path table.  Typically, this will occur if the
	 * path is invalid.
	 * @param pPath Path to remove.
	 */
	public void removePath( ArrayList<Integer> pPath ) {
		pathTable.removePath( pPath );
	}
	
	/**
	 * Merges two paths together so that shared nodes that appear at the end
	 * of the first segment and at the beginning of the second segment are 
	 * merged.  The first segment will have its trailing ID removed and then
	 * have all elements of the second segment added to it.
	 * 
	 * @param pFirstSegment The first path.
	 * @param pSecondSegment The second path.
	 * @return The merged path.
	 */
	public ArrayList<Integer> mergePathsInMiddle( 
			ArrayList<Integer> pFirstSegment, 
			ArrayList<Integer> pSecondSegment) {		
		
		int positionOfRedundantNode = pFirstSegment.size() - 1;
		pFirstSegment.remove(positionOfRedundantNode);
		pFirstSegment.addAll(pSecondSegment);
		
		return pFirstSegment;
	}
	
	
	/**
	 * Allows access to an internal object.
	 * 
	 * @return The internal path table.
	 */
	public PathTable getPathTable() {
		return pathTable;
	}
	
	/**
	 * Holds an association: destinations have a pathset of paths which can be
	 * used to route on the network.
	 * 
	 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
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
		 * 
		 * @return PathSet of paths to a given destination.
		 */
		public PathSet getPathSet( int pDestination ) {
			return pathSetMap.get( pDestination );
		}
		
		/**
		 * Determines whether we have a path to the specified destination 
		 * available for use.
		 * 
		 * @param pDestination
		 * @return True if we have a path capable of reaching the provided
		 * 			destination, false otherwise.
		 */
		public boolean hasPath( int pDestination ) {
			
			if ( pathSetMap.get( pDestination ) == null ) {
				return false;
			}
			return true;
		}
		
		/**
		 * Determine whether the given path is stored in our pathtable.
		 * @param pPath Path whose occupancy is in question.
		 * @return True if we have the path stored, false otherwise.
		 */
		public boolean hasPath( ArrayList<Integer> pPath ) {
			int dest = pPath.get( pPath.size()-1 );
			
			PathSet pathSet = pathSetMap.get( dest );
			
			if ( pathSet == null ) {
				return false;
			}
			
			return pathSetMap.get( dest ).hasPath(pPath);
		}
		
		/**
		 * Add an additional path to a set of paths associated with a given
		 * destination node id.
		 * 
		 * @param pPath Path to a given destination, this path will be added to
		 * 			the pathset for that destination.
		 */
		public void addPath( ArrayList<Integer> pPath ) {
			int dest = pPath.get( pPath.size()-1 );
			PathSet pathSet = pathSetMap.get( dest );
			
			if ( pathSet != null ){	//destination already exists in map
				pathSet.addPath( pPath );
			} 
			else {				// destination does not exist yet
				PathSet tempSet = new PathSet();
				tempSet.addPath( pPath );
				pathSetMap.put( dest, tempSet );
			}
		} 
		
		/**
		 * Removes path to a destination device.
		 * 
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
		 * Overrides the default tostring.
		 */
		public String toString() {
			return pathSetMap.toString();
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
			 * 
			 * @param pPath Path to add to this set.
			 */
			public void addPath( ArrayList<Integer> pPath ){
				paths.add( pPath );
			}
			
			/**
			 * Tests if a given path exists in the set of paths.
			 * 
			 * @param pPath A path to test for.
			 * @return True if the path exists.
			 */
			public boolean hasPath( ArrayList<Integer> pPath ) {
				return paths.contains(pPath);
			}
			
			/**
			 * Removes the specified arraylist path from our pathset.
			 * 
			 * @param pPath Path to remove from this set.
			 */
			public void removePath( ArrayList<Integer> pPath ) {
				paths.remove( pPath );
			}
			
			/**
			 * Overrides the default tostring.
			 */
			public String toString() {
				return paths.toString();
			}
		}
	}
	
	
	////
	////
	//// Transmission cost related storage and maintenance.
	//// 
	////
	
	/**
	 * Update the transmission cost associated with a node pair.
	 * 
	 * @param pNodePair The two nodes to associate.
	 * @param pCost The cost associated.
	 */
	public void updateTransmissionCost( NodePair pNodePair, int pCost ) {
		transmissionCosts.put( pNodePair, pCost );
	}
	
	/**
	 * Update the transmission hop cost table.
	 * 
	 * @param pNodeIDs All nodes to update the transmission costs.
	 * @param pTransmissionCosts The transmission costs.
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
	 * Return the transmission cost associated with two nodes.
	 * 
	 * @param pNodeA The first node.
	 * @param pNodeB The other node.
	 * @return The transmission cost between nodes.
	 */
	public int getTransmissionCost( int pNodeA, int pNodeB ) {
		
		Integer tCost = transmissionCosts.get( new NodePair( pNodeA, pNodeB ) );
		if( tCost == null ) {
			return NOTCONNECTED;
		}
		
		return tCost.intValue();
	}
	
	/**
	 * Designed to aid in hop transmission cost storage.
	 */
	private class NodePair {
		
		private int nodeA;
		private int nodeB;
		
		/**
		 * Constructor.
		 * 
		 * @param pNodeA
		 * @param pNodeB
		 */
		public NodePair( int pNodeA, int pNodeB ) {
			nodeA = Math.min( pNodeA, pNodeB );
			nodeA = Math.max( pNodeA, pNodeB );
		}
		
		/**
		 * Overrides the default equals.
		 */
		public boolean equals( Object o ) {
			
			if( !(o instanceof NodePair) ) {
				return false;
			}
			
			return (((NodePair) o).nodeA == this.nodeA) &&
				((NodePair) o).nodeB == this.nodeB;
		}
		
		/**
		 * Overrides the default hashcode
		 */
		public int hashCode() {
			return (nodeA * 1271) + nodeB;
		}
	}
}
