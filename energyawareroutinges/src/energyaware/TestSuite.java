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
		System.out.printf( "TEST 3:\n%s\n%s\n%s \n", "-----", TestRuleRREQ1(), "-----");
		System.out.printf( "TEST 4:\n%s\n%s\n%s \n", "-----", TestRuleRREQ2(), "-----");
		System.out.printf( "TEST 5:\n%s\n%s\n%s \n", "-----", TestRuleRREQ3(), "-----");
		System.out.printf( "TEST 6:\n%s\n%s\n%s \n", "-----", TestRuleRREQ4(), "-----");
		System.out.printf( "TEST 7:\n%s\n%s\n%s \n", "-----", TestRuleRREP1(), "-----");
		System.out.printf( "TEST 8:\n%s\n%s\n%s \n", "-----", TestRuleRREP2(), "-----");
		System.out.printf( "TEST 9:\n%s\n%s\n%s \n", "-----", TestRuleRREP3(), "-----");
		//System.out.printf( "TEST 9:\n%s\n%s\n%s \n", "-----", TestRuleRREP3(), "-----");
		
//		System.out.println( "TEST 3:\t" + TestRuleRREQ2() );
		
		TestPathUpdate();	// ALEX THE UPDATE PATH WORKS!
		}
		catch( JessException e ) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * ========== A listing of Test conditions for our JESS rules =========
	 */
	
	/**
	 * Test: We need access to an Agent and its ID in JESS
	 */
	public String TestRuleInit1() throws JessException {
		// setAgent
		StringBuilder results = new StringBuilder();
		
		Node node1 = Node.getInstance( 1 );
		
		Rete engine = node1.getAgent().getEngine();
		Context context = engine.getGlobalContext();
		
		Defglobal dg = engine.findDefglobal("*agent*");
		Value val = dg.getInitializationValue();
		
		Object ob = val.javaObjectValue( context );
		
		results.append("TRI1: Agent assignment: ");
		if( node1.getAgent() != ob ) {
			results.append("PASSED\n");
		}
		else {
			results.append("FAILED\n");
		}
		
		dg = engine.findDefglobal("*id*");
		val = dg.getInitializationValue();
		ob = val.javaObjectValue(context);
		
		results.append("TRI1: ID assignment: ");
		if( ((Integer) ob ).intValue() == 1 && ((Integer) ob).intValue() == node1.getID() ) {
			results.append("PASSED\n");
		}
		else {
			results.append("FAILED\n");
		}
		
		return results.toString();
	}
	
	/**
	 * Test: Extracts the Battery Metric, Path & Transmission Cost from a datagram.
	 */
	public String TestRuleUniversal1() throws JessException {
		// ExtractTransmissionCost
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
		results.append( "UR1: Datagram inserted in database: ");
		if ( engine.containsObject( dg ) ) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		
		// check our path
		results.append( "UR1: Path added to PathTable: " );
		if ( agent.hasPath( path ) ) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		// check battery metrics
		results.append( "UR1: BatteryMetrics stored in database: ");
		boolean batteryOK = true;
		for ( int i = 0; i < batteryMetrics.size(); ++i) {
			if (agent.getBatteryMetrics( path.get( i ) ) != batteryMetrics.get( i ) ) {
				batteryOK = false;
			}
		}
		if ( batteryOK ) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		// check our transmission cost
		results.append( "UR1: Transmission costs stored in database: ");
		boolean transCostOK = true;
		for ( int i = 0; i < transmissionCosts.size() - 1; ++i) {
			int agentsCost = 
				agent.getTransmissionCost(path.get( i ), path.get( i + 1 ));
			int origCost = transmissionCosts.get( i );
				
			if (agentsCost != origCost) {
				transCostOK = false;
			}
		}
		
		if ( transCostOK ) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n ");
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
	public String TestRuleRREQ1() {
		// RREQtoRREP
		StringBuilder results = new StringBuilder();
		
		int source = 1;
		int middle = 2;
		int destination = 3;

		int sourceBattery = 4;
		int middleBattery = 5;
		int destinationBattery = 6;
		
		int firstLink = 5;
		int secondLink = 10;

		
		Node node1 = Node.getInstance( destination );
		Agent agent1 = node1.getAgent();
		Rete engine1 = agent1.getEngine();
		
		Datagram dg = 
			new Datagram(
				Datagram.RREQ,
				source,
				destination);
	
		// set segment
		Segment segment = new Segment();
		dg.setSegment( segment );
		
		// add path
		ArrayList<Integer> path = TestSuite.makeList( source, middle );
		dg.setPath( path );
		
		// add batteries
		ArrayList<Integer> batteryMetrics = 
			TestSuite.makeList( sourceBattery, middleBattery, destinationBattery );
		dg.setBatteryMetricValues( batteryMetrics );
		
		// transmission costs
		ArrayList<Integer> transmissionCosts =
			TestSuite.makeList( firstLink, secondLink );
		dg.setTransmissionValues( transmissionCosts );
	
		
		agent1.receiveDatagram( dg, 10 );
		
		
		Datagram datagramFromNode = node1.getLastFrameSent().getDatagram();
		
		results.append( "RREQ1: Response type is RREP: " );
		if (datagramFromNode.getType().equals(Datagram.RREP)) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		results.append( "RREQ1: RREP's source is now the RREQ's destination value: " );
		if (datagramFromNode.getSource() == dg.getDestination()) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		results.append( "RREQ1: RREP's destination is now the RREQ's source value: " );		
		if (datagramFromNode.getDestination() == dg.getSource()) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		results.append( "RREQ1: RREP's segment same as RREQ's segment: " );		
		if (datagramFromNode.getSegment().equals(dg.getSegment())) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		results.append( "RREQ1: RREP's path correctly reversed RREQ's path: " );		
		ArrayList<Integer> revPath = TestSuite.makeList( destination, middle, source );
		if (datagramFromNode.getPath().equals(revPath)) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		results.append( "RREQ1: Battery metrics cleared and current metric added: ");
		ArrayList<Integer> revBatteryMetrics = TestSuite.makeList( agent1.getBatteryMetric() );
		if (datagramFromNode.getBatteryMetricValues().equals(revBatteryMetrics)) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		results.append( "RREQ1: Original RREQ removed from database: " );
		if (!engine1.containsObject( dg )) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		return results.toString();
	}
	
	/**
	 * Test: We have received a RREQ for which we are not the destination and
	 * the RREQ_ID is not new.
	 */
	public String TestRuleRREQ2() {
		// NonNovelRREQID
		StringBuilder results = new StringBuilder();
		
		int source = 1;
		int middle = 2;
		int destination = 3;
		
		int RREQID = 1;
		
		Node node = Node.getInstance( middle );
		Agent agent = node.getAgent();
		agent.addRREQID( RREQID );
		
		Rete engine = agent.getEngine();
		
		Datagram dg = 
			new Datagram(
					Datagram.RREQ,
					source, 
					destination);
		
		dg.setRreqID( RREQID );
		
		agent.receiveDatagram( dg, 10 );
		
		
		results.append( "RREQ2: Datagram was dropped as redundant: ");
		if (!engine.containsObject(dg)) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		return results.toString();
	}
	
	/**
	 * Test: We have receive a RREQ datagram and we already have its path.
	 */
	public String TestRuleRREQ3() {
		// ShortCircuitRREQ
		StringBuilder results = new StringBuilder();
		
		int source = 1;
		int middle = 2;
		int destination = 3;
		
		int sourceBattery = 5;
		
		int RREQID = 1;
		
		Node node = Node.getInstance( middle );
		Agent agent = node.getAgent();
		Rete engine = agent.getEngine();
		
		Datagram dg = 
			new Datagram(
				Datagram.RREQ,
				source,
				destination);
		
		// set segment
		Segment segment = new Segment();
		dg.setSegment( segment );
		
		// add path
		ArrayList<Integer> path = TestSuite.makeList( source, middle );
		dg.setPath( path );
		
		// add batteries
		ArrayList<Integer> batteryMetrics = 
			TestSuite.makeList( sourceBattery );
		dg.setBatteryMetricValues( batteryMetrics );
		
		dg.setRreqID( RREQID );
		
		// give our agent a path to the destination to perform short-circuiting
		ArrayList<Integer> existingPath = 
			TestSuite.makeList( middle, destination );
		agent.updatePathTable( existingPath );

		
		agent.receiveDatagram( dg, 10 );
		
		
		// ensure that the rreq id was added to our table
		results.append( "RREQ3: RREQ ID was added to our table: " );
		if (!agent.isNovelRREQID( RREQID )) {
			results.append( "PASSED\n" ); 
		}
		else {
			results.append( "FAILED\n" );
		}
		
		
		Datagram datagramFromNode = node.getLastFrameSent().getDatagram();
		
		
		// make sure that it is a rrep being sent
		results.append( "RREQ3: Response type is RREP: " );
		if (datagramFromNode.getType().equals(Datagram.RREP)) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}

		// make sure that we are the source and they are the destination
		results.append( "RREQ3: RREP's source is now our ID: " );
		if (datagramFromNode.getSource() == agent.getID()) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		results.append( "RREQ3: RREP's destination is now the RREQ's source value: " );		
		if (datagramFromNode.getDestination() == dg.getSource()) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		results.append( "RREQ3: RREP's segment same as RREQ's segment: " );		
		if (datagramFromNode.getSegment().equals(dg.getSegment())) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		
		// check to make sure the new concatenated path is correct
		results.append( "RREQ3: RREP has the full reverse path from the destination to the source: ");
		ArrayList<Integer> revPath = TestSuite.makeList( destination, middle, source );
		if (datagramFromNode.getPath().equals(revPath)) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		// check to make sure that the only battery metric is ours
		results.append( "RREQ3: Battery metrics cleared and current metric added: ");
		ArrayList<Integer> revBatteryMetrics = TestSuite.makeList( agent.getBatteryMetric() );
		if (datagramFromNode.getBatteryMetricValues().equals(revBatteryMetrics)) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}

		
		// ensure that it was removed
		results.append( "RREQ3: RREQ was removed from database: " );
		if (!engine.containsObject( dg )) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		
		
		return results.toString();
	}
	
	/**
	 * Test: Forward a RREQ datagram for which we have no path and is not
	 * addressed to us.
	 */
	public String TestRuleRREQ4() {
		// ForwardRREQ
		StringBuilder results = new StringBuilder();
		
		int source = 1;
		int middle = 2;
		int destination = 3;
		
		int sourceBattery = 5;

		int RREQID = 1;
		
		Node node = Node.getInstance( middle );
		Agent agent = node.getAgent();
		Rete engine = agent.getEngine();
		
		
		Datagram dg = 
			new Datagram(
				Datagram.RREQ,
				source,
				destination);
		
		// add path
		ArrayList<Integer> path = TestSuite.makeList( source );
		dg.setPath(path);
		
		// add batteries
		ArrayList<Integer> batteryMetrics = TestSuite.makeList( sourceBattery );
		dg.setBatteryMetricValues( batteryMetrics );
		
		// add rreqid
		dg.setRreqID( RREQID );

		
		agent.receiveDatagram( dg, 10 );
		
		
		// ensure that the rreq id was added to our table
		results.append( "RREQ4: RREQ ID was added to our table: " );
		if (!agent.isNovelRREQID( RREQID )) {
			results.append( "PASSED\n" ); 
		}
		else {
			results.append( "FAILED\n" );
		}
		
		
		Datagram datagramFromNode = node.getLastFrameSent().getDatagram();
		
		// make sure that it is a rreq is being forwarded on
		results.append( "RREQ4: Response type is a forwarded RREQ: " );
		if (datagramFromNode.getType().equals(Datagram.RREQ)) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}

		
		
		// make sure that we are the source and they are the destination
		results.append( "RREQ4: RREQ's source is now us: " );
		if (datagramFromNode.getSource() == agent.getID()) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		results.append( "RREQ4: New RREQ's destination remains unchanged: " );		
		if (datagramFromNode.getDestination() == dg.getDestination()) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		results.append( "RREQ4: New RREQ's segment same as old RREQ's segment: " );		
		if (datagramFromNode.getSegment().equals(dg.getSegment())) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		
		// check to make sure the new concatenated path is correct
		results.append( "RREQ4: RREQ's path has been updated: ");
		ArrayList<Integer> newPath = TestSuite.makeList( source, middle );
		if (datagramFromNode.getPath().equals(newPath)) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		// check to make sure that there are two battery metrics 
		results.append( "RREQ4: Battery metrics cleared and current metric added: ");
		ArrayList<Integer> rreqBatteryMetrics = 
			TestSuite.makeList( 
				sourceBattery, 
				agent.getBatteryMetric() );
		if (datagramFromNode.getBatteryMetricValues().equals(rreqBatteryMetrics)) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}

		
		// ensure that it was removed
		results.append( "RREQ4: RREQ was removed from database: " );
		if (!engine.containsObject( dg )) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}

		
		
		return results.toString();
	}
	
	/**
	 * Test: RREP returns to original RREQer
	 */
	public String TestRuleRREP1() {
		// RrepAtSource
		StringBuilder results = new StringBuilder();
		
		int source = 1;
		int middle = 2;
		int destination = 3;
		
		int sourceBattery = 5;
		int middleBattery = 6;
		int destinationBattery = 7;
		
		Node node = Node.getInstance( source );
		Agent agent = node.getAgent();
		Rete engine = agent.getEngine();
		
		Datagram dg = 
			new Datagram(
					Datagram.RREP,
					destination,
					source);
		
		
		ArrayList<Integer> path = TestSuite.makeList( destination, middle, source );
		dg.setPath(path);
		
		ArrayList<Integer> batteryMetrics = 
			TestSuite.makeList( destinationBattery, middleBattery, sourceBattery );
		dg.setPath( batteryMetrics );
		
		
		agent.receiveDatagram( dg, 10 );
		
		
		
		
		// ensure that it was removed
		results.append( "RREP1: RREP was removed from database: " );
		if (!engine.containsObject( dg )) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}

		// ensure that agent has a path from itself to destination
		results.append( "RREP1: Agent has path from itself to destination: " );
		ArrayList<Integer> revPath = 
			TestSuite.makeList( source, middle, destination );
		if (agent.hasPath( revPath )) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		return results.toString();
	}
	
	/**
	 * Test: RREP and we are the next node in the path but not the destination
	 */
	public String TestRuleRREP2() {
		// ForwardRREP
		StringBuilder results = new StringBuilder();
		
		int source = 1;
		int middle = 2;
		int destination = 3;
		
		int sourceBattery = 4;
		int middleBattery = 5;
		int destinationBattery = 6;
		
		Node node = Node.getInstance( middle );
		Agent agent = node.getAgent();
		Rete engine = agent.getEngine();
		
		Datagram dg = 
			new Datagram(
					Datagram.RREP,
					source,
					destination);
		
		// add the routing path
		ArrayList<Integer> path = TestSuite.makeList( source, middle, destination );
		dg.setPath( path );
		
		// add the batteries so far
		ArrayList<Integer> batteryMetrics = TestSuite.makeList( sourceBattery );
		dg.setBatteryMetricValues( batteryMetrics );
		
		// give to agent
		agent.receiveDatagram( dg, 10 );
		
		Datagram datagramFromNode = node.getLastFrameSent().getDatagram();
		
		// check the new datagram generated
		// make sure that it is a rrep is being forwarded on
		results.append( "RREP2: Response type is a forwarded RREP: " );
		if (datagramFromNode.getType().equals(Datagram.RREP)) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}

		
		// make sure that we are the source and they are the destination
		results.append( "RREP2: RREP's source is now us: " );
		if (datagramFromNode.getSource() == agent.getID()) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		results.append( "RREP2: New RREP's destination remains unchanged: " );		
		if (datagramFromNode.getDestination() == dg.getDestination()) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}

		results.append( "RREP2: New RREP's segment same as old RREP's segment: " );		
		if (datagramFromNode.getSegment().equals(dg.getSegment())) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}

		// check to make sure the path remains unchanged
		results.append( "RREP2: RREP's path remains unchanged: ");
		if (datagramFromNode.getPath().equals(dg.getPath())) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}
		
		// check to make sure that there are two battery metrics 
		results.append( "RREP2: Battery metrics cleared and current metric added: ");
		ArrayList<Integer> rreqBatteryMetrics = 
			TestSuite.makeList( 
				sourceBattery, 
				agent.getBatteryMetric() );
		if (datagramFromNode.getBatteryMetricValues().equals(rreqBatteryMetrics)) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}

		
		
		// ensure that it was removed
		results.append( "RREP2: RREP was removed from database: " );
		if (!engine.containsObject( dg )) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}

		
		return results.toString();
	}
	
	/**
	 * Test: RREP and we are not the next node in the path or the destination
	 */
	public String TestRuleRREP3() {
		// DropRREP
		StringBuilder results = new StringBuilder();
		
		int source = 1;
		int middle = 2;
		int destination = 3;
		int bystander = 4;
		
		int sourceBattery = 4;
		int middleBattery = 5;
		int destinationBattery = 6;
		
		Node node = Node.getInstance( bystander );
		Agent agent = node.getAgent();
		Rete engine = agent.getEngine();
		
		Datagram dg = 
			new Datagram(
					Datagram.RREP,
					source,
					destination);

		// add the path
		ArrayList<Integer> path = TestSuite.makeList( source, middle, destination );
		dg.setPath( path );
		
		// add the metrics so far
		ArrayList<Integer> batteryMetrics = TestSuite.makeList( sourceBattery, middleBattery );
		dg.setBatteryMetricValues( batteryMetrics );
		
		agent.receiveDatagram( dg, 10 );
		
		
		
		
		// ensure that it was removed
		results.append( "RREP3: RREP was removed from database: " );
		if (!engine.containsObject( dg )) {
			results.append( "PASSED\n" );
		}
		else {
			results.append( "FAILED\n" );
		}

		
		return results.toString();
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
	public void TestRuleRREQ13() { // ReceiveSegmentFromUser
		
		int source = 1, destination = 2;
		Node node = Node.getInstance( source );
		Message message = new Message("Hi Guys!");
		
		// Tell node it has a message to send
		node.sendMessage(message, destination );
		
//		Datagram dg = new Datagram("DATA",source,destination,message);
		// Determine if the node made a Datagram
		// make sure we have a datagram in there
//		if ( engine.containsObject( dg ) ) {
//			results.append( "UR1: Datagram inserted properly.\n" );
//		}
	}

	/**
	 * Test whether we update/add paths correctly. 
	 */
	public void TestPathUpdate() {
		
		int source = 1, destination = 4;
		Node node = Node.getInstance( source );
		Agent agent = node.getAgent();

		ArrayList<Integer> newPath = new ArrayList<Integer>();
		newPath.add( source );
		newPath.add( 2 );
		newPath.add( 3 );
		newPath.add( destination );
		
		agent.updatePathTable(newPath, source);
		
		System.out.println( agent.getPathTable() );
	}
}
