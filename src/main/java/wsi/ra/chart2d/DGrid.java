/**
 *  Filename: $RCSfile: DGrid.java,v $
 *  Purpose:
 *  Language: Java
 *  Compiler: JDK 1.3
 *  Authors:  Fabian Hennecke
 *  Version:  $Revision: 1.3 $
 *            $Date: 2012/03/02 14:08:59 $
 *            $Author: battke $
 *  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
 */

package wsi.ra.chart2d;

/*==========================================================================*
 * IMPORTS
 *==========================================================================*/

import java.awt.Color;
import java.awt.Graphics;

/*==========================================================================*
 * CLASS DECLARATION
 *==========================================================================*/

/**
 * this class paints a grid with certain line distances on a DParent
 */
public class DGrid extends DComponent
{
  /**
   * the distances between the lines
   */
  double hor_dist;
  double hor_emph; // horizontal emphasize interval (works only as a multiple of hor_dist)

  /**
   * the distances between the lines
   */
  double ver_dist;
  double ver_emph; // vertical emphasize interval (works only as a multiple of ver_dist)

  public static final Color DEFAULT_COLOR = new Color(235, 235, 235);

  /**
   * constructor with the size and position of the grid and the line distances
   *
   * @param rectangle the rectangle around the grid
   * @param hor_dist the horizontal distance between the lines in D-coordinates,
   *        not in pixel coordinates!
   * @param ver_dist vertical distance between the lines in D-coordinates,
   *        not in pixel coordinates!
   */
  public DGrid( DRectangle rectangle, double hor_dist, double ver_dist ){
	  this(rectangle, hor_dist, ver_dist, DEFAULT_COLOR);
  }

  /**
   * constructor with the size and position of the grid and the line distances
   *
   * @param rectangle the rectangle around the grid
   * @param hor_dist the horizontal distance between the lines in D-coordinates,
   *        not in pixel coordinates!
   * @param ver_dist the vertical distance between the lines in D-coordinates,
   *        not in pixel coordinates!
   * @param color the color of the grid
   *        ( can also be set by setColor( java.awt.Color ) )
   */
  public DGrid( DRectangle rectangle, double hor_dist, double ver_dist, Color color ){
    this(rectangle, hor_dist, ver_dist, .0, .0, color);
  }
  
  /**
   * constructor with the size and position of the grid and the line distances
   *
   * @param rectangle the rectangle around the grid
   * @param hor_dist the horizontal distance between the lines in D-coordinates,
   *        not in pixel coordinates!
   * @param ver_dist the vertical distance between the lines in D-coordinates,
   *        not in pixel coordinates!
   * @param hor_emph after each hor_emph lines, emphasize one grid line
   * @param ver_emph after each ver_emph lines, emphasize one grid line
   * @param color the color of the grid
   *        ( can also be set by setColor( java.awt.Color ) )
   */
  public DGrid( DRectangle rectangle, double hor_dist, double ver_dist, double hor_emph, double ver_emph, Color color ){
    this.rectangle = rectangle;
    this.hor_dist = hor_dist;
    this.ver_dist = ver_dist;
    this.hor_emph = hor_emph;
    this.ver_emph = ver_emph;
    this.color = color;
  }

  /**
   * paints the grid...
   *
   * @param m the <code>DMeasures</code> object to paint the grid
   */
  public void paint( Graphics g, DMeasures m ){
//    Graphics g = m.getGraphics();
    if( color != null ) g.setColor( color );
    double minX, minY, pos;
    DPoint p1, p2;
    DLine l;

    minX = (int)( rectangle.x / hor_dist );
    if( minX * hor_dist <= rectangle.x ) minX++;
    minX *= hor_dist;
    minY = (int)( rectangle.y / ver_dist );
    if( minY * ver_dist <= rectangle.y ) minY++;
    minY *= ver_dist;

    p1 = new DPoint( 0, rectangle.y );
    p2 = new DPoint( 0, rectangle.y + rectangle.height );
    
    if(hor_dist > .0)
    {
	    for(pos = minX; pos<=rectangle.x + rectangle.width; pos += hor_dist ){
	      p1.x = p2.x = pos;
	      l = new DLine( p1, p2 );
	      l.paint(g, m ); 
	    }
    }

    if(ver_dist > .0)
    {
	    p1.x = rectangle.x;
	    p2.x = p1.x + rectangle.width;
	    for(pos = minY; pos<=rectangle.y + rectangle.height; pos += ver_dist ){
	      p1.y = p2.y = pos;
	      l = new DLine( p1, p2 );
	      l.paint(g, m ); 
	    }
    }
    
    if(hor_emph > .0)
    {
    	minX = (int)( rectangle.x / hor_emph );
        if( minX * hor_emph <= rectangle.x ) minX++;
        minX *= hor_emph;
        
	    // draw emphasized lines
	    p1 = new DPoint( 0, rectangle.y );
	    p2 = new DPoint( 0, rectangle.y + rectangle.height );
	    for(pos = minX; pos<=rectangle.x + rectangle.width; pos += hor_emph ){
	      p1.x = p2.x = pos;
	      l = new DLine( p1, p2, color.darker() );
	      l.paint(g, m ); 
	    }
    }
    
    if(ver_emph > .0)
    {
	    minY = (int)( rectangle.y / ver_emph );
	    if( minY * ver_emph <= rectangle.y ) minY++;
	    minY *= ver_emph;
    	    
	    p1.x = rectangle.x;
	    p2.x = p1.x + rectangle.width;
	    for(pos = minY; pos<=rectangle.y + rectangle.height; pos += ver_emph ){
	      p1.y = p2.y = pos;
	      l = new DLine( p1, p2, color.darker() );
	      l.paint(g, m ); 
	    }
    }
  }

  @Override
public String toString(){
    return "chart2d.DGrid[ hor: "+hor_dist+", ver: "+ver_dist+" ]";
  }
}

/****************************************************************************
 * END OF FILE
 ****************************************************************************/
