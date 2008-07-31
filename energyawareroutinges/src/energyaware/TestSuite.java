package energyaware;

/**
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 * @version July 2008
 *
 * Test Suite is used for testing all aspects of the Energy Aware Protocol
 */
public class TestSuite {

	public static void main( String [] args ) {
		
		new TestSuite();
	}
	
	public TestSuite() {
		
		System.out.println( "TEST 1:\t" + Test1() );
		System.out.println( "TEST 2:\t" + Test2() );
		System.out.println( "TEST 3:\t" + Test3() );
		System.out.println( "TEST 4:\t" + Test4() );
		System.out.println( "TEST 5:\t" + Test5() );
		System.out.println( "TEST 6:\t" + Test6() );
		System.out.println( "TEST 7:\t" + Test7() );
	}
	
	// Test an empty battery
	private String Test1() {
	
		Battery bat = new Battery(0, 200);
		if( BatteryMetric.calculateBatteryMetric(bat) == 5 )
			return "PASS";
		
		return "FAIL";
	}
	
	// Test a full battery
	private String Test2() {
	
		Battery bat = new Battery( 150, 150 );
		if( BatteryMetric.calculateBatteryMetric( bat ) == 1 )
			return "PASS";
		return "FAIL";
	}
	
	// Test a half full battery
	private String Test3() {
	
		Battery bat = new Battery( 6000, 12000 );
		if( BatteryMetric.calculateBatteryMetric( bat ) == 4)
			return "PASS";
		return "FAIL";
	}
	
	private String Test4() { return "NA"; }
	private String Test5() { return "NA"; }
	private String Test6() { return "NA"; }
	private String Test7() { return "NA"; }
}
