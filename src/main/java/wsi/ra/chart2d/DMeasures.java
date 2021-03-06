/**
 *  Filename: $RCSfile: DMeasures.java,v $
 *  Purpose:
 *  Language: Java
 *  Compiler: JDK 1.3
 *  Authors:  Fabian Hennecke
 *  Version:  $Revision: 1.3 $
 *            $Date: 2010/01/28 16:47:27 $
 *            $Author: battke $
 *  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
 */

package wsi.ra.chart2d;

/*==========================================================================*
 * IMPORTS
 *==========================================================================*/


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

/*==========================================================================*
 * CLASS DECLARATION
 *==========================================================================*/

/**
 */
public class DMeasures
{
  private boolean under_construction = false;
  // when in use for a DArea:
//  Graphics g;
  // when in use for a ScaledBorder:
  ScaledBorder sb;
  // for both:
  DFunction x_scale;
  // for both:
  DFunction y_scale;
  Component comp;
  Insets insets;
  boolean antialias = false;
  float alpha = 1;

  /**
   * package private constructor for the DMeasures object
   * the object can be obtained by calling the method getDMeasures of an DArea
   * object
   *
   * @param area the DArea object
   */
  DMeasures( DArea area ){
    comp = area;
  }

  DMeasures( ScaledBorder sb ){
    this.sb = sb;
  }

  /**
   * method returns the pixel-point which belongs to the DPoint in the
   * D-coordinates
   * it says where to paint a certain DPoint
   * returns <code>null</code> if the double coordinates do not belong to the
   * image of the scale functions
   *
   * @param p the DPoint
   * @return the coresponding pixel Point
   */
  public Point getPoint( DPoint p ){
    //System.out.println("DMeasures.getPoint :"+org );
    return getPoint( p.x, p.y );
  }

  /**
   * method returns the pixel-point which belongs to the given D-coordinates
   * it says where to paint a certain DPoint
   * returns <code>null</code> if the double coordinates do not belong to the
   * image of the scale functions
   *
   * @param x the double x D-coordinate
   * @param y the double y D-coordinate
   * @return the coresponding pixel Point
   */
  public Point getPoint( double x, double y ){
    DRectangle rect = getSourceOf( getDRectangle() );
    try{
      if( x_scale != null ) x = x_scale.getSourceOf( x );
      if( y_scale != null ) y = y_scale.getSourceOf( y );
    }
    catch( IllegalArgumentException e ){ return null; }
    Point dp = new Point();
    Dimension dim = getInner();
    Insets insets = getInsets();
    dp.x = (int)( dim.width * (x - rect.x)/rect.width ) + insets.left;
    dp.y = (int)( dim.height * (1 - (y - rect.y)/rect.height)) + insets.top;
    return dp;
  }

  /**
   * method returns the point in D-coordinates which corresponds to the
   * given pixel-point
   * returns <code>null</code> if the given coordinates can not be calculated to
   * the double coordinates, when they represent a point outside of the definition
   * area of the scale functions
   *
   * @param p Point in pixel coordinates
   * @return the coresponding DPoint
   */
  public DPoint getDPoint( Point p ){
    return getDPoint( p.x, p.y );
  }

  /**
   * method returns the point in D-coordinates which corresponds to the
   * given pixel-coordinates
   * returns <code>null</code> if the given coordinates can not be calculated to
   * the double coordinates, when they represent a point outside of the definition
   * area of the scale functions
   *
   * @param x x-pixel coordinate
   * @param y y-pixel coordinate
   * @return the coresponding DPoint
   */
  public DPoint getDPoint( int x, int y ){
    DRectangle rect = getSourceOf( getDRectangle() );
    Dimension dim = getInner();
    Insets insets = getInsets();
    x -= insets.left;
    y -= insets.top;
    double dx, dy;
    dx = rect.x + rect.width * x/dim.width;
    dy = rect.y + rect.height * (1 - y/(double)dim.height );
    try{
      if( x_scale != null ) dx = x_scale.getImageOf( dx );
      if( y_scale != null ) dy = y_scale.getImageOf( dy );
    }
    catch( IllegalArgumentException nde ){ return null; }
    return new DPoint( dx, dy );
  }

  /**
   * returns the visible rectangle in D-coordinates of the shown component
   *
   * return the visible rectangle
   */
  public DRectangle getDRectangle(){
    if( under_construction ) System.out.println("DMeasures.getDRectangle");
    if( sb != null ) return getImageOf( sb.src_rect );
    return ((DArea)comp).getDRectangle();
  }

