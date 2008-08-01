package energyaware;

import jess.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 * @version July 2008
 * 
 * An agent has the communication processing protocol.
 */
public class Agent{

	private Rete engine;
	private WorkingMemoryMarker marker;
	
	private Node node;
	
	
	// private Database database;

	/**
	 * Default agent constructor.
	 */
	public Agent() {

		// Database aDatabase
		try {

			// Create a Jess rule engine
			engine = new Rete();
			engine.reset();

			// Load the pricing rules
			engine.batch("rules.clp");

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
	
	/**
	 * 
	 */
	public void setNode( Node pNode ) throws JessException {
		
		node = pNode;
		engine.add( this );
		engine.run();
		//engine.add( new NodeID( 789 ) );
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
			engine.run();
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
		node.receiveMessage(pMessage);
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
		node.sendFrame(frame, pTransmissionDistance);
	}
	
	/**
	 * Obtain a datagram from down in the datalink layer.  This datagram is 
	 * passed to JESS.
	 * @param pDatagram Datagram received from the datalink layer. 
	 */
	public void receiveDatagram(Datagram pDatagram) {
		try {
			engine.add(pDatagram);
			engine.run();
			
			
			for (Iterator it = engine.listFacts(); it.hasNext();) {
				System.out.println(it.next());
			}
//			for( Iterator it = engine.listDefglobals(); it.hasNext(); ) {
//				System.out.println( it.next() );
//			}
		} 
		catch ( JessException je ) {
			je.printStackTrace();
		}
	}
	
	/**
	 * Yep you guessed it.  Get the ID
	 * @return take a guess
	 */
	public int getID() {
		
		return node.getID();
	}
	
	public void setID( int pId ) {
		
	}
}
