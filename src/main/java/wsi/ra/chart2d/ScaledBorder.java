/**
 *  Filename: $RCSfile: ScaledBorder.java,v $
 *  Purpose:
 *  Language: Java
 *  Compiler: JDK 1.3
 *  Authors:  Fabian Hennecke
 *  Version:  $Revision: 1.6 $
 *            $Date: 2012/10/29 06:19:15 $
 *            $Author: jaeger $
 *            $Revision: 1.6 $
 *            $Date: 2012/10/29 06:19:15 $
 *            $Author: jaeger $
 *  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
 */



package wsi.ra.chart2d;

/*==========================================================================*
 * IMPORTS
 *==========================================================================*/

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

/*==========================================================================*
 * CLASS DECLARATION
 *==========================================================================*/

/**
 */
/**
 * ScaledBorder puts an border around Components
 * ( especially around DrawingAreas ) with scaled and labeled axes
 */
public class ScaledBorder implements Border
{
	protected boolean under_construction = false;

	/**
	 * length of the distance markers on the axes in pixels
	 */
	protected int marker_length = 2;

	/**
	 * length in pixels of the arrows at the ends of the axes
	 */
	protected int arrow_length = 10;

	/**
	 * a flag if the arrows should be visible
	 */
	public boolean show_arrows = true;

	/**
	 * distance between the x-values in digits
	 */
	protected int x_value2value = 2;

	/**
	 * distance between y-label and y-values in digit width
	 */
	protected int y_label2values = 1;

	/**
	 * distance between y-values and y-axis markers in parts of the digit width
	 */
	protected int y_values2marker = 2;

	/**
	 * distance between values and arrows in pixels
	 */
	protected int x_values2arrow = 10 ;
	/**
	 * distance between values and arrows in pixels
	 */
	protected int y_values2arrow = 10 ;

	/**
	 * distance between arrows and outer border
	 */
	protected int axis2border = 4;

	/**
	 * distance between labels and the border in pixels
	 */
	public int x_label2border = 6 ;
	/**
	 * distance between labels and the border in pixels
	 */
	public int y_label2border = 6 ;

	/**
	 * the size of the source rectangle
	 * that means before the values are mdified by scale functions
	 */
	protected DRectangle src_rect;

	/**
	 * the minimal increment of the scales
	 */
	public double minimal_increment;

	/**
	 * the  displayed labels
	 */
	public String x_label;
	/**
	 * the  displayed labels
	 */
	public String y_label;

//	/**
//	 * foreground and background colors
//	 */
//	public Color foreground;
//	/**
//	 * foreground and background colors
//	 */
//	public Color background;

	/**
	 * the border which is shown around the scaled border
	 */
	protected Border outer_border;

	/**
	 * flag if the outer border should be displayed
	 */
	boolean show_outer_border = true;

	/**
	 * scale functions if, for example, an logarithmic function is needed instead
	 * of a linear.
	 */
	public DFunction x_scale;
	/**
	 * scale functions if, for example, an logarithmic function is needed instead
	 * of a linear.
	 */
	public DFunction y_scale;

	private boolean antialias = false;

	/**
	 * hash maps for customizable axis labeling (coordinate => label)
	 * added 080613 by pbruns
	 */
	protected Map<Double, String> x_labeling;
	protected Map<Double, String> y_labeling;

	/** minimum / maximum values to be drawn on the axis (even though the real axis range might be larger) */
	private double min_x_draw, max_x_draw, min_y_draw, max_y_draw;

	/**
	 * formatters of the x- and y-axis numbers
	 * @see java.text.NumberFormat
	 */
	public NumberFormat format_x = new DecimalFormat ( ) ;
	/**
	 * formatters of the x- and y-axis numbers
	 * @see java.text.NumberFormat
	 */
	public NumberFormat format_y = new DecimalFormat ( ) ;

	protected double src_dX = - 1 ;
	protected double src_dY = - 1 ;

	protected boolean do_refresh;
	protected boolean auto_scale_x = true ;
	protected boolean auto_scale_y = true ;

	protected Insets old_insets;

	protected DMeasures m;

	protected boolean draw = true;

	/**
	 * constructor creates a default ScaledBorder inside of a lowered BevelBorder
	 */
	public ScaledBorder(){
		this(
				//      BorderFactory.createBevelBorder(
				//        BevelBorder.LOWERED,
				//        Color.white,
				//        Color.lightGray,
				//        Color.black,
				//        Color.lightGray
				//      )
				BorderFactory.createEmptyBorder()
		);
		min_x_draw = min_y_draw = Double.NEGATIVE_INFINITY;
		max_x_draw = max_y_draw = Double.POSITIVE_INFINITY;    
	}
	/**
	 * constructor creates a new <code>ScaledBorder<code/>
	 * surrounded by the specified <code>Border<code/>
	 */
	public ScaledBorder( Border outer ){
		outer_border = outer;
		m = new DMeasures( this );
	}

