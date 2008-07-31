package energyaware;

import java.util.List;

/**
 * Responsible for next-hop transportation.
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 *
 */
public class Frame {

	protected Datagram datagram;
	protected int source;
	protected int destination;
	
	/**
	 * Default constructor.
	 * @param pDatagram
	 */
	public Frame(Datagram pDatagram) {
		setDatagram(pDatagram);
		setSource(pDatagram.getSource());
		destination = Frame.getNextHopInPath(source, datagram.getPath());
	}
	
	/**
	 * Get the encapsulated datagram.
	 * @return
	 */
	public Datagram getDatagram() {
		return datagram;
	}
	
	/**
	 * Set the encapsulated datagram to the one specified.
	 * @param pDatagram Datagram to encapsulate.
	 */
	public void setDatagram(Datagram pDatagram) {
		datagram = pDatagram;
	}
	
	
	/**
	 * Get the source/sender's identification.
	 * @return ID of the source of this frame.
	 */
	public int getSource() {
		return source;
	}
	
	/**
	 * Set the source/sender's identification.
	 * @param pSource ID of the source for this frame.
	 */
	public void setSource(int pSource) {
		source = pSource;
	}
	
	
	/**
	 * Get the intended next-hop destination of the frame.
	 * @return Next-hop destination of the frame.
	 */
	public int getDestination() {
		return destination;
	}
	
	/**
	 * Set the intended next-hop destination of the frame.
	 * @param pDestination Next-hop destination of the frame.
	 */
	public void setDestination(int pDestination) {
		destination = pDestination;
	}
	
	
	/**
	 * Obtains the next-hop destination based upon the predetermined routing
	 * path stored in the datagram.
	 * @param source
	 * @param path
	 * @return
	 */
	public static int getNextHopInPath(int source, List<Integer> path) {
		int ourPosition = path.indexOf(source);
		int nextHopPosition = ourPosition + 1;
		
		return path.get(nextHopPosition);
	}
}
