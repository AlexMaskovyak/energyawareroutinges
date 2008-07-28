package energyaware;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Point;

public class Network {

	private ArrayList<Node> nodes;
	private HashMap <Node,Point> geography;
	
	private static Network network;
	
	private Network() {
		nodes = new ArrayList<Node>( 20 );
	}
	
	/**
	 * Create an instance of network if one does not exist.
	 * 
	 * @return The network object.
	 */
	public static Network GetInstance() {
		
		if( network == null ) {
			network = new Network();
		}
		
		return network;
	}
}
