package energyaware;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import jess.*;

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
		
		try {
			
		System.out.println( "TEST 1:\t"); TestRuleInit1();
		System.out.println( "TEST 2:\t" ); TestUniversalRule1();
//		System.out.println( "TEST 3:\t" + TestRule3() );
//		System.out.println( "TEST 4:\t" + Test4() );
//		System.out.println( "TEST 5:\t" + Test5() );
//		System.out.println( "TEST 6:\t" + Test6() );
//		System.out.println( "TEST 7:\t" + Test7() );
		}
		catch( JessException e ) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * ========== A listing of Test conditions for our JESS rules =========
	 */
	
	// 
	public void TestRuleInit1() throws JessException {
		
		Node node1 = Node.getInstance( 1 );
		
		Rete engine = node1.getAgent().getEngine();
		Context context = engine.getGlobalContext();
		
		Defglobal dg = engine.findDefglobal("*agent*");
		Value val = dg.getInitializationValue();
		
		Object ob = val.javaObjectValue( context );
		
		if( node1.getAgent() != ob ) {
			
			System.out.println("TRI1: Agent assigned");
		}
		
		dg = engine.findDefglobal("*id*");
		val = dg.getInitializationValue();
		ob = val.javaObjectValue(context);
		
		if( ((Integer) ob ).intValue() == 1 && ((Integer) ob).intValue() == node1.getID() ) {
			System.out.println("TRI1: ID assigned");
		}
	}
	
	public void TestUniversalRule1() throws JessException {
		
		Node node1 = Node.getInstance( 1 );
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
/*
	public static void TestN() {
		
		Network network = Network.getInstance();
		
		Node node1 = Node.getInstance( 1 );
		Node node789 = Node.getInstance( 789 );
		Point point1 = new Point(0, 0);
		Point point789 = new Point(1, 1);
		
		network.addNode(node1, point1);
		network.connect(node1);
		network.addNode(node789, point789);
		network.connect(node789);
		
		Datagram dg1 = new Datagram("RREQ", 1, 789, new Segment(), makeList(), 3);
//		Datagram dg2 = new Datagram("RREP", 2, -2, new Segment(), makeList(), 3);
		
		Frame frame1 = new Frame( dg1 );
//		Frame frame2 = new Frame( dg2 );
		
		node1.receiveFrame(frame1, 0);
		
		
//		node.receiveFrame(frame2);
	}
	
	private static ArrayList<Integer> makeList() {
		ArrayList <Integer> a = new ArrayList<Integer>();
		a.add( 1 );
		a.add( 789 );
		return a;
	}
	
	// Test an empty battery
	private String Testb1() {
	
		Battery bat = new Battery(0, 200);
		if( BatteryMetric.calculateBatteryMetric(bat) == 5 )
			return "PASS";
		
		return "JFAIL";
	}
	
	// Test a full battery
	private String Testb2() {
	
		Battery bat = new Battery( 150, 150 );
		if( BatteryMetric.calculateBatteryMetric( bat ) == 1 )
			return "PASS";
		return "FAIL";
	}
	
	// Test a half full battery
	private String Testb3() {
	
		Battery bat = new Battery( 6000, 12000 );
		if( BatteryMetric.calculateBatteryMetric( bat ) == 4)
			return "PASS";
		return "FAIL";
	}
	*/
}
