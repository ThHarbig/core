/**
 * 
 */
package mayday.vis3.graph.edges.router;

import java.awt.geom.Point2D;

public class EdgePoints
{
	public Point2D source;
	public Point2D target;
	
	public EdgePoints(Point2D source, Point2D target) 
	{
		this.source=source;
		this.target=target;
	}
	
	/**
	 * @return A new instance of EdgePoints with the coordinates swapped. Leaves the instance it is called on unchanged. 
	 */
	public EdgePoints swapped()
	{
		return new EdgePoints(target,source);
	}
	
	
}