	/**
	 * method tells the border to calculate the differences between displayed
	 * x-values by itself
	 */
	public void setAutoScaleX(){ auto_scale_x = true; }

	/**
	 * method tells the border to calculate the differences between displayed
	 * y-values by itself
	 */
	public void setAutoScaleY(){ auto_scale_y = true; }

	/**
	 * method sets the differences between source x-values of the displayed values
	 * @see setAutoScaleX().
	 * If scale functions are used there might be a difference between the shown values
	 * and the source values
	 */
	public void setSrcdX(double dX){
		auto_scale_x = false;
		src_dX = dX;
	}

	/**
	 * method sets the differences between source y-values of the displayed values
	 * @see setAutoScaleY().
	 * If scale functions are used there might be a difference between the shown values
	 * and the source values
	 */
	public void setSrcdY(double dY){
		auto_scale_y = false;
		src_dY = dY;
	}

	public void setAntialias(boolean a)
	{
		antialias = a;
	}

	public boolean getAntialias()
	{
		return antialias;
	}

	public void setVisible(boolean visible) {
		draw = visible;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height){
		
		if (!draw)
			return;

		if(antialias)
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if( under_construction ) System.out.println("ScaledBorder.paintBorder()");
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

		Dimension d = new Dimension(c.getWidth(), c.getHeight());
		Dimension cd = new Dimension( d.width - inner_insets.left - inner_insets.right,
				d.height - inner_insets.top - inner_insets.bottom);
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
			//AffineTransform aff = AffineTransform.getRotateInstance(Math.toRadians(-90.0), y_label2border + fm.getAscent(), inner_insets.top + ( cd.height + yld.height )/ 2);
			//AffineTransform oldAff = g2.getTransform();
			AffineTransform T = new AffineTransform(0, -1, 1, 0, 0, 0);
			Font old = g.getFont(), f = old.deriveFont( T );
			g.setFont( f );
			//g2.setTransform(aff);
			g2.drawString( y_label, y_label2border + fm.getAscent(), inner_insets.top + ( cd.height + yld.height )/ 2 );
			//g2.setTransform(oldAff);
			g.setFont( old );
		}

		if( x_label != null )
			g.drawString(
					x_label, inner_insets.left + ( cd.width - fm.stringWidth( x_label ) ) / 2,
					d.height - outer_insets.bottom - x_label2border - fm.getDescent() );

		if( src_rect.x == 0 && src_rect.y == 0 ){
			int v2m = fm.stringWidth("0") / y_values2marker;
			g.drawString( "0", inner_insets.left - fm.stringWidth( "0" ) - v2m - marker_length,
					inner_insets.top + cd.height + fontAsc );
			g.drawLine( inner_insets.left, inner_insets.top + cd.height + fm.getAscent(),
					inner_insets.left, inner_insets.top + cd.height);
			g.drawLine( inner_insets.left, inner_insets.top + cd.height,
					inner_insets.left - fm.stringWidth( "0" ) - v2m - marker_length,
					inner_insets.top + cd.height );
		}

		drawYValues( g, inner_insets, cd );
		drawXValues( g, inner_insets, cd );

