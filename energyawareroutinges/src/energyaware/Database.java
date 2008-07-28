package energyaware;
import java.util.Collection;

public interface Database {
	public Collection getCatalogItems();
	public Order getOrder(int orderNumber);
}