package energyaware;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.awt.Point;

public class Network {

	public static final int maxTransmissionDistance = 1;
	
	
	
	private ArrayList<Node> nodes;
	private HashMap <Node, Point> geography;
	
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
		
		
		return addNode(pNode);
	}
	
	
	public Node addNode(Node pNode, Point pPoint) {
		
	}
	
	/**
	 * Generates a point at a random location that is within the transmission
	 * distance of another Node on the network.
	 * @return
	 */
	private Point getRandomConnectedPoint() {
		
	}
	
}
