/**
 *  Filename: $RCSfile: DPointSet.java,v $
 *  Purpose:
 *  Language: Java
 *  Compiler: JDK 1.3
 *  Authors:  Fabian Hennecke
 *  Version:  $Revision: 1.9 $
 *            $Date: 2012/02/20 10:03:57 $
 *            $Author: battke $
 *  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
 */

package wsi.ra.chart2d;

/*==========================================================================*
 * IMPORTS
 *==========================================================================*/

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import wsi.ra.tool.IntegerArrayList;

/*==========================================================================*
 * CLASS DECLARATION
 *==========================================================================*/

/**
 */
public class DPointSet extends DComponent
{
	protected DPointIcon icon = null;

	/*-------------------------------------------------------------------------*
	 * private member variables
	 *-------------------------------------------------------------------------*/

	protected DIntDoubleMap x;
	/*-------------------------------------------------------------------------*
	 * private member variables
	 *-------------------------------------------------------------------------*/

	protected DIntDoubleMap y;
	protected DIntDoubleMap s; // optional use!
	protected ArrayList<Object> o; // optional use!
	protected boolean connected;
	protected Stroke stroke = new BasicStroke();
	protected JumpManager jumper = new JumpManager();
	protected Float fixedAlpha = null; // if set, overrides the alpha value of the DMeasures object passed to paint

	protected GraphicsModifier afterJumpModifier;

	/*-------------------------------------------------------------------------*
	 * constructor
	 *-------------------------------------------------------------------------*/
	public DPointSet(){
		this(10, 2);
	}


	public DPointSet( int initial_capacity ){
		this(initial_capacity, 2);
	}

	public DPointSet( int initial_capacity, int length_multiplier ){
		this( new DArray(initial_capacity, length_multiplier),
				new DArray(initial_capacity, length_multiplier),
				new DArray(initial_capacity, length_multiplier), 
				null);
	}

	public DPointSet(DIntDoubleMap x_values, DIntDoubleMap y_values)
	{
		this(x_values, y_values, null, null);
	}

	public DPointSet(DIntDoubleMap x_values, DIntDoubleMap y_values, DIntDoubleMap s_values, ArrayList<Object> o_values){
		if( x_values.getSize() != y_values.getSize() ) throw
		new IllegalArgumentException(
				"The number of x-values has to be the same than the number of y-values"
		);
		x = x_values;
		y = y_values;
		s = s_values;
		if(o_values == null)
			o = new ArrayList<Object>();
		else 
			o = o_values;
		restore();
		setDBorder(new DBorder(1,1,1,1));
	}

	/*-------------------------------------------------------------------------*
	 * public methods
	 *-------------------------------------------------------------------------*/

	public Color modifyColor(Color prev_color, double multAlpha) {
		return new Color(prev_color.getRed(),prev_color.getGreen(),prev_color.getBlue(),(int)(prev_color.getAlpha()*multAlpha));
	}
	
