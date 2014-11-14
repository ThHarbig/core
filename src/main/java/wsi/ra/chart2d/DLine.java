/**
 *  Filename: $RCSfile: DLine.java,v $
 *  Purpose:
 *  Language: Java
 *  Compiler: JDK 1.3
 *  Authors:  Fabian Hennecke
 *  Version:  $Revision: 1.2 $
 *            $Date: 2010/01/28 16:47:27 $
 *            $Author: battke $
 *  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
 */

package wsi.ra.chart2d;

/*==========================================================================*
 * IMPORTS
 *==========================================================================*/

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/*==========================================================================*
 * CLASS DECLARATION
 *==========================================================================*/

/**
 */
public class DLine extends DComponent
{
	DPoint start;
	DPoint end;
	int width;

	public DLine( double x1, double y1, double x2, double y2 ){
		this( new DPoint( x1, y1 ), new DPoint( x2, y2 ) );
	}

	public DLine( DPoint start, DPoint end ){
		this.start = start;
		this.end = end;
	}
	public DLine( double x1, double y1, double x2, double y2, Color color ){
		this( new DPoint( x1, y1 ), new DPoint( x2, y2 ), color );
		width = 1;
	}

	public DLine( DPoint start, DPoint end, Color color ){
		this.start = start;
		this.end = end;
		this.color = color;
	}

	@Override
	public DRectangle getRectangle(){
		double x = start.x, y = start.y, width = end.x - x, height = end.y - y;
		if( width < 0 ) { x += width; width *= -1; }
		if( height < 0 ) { y += height; height *= -1; }
		return new DRectangle( x, y, width, height );
	}

	public void paint( Graphics g, DMeasures m ){
		//System.out.println("DLine.paint(Measures): "+this);
		//    Graphics g = m.getGraphics();
		if( color != null ) g.setColor( color );
		Point p1 = m.getPoint( start ),
		p2 = m.getPoint( end ) ;
		g.drawLine( p1.x, p1.y, p2.x, p2.y );
	}

	@Override
	public void setColor( Color color ){
		this.color = color;
	}

	@Override
	public String toString(){
		return "DLine[("+start.x+","+start.y+") --> ("+end.x+","+end.y+", color: "+color+"]";
	}
}

/****************************************************************************
 * END OF FILE
 ****************************************************************************/
