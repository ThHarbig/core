package mayday.vis3.vis2base;

import mayday.core.Probe;
import wsi.ra.chart2d.DPoint;
import wsi.ra.chart2d.DPointSet;

/**
 * Wrapper class for comfortable use of data series (DPointSet) in plots 
 * @author Philipp Bruns
 *
 */

public class DataSeries extends DPointSet
{
	// coordinates of the point added previously, to map the slope
	private Double[] prev = null;
	
	public void addPoint(double x, double y, Object p) {
		double slope = (prev == null) ? Double.NaN : (y-prev[1])/(x-prev[0]);
		super.addDPoint(x, y, slope, p);
		prev = new Double[3];
		prev[0] = x;
		prev[1] = y;
		prev[2] = slope;
	}
	
	/**
	 * Remove all points
	 */
	public void removeAllPoints() {
		super.removeAllPoints();
		prev = null;
	}
	
	/**
	 * Do not connect the last point with the next one.
	 * Useful for drawing multiple lines/curves within one DataSeries object.
	 */
	public void jump() {
		super.jump();
		prev = null;
	}
	
	/**
	 * returns a double array containing the point coordinates of the 
	 * nearest point to the input point
	 * @param x X coordinate of the input point 
	 * @param y Y coordinate of the input point 
	 * @return coordinates of the nearest point: [0] = X, [1] = Y;
	 */
	public double[] getNearestPoint(double x, double y)
	{
		DPoint p = getNearestDPoint(new DPoint(x, y));
		return new double[] {p.x, p.y};
	}
	
	public void setShape(Shape s) {
		super.setIcon(s);
	}
}
