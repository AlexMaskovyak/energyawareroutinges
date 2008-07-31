package energyaware;

/**
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 * @version July 2008
 *
 * A battery is the storage object for our power.  A battery knows of its capacity and current level.
 */
public class Battery {

		public int level;		// The current level of the battery 
		public int capacity;	// The full capabilities of the battery
	
		/**
		 * Create a new battery object.
		 * 
		 * @param pLevel
		 * @param pCapacity
		 */
		public Battery( int pLevel, int pCapacity ) {
			
			level = pLevel;
			capacity = pCapacity;
		}
		
		/**
		 * Get the battery's capacity.
		 * 
		 * @return The capacity.
		 */
		public int getCapacity() {
			
			return capacity;
		}
		
		/**
		 * Set the battery's capacity.
		 * 
		 * @param pCapacity A battery capacity.
		 */
		public void setCapacity( int pCapacity ) {
			
			capacity = pCapacity;
		}
		
		/**
		 * Get the battery's current power level.
		 * 
		 * @return The current power level.
		 */
		public int getLevel() {
			
			return level;
		}
		
		/**
		 * Set the battery's current power level.
		 * 
		 * @param A battery power level.
		 */
		public void setLevel( int pLevel ) {
			
			level = pLevel;
		}
}
