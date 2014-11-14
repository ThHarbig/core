package mayday.vis3.vis2base;

import java.awt.Graphics;
import java.awt.Graphics2D;

import wsi.ra.chart2d.DBorder;
import wsi.ra.chart2d.DPointIcon;

/** 
 * Basic class for the implementation of a shape that should be used for drawing 
 * points of a DataSeries object. <code>paint(Graphics g)</code> should be overridden.
 */
public class Shape implements DPointIcon
{
	public Shape() {}
	
	public DBorder getDBorder() 
	{
		return new DBorder();
	}

	public void paint(Graphics2D g) {
		g.drawLine(0, 0, 1, 1);
	}
	
	public final void paint(Graphics g) { // this is final to prevent mistakes. Overload paint(Graphics2D) instead!
		paint((Graphics2D)g);
	}

	public boolean wantDeviceCoordinates() {
		return false;
	}
	
}