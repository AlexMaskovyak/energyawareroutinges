import java.util.List;
import java.util.ArrayList;

public class Datagram {
	public String type;
	public String source;
	public String destination;
	public List<String> paths;
	public List<Integer> batteryMetricValues;
	
	public Datagram() {
		
		paths = new ArrayList<String>();
		batteryMetricValues = new ArrayList<Integer>();
	}
}
