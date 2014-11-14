package mayday.vis3.graph.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import mayday.vis3.components.BasicPlotPanel;


@SuppressWarnings("serial")
public class SputnikLabel extends JLabel 
{
	private CanvasComponent parentComponent;
	
	private TextStyle style=TextStyle.TRUNCATE;
	
	private Orientation labelOrientation=Orientation.LOWER;
	
	private int verticalAlignment=SwingConstants.CENTER;
	
//	private static final int SPILL_SIZE=100;
	
	private Dimension baseSize;
	private double outerZoomFactor=1.0;
	
	public SputnikLabel(CanvasComponent parentComponent, ImageIcon icon, TextStyle style) 
	{
		super(parentComponent.getLabel(),icon,JLabel.LEFT);
		this.style=style;
		this.parentComponent=parentComponent;		
		init();
	}
	
	public SputnikLabel(CanvasComponent parentComponent, TextStyle style) 
	{
		super(parentComponent.getLabel());
		this.style=style;
		this.parentComponent=parentComponent;
		
		init();
	}
	
	public SputnikLabel(CanvasComponent parentComponent) 
	{
		super(parentComponent.getLabel());
		this.parentComponent=parentComponent;
		init();
	}
	
	private void init()
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
			int w=Math.min(Math.max(80,2*parentComponent.getWidth()),(int)r.getWidth());
			setSize(w+w0, Math.max(h0,estimateHeight(w)) );// Math.max(100, (int)r.getHeight()));
		}
		if(style==TextStyle.TRUNCATE)
		{
			int w=Math.min(Math.max(80,2*parentComponent.getWidth()),(int)r.getWidth());
			setSize(w+w0, Math.max((int)r.getHeight(),h0) );// Math.max(100, (int)r.getHeight()));
		}
		
		if(style==TextStyle.SPILL)
			setSize( (int)r.getWidth()+w0, Math.max(h0,(int)r.getHeight()) );
		
		baseSize=getSize();
		setBackground(parentComponent.getBackground());
		setOpaque(true);
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

	/* (non-Javadoc)
	 * @see java.awt.Component#getLocation()
	 */
	@Override
	public Point getLocation() 
	{
		return new Point(getX(),getY());
	}
	
	public Rectangle getBounds()
	{
		return new Rectangle(getX(),getY(),getWidth(),getHeight());
	}
		
	public int getX()
	{
		return parentComponent.getX()+parentComponent.getWidth()/2 - (int)(getWidth()/2);
	}
	
	public int getY()
	{
		switch (labelOrientation) 
		{
		case UPPER: return parentComponent.getY()+1;
		case CENTER: return parentComponent.getY()+parentComponent.getHeight()/2 - getHeight()/2;	
		case LOWER: return parentComponent.getY()+parentComponent.getHeight()/2 + getHeight()/2;
		case ABOVE: return parentComponent.getY()- getHeight()-1;
		case BELOW: return parentComponent.getY()+ parentComponent.getHeight()+1;
		default: return parentComponent.getY()+parentComponent.getHeight()/2 - getHeight()/2-12;
		}
		
	}
	
	public void zoomed(double zoomFactor) 
	{
		outerZoomFactor=zoomFactor;
		if(zoomFactor > 1)
			zoomFactor=1;
		if(zoomFactor < .1)
			zoomFactor=.1;
		revalidate();
		repaint();
	}
	
	protected void scale(int w, int h)
	{
		super.setSize(w,h);
	}
	
	
	
	@Override
	public void paint(Graphics g1) 
	{
		setHorizontalAlignment(JLabel.CENTER);
		if(outerZoomFactor < 0.4)
			return;
		if(parentComponent.isSelected())
			setBackground(Color.red);
		else
			setBackground(Color.lightGray);
		Graphics2D g=(Graphics2D)g1;
//		g.setColor(Color.black);
//		g.fillRect(0, 0, getWidth(), getHeight());
//		g.clearRect(0, 0, getWidth(), getHeight());
//		g.scale( (1.0*getHeight())/baseSize.getHeight() ,  (1.0*getHeight())/baseSize.getHeight());
//		g.scale( (1.0*getWidth())/baseSize.getWidth(), (1.0*getWidth())/baseSize.getWidth() );
//		g.scale( (1.0*getWidth())/baseSize.getWidth(),  (1.0*getHeight())/baseSize.getHeight());
//		g.scale(outerZoomFactor, outerZoomFactor);
//		g.clearRect(0, 0, getWidth(), getHeight());
		if(style==TextStyle.SPILL || style==TextStyle.TRUNCATE)
		{			
			super.paint(g);
			return;
		}
		
		
			
		AttributedCharacterIterator styledText=new AttributedString(getText()).getIterator();
		
	     Point pen = new Point(0,0);
	     FontRenderContext frc = g.getFontRenderContext();

	     // let styledText be an AttributedCharacterIterator containing at least
	     // one character

	     LineBreakMeasurer measurer = new LineBreakMeasurer(styledText, frc);
	     float wrappingWidth = getSize().width;

	     while (measurer.getPosition() < getText().length()) 
	     {

	         TextLayout layout = measurer.nextLayout(wrappingWidth);

	         pen.y += (layout.getAscent());

	         layout.draw(g, pen.x, pen.y);
	         pen.y += layout.getDescent() + layout.getLeading();
//	         if(style==TextStyle.TRUNCATE)
//	    		 break;
	     }	
	     
	
	}
	
	public enum TextStyle
	{
		SPILL,
		WRAP,
		TRUNCATE
	}
	
	public enum Orientation
	{
		UPPER,
		CENTER,
		LOWER,
		ABOVE,
		BELOW
	}

	/**
	 * @return the style
	 */
	public TextStyle getStyle() {
		return style;
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(TextStyle style) 
	{
		this.style = style;
		init();
	}
	
	@Override
	public boolean isVisible() 
	{
		return parentComponent.isVisible();
	}
	
	@Override
	public int getWidth() 
	{
		int w0=0;
		if(getIcon()!=null)
		{
			w0=getIcon().getIconWidth()+5;
		}
		int w=w0;
		Rectangle2D r=getFontMetrics(getFont()).getStringBounds(getText(), getGraphics());
		if(style==TextStyle.WRAP)
		{
			w=Math.min(Math.max(80,2*parentComponent.getWidth()),(int)r.getWidth());			
		}
		if(style==TextStyle.TRUNCATE)
		{
			w=Math.max(Math.max(80,2*parentComponent.getWidth()),(int)r.getWidth());
		}
		
		if(style==TextStyle.SPILL)
			w= (int)r.getWidth()+w0;
		if(w > r.getWidth()+w0+6)
			w=(int)r.getWidth()+w0+6;
//		if(w <2*parentComponent.getWidth() )
//			w=2*parentComponent.getWidth();
//		if(w < w0)
//			w=w0;
		
//		return super.getWidth();
//		int w=Math.min(parentComponent.getWidth()+SPILL_SIZE, super.getWidth());
		return (int)(w);
	}
	
	@Override
	public int getHeight() 
	{
		return (int)(baseSize.height*outerZoomFactor);
	}

	/**
	 * @return the verticalAlignment
	 */
	public int getVerticalAlignment() {
		return verticalAlignment;
	}

	/**
	 * @param verticalAlignment the verticalAlignment to set
	 */
	public void setVerticalAlignment(int verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
	}
	
	public void hide(boolean h)
	{
		if(labelOrientation==Orientation.ABOVE || labelOrientation==Orientation.BELOW)
		{
			return;
		}
		if(h)
		{
//			originalSize=getSize();
			setOpaque(false);
			setForeground(new Color(0,0,0,60));
			setVisible(false);
		}else
		{
//			setSize(originalSize);
			setOpaque(true);
			setForeground(Color.black);
		}
		repaint();
	}

	public Orientation getLabelOrientation() {
		return labelOrientation;
	}

	public void setLabelOrientation(Orientation labelOrientation) {
		this.labelOrientation = labelOrientation;
	}

	@Override
	public void setText(String text) 
	{
		if(getParent()==null || parentComponent==null)
		{
			super.setText(text);
			return;
		}
		super.setText(text);
		if(text.isEmpty())
		{

			setVisible(false);
			init();
		}
		else
		{
			setVisible(true);
			init();
		
		}
		revalidate();
		repaint();
		((BasicPlotPanel)getParent()).updatePlot();
	}
}
