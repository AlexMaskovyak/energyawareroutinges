package energyaware;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
			
		System.out.printf( "TEST 1:\n%s\n%s\n%s \n", "-----", TestRuleInit1(), "-----");
		System.out.printf( "TEST 2:\n%s\n%s\n%s \n", "-----", TestRuleUniversal1(), "-----");
		
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
	public String TestRuleInit1() throws JessException {
		StringBuilder results = new StringBuilder();
		
		Node node1 = Node.getInstance( 1 );
		
		Rete engine = node1.getAgent().getEngine();
		Context context = engine.getGlobalContext();
		
		Defglobal dg = engine.findDefglobal("*agent*");
		Value val = dg.getInitializationValue();
		
		Object ob = val.javaObjectValue( context );
		
		if( node1.getAgent() != ob ) {
			results.append("TRI1: Agent assigned\n");
		}
		
		dg = engine.findDefglobal("*id*");
		val = dg.getInitializationValue();
		ob = val.javaObjectValue(context);
		
		if( ((Integer) ob ).intValue() == 1 && ((Integer) ob).intValue() == node1.getID() ) {
			results.append("TRI1: ID assigned\n");
		}
		
		return results.toString();
	}
	
	public String TestRuleUniversal1() throws JessException {
		StringBuilder results = new StringBuilder();
		
		int source = 1;
		int middle = 2;
		int destination = 3;
		
		int sourceBattery = 4;
		int middleBattery = 5;
		int destinationBattery = 6;
		
		int firstLink = 5;
		int secondLink = 10;
		
		Node node1 = Node.getInstance( source );
		
		Agent agent = node1.getAgent();
		Rete engine = agent.getEngine();
		
		Datagram dg = 
			new Datagram (
				Datagram.UNINIT,
				source,
				destination);
		
		// add path
		ArrayList<Integer> path = TestSuite.makeList( source, middle, destination );
		dg.setPath( path );
		
		// add batteries
		ArrayList<Integer> batteryMetrics = 
			TestSuite.makeList( sourceBattery, middleBattery, destinationBattery );
		dg.setBatteryMetricValues( batteryMetrics );
		
		// transmission costs
		ArrayList<Integer> transmissionCosts =
			TestSuite.makeList( firstLink, secondLink );
		dg.setTransmissionValues( transmissionCosts );
		
		agent.receiveDatagram( dg, 10 );
		
		
		
		// make sure we have a datagram in there
		if ( engine.containsObject( dg ) ) {
			results.append( "UR1: Datagram inserted properly.\n" );
		}
		
		// check our path
		if ( agent.hasPath( path ) ) {
			results.append( "UR1: Path succesfully added to table.\n" );
		}
		
		// check battery metrics
		boolean batteryOK = true;
		for ( int i = 0; i < batteryMetrics.size(); ++i) {
			if (agent.getBatteryMetrics( path.get( i ) ) != batteryMetrics.get( i ) ) {
				batteryOK = false;
			}
		}
		if ( batteryOK ) {
			results.append( "UR1: Battery metrics successfully added.\n" );
		}
		
		// check our transmission cost
		boolean transCostOK = true;
		for ( int i = 0; i < transmissionCosts.size() - 1; ++i) {
			int agentsCost = 
				agent.getTransmissionCost(path.get( i ), path.get( i + 1 ));
			int origCost = transmissionCosts.get( i );
				
			if (agentsCost != origCost) {
				System.out.printf("agentsCost: %d origCost: %d\n", agentsCost, origCost);
				transCostOK = false;
			}
		}
		
		if ( transCostOK ) {
			results.append( "UR1: Transmission metrics successfully added.\n" );
		}
		
		return results.toString();
	}

	
	
	/**
	 * 
	 * @param integers
	 * @return
	 */
	private static ArrayList<Integer> makeList( Integer ... integers ) {
		ArrayList <Integer> a = new ArrayList<Integer>();
		
		for (Integer i : integers) {
			a.add( i );
		}
		
		return a;
	}

	/**
	 * Test: A RREQ Datagram arrives at the destination and a RREP Datagram is
	 * sent back.
	 */
	public void TestRuleRREQ1() {
		// RREQtoRREP
	}
	
	/**
	 * Test: We have received a RREQ for which we are not the destination and
	 * the RREQ_ID is not new.
	 */
	public void TestRuleRREQ2() {
		// NonNovelRREQID
	}
	
	/**
	 * Test: We have receive a RREQ datagram and we already have its path.
	 */
	public void TestRuleRREQ3() {
		// ShortCircuitRREQ
	}
	
	/**
	 * Test: Forward a RREQ datagram for which we have no path and is not
	 * addressed to us.
	 */
	public void TestRuleRREQ4() {
		// ForwardRREQ
	}
	
	/**
	 * Test: RREP returns to original RREQer
	 */
	public void TestRuleRREQ5() {
		// RrepAtSource
	}
	
	/**
	 * Test: RREP and we are the next node in the path but not the destination
	 */
	public void TestRuleRREQ6() {
		// ForwardRREP
	}
	
	/**
	 * Test: RREP and we are not the next node in the path or the destination
	 */
	public void TestRuleRREQ7() {
		// DropRREP
	}
	
	/**
	 * Test: Data type datagram arrived at final destination
	 */
	public void TestRuleRREQ8() {
		// SegmentForUs
	}
	
	/**
	 * Test: Data type datagram arrived at final destination
	 */
	public void TestRuleRREQ9() {
		// ForwardReceivedDatagram
	}
	
	/**
	 * Test: Data type datagram arrived that we overheard, we aren't the next
	 * hop in the path, so we drop it.
	 */
	public void TestRuleRREQ10() {
		// DropDatagram
	}
	
	/**
	 * Test: Data type datagram was created from a segment and must be sent out.
	 */
	public void TestRuleRREQ11() {
		// ForwardOurDatagram
	}
	
	/**
	 * Test: Data type datagram was created from a segment, but we need a path
	 * first.  We want to keep this datagram until we get the response.
	 */
	public void TestRuleRREQ12() {
		// CreateRREQForDatagram
	}
	
	/**
	 * Test: Segment received, create a datagram without path information.
	 */
	public void TestRuleRREQ13() {
		// ReceiveSegmentFromUser
	}

}
