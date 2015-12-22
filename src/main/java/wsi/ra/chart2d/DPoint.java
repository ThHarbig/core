/**
 *  Filename: $RCSfile: DPoint.java,v $
 *  Purpose:
 *  Language: Java
 *  Compiler: JDK 1.3
 *  Authors:  Fabian Hennecke
 *  Version:  $Revision: 1.3 $
 *            $Date: 2010/01/28 16:47:28 $
 *            $Author: battke $
 *  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
 */

package wsi.ra.chart2d;

/*==========================================================================*
 * IMPORTS
 *==========================================================================*/

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

/*==========================================================================*
 * CLASS DECLARATION
 *==========================================================================*/

/**
 */
public class DPoint extends DComponent
{
	public double x;
	public double y;
	public String label;
	protected DPointIcon icon = null;
	public Object o; // associates this point with any kind object, use null if not required
	public double s; // slope from the "previously painted" point (cannot be accessed from here) to this point
	// helpful for interactions with connected lines, use 0 if not required
	public DPoint( ){
	}
	public void initpoint( double x, double y ){
		this.x = x;
		this.y = y;
		rectangle = new DRectangle( x, y, 0, 0 );
	}
	public DPoint( double x, double y, double s, Object o ){
		this.x = x;
		this.y = y;
		this.s = s;
		this.o = o;
		rectangle = new DRectangle( x, y, 0, 0 );
	}

	public DPoint(double x, double y)
	{
		this(x,y,0,null);
	}

	public void paint( Graphics g, DMeasures m ){
		//    Graphics g = m.getGraphics();
		if( color != null ) g.setColor( color );
		Point dp = m.getPoint( this );
		if( label != null ){
			FontMetrics fm = g.getFontMetrics();
			g.drawString( label,
					dp.x - fm.stringWidth( label ) / 2,
					dp.y + fm.getAscent()
			);
		}
		if( icon == null )
			g.drawRect( dp.x, dp.y, 1, 1 );
		else{
			g.translate( dp.x, dp.y );
			icon.paint( (Graphics2D)g );
			g.translate( -dp.x, -dp.y );
		}
	}

	/**
	 */
	/**
	 * method sets an icon for a better displaying of the point
	 *
	 * @param icon the DPointIcon
	 */
	public void setIcon( DPointIcon icon ){
		this.icon = icon;
		if( icon == null ) setDBorder(new DBorder(1,1,1,1));
		else setDBorder( icon.getDBorder() );
	}

	/**
	 */
	/**
	 * method returns the current icon of the point
	 *
	 * @return the DPointIcon
	 */
	public DPointIcon getIcon(){
		return icon;
	}

	@Override
	public Object clone(){
		DPoint copy = new DPoint( x, y );
		copy.color = color;
		return copy;
	}

	@Override
	public String toString(){
		String text = "DPoint[";
		if( label != null ) text += label+", ";
		text += "x: "+x+", y: "+y+", color: "+color+"]";
		return text;
	}
}

/****************************************************************************
 * END OF FILE
 ****************************************************************************/
