package energyaware;

import java.util.List;

/**
 * 
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 *
 * A frame is the encapsulation type used by the Datalink layer.  It is
 * responsible for next-hop transportation.
 */
public class Frame {

	protected Datagram datagram;
	protected int source;			// Set by the encapsulated datagram
	protected int destination;		// Set by the encapsulated datagram
	private static final int BROADCASTFRAME = -1;
	
	/**
	 * Default constructor.
	 * 
	 * @param pDatagram The datagram to be encapsulated.
	 */
	public Frame(Datagram pDatagram) {
		setDatagram(pDatagram);
		setSource(pDatagram.getSource());
		
		if (!pDatagram.getType().equals(Datagram.RREQ)) {
			destination = Frame.getNextHopInPath(source, datagram.getPath());
		}
		
		// if the type is a RREQ, this is a broadcast frame
		destination = Frame.BROADCASTFRAME;
	}
	
	/**
	 * Get the encapsulated datagram.
	 * 
	 * @return The datagram
	 */
	public Datagram getDatagram() {
		return datagram;
	}
	
	/**
	 * Set the encapsulated datagram to the one specified.
	 * 
	 * @param pDatagram Datagram to encapsulate.
	 */
	public void setDatagram(Datagram pDatagram) {
		datagram = pDatagram;
	}
	
	
	/**
	 * Get the source/sender's identification.
	 * 
	 * @return ID of the source of this frame.
	 */
	public int getSource() {
		return source;
	}
	
	/**
	 * Set the source/sender's identification.
	 * 
	 * @param pSource ID of the source for this frame.
	 */
	public void setSource(int pSource) {
		source = pSource;
	}
	
	
	/**
	 * Get the intended next-hop destination of the frame.
	 * 
	 * @return Next-hop destination of the frame.
	 */
	public int getDestination() {
		return destination;
	}
	
	/**
	 * Set the intended next-hop destination of the frame.
	 * 
	 * @param pDestination Next-hop destination of the frame.
	 */
	public void setDestination(int pDestination) {
		destination = pDestination;
	}
	
	
	/**
	 * Obtains the next-hop destination based upon the predetermined routing
	 * path stored in the datagram.
	 * 
	 * @param pSource A specified source
	 * @param pPath A specified path
	 * @return The next hop's node ID
	 */
	public static int getNextHopInPath(int pSource, List<Integer> pPath) {
		int size = pPath.size();
		
		// if the path is empty or null, escape early
		if ( pPath == null || size == 0) {
			return -1;
		}
		
		int ourPosition = 0;
		for (int i = 0; i < size; ++i) {
			if ( pPath.get( i ) == ourPosition ) {
				ourPosition = i;
				break;
			}
		}
		
		int nextHopPosition = ourPosition + 1;
		
		return pPath.get(nextHopPosition);
	}
}
