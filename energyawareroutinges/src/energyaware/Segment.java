package energyaware;

/**
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 * @version July 2008
 * 
 * A segment is the data encapsulation used to hold a message and is exchanged between
 * the Transport and Network layers.  A Segment holds an applications "message" and a
 * specific destination.
 */
public class Segment {
	
	private Message message;		// The pay load of this Segment
	private int destination;		// The requested Node ID for this Segment
	
	/**
	 * Default constructor
	 */
	public Segment() {
		
		message = null;
		destination = -1;
	}
	
	/**
	 * Construct a new Segment for the specified destination with the supplied pay load. 
	 * 
	 * @param pMessage The datagram's pay load.
	 * @param pDestination The datagram's requested destination.
	 */
	public Segment( Message pMessage, int pDestination ) {
		
		message = pMessage;
		destination = pDestination;
	}
	
	/**
	 * Get the contents of this datagram's pay load.
	 * 
	 * @return The datagram's pay load.
	 */
	public Message getMessage() {
		
		return message;
	}
	
	/**
	 * Put a different message in the pay load.
	 * 
	 * @param pMessage A message to become the pay load.
	 */
	public void setMessage( Message pMessage ) {
		
		message = pMessage;
	}
	
	/**
	 * Get the segment's destination of this segment.
	 * 
	 * @return The segment's destination.
	 */
	public int getDestination() {
		
		return destination;
	}
	
	/**
	 * Set the segment's destination.
	 * 
	 * @param pDestination A new destination for this segment.
	 */
	public void setDestination( int pDestination ) {
		
		destination = pDestination;
	}
	
	public boolean equals( Object o ) {
		if ( !(o instanceof Segment)) {
			return false;
		}
		return 
			(getDestination() == ((Segment)o).getDestination()) &&
			(getMessage() == ((Segment)o).getMessage());
	}
	
}