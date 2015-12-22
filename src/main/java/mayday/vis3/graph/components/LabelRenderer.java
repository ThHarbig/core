package mayday.vis3.graph.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.swing.JLabel;

import mayday.vis3.graph.GraphCanvas;

@SuppressWarnings("serial")
public class LabelRenderer extends JLabel 
{
	private TextStyle style;
	private Orientation orientation;

	public LabelRenderer()
	{
		setBackground(Color.lightGray);
		setOpaque(true);
		orientation=Orientation.CENTER;
		style=TextStyle.TRUNCATE;
	}

	public JLabel getLabelComponent(GraphCanvas canvas, CanvasComponent component, boolean selected)
	{
		setText(component.getLabel());
		setSize(calculateSize(component));
		setLocation(calculateLocation(component));
		if(selected)
			setBackground(Color.red);
		else
			setBackground(Color.lightGray);

		return this;
	}
	
	public JLabel getLabelComponent(GraphCanvas canvas, CanvasComponent component, boolean selected, Orientation orientation)
	{
		Orientation bak=getOrientation();
		setOrientation(orientation);
		setText(component.getLabel());
		setSize(calculateSize(component));
		setLocation(calculateLocation(component));
		if(selected)
			setBackground(Color.red);
		else
			setBackground(Color.lightGray);

		setOrientation(bak);
		return this;
	}

	protected Point calculateLocation(CanvasComponent component) 
	{
		
		int x=component.getX()+component.getWidth()/2 - getWidth()/2;
		int y=0;
		switch (orientation) 
		{
		case UPPER: y= component.getY()+1;
		break;
		case CENTER: y= component.getY()+component.getHeight()/2 - getHeight()/2;	
		break;
		case LOWER: y= component.getY()+component.getHeight()/2 + getHeight()/2;
		break;
		case ABOVE: y= component.getY()- getHeight()-1;
		break;
		case BELOW: y= component.getY()+ component.getHeight()+1;
		break;
		default: y= component.getY()+component.getHeight()/2 - getHeight()/2-12;
		}
		return new Point(x,y);

	}

	@Override
	public void paint(Graphics g) 
	{
		g.clearRect(0, 0, getWidth(), getHeight());
		if(style==TextStyle.SPILL || style==TextStyle.TRUNCATE)
		{
			super.paint(g);
			return;
		}


		AttributedCharacterIterator styledText=new AttributedString(getText()).getIterator();

		Point pen = new Point(0,0);
		Graphics2D g2 = (Graphics2D)g;
		FontRenderContext frc = g2.getFontRenderContext();

		// let styledText be an AttributedCharacterIterator containing at least
		// one character

		LineBreakMeasurer measurer = new LineBreakMeasurer(styledText, frc);
		float wrappingWidth = getSize().width;

		while (measurer.getPosition() < getText().length()) 
		{

			TextLayout layout = measurer.nextLayout(wrappingWidth);

			pen.y += (layout.getAscent());

			layout.draw(g2, pen.x, pen.y);
			pen.y += layout.getDescent() + layout.getLeading();
			//	         if(style==TextStyle.TRUNCATE)
			//	    		 break;
		}	    

	}

	protected Dimension calculateSize(CanvasComponent component)
	{
		int h0=0;
		int w0=0;
		if(getIcon()!=null)
		{
			h0=getIcon().getIconHeight();
			w0=getIcon().getIconWidth()+5;
		}
		Rectangle2D r=getFontMetrics(getFont()).getStringBounds(getText(), getGraphics());
		if(style==TextStyle.WRAP)
		{
			int w=Math.min(Math.max(80,2*component.getWidth()),(int)r.getWidth());
			return new Dimension(w+w0, Math.max(h0,estimateHeight(w)));

		}
		if(style==TextStyle.TRUNCATE)
		{
			int w=Math.min(Math.max(80,2*component.getWidth()),(int)r.getWidth());
			return new Dimension(w+w0, Math.max((int)r.getHeight(),h0) );// Math.max(100, (int)r.getHeight()));
		}

		if(style==TextStyle.SPILL)
			return new Dimension( (int)r.getWidth()+w0, Math.max(h0,(int)r.getHeight()) );

		return null; //ouch.
	}

	private int estimateHeight(int width)
	{
		AttributedCharacterIterator styledText=new AttributedString(getText()).getIterator();

		Point pen = new Point(0,0);
		//	     Graphics2D g2 = (Graphics2D)getGraphics();

		// let styledText be an AttributedCharacterIterator containing at least
		// one character

		LineBreakMeasurer measurer = new LineBreakMeasurer(styledText, new FontRenderContext(AffineTransform.getTranslateInstance(0,0),false,true));
		float wrappingWidth = width;

		while (measurer.getPosition() < getText().length()) 
		{
			if(style==TextStyle.TRUNCATE)
				break;
			TextLayout layout = measurer.nextLayout(wrappingWidth);
			pen.y += (layout.getAscent());
			pen.y += layout.getDescent() + layout.getLeading();
		}
		return pen.y;
	}

	/**
	 * <ul>
	 * <li><b>SPILL</b> make the label as long as necessary</li>
	 * <li><b>WRAP</b> wrap the text to multiple lines </li>
	 * <li><b>TRUNCATE</b> cut the text to a given multiple of the component.</li>
	 * </ul>
	 * @author symons
	 *
	 */
	public enum TextStyle
	{
		SPILL,
		WRAP,
		TRUNCATE
	}


	/**
	 * <ul>
	 * <li><b>UPPER</b> Base Line of the text just above the component</li>
	 * <li><b>ABOVE</b> place label in the upper part of component </li>
	 * <li><b>CENTER</b> center the label inside the component</li>
	 * <li><b>LOWER</b>  place label in the lower part of component</li>
	 * <li><b>UPPER</b> top of the text just below the component</li>
	 * </ul>
	 * @author symons
	 *
	 */
	public enum Orientation
	{
		UPPER,
		CENTER,
		LOWER,
		ABOVE,
		BELOW
	}


	public TextStyle getStyle() {
		return style;
	}

	public void setStyle(TextStyle style) {
		this.style = style;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}
	
	

}
