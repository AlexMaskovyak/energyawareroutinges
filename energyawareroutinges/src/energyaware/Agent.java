package energyaware;

import jess.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 * @version July 2008
 * 
 *          An agent has the communication processing protocol.
 */
public class Agent {

	private Rete engine;
	private WorkingMemoryMarker marker;
	
	private Node ourNode;
	
	
	// private Database database;

	// TEST STUB METHOD
	public static void main(String[] args) {
		System.out.println("In main");
		new Agent();
	}

	/**
	 * Default agent constructor.
	 */
	public Agent() {

		// Database aDatabase
		try {
			System.out.println("YES");

			// Create a Jess rule engine
			engine = new Rete();
			engine.reset();

			// Load the pricing rules
			engine.batch("rules.clp");

			Datagram d = new Datagram("RREQ", -1, 2, new Segment(), null, -1);
			Datagram e = new Datagram("RREP", 1, -2, new Segment(), null, -1);
			engine.add(d);
			engine.add(e);

			engine.assertString("(ourid (id 2))");
			for (Iterator it = engine.listFacts(); it.hasNext();) {
				System.out.println(it.next());
			}

			ArrayList<Integer> a = new ArrayList<Integer>();
			a.add(1);
			a.add(2);
			a.add(3);

			engine.add(a);

			engine.run();
			// Load the catalog data into working memory
			// database = aDatabase;
			// engine.addAll(database.getCatalogItems());

			// Mark end of catalog data for later
			marker = engine.mark();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/// agent has to receive messages from node
	/// agent has to send message to node
	
	/// agent has to receive frames from node
	/// agent has to send
	
	/**
	 * Obtain a segment from up in the transport layer.  This segment is to be
	 * passed to JESS.
	 * @param pMessage Segment from the application layer.
	 * @param pDestinationNodeID Destination to receive this message 
	 */
	public void receiveMessage(Message pMessage, int pDestinationNodeID) {
		// encapsulate into segment for transport layer 
		Segment segment = new Segment(pMessage, pDestinationNodeID);
		
		// give this to JESS the network layer
		try {
			engine.add(segment);
		}
		catch ( JessException je ) {
			je.printStackTrace();
		}
	}
	
	/**
	 * Sends a segment up to the transport layer.  This layer's operations are
	 * simulated completely by this method.
	 * @param pSegment to the transport layer.
	 */
	public void sendMessage(Message pMessage) {
		ourNode.receiveMessage(pMessage);
	}
	
	/**
	 * Sends the datagram down the stack to this node's datalink layer.  This
	 * layer's operations are simulated by this method with the help of 
	 * "network".
	 * @param pDatagram Datagram to send out.
	 * @param pTransmissionDistance Distance to send this packet which
	 * 			influences energy expenditure.
	 */
	public void sendDatagram(Datagram pDatagram, int pTransmissionDistance) {
		// encapsulate into frame for the datalink operation
		Frame frame = new Frame(pDatagram);
		
		// send it to the node
		ourNode.sendFrame(frame, pTransmissionDistance);
	}
	
	/**
	 * Obtain a datagram from down in the datalink layer.  This datagram is 
	 * passed to JESS.
	 * @param pDatagram Datagram received from the datalink layer. 
	 */
	public void receiveDatagram(Datagram pDatagram) {
		try {
			engine.add(pDatagram);
		} 
		catch ( JessException je ) {
			je.printStackTrace();
		}
	}
	
}
