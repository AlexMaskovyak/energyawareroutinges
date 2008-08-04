package energyaware;

/**
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 * @version July 2008
 * 
 * A message stores information from the Application Layer.
 */
public class Message {

	private String contents;
	
	/**
	 * Create a new message from the Application Layer.
	 * 
	 * @param pContents The contents of the message.
	 */
	public Message( String pContents ) {
		
		contents = pContents;
	}
	
	/**
	 * Return the contents of the message.
	 * 
	 * @return The message contents.
	 */
	public String getContents() {
		return contents;
	}
	
	/**
	 * Set new contents for this message.  Generally not used.
	 * 
	 * @param aInformation The new message contents.
	 */
	public void setContents( String pContents ) {
		contents = pContents;
	}
}
