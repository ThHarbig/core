/**
 *  Filename: $RCSfile: DPeakSet.java,v $
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

/*==========================================================================*
 * CLASS DECLARATION
 *==========================================================================*/

public class DPeakSet extends DContainer
{
  private double width;
  private Color fill_color = Color.lightGray;

  public DPeakSet( double width ){
    this( width, 5 );
  }

  public DPeakSet( double width, int initial_capacity ) {
    super( initial_capacity );
    if( width <= 0 ) throw
      new IllegalArgumentException("The width of the peaks has to be a positive value");
    this.width = width;
  }

  public void setFillColor( Color fill_color ){
    boolean changed = this.fill_color == fill_color;
    this.fill_color = fill_color;
    for( int i=0; i<elements.size(); i++ ){
      DElement e = (DElement)elements.get(i);
      if( e instanceof DRectangle ) ((DRectangle)e).fillColor = fill_color;
    }
    if( changed ) repaint();
  }

  public Color getFillColor(){ return fill_color; }

  public void addValue( double v ){
    boolean found = false;
    for( int i=0; i<elements.size() && !found; i++ ){
      DElement e = (DElement)elements.get(i);
      if( e instanceof DCountingPeak ){
        DCountingPeak p = (DCountingPeak)e;
        if( p.addValue( v ) ) {
          found = true;
          if( p.height > rectangle.height ) {
            rectangle.height = p.height;
            repaint();
          }
        }
      }
    }
    if( found ) return;
    double min = (int)( v / width );
    DCountingPeak p = new DCountingPeak( min, width );
    p.setColor( color );
    p.setFillColor( fill_color );
    p.addValue();
    addDElement( p );
  }
}

/**
 * this class can be used as part of a peak diagramm
 */
class DCountingPeak extends DRectangle{

  public DCountingPeak( double minx, double width ) {
    super( minx, 0, width, 0 );
    color = Color.gray;
    fillColor = Color.lightGray;
  }

  public boolean addValue(){ height++; repaint(); return true; }

  public boolean addValue( double v ){
    if( v >= x && v < x + width ){
      height++;
      repaint();
      return true;
    }
    return false;
  }

  public void reset(){
    if( height == 0 ) return;
    height = 0;
    repaint();
  }
}

/****************************************************************************
 * END OF FILE
 ****************************************************************************/
