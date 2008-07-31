package energyaware;

/**
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 * @version July 2008
 *
 * Battery metric provides a method for converting a specified battery level into
 * a rankable heuristic.
 */
public class BatteryMetric {
	
	/**
	 * Analyzes a given battery and produces a rankable heuristic.
	 * 
	 * @param pBattery A battery to be analyzed.
	 * @return The heuristic implied by this battery.
	 */
	public static int calculateBatteryMetric( Battery pBattery ) {
		
<<<<<<< .mine
		double level = pBattery.level;
		double max = pBattery.capacity;
		
		if( level/max > .90 ) {
			
			return 1;
			
		} else if( level/max > .70 ) {
			
			return 2;
			
		} else if( level/max > .50 ) {
			
			return 3;
			
		} else if( level/max > .25 ) {
			
			return 4;
			
		}

		return 5; // Returned if there is less than 25% left of the battery
=======
		return -1;
>>>>>>> .r18
	}
}
