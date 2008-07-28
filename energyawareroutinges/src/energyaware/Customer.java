package energyaware;
public class Customer {	
    private int orderCount;
    
    public Customer(int anOrderCount) {
	orderCount = anOrderCount;
    }	
    
    public int getOrderCount() {
	return orderCount;
    }
}