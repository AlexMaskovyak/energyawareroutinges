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
		
		double ratio = pBattery.getLevel() / pBattery.getCapacity();
		
		if( ratio > 0.90 ) {
			return 1;	
		}
		else if( ratio > 0.70 ) {
			
			return 2;	
		}
		else if( ratio > 0.50 ) {	
			return 3;
		}
		else if( ratio > 0.25 ) {
			return 4;
		}

		return 5; // Returned if there is less than 25% left of the battery
	}
}
