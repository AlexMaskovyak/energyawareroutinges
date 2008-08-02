package energyaware;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 * @version July 2008
 *
 * Test Suite is used for testing all aspects of the Energy Aware Protocol
 */
public class TestSuite {

	public static void main( String [] args ) {
		
		Node node = Node.getInstance( 789 );
		
		Datagram dg1 = new Datagram("RREQ", 1, 789, new Segment(), makeList(), 3);
//		Datagram dg2 = new Datagram("RREP", 2, -2, new Segment(), makeList(), 3);
		
		Frame frame1 = new Frame( dg1 );
//		Frame frame2 = new Frame( dg2 );
		
		node.receiveFrame(frame1, 0);
		
		
//		node.receiveFrame(frame2);
	}
	
	private static ArrayList<Integer> makeList() {
		ArrayList <Integer> a = new ArrayList<Integer>();
		a.add( 0 );
		a.add( 1 );
		a.add( 2 );
		a.add( 3 );
		return a;
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
		
		return "JFAIL";
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