		g.setColor( old_color );
	}

	private void drawYValues( Graphics g, Insets insets, Dimension cd ){
		if( under_construction ) System.out.println("ScaledBorder.drawYValues()");
		FontMetrics fm = g.getFontMetrics();
		int n, fontAsc = fm.getAscent(), v2m = fm.stringWidth("0") / y_values2marker;
		n = (int)( src_rect.y / src_dY );
		if( n * src_dY < src_rect.y || ( src_rect.x == 0 && src_rect.y == 0 ) ) n++;


		double v, minx = src_rect.x;
		if( x_scale != null ) minx = x_scale.getImageOf( minx );
		for(; (v = n * src_dY) <= src_rect.y + src_rect.height; n++ ){
			if( y_scale != null ) v = y_scale.getImageOf( v );
			String text = format_y.format(v);
			Point p = m.getPoint( minx, v );
			if(y_labeling != null)
			{
				//avoid round errors for double values
				v = Math.round(v * 1000000.) / 1000000.;
				
				if(y_labeling.containsKey(v))
				{
					text = y_labeling.get(v);
					g.drawString(text,
							insets.left - fm.stringWidth( text ) - v2m - marker_length,
							p.y  + fontAsc / 2 );
					g.drawLine( insets.left - marker_length, p.y, insets.left, p.y );
				}
			}
			else if(v >= min_y_draw && v <= max_y_draw)
			{
				try{ v = format_y.parse(text).doubleValue(); }
				catch( java.text.ParseException ex ){ }

				if( p != null ){
					g.drawString( text,
							insets.left - fm.stringWidth( text ) - v2m - marker_length,
							p.y  + fontAsc / 2 );
					g.drawLine( insets.left - marker_length, p.y, insets.left, p.y );
				}
			}
		}
	}

	public double getSrcdY( FontMetrics fm, Dimension cd ){
		if( under_construction ) System.out.println("ScaledBorder.getSrcdY()");
		if( (!do_refresh && src_dY != -1) || !auto_scale_y ) return src_dY;
		int max = cd.height / fm.getHeight();
		double minsrc_dY = 2 * src_rect.height / max; // die 2 einfach mal so eingesetzt   <--------------------------
		src_dY = aBitBigger( minsrc_dY );
		if( src_dY < minimal_increment ) src_dY = minimal_increment;
		return src_dY;
	}

	private void drawXValues( Graphics g, Insets insets, Dimension cd ){
		Graphics2D g2 = (Graphics2D)g;
		if( under_construction ) System.out.println("ScaledBorder.drawXValues()");
		Font old = null, f = null;
		FontMetrics fm = g2.getFontMetrics();
		double mx = cd.width / src_rect.width;
		int n, labelX,
		xnull = insets.left + (int)( - src_rect.x * mx );

		n = (int)( src_rect.x / src_dX );
		if( n * src_dX < src_rect.x || ( src_rect.x == 0 && src_rect.y == 0 ) ) n++;

		int fontAsc = fm.getAscent(), xLineY = insets.top + cd.height;
		labelX = xnull + (int)(n * src_dX * mx);
		while( n * src_dX <= src_rect.x + src_rect.width ){
			double v = n * src_dX;
			if( x_scale != null ) v = x_scale.getImageOf(v);
			String text = format_x.format(v);
			if(x_labeling != null)
			{
				//GJ: removed -> see below!
				AffineTransform T = new AffineTransform(0, -1, 1, 0, 0, 0);
				old = g2.getFont();
				f = old.deriveFont( T );
				g2.setFont(f);
				
				//avoid round errors for double values
				v = Math.round(v * 1000000.) / 1000000.;
				
				if(x_labeling.containsKey(v))
				{
					text = x_labeling.get(v);
					int strW = fm.stringWidth( text );
					int strH = fm.getMaxAscent();
					int ypos = xLineY + strW + fontAsc;
					//GJ (20.11.2014): since rotating the font did not work on mac, we now rotate the canvas
					//AffineTransform aff = AffineTransform.getQuadrantRotateInstance(1);
					//AffineTransform oldAff = g2.getTransform();
					//g2.setTransform(aff);
					
					g2.drawString(text, labelX+strH/2, ypos);
					//g2.setTransform(oldAff);
					g2.setFont(old);
					g2.drawLine( labelX, xLineY, labelX, xLineY + marker_length );
				}
			}
			else if(v >= min_x_draw && v <= max_x_draw)
			{
				try{ v = format_x.parse(text).doubleValue(); }
				catch( java.text.ParseException ex ){ }
				int strW = fm.stringWidth( text );
				g2.drawString( text, labelX - strW / 2, xLineY + fontAsc );
				g2.drawLine( labelX, xLineY, labelX, xLineY + marker_length );
			}
			n++;
			labelX = xnull + (int)( n * src_dX * mx);
		}
	}

	public double getSrcdX( FontMetrics fm, Dimension cd ){
		if( under_construction ) System.out.println("ScaledBorder.getSrcdX()");
		if( (!do_refresh && src_dX != - 1) || !auto_scale_x ) return src_dX;
		int digit_width = fm.stringWidth("0"),
		max = cd.width / ( digit_width * ( x_value2value + 1 ) );
		src_dX = src_rect.width / max;
		int n, labelX, olsrc_dX;

		boolean ok = false;
		while( !ok ){
			src_dX = aBitBigger( src_dX );

			n = (int)( src_rect.x / src_dX );
			if( n * src_dX < src_rect.x ) n++;

			olsrc_dX = 0;

			boolean suits = true, first = true;
			while( suits && n * src_dX <= src_rect.x + src_rect.width ){
				double v = n * src_dX;
				if( x_scale != null ) v = x_scale.getImageOf( v );
				String text = format_x.format(v);
				int strW = fm.stringWidth( text );
				labelX = (int)((( n * src_dX - src_rect.x ) / src_rect.width ) * cd.width ) - strW / 2;
				if( !first && labelX <= olsrc_dX + digit_width * x_value2value ) suits = false;
				else{
					olsrc_dX = labelX + strW;
					n++;
				}
				first = false;
			}
			if( !suits ) ok = false;
			else ok = true;
		}
		if( src_dX < minimal_increment ) src_dX = minimal_increment;
		return src_dX;
	}

	/**
	 * method returns to a certain minimal value the next higher value which can be
	 * displayed, which looks a bit nicer
	 * it returns values like ... 0.05, 0.1, 0.2, 0.5, 1, 2, 5, 10, ...
	 *
	 * @param the double value next to which the displayable value should be found
	 * @return the displayable value
	 */
	public static double aBitBigger( double min ){
		if( min <= 0 ) return 1;
		double d = 1;
		if( min < d ){
			while( d * .5 > min ) {
				d *= .5;
				if( d * .4 > min ) d *= .4;
				if( d * .5 > min ) d *= .5;
			}
		}
		else{
			while( d <= min && !Double.isInfinite(d)) {
				d *= 2;
				if( d <= min ) d *= 2.5;
				if( d <= min ) d *= 2;
			}
		}
		return d;
	}

	public boolean isBorderOpaque(){
		return outer_border.isBorderOpaque();
	}

	//  private String stringOf( double v ){
	//    if( (int)v == v ) return String.valueOf( (int)v );
	//    return String.valueOf( v );
	//  }

	public Insets getBorderInsets(Component c) {
		if( under_construction ) System.out.println("ScaledBorder.getBorderInsets()");
		if( !do_refresh && old_insets != null ) return old_insets;

		Graphics g = c.getGraphics();

		Insets insets = new Insets(0, 0, 0, 0);
		if( show_outer_border ) insets = outer_border.getBorderInsets( c );

		if( g == null ) return insets;

		FontMetrics fm = g.getFontMetrics();
		int //fontAsc = fm.getAscent(),
		fontHeight = fm.getHeight(),
		digit_width = fm.stringWidth("0");

		if( c instanceof DArea ){
			DArea area = (DArea)c;
			DMeasures m = area.getDMeasures();
			DRectangle rect = m.getSourceOf( area.getDRectangle() );
			src_rect = (DRectangle)rect.clone();
			x_scale = area.getDMeasures().x_scale;
			y_scale = area.getDMeasures().y_scale;
		}

		// left:
			if( y_label != null ) insets.left += fm.getAscent() + fm.getDescent();
			insets.left += y_label2values * digit_width;
			getSrcdY( fm, c.getSize() );
			int n, maxWidth = 0;
			n = (int)( src_rect.y / src_dY );
			if( n * src_dY < src_rect.y ) n++;
			while( n * src_dY <= src_rect.y + src_rect.height ){
				double v = n * src_dY;
				if( y_scale != null ) v = y_scale.getImageOf( v );
				int w = fm.stringWidth( format_y.format(v) );
				if( w > maxWidth ) maxWidth = w;
				n++;
			}
			insets.left += 1 + y_label2border + maxWidth + digit_width / y_values2marker + marker_length;
			int max_str_len = 0;
			if(y_labeling != null)
			{
				for(String str : y_labeling.values())
					max_str_len = Math.max(max_str_len, fm.stringWidth(str));
			}
			insets.left += max_str_len;

			// bottom:
				insets.bottom += 1 + fontHeight + x_label2border;
				max_str_len = 0;
				if(x_labeling != null)
				{
					for(String str : x_labeling.values())
						max_str_len = Math.max(max_str_len, fm.stringWidth(str));
				}
				insets.bottom += max_str_len;
				if( x_label != null ) insets.bottom += fontHeight;

				// top:
				if( show_arrows ) insets.top += y_values2arrow + arrow_length;
				insets.top += axis2border;

				// right:
				if( show_arrows ) insets.right += x_values2arrow + arrow_length;
				insets.right += axis2border;
				getSrcdX( fm, c.getSize() );
				n = (int)( src_rect.x + src_rect.width / src_dX );
				if( n < 0 ) n ++;
				int w = fm.stringWidth( format_x.format(n * src_dX) );
				if( w / 2 > insets.right ) insets.right = w / 2;

				old_insets = insets;
				return insets;
	}

	public void setYLabeling(Map<Double, String> map)
	{
		y_labeling = map;
	}

	public void setXLabeling(Map<Double, String> map)
	{
		x_labeling = map;
	}

	public void setYScalingRange(double min, double max)
	{
		min_y_draw = min;
		max_y_draw = max;
	}

	public void setXScalingRange(double min, double max)
	{
		min_x_draw = min;
		max_x_draw = max;
	}
}

/****************************************************************************
 * END OF FILE
 ****************************************************************************/
