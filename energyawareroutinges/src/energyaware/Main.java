package energyaware;

import jess.JessException;
import jess.Rete;

public class Main {

	private Rete engine;	// Our Intelligent Agent
//	private Database paths;	// Our list of known paths
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
			
		try {
			
			new Main();
			
		}
		catch( JessException error ) {
			
			System.out.println("Error!");
			
		}
	}
	
	public Main() throws JessException {
		
		engine = new Rete();	// Create the Rule Engine
		engine.reset();
		
		engine.batch("rules.clp");	// Load the rules
		
		
		
	}

}
