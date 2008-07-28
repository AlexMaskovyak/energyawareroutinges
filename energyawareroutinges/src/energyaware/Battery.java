package energyaware;

public class Battery {

		public int mPowerLevel;
		public int mMaxCapacity;
	
		public Battery( int pInitialLevel, int pMaxCapacity ) {
			
			mPowerLevel = pInitialLevel;
			mMaxCapacity = pMaxCapacity;
		}
}
