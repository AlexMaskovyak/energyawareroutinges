import java.util.List;
import java.util.ArrayList;

public class Datagram {
	private String type;
	private String source;
	private String destination;
	private List<String> path;
	private List<Integer> powerMetric;
	
	public Datagram() {
		
		path = new ArrayList<String>();
		powerMetric = new ArrayList<Integer>();
	}
	
	public String getType() {
		return type;
	}
	public void setType( String pType ) {
		type = pType;
	}
	
	public String getSource() {
		return source;
	}
	public void setSource( String pSource ) {
		type = pSource;
	}
	
	public String getDestination() {
		return destination;
	}
	public void setDestination( String pDestination ) {
		destination = pDestination;
	}
	
	public List<String> getPath() {
		return path;
	}
	public void setPath( List<String> pPath ) {
		path = pPath;
	}
	
	public List<Integer> getPowerMetrics() {
		return powerMetric;
	}
	public void setPowerMetric( List<Integer> pPowerMetric ) {
		powerMetric = pPowerMetric;
	}
	
	public Integer getPowerMetric( int pNode ) {
		return powerMetric.get( pNode );
	}
	public void addPowerMetric( int pPowerMetric ) {
		powerMetric.add( pPowerMetric );
	}
	
}
