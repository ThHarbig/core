package mayday.core.structures.trees.layout;
import java.awt.geom.Point2D;

/**
 * A wrapper for Point2D.Double coordinates to implement ILayoutValue.
 * @param x the x coordinate of this point in double precision
 * @param y the y coordinate of this point in double precision
 * @author Andreas Friedrich
 * @see @Point2D @ILayoutValue
 */
public class Coordinate extends Point2D.Double implements ILayoutValue {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * constructs a new location with coordinates x,y
	 * @param x the x coordinate of this point in double precision
	 * @param y the y coordinate of this point in double precision
	 */
	public Coordinate(double x, double y) {
		super (x,y);
	}
	
	public Coordinate(String s) {
		parse(s);
	}

	public void parse(String s) {
		String[] parts = s.split(";");
		x = java.lang.Double.parseDouble(parts[0]);
		y = java.lang.Double.parseDouble(parts[1]);
	}

	public String serialize() {
		return x+";"+y;
	}
}
