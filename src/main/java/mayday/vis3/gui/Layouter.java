package mayday.vis3.gui;

/**
 * Computes the object size and the positioning of visualization objects (of any kind)
 * for a grid of the given dimension i*j. Furthermore, a margin, a screen size
 * and a screen scaling can be specified
 * 
 * @version 080530
 * @author Philipp Bruns
 */

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;

public class Layouter 
{
	private int i,j;				// grid dimension (number of elements)
	private int o_width, o_height;	// object dimension @default proportion (pixels)
	private int margin;				// space between two elements (pixels)
	private double scaling;			// scaling factor for the screen (0..1)
	private Dimension scrdim;		// screen dimension (pixels)
	int counter;					// counts the elements that have been plotted to obtain the next grid position
	
	private static final int DEFAULT_MARGIN = 30;
	
	public class LayoutElement
	{
		public int r;	// grid coordinates
		public int s;
		
		public double rel_x;	// rectangle proportion 
		public double rel_y;

		private Layouter grid; 
		
		public LayoutElement(Layouter grid, int r, int s)
		{
			this(grid, r, s, grid.getScreenDimension().width, grid.getScreenDimension().height);
		}
		
		public LayoutElement(Layouter grid, int r, int s, double rel_x, double rel_y)
		{
			this.r = r;
			this.s = s;
			this.grid = grid;
			this.rel_x = rel_x;
			this.rel_y = rel_y;
		}
		
		public Dimension getSize()
		{
			return grid.getObjectSize(rel_x, rel_y);
		}
		
		public Point getPosition()
		{
			return grid.getPosition(r, s, rel_x, rel_y);
		}
		
		public void placeWindow(Window w) {
			w.setLocation(getPosition());
			w.setSize(getSize());
		}
	}
	
	/**
	 * Simple Visual Grid for 1 element (1x1)
	 */
	public Layouter()
	{
		this(1,1);
	}
	
	/**
	 * Visual Grid for i * j elements
	 * @param i
	 * @param j
	 */
	public Layouter(int i, int j)
	{
		this.i = i;
		this.j = j;
		this.counter = 0;
		margin = DEFAULT_MARGIN;
		GraphicsDevice[] sd = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		//Use first/primary screen 
		Rectangle primaryScreen = sd[0].getDefaultConfiguration().getBounds();
		scrdim = primaryScreen.getSize();
		setScaling(1.0);
	}
	
	/**
	 * Set the scaling factor of the grid. The complete grid takes
	 * the screen dimension multiplied with the scaling factor
	 * @param scaling factor (0..1)
	 */
	public void setScaling(double scaling)
	{
		if(scaling <= .0 || scaling > 1.)
		{
			System.err.println("invalid grid scaling factor ( allowed values: ]0;1] )");
			return;
		}
			
		o_width = (int) (scaling * (scrdim.width/i - i*margin));
		o_height = (int) (scaling * (scrdim.height/j - j*margin));
		this.scaling = scaling;
	}
	
	/**
	 * Get the position of Element (r,s) with respect to the screen size/scaling
	 * @param r x-Position of the element on the grid
	 * @param s y-Position of the element on the grid
	 * @return position as Point
	 */
	public Point getPosition(int r, int s)
	{	
		// compute positioning with grid indices and with respect to screen scaling
		return new Point((int) ((scrdim.width*((1-scaling)/2) + (r-1)*o_width+r*margin)), (int) ((scrdim.height*((1-scaling)/2) + (s-1)*o_height+s*margin)));
	}
	
	public Point getPosition(int r, int s, double rel_x, double rel_y)
	{
		// compute positioning for normal scaling (i.e. screen proportion)
		Point p = getPosition(r, s);
		
		// compute the offset which results from the specified screen proportion
		int xmov = (getObjectSize().width - getObjectSize(rel_x, rel_y).width)/2;
		int ymov = (getObjectSize().height - getObjectSize(rel_x, rel_y).height)/2;
		
		return new Point(p.x+xmov, p.y+ymov);
	}
	
	/**
	 * Get the object size
	 * @return object size as Dimension
	 */
	public Dimension getObjectSize()
	{
		return new Dimension(o_width, o_height);
	}
		
	/**
	 * Get an object size that fits with the grid, as well as width the 
	 * given proportion (crop either width or height), e.g. 16:9
	 * @param rel_x rel. x
	 * @param rel_y rel. y
	 * @return object size as Dimension
	 */
	public Dimension getObjectSize(double rel_x, double rel_y)
	{
		Dimension d = getObjectSize();
		if((rel_x/rel_y) > (((double)d.width/(double)d.height)))
		{
			d.height = (int) ((rel_y/rel_x) * d.width); // fit height
		}
		
		else if((rel_x/rel_y) < (((double)d.width/(double)d.height)))
		{
			d.width = (int) ((rel_x/rel_y) * d.height);  // fit width
		}

		return d;
	}
	
	public LayoutElement nextElement()
	{
		return nextElement(scrdim.width, scrdim.height);
	}
	
	public LayoutElement nextElement(double rel_x, double rel_y)
	{
		int next_r = counter % i + 1;
		int next_s = (counter / i) % j + 1;
		counter++;
		return new LayoutElement(this, next_r, next_s, rel_x, rel_y);
	}
	
	public void setScreenDimension(Dimension d)
	{
		this.scrdim = d;
		setScaling(scaling);
	}
	
	public Dimension getScreenDimension()
	{
		return scrdim;
	}
	
	public void setMargin(int m)
	{
		this.margin = m;
	}
}