	public void paint( Graphics gg, DMeasures m ){
		//    Graphics2D g = (Graphics2D)m.getGraphics();
		Graphics2D g = (Graphics2D)gg;

		Point zerozero = m.getPoint(0,0);
		Point oneone = m.getPoint(1000,1000);
		double xscale = oneone.x - zerozero.x;
		double yscale = oneone.y - zerozero.y;
		xscale /= 1000.0;
		yscale /= 1000.0;

//		((Graphics2D) g).setComposite( AlphaComposite.getInstance( AlphaComposite.SRC,m.getAlpha()));
		float alpha = m.getAlpha();
		if (fixedAlpha!=null)
			alpha = fixedAlpha;
		
		Color prev_color = color;
		if (prev_color==null)
			prev_color = g.getColor();		
		Color transp_col = modifyColor(prev_color, alpha);
		g.setColor(transp_col);		
		
		g.setStroke(stroke);
//		if( color != null ) 
//			g.setColor( color );
		int size = getSize();
		if( connected && size > 1 ){
			jumper.restore();
			while( jumper.hasMoreIntervals() ){
				int[] interval = jumper.nextInterval();
				Point p1 = null, p2;
				// 081008 fb: allow modification of the graphics object after each jump         
				if (afterJumpModifier!=null && interval[0]>=0 && interval[0]<o.size()) {
					afterJumpModifier.modify(g, o.get(interval[0]));
					Color c = g.getColor();
					if (c!=transp_col) {
						transp_col = modifyColor(c, alpha);
						g.setColor(transp_col);
					}
				}
				for( int i=interval[0]; i<interval[1]; i++ ){
					p2 = m.getPoint( x.getImage(i), y.getImage(i) );
					if( p1 != null)
						g.drawLine( p1.x, p1.y, p2.x, p2.y );
					if( icon != null ){
						// 090325 fb : also scale the coordinate space for icons now, if they don't object 
						g.setStroke( new BasicStroke(0.0f) ); // thinnest possible line (e.g. one pixel on screen, regardless of scaling)
						AffineTransform trans = g.getTransform();
						g.translate(p2.x, p2.y);
						if (!icon.wantDeviceCoordinates()) {
							g.scale(xscale, yscale); // first scale, THEN translate
						}
						icon.paint(g);
						g.setTransform(trans);
						g.setStroke( stroke );
					}
					p1 = p2;
				}
			}
		}
		else{
			Point p;
			for( int i=0; i<size; i++ ){
				p = m.getPoint( x.getImage(i), y.getImage(i) );
				// 081008 fb: allow modification of the graphics object after each jump
				if (afterJumpModifier!=null) {
					afterJumpModifier.modify(g, o.get(i));
					Color c = g.getColor();
					if (c!=transp_col) {
						transp_col = modifyColor(c, alpha);
						g.setColor(transp_col);
					}
				}
				if( icon == null ){
					g.drawLine(p.x - 1, p.y - 1, p.x + 1, p.y + 1);
					g.drawLine(p.x + 1, p.y - 1, p.x - 1, p.y + 1);
				}
				else{
					// 090325 fb : also scale the coordinate space for icons now, if they don't object 
					g.setStroke( new BasicStroke(0.0f) ); // thinnest possible line (e.g. one pixel on screen, regardless of scaling)
					AffineTransform trans = g.getTransform();
					g.translate(p.x, p.y);
					if (!icon.wantDeviceCoordinates()) {
						g.scale(xscale, yscale); 
					}
					icon.paint(g);
					g.setTransform(trans);
					g.setStroke(stroke); // added for symmetry with the above code
				}
			}
		}
		g.setStroke( new BasicStroke() );
		g.setColor(prev_color);
//		((Graphics2D) g).setComposite( AlphaComposite.getInstance( AlphaComposite.SRC,1f));
	}

	public void addDPoint( DPoint p ){
		x.addImage(p.x);
		y.addImage(p.y);
		s.addImage(p.s);
		o.add(p.o);
		rectangle.insert(p);
		repaint();
	}

	public void addDPoint( double x, double y ){
		addDPoint(new DPoint(x, y));
	}

	public void addDPoint(double x, double y, double s, Object o)
	{
		addDPoint(new DPoint(x, y, s, o));
	}

	/**
	 * method causes the DPointSet to interupt the connected painting at the
	 * current position
	 */
	public void jump(){
		jumper.addJump();
	}

	/**
	 * method removes all jump positions
	 * if the DPointSet is connected, all points will be painted connected to
	 * their following point
	 */
	public void removeJumps(){
		jumper.reset();
	}

	/**
	 * method returns the DPoint at the given index
	 *
	 * @param index the index of the DPoint
	 * @return the DPoint at the given index
	 */
	public DPoint getDPoint( int index ){
		if( index >= x.getSize() ) {
			System.out.println("getDPoint() index"+index);
			System.out.println("x.getSize() "+x.getSize());
			throw new ArrayIndexOutOfBoundsException(index);
		}
		DPoint p = new DPoint( x.getImage( index ), y.getImage( index ), s.getImage( index ), o.get(index) );
		p.setIcon( icon );
		p.setColor( color );
		return p;
	}

	/**
	 * method puts the given DPoint at the given position in the set
	 *
	 * @param index the index of the point
	 * @param p     the point to insert
	 */
	public void setDPoint( int index, DPoint p ){
		if( index >= x.getSize() ) throw new ArrayIndexOutOfBoundsException(index);
		rectangle.insert(p);
		x.setImage(index,p.x);
		y.setImage(index,p.y);
		s.setImage(index, p.s);
		o.set(index, p.o);
		restore();
		repaint();
	}

	/**
	 */
	/**
	 * method sets an icon for a better displaying of the point set
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
	 * method returns the current icon of the point set
	 *
	 * @return the DPointIcon
	 */
	public DPointIcon getIcon(){
		return icon;
	}

