package energyaware;

import jess.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Steve Baylor, Jeff Corcoran & Alex Maskovyak
 * @version July 2008
 * 
 *          An agent has the communication processing protocol.
 */
public class Agent {

	private Rete engine;
	private WorkingMemoryMarker marker;

	// private Database database;

	// TEST STUB METHOD
	public static void main(String[] args) {
		System.out.println("In main");
		new Agent();
	}

	/**
	 * Default agent constructor.
	 */
	public Agent() {

		// Database aDatabase
		try {
			System.out.println("YES");

			// Create a Jess rule engine
			engine = new Rete();
			engine.reset();

			// Load the pricing rules
			engine.batch("rules.clp");

			Datagram d = new Datagram("RREQ", -1, 2, null);
			Datagram e = new Datagram("RREP", 1, -2, null);
			engine.add(d);
			engine.add(e);

			engine.assertString("(ourid (id 2))");
			for (Iterator it = engine.listFacts(); it.hasNext();) {
				System.out.println(it.next());
			}

			ArrayList<Integer> a = new ArrayList<Integer>();
			a.add(1);
			a.add(2);
			a.add(3);

			engine.add(a);

			engine.run();
			// Load the catalog data into working memory
			// database = aDatabase;
			// engine.addAll(database.getCatalogItems());

			// Mark end of catalog data for later
			marker = engine.mark();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
