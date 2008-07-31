package energyaware;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.awt.Point;

/**
 * The network object abstracts out the physical transmission medium as well as
 * some aspects of the datalink layer.
 * 
 * Network is responsible for keeping track of all nodes on the network, which
 * nodes are connected, and which nodes are disconnected.  Network determines
 * which nodes receive frames that are broadcast from a source node based
 * upon distance. 
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 *
 */
public class Network {

	public static final int maxTransmissionDistance = 10;
	
	
	
	private ArrayList<Node> nodes;
	private ArrayList<Node> connectedNodes;
	private Map <Node, Point> geography;
	
	private Random generator;
	
	// network is a singleton
	private static Network network;
	
	
	/**
	 * Default constructor.
	 */
	private Network() {
		nodes = new ArrayList<Node>( 20 );
		geography = new HashMap<Node, Point>( 20 );	
		generator = new Random(System.nanoTime());
	}
	
	/**
	 * Create an instance of network if one does not exist.
	 * 
	 * @return The network object.
	 */
	public static Network getInstance() {
		
		if( network == null ) {
			network = new Network();
		}
		
		return network;
	}
	
	/**
	 * Add a new node to the network.  This node will be placed at some random
	 * point in the geography such that it is within transmitting distance of
	 * at least one other Node.
	 * @return A reference to the Node added.
	 */
	public Node addNode() {
		return addNode(new Node());
	}
	
	/**
	 * Adds the specified node to the network.  This node will be placed at
	 * some random point in the geography such that it is within transmitting
	 * distance of at least one other Node.
	 * @param pNode Node to add to the geography.
	 * @return A reference to the Node added.
	 */
	public Node addNode(Node pNode) {
		return addNode(pNode, getRandomConnectedPoint());
	}
	
	/**
	 * Adds the specified node to the network at the specified geographical
	 * point.  Note: nodes connected in this way are not guaranteed to be 
	 * within communication distance of another node.  There are no guarantees
	 * that the network is fully connected and reachable.
	 * @param pNode Node to add to the geography.
	 * @param pPoint Point at which to add the node.
	 * @return The node added to the geography.
	 */
	public Node addNode(Node pNode, Point pPoint) {
		geography.put(pNode, pPoint);
		nodes.add(pNode);
		return pNode;
	}
	
	/**
	 * Broadcasts the frame to all connected nodes within receiving distance
	 * of the source broadcaster.
	 * @param pSourceNode Node that is broadcasting on the network.
	 * @param pFrame Frame to send to those within range.
	 * @param pTransmissionDistance Distance that is reached by the strength of
	 * 			the signal.
	 */
	public void broadcast (
			Node pSourceNode, Frame pFrame, int pTransmissionDistance) {
		// get the position of the source
		Point source = geography.get(pSourceNode);
		
		// set RERR determiner
		boolean nextHopReached = false;
		
		// frames store the next hop
		int nextHopID = pFrame.getDestination();
		
		// go through all of the nodes in the network
		for (Node node : connectedNodes) {
			// obtain the position of this node
			Point potentialDestination = geography.get(node);
			
			// normalize the transmission distance, there is a maximum present
			// on the network (later we'll actually map out some way of adding
			// antenna properties to nodes)
			pTransmissionDistance = 
				Math.min(
					pTransmissionDistance, maxTransmissionDistance);
			
			if (source.distance(potentialDestination)<=pTransmissionDistance) {
				node.receiveFrame(pFrame);
				
				// determine whether we got to the next hop
				if (node.getID() == nextHopID) {
					nextHopReached = true;
				}
			}
		}
		
		// determine whether to send a RERR
		if (!nextHopReached) {	
			this.sendRERRDatagramBackToFrameSource(pSourceNode, pFrame);
		}
	}
	
	/**
	 * Generates a point at a random location that is within the transmission
	 * distance of another Node on the network.
	 * @return A point guaranteed to be transmission reachable to another Node
	 * 			in the network.
	 */
	private Point getRandomConnectedPoint() {
		Point returnPoint = new Point();
		
		int returnPointX = 0;
		int returnPointY = 0;
		int xOffset = 0;
		int yOffset = 0;
		
		// obtain information about current nodes
		List<Point> values = new ArrayList<Point>(geography.values());
		int size = geography.size();
		
		// case where there exists other nodes, get one of those to branch out
		// from
		if (size > 0) {
			// grab a random node that exists
			int position = this.generator.nextInt() % size;
			position = (position < 0 ? position * -1 : position);
			
			// obtain a random existing point
			Point randomPoint = values.get(position);
			
			// ensure the second offset 
			returnPointX += randomPoint.getX();
			returnPointY += randomPoint.getY();
		}

		// determine the offsets, the amount to branch out
		xOffset = generator.nextInt() % maxTransmissionDistance;
		yOffset = (int)Math.sqrt(Math.pow(maxTransmissionDistance, 2) - Math.pow(xOffset, 2));
		
		// ensure that we use all quadrants, periodically modify our y
		// offset to be negative and not always positive
		yOffset = ((generator.nextInt() > 0) ? yOffset *= -1 : yOffset);
		
		returnPoint.setLocation(
				returnPointX += xOffset,
				returnPointY += yOffset);
		
		return returnPoint;
	}
	
	/**
	 * Connects a node to the network topology, allowing it to both send and 
	 * receive messages to other nodes.
	 * @param pNode Node to connect to the topology.
	 */
	public void connect(Node pNode) {
		if (nodes.contains(pNode) && !connectedNodes.contains(pNode)) {
			connectedNodes.add(pNode);
		}
	}
	
	/**
	 * Disconnects a node from the network topology, preventing it from either
	 * sending or receiving messages from other nodes.  This will typically be
	 * called when a node depletes its energy below the sending/receiving
	 * threshold.
	 * @param pNode
	 */
	public void disconnect(Node pNode) {
		connectedNodes.remove(pNode);
	}
	
	/**
	 * Physically removes the node from the geography.
	 * @param pNode Node to remove.
	 */
	public void removeNode(Node pNode) {
		this.nodes.remove(pNode);
		this.geography.remove(pNode);
	}
		
	/**
	 * Models the datalink layer responsibility of sending a RERR datagram
	 * to a source node when the datagram's next hop is not reachable.
	 * @param pSourceNode Source to receive this frame.
	 * @param pFrame Frame with contact and path information.
	 */
	public void sendRERRDatagramBackToFrameSource(
			Node pSourceNode, Frame pFrame) {
		
		Datagram RERRDatagram = 
			new Datagram(
				Datagram.UNINIT, 
				Datagram.NONE,
				Datagram.NONE,
				pFrame.getDatagram().getPath(),
				Datagram.NONE);
		Frame frame = new Frame(RERRDatagram);
		
		pSourceNode.receiveFrame(frame);
	}
	
	public static void main (String[] args) {
		
		Network network = Network.getInstance();
		
		network.addNode();
		network.addNode();
		network.addNode();
		network.addNode();
		network.addNode();
		
		for (Point p : network.geography.values()) {
			System.out.println(p.toString());
		}
	}
}