	/**
	 */
	/**
	 *  method sets the stroke of the line
	 *  if the points were not connected, they now will be connected
	 *
	 * @param s the new stroke
	 */
	public void setStroke( Stroke s ){
		if( s == null ) s = new BasicStroke();
		stroke = s;
		repaint();
	}

	/**
	 */
	/**
	 * method returns the current stroke of the line
	 *
	 * @return the stroke
	 */
	public Stroke getStroke(){
		return stroke;
	}

	/**
	 */
	public void setConnected( boolean aFlag ){
		boolean changed = !( aFlag == connected );
		connected = aFlag;
		if( changed ) repaint();
	}

	public void removeAllPoints(){
		if( x.getSize() == 0 ) return;
		x.reset();
		y.reset();
		jumper.reset();
		repaint();
		rectangle = DRectangle.getEmpty();
	}

	@Override
	public String toString(){
		String text = "wsi.ra.chart2d.DPointSet[size:"+getSize();
		for( int i=0; i<x.getSize(); i++ )
			text += ",("+x.getImage(i)+","+y.getImage(i)+")";
		text += "]";
		return text;
	}

	/**
	 * method returns the index to the nearest <code>DPoint</code> in this <code>DPointSet</code>.
	 *
	 * @return the index to the nearest <code>DPoint</code>. -1 if no nearest <code>DPoint</code> was found.
	 */
	public int getNearestDPointIndex(DPoint point){
		double minValue = Double.MAX_VALUE;
		int    minIndex = -1;
		for( int i=0; i<x.getSize(); i++ ){
			double dx = point.x - x.getImage(i);
			double dy = point.y - y.getImage(i);
			double dummy = dx*dx + dy*dy;
			if (dummy < minValue){
				minValue = dummy;
				minIndex = i;
			}
		}
		return minIndex;
	}

	/**
	 * method returns the nearest <code>DPoint</code> in this <code>DPointSet</code>.
	 *
	 * @return the nearest <code>DPoint</code>
	 */
	public DPoint getNearestDPoint(DPoint point){
		int    minIndex = getNearestDPointIndex(point);

		if(minIndex == -1) return null;
		else return new DPoint(x.getImage(minIndex), y.getImage(minIndex));
	}

	//  public int getSize(){
	//    int size = x.getSize();
	//    if( size != y.getSize() ) throw
	//      new ArrayStoreException(
	//        "The number of x-values is not equal to the number of y-values.\n"
	//        +"The size of the DPointSet isn�t clear."
	//      );
	//    return size;
	//  }
	/**
	 *
	 */
	public int getSize(){  // testhu
		int size = x.getSize();
		if( size <= y.getSize() )
			return size;
		return x.getSize();
	}


	protected void restore(){
		if( getSize() == 0){
			rectangle = DRectangle.getEmpty();
			return;
		}
		double min_x = x.getMinImageValue(),
		max_x = x.getMaxImageValue(),
		min_y = y.getMinImageValue(),
		max_y = y.getMaxImageValue();
		rectangle = new DRectangle(min_x, min_y, max_x - min_x, max_y - min_y );
	}

	/**
	 */
	/**
	 * this class stores the jump positions (see this.jump)
	 */
	class JumpManager{
		protected IntegerArrayList jumps = new IntegerArrayList();
		protected int index = -1;

		public void addJump(){
			jumps.add(getSize());
		}

		public int[] nextInterval(){
			int no_jumps = jumps.size();
			if( index >= no_jumps ) throw
			new ArrayIndexOutOfBoundsException("No more intervals in JumpManager");

			int[] inter = new int[2];

			if( index == -1 ) inter[0] = 0;
			else inter[0] = jumps.get(index);

			index++;

			if( index < no_jumps ) inter[1] = jumps.get(index);
			else inter[1] = getSize();

			return inter;
		}

		public boolean hasMoreIntervals(){
			return index < jumps.size();
		}

		public void restore(){
			index = -1;
		}

		public void reset(){
			index = -1;
			jumps.clear();
		}
	}

	public GraphicsModifier getAfterJumpModifier() {
		return afterJumpModifier;
	}


	public void setAfterJumpModifier(GraphicsModifier afterJumpModifier) {
		this.afterJumpModifier = afterJumpModifier;
	}
	
	public void setFixedAlpha(Float fixedAlphaValue) {
		this.fixedAlpha=fixedAlphaValue;
	}
}

/****************************************************************************
 * END OF FILE
 ****************************************************************************/
