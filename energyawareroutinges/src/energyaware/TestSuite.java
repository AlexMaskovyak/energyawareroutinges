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
			
		System.out.println( "TEST 1:\t"); TestRuleInit1();
		System.out.println( "TEST 2:\t" ); TestRuleUniversal1();
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
	
	public void TestRuleUniversal1() throws JessException {
		int source = 1;
		int middle = 2;
		int destination = 3;
		
		Node node1 = Node.getInstance( source );
		
		Agent agent = node1.getAgent();
		Rete engine = agent.getEngine();
		Datagram dg = 
			new Datagram (
				Datagram.UNINIT,
				source,
				destination);
		
		ArrayList<Integer> path = TestSuite.makeList( source, middle, destination );
		dg.setPath( path );
		agent.receiveDatagram( dg, 10 );
		engine.add( dg );
		
		// make sure we have a datagram in there
		if ( engine.containsObject( dg ) ) {
			
			System.out.println( "UR1: Datagram inserted properly." );
		}
		
		// check our path
		if ( agent.hasPath( path ) ) {
			
			System.out.println( "UR1: Path succesfully added to table." );
		}
		
		// check our battery metric
		
		// check our transmission cost
		
	}

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