  /**
   * returns the current Graphics object, which might be used by components to
   * paint themselves
   * the method sets the clipping area of the Graphics object to the currently
   * visible rectangle
   *
   * @return the Graphics object ( or null if no object was set )
   */
  public Graphics configureGraphics(Graphics g){
    if( under_construction ) System.out.println("DMeasures.getGraphics");
    if( g != null ){
      Rectangle what_I_get = g.getClipBounds();
      if (what_I_get!=null) {
    	  Dimension d = comp.getSize();
    	  Insets insets = getInsets();
    	  Rectangle what_I_want = new Rectangle( 
    				  insets.left + 1, // dann sieht man noch was von der linken Achse
    				  insets.top,
    				  d.width - insets.left - insets.right,
    				  d.height - insets.top - insets.bottom); 
    	  what_I_get = what_I_get.intersection(what_I_want);
    	  //g.setColor(Color.red);
    	  //g.fillRect(what_I_get.x, what_I_get.y, what_I_get.width, what_I_get.height);
    	  g.setClip(what_I_get);
      }
      // try this if you have a million core processor
      if(antialias)
    	  ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
    return g;
  }

  /**
   * used by DArea to set a new Graphics object
   */
//  void setGraphics( Graphics g ){
//    if( under_construction ) System.out.println("DMeasures.setGraphics");
//    this.g = g;
//  }

  /**
   * used by ScaledBorder to update the DMeasures object
   *
   * @param c the parent component the border
   */
  void update( Component c, Insets insets ){
    this.comp = c;
    this.insets = insets;
    x_scale = sb.x_scale;
    y_scale = sb.y_scale;
  }

  private Dimension getInner(){
    Dimension d = comp.getSize();
    Insets insets = getInsets();
    d.width -= insets.left + insets.right;
    d.height -= insets.top + insets.bottom;
    return d;
  }
  
  public void setAntialias(boolean a)
  {
	  antialias = a;
  }
  
  public boolean getAntialias()
  {
	  return antialias;
  }
  

  // only to be used by DPointSet!
  public void setAlpha(float f)
  {
	  alpha = f;
  }
  
  public float getAlpha()
  {
	  return alpha;
  }



  /**
   * method returns the source rectangle of the given rectangle
   * they differ if there are scale functions selected which are not the identity
   * if the given rectangle does not belong to the image area of the scale
   * functions, the method returns <code>null</code>
   *
   * @param rect the image rectangle
   * @return the source of it
   */
  DRectangle getSourceOf( DRectangle rect ){
    if( under_construction ) System.out.println("DMeasures.getSourceOf: "+rect);
    if( !getDRectangle().contains( rect ) ) throw
      new IllegalArgumentException("The rectangle lies not in the currently painted rectangle");
    if( x_scale == null && y_scale == null ) return rect;
    if( rect.isEmpty() ) return (DRectangle)rect.clone();
    DPoint p1 = new DPoint( rect.x, rect.y ),
           p2 = new DPoint( rect.x + rect.width, rect.y + rect.height );
    try{
      if( x_scale != null ){
        p1.x = x_scale.getSourceOf( p1.x );
        p2.x = x_scale.getSourceOf( p2.x );
      }
      if( y_scale != null ){
        p1.y = y_scale.getSourceOf( p1.y );
        p2.y = y_scale.getSourceOf( p2.y );
      }
    }
    catch( IllegalArgumentException nde ){ return null; }
    return new DRectangle( p1.x, p1.y, p2.x - p1.x, p2.y - p1.y );
  }

  /**
   * method returns the image rectangle of the given rectangle
   * they differ if there are scale functions selected which are not the identity
   * if the given rectangle does not belong to the defintion area of the scale
   * functions, the method returns <code>null</code>
   *
   * @param rect the source rectangle
   * @return the source of it
   */
  DRectangle getImageOf( DRectangle rect ){
    if( under_construction ) System.out.println("DMeasures.getImageOf: "+rect);
    if( x_scale == null && y_scale == null ) return rect;
    if( rect.isEmpty() ) return (DRectangle)rect.clone();
    DPoint p1 = new DPoint( rect.x, rect.y ),
           p2 = new DPoint( rect.x + rect.width, rect.y + rect.height );
    try{
      if( x_scale != null ){
        p1.x = x_scale.getImageOf( p1.x );
        p2.x = x_scale.getImageOf( p2.x );
      }
      if( y_scale != null ){
        p1.y = y_scale.getImageOf( p1.y );
        p2.y = y_scale.getImageOf( p2.y );
      }
    }
    catch( IllegalArgumentException nde ){ return null; }
    return new DRectangle( p1.x, p1.y, p2.x - p1.x, p2.y - p1.y );
  }

  /**
 */
private Insets getInsets(){
    if( sb != null ) return insets;
    return ((DArea)comp).getInsets();
  }
}

/****************************************************************************
 * END OF FILE
 ****************************************************************************/
