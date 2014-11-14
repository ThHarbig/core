package mayday.core.structures.trees.layout;


/**
 * @author Andreas Friedrich
 * Interface for EdgeLayout, NodeLayout and Coord, provides Generic-Value for Maps
 */
public interface ILayoutValue extends Cloneable {

	/** serialize the value. may NOT contain colons (":")! */
	public String serialize();
	
	public void parse(String s);
	
}
