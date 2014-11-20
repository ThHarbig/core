



package wsi.ra.chart2d;

/*==========================================================================*
 * IMPORTS
 *==========================================================================*/

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;


/*==========================================================================*
 * CLASS DECLARATION
 *==========================================================================*/

/**
 */
/**
 * ScaledBorder puts an border around Components
 * ( especially around DrawingAreas ) with scaled and labeled axes
 */
/**
 * @author Nastasja Trunk
 * the inverse Scaled Border is for mostly my use because I need another kind of labels on the y-axis
 * It is build with the ScaledBorder class written by 
 *
 */
public class InverseScaledBorder extends ScaledBorder
{

  private double max;
  
  /**
   * This Contructor constructs a Scaled Border with an inverse Y-Axis which just displays inverse values
   * all values are diplayed as max-value
 * @param max
 */
public InverseScaledBorder(){
	  super();
	 
  }
  
  /* (non-Javadoc)
 * @see wsi.ra.chart2d.ScaledBorder#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int)
 * This function was taken from the file above and only the displayed values are changed
 */
@Override
public void paintBorder(Component c, Graphics g, int x, int y, int width, int height){
    if( under_construction ) System.out.println("InverseScaledBorder.paintBorder()");
    Color foreground = c.getForeground();
    Color background = c.getBackground();
    Color old_color = g.getColor();
    g.setColor( background );
    g.fillRect( x, y, width, height );
    g.setColor( old_color );

    Insets outer_insets = new Insets( 0, 0, 0, 0);// insets of the outer border
    if( show_outer_border ) {
      outer_border.paintBorder( c, g, x, y, width, height );
      outer_insets = outer_border.getBorderInsets( c );
    }

    do_refresh = true;
    Insets inner_insets = getBorderInsets(c);

    Dimension d = c.getSize(),
              cd = new Dimension( d.width - inner_insets.left - inner_insets.right,
                                  d.height - inner_insets.top - inner_insets.bottom );
    FontMetrics fm = g.getFontMetrics();
    int fontAsc = fm.getAscent();
    do_refresh = false;

    m.update(c, inner_insets);

    // axes
    g.setColor( foreground );
    g.drawLine( inner_insets.left, inner_insets.top,
                inner_insets.left, inner_insets.top + cd.height );
    g.drawLine( inner_insets.left, inner_insets.top + cd.height,
                inner_insets.left + cd.width, inner_insets.top + cd.height );

    if( show_arrows ){
      g.drawLine( inner_insets.left, inner_insets.top,
                  inner_insets.left, inner_insets.top - y_values2arrow );
      g.drawLine( inner_insets.left - marker_length, inner_insets.top - y_values2arrow,
                  inner_insets.left, inner_insets.top - y_values2arrow - arrow_length );
      g.drawLine( inner_insets.left + marker_length, inner_insets.top - y_values2arrow,
                  inner_insets.left, inner_insets.top - y_values2arrow - arrow_length);
      g.drawLine( inner_insets.left - marker_length, inner_insets.top - y_values2arrow,
                  inner_insets.left + marker_length, inner_insets.top - y_values2arrow );

      g.drawLine( inner_insets.left + cd.width , inner_insets.top + cd.height,
                  inner_insets.left + cd.width + x_values2arrow, inner_insets.top + cd.height );
      g.drawLine( inner_insets.left + cd.width + x_values2arrow,
                  inner_insets.top + cd.height - marker_length,
                  inner_insets.left + cd.width + x_values2arrow + arrow_length,
                  inner_insets.top + cd.height );
      g.drawLine( inner_insets.left + cd.width + x_values2arrow,
                  inner_insets.top + cd.height + marker_length,
                  inner_insets.left + cd.width + x_values2arrow + arrow_length,
                  inner_insets.top + cd.height );
      g.drawLine( inner_insets.left + cd.width + x_values2arrow,
                  inner_insets.top + cd.height - marker_length,
                  inner_insets.left + cd.width + x_values2arrow,
                  inner_insets.top + cd.height + marker_length );
    }

    if( y_label != null ) {
    	Dimension yld = new Dimension(fm.getAscent()+fm.getDescent(), fm.stringWidth(y_label));
      
    	
    	//GJ (20.11.2014): since rotating the font did not work on mac, we now rotate the canvas
    	Graphics2D g2 = (Graphics2D)g;
    	AffineTransform aff = AffineTransform.getRotateInstance(Math.toRadians(-90.0), y_label2border + fm.getAscent(), inner_insets.top + ( cd.height + yld.height )/ 2);
		AffineTransform oldAff = g2.getTransform();
      
//		AffineTransform T = new AffineTransform(0, -1, 1, 0, 0, 0);
//		Font old = g.getFont(), f = old.deriveFont( T );
//		g.setFont( f );
		g2.setTransform(aff);
		g.drawString( y_label, y_label2border + fm.getAscent(), inner_insets.top + ( cd.height + yld.height )/ 2 );
		g2.setTransform(oldAff);
//      g.setFont( old );
    }

    if( x_label != null )
      g.drawString(
        x_label, inner_insets.left + ( cd.width - fm.stringWidth( x_label ) ) / 2,
        d.height - outer_insets.bottom - x_label2border - fm.getDescent() );

    if( src_rect.x == 0 && src_rect.y == 0 ){
      int v2m = fm.stringWidth(((Double) max).toString()) / y_values2marker;
      g.drawString("", inner_insets.left - fm.stringWidth( "0" ) - v2m - marker_length,
                         inner_insets.top + cd.height + fontAsc );
      //g.drawLine( inner_insets.left, inner_insets.top + cd.height + fm.getAscent(),
      //            inner_insets.left, inner_insets.top + cd.height);
     // g.drawLine( inner_insets.left, inner_insets.top + cd.height,
     //             inner_insets.left - fm.stringWidth( "0" ) - v2m - marker_length,
     //             inner_insets.top + cd.height );
    }

    //drawYValues( g, inner_insets, cd );
    //drawXValues( g, inner_insets, cd );

    g.setColor( old_color );
  }

 
 
}

/****************************************************************************
 * END OF FILE
 ****************************************************************************/
