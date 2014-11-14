/**
 *  Filename: $RCSfile: DMouseZoom.java,v $
 *  Purpose:
 *  Language: Java
 *  Compiler: JDK 1.3
 *  Authors:  Fabian Hennecke
 *  Version:  $Revision: 1.1 $
 *            $Date: 2008/12/02 15:27:26 $
 *            $Author: battke $
 *  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
 */

package wsi.ra.chart2d;

/*==========================================================================*
 * IMPORTS
 *==========================================================================*/

import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/*==========================================================================*
 * CLASS DECLARATION
 *==========================================================================*/

/**
 */
public class DMouseZoom extends DRectangle implements MouseMotionListener, MouseListener
{
  DArea area;
  private DPoint start;
  private int zoomIn  = InputEvent.BUTTON1_MASK; // left
  private int zoomOut = InputEvent.BUTTON3_MASK; // right
  //private int getInfo = InputEvent.BUTTON2_MASK; // middle


  public DMouseZoom( DArea area ) {
    super( 0, 0, 0, 0 );
    setVisible( false );
    this.area = area;
    area.addMouseListener( this );
    area.addMouseMotionListener( this );
    color = Color.yellow;
  }

  /**
   * Sets the zoom mouse flags from the type InputEvent, e.g. InputEvent.BUTTON1_MASK.
   */
  public void setZoomFlags(int zoomIn, int zoomOut){
    this.zoomIn  = zoomIn;
    this.zoomOut = zoomOut;
  }
  
  /**
 */
/**
   * Returns the drawing area.
   *
   * @return drawing area
   * @author Igor Fischer
   */
  protected DArea getArea() { return area; }

  /**
   * Invoked when the mouse has been clicked on a component.
   */
  public void mouseClicked(MouseEvent e) {

    DRectangle r = area.getDRectangle();
    if( ( e.getModifiers() & zoomIn ) > 0 ){
      DMeasures m = area.getDMeasures();
      DPoint c = m.getDPoint( e.getPoint() );
      r.x = c.x - r.width * .5;
      r.y = c.y - r.height * .5;
    }
    else if( ( e.getModifiers() & zoomOut) > 0 ){
      r.x -= r.width * .5;
      r.y -= r.height * .5;
      r.width *= 2;
      r.height *= 2;
    }
    area.setVisibleRectangle( r );
  }

  /**
   * Invoked when a mouse button has been pressed on a component.
   */
  public void mousePressed(MouseEvent e) {
    if( (e.getModifiers() & zoomIn ) < 1 ) return;
    setVisible( true );
    DMeasures m = area.getDMeasures();
    DPoint p = m.getDPoint( e.getPoint() );
    start = new DPoint( p.x, p.y );
    x = p.x;
    y = p.y;
    width = 0;
    height = 0;
    area.addDElement( this );
  }

  /**
   * Invoked when a mouse button has been released on a component.
   */
  public void mouseReleased(MouseEvent e) {
    if( isVisible() )
      area.setVisibleRectangle( this );
    setVisible( false );
    area.removeDElement( this );
  }

  public void mouseExited( MouseEvent e ){
    setVisible( false );
  }

  public void mouseEntered( MouseEvent e ){
    setVisible( true );
  }

  /**
   * Invoked when a mouse button is pressed on a component and then
   * dragged.  Mouse drag events will continue to be delivered to
   * the component where the first originated until the mouse button is
   * released (regardless of whether the mouse position is within the
   * bounds of the component).
   */
  public void mouseDragged(MouseEvent e){
    if( !isVisible() ) return;
    DMeasures m = area.getDMeasures();
    DPoint p = m.getDPoint( e.getPoint() );
    width = p.x - start.x;
    height = p.y - start.y;
    if( width < 0 ){
      x = p.x;
      width *= -1;
    }
    if( height < 0 ){
      y = p.y;
      height *= -1;
    }
    area.repaint();
  }

  /**
   * Invoked when the mouse button has been moved on a component
   * (with no buttons no down).
   */
  public void mouseMoved(MouseEvent e){}
}

/****************************************************************************
 * END OF FILE
 ****************************************************************************/
