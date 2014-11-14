package mayday.vis3.graph.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.List;

public abstract class RendererTools 
{
	public static void drawLabel(Graphics2D g, Rectangle bounds, String label)
	{	
		if(label.isEmpty())
			return;
		
		int w=g.getFontMetrics().stringWidth(label);
		double l=(bounds.getWidth()-w)/2;
		int h= g.getFontMetrics().getHeight();
		
		if(h+4 > bounds.height)
			return;
		
		g.setColor(Color.lightGray);
		g.fillRect((int)l-4, (int)(bounds.getHeight()-((bounds.getHeight()-h)))-h, w+8, h+4);
		g.setColor(Color.black);
		g.drawString(label, (int) l, (int)(bounds.getHeight()-((bounds.getHeight()-h))) );	
	}
	
	public static int Brightness(Color c)
	{
	   return (int)Math.sqrt(
	      c.getRed() * c.getRed() * .241 + 
	      c.getGreen() * c.getGreen() * .691 + 
	      c.getBlue() * c.getBlue()* .068);
	}
	
	public static Color getInverseBlackOrWhite(Color c)
	{
		Color textColor = Brightness(c) < 130 ? Color.white : Color.black;
		return textColor;
	}
	
	public static void drawBox(Graphics2D g, Rectangle bounds, boolean selected, Color color)
	{
		if(selected)
		{			
			g.setStroke(new BasicStroke(4));
			g.setColor(color);
		}else
		{
			g.setStroke(new BasicStroke(1));
			g.setColor(Color.black);			
		}
		g.drawRect(bounds.x, bounds.y, (int)bounds.getWidth()-1, (int)bounds.getHeight()-1);		
	}
	
	public static void drawEllipse(Graphics2D g, Rectangle bounds, boolean selected, Color color)
	{
		if(selected)
		{			
			g.setStroke(new BasicStroke(4));
			g.setColor(color);
		}else
		{
			g.setStroke(new BasicStroke(1));
			g.setColor(Color.black);			
		}
		g.drawOval(0, 0, (int)bounds.getWidth()-1, (int)bounds.getHeight()-1);	
	}
	
	public static void fillEllipse(Graphics2D g, Rectangle bounds, boolean selected, Color color)
	{
		g.fillOval(0, 0, (int)bounds.getWidth()-1, (int)bounds.getHeight()-1);	
		drawEllipse(g, bounds, selected);
	}
	
	public static void drawRoundBox(Graphics2D g, Rectangle bounds, boolean selected, Color color, int p)
	{
		if(selected)
		{			
			g.setStroke(new BasicStroke(4));
			g.setColor(color);
		}else
		{
			g.setStroke(new BasicStroke(1));
			g.setColor(Color.black);			
		}
		g.drawRoundRect((selected?2:0), (selected?2:0), (int)bounds.getWidth()-(selected?2:1), (int)bounds.getHeight()-(selected?2:1),p,p);		
	}
	
	public static void drawBox(Graphics2D g, Rectangle bounds, boolean selected)
	{
		drawBox(g, bounds, selected, Color.red);		
	}
	
	public static void drawEllipse(Graphics2D g, Rectangle bounds, boolean selected)
	{
		drawEllipse(g, bounds, selected, Color.red);		
	}
	
	public static void fillEllipse(Graphics2D g, Rectangle bounds, boolean selected)
	{
		fillEllipse(g, bounds, selected, Color.red);		
	}
	
	public static void fill(Graphics2D g, Rectangle bounds, Color c)
	{
		Color cbak=g.getColor();
		g.setColor(c);
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		g.setColor(cbak);
	}
	
	public static void fill(Graphics2D g, Rectangle bounds, Color c,int r)
	{
		Color cbak=g.getColor();
		g.setColor(c);
		g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height,r,r);
		g.setColor(cbak);
	}
	
	public static Color invertColor(Color c)
	{
		int r=255-c.getRed();
		int g=255-c.getGreen();
		int b=255-c.getBlue();
		
		return new Color(r,g,b); 
	}
	
	public static void drawColorLine(Graphics2D g, List<Color> colors, Rectangle bounds)
	{
		AffineTransform tBak=g.getTransform();
		double sx=bounds.getWidth()/(colors.size());
		g.translate(bounds.x, 0);
		g.scale(sx, 1);
		
		g.setStroke(new BasicStroke(0));
		for(int i=0; i!= colors.size(); ++i)
		{			
			g.setColor(colors.get(i));
			g.fillRect(i, bounds.y, 1, bounds.height);
		}
		g.setTransform(tBak);
	}
	
	public static void drawHighlightLine(Graphics2D g, int num, Color highlightColor, int highlight, Color defaultColor, Rectangle bounds)
	{
		AffineTransform tBak=g.getTransform();
		double sx=bounds.getWidth()/(num);
		g.translate(bounds.x, 0);
		g.scale(sx, 1);
		
		g.setStroke(new BasicStroke(0));
		for(int i=0; i!= num; ++i)
		{	
			g.setColor(defaultColor);
			if(i==highlight)
				g.setColor(highlightColor);
			g.fillRect(i, bounds.y, 1, bounds.height);
		}
		g.setTransform(tBak);
	}
	
	public static void drawHighlightLine(Graphics2D g, int num, Color highlightColor, int highlight, Rectangle bounds)
	{
		AffineTransform tBak=g.getTransform();
		double sx=bounds.getWidth()/(num);
		g.translate(bounds.x, 0);
		g.scale(sx, 1);
		
		g.setStroke(new BasicStroke(0));
		g.setColor(highlightColor);
		g.fillRect(highlight, bounds.y, 1, bounds.height);
		g.setTransform(tBak);
	}

	public static void drawColorLine(Graphics2D g, List<Color> colors, Rectangle2D bounds)
	{
		AffineTransform tBak=g.getTransform();
		double sx=bounds.getWidth()/(colors.size());
		g.translate(bounds.getX(), 0);
		g.scale(sx, 1);
		
		g.setStroke(new BasicStroke(0));
		for(int i=0; i!= colors.size(); ++i)
		{
			
			g.setColor(colors.get(i));
			g.fill(new Rectangle2D.Double(i, bounds.getY(), 1, bounds.getHeight()));
		}
		g.setTransform(tBak);
	}
	
	public static void drawColorLineSelected(Graphics2D g, List<Color> colors, Rectangle bounds)
	{
		AffineTransform tBak=g.getTransform();
		double sx=bounds.getWidth()/(colors.size());
		g.scale(sx, 1);
		g.setStroke(new BasicStroke(0));
		for(int i=0; i!= colors.size(); ++i)
		{
			
			g.setColor(colors.get(i));
			g.fillRect(i, bounds.y, 1, bounds.height);
			g.setColor(Color.black);
			g.drawLine(i, bounds.height, i+1, 0);
		}
		g.setTransform(tBak);
	}
	
	public static void drawColorColumn(Graphics2D g, List<Color> colors, Rectangle bounds)
	{
		AffineTransform tBak=g.getTransform();
		double sy=bounds.getHeight()/(colors.size());
		g.scale(1, sy);
		g.setStroke(new BasicStroke(0));
		for(int i=0; i!= colors.size(); ++i)
		{			
			g.setColor(colors.get(i));
			g.fillRect(bounds.x,i, bounds.width,1);
		}
		g.setTransform(tBak);
	}
	
	public static void drawIndexLine(Graphics2D g, int numIdx, Rectangle bounds)
	{
		double sx=bounds.getWidth()/(numIdx-1);
		g.setStroke(new BasicStroke(1));
		for(int i=0; i<= numIdx; ++i)
		{
			g.drawLine((int)(i*sx), bounds.y, (int)(i*sx), bounds.y+bounds.height);
		}
		g.drawLine(bounds.x+bounds.width-1, bounds.y,bounds.x+bounds.width-1 , bounds.y+bounds.height);
		
//		AffineTransform tBak=g.getTransform();
//		double sx=bounds.getWidth()/(numIdx);
//		g.scale(sx, 1);
//		g.setStroke(new BasicStroke(1));
//		for(int i=0; i!= numIdx+1; ++i)
//		{
//			g.drawLine(i, bounds.y, i, bounds.y+bounds.height);
//		}
//		g.setTransform(tBak);
	}
	
	/**
	 * Draw the string at the specified position with the specified width. 
	 * @param g
	 * @param text
	 * @param width
	 * @param x
	 * @param y
	 */
	public static void drawBreakingString(Graphics2D g, String text, int width, int x, int y )
	{
		AttributedCharacterIterator styledText=new AttributedString(text).getIterator();
		
		Point pen = new Point(x,y);
		FontRenderContext frc = g.getFontRenderContext();

		// let styledText be an AttributedCharacterIterator containing at least
		// one character

		LineBreakMeasurer measurer = new LineBreakMeasurer(styledText, frc);
		float wrappingWidth = width;

		while (measurer.getPosition() < text.length()) 
		{

			TextLayout layout = measurer.nextLayout(wrappingWidth);
			
			pen.y += (layout.getAscent());

			layout.draw(g, pen.x, pen.y);
			pen.y += layout.getDescent() + layout.getLeading();
		}	
	}
	

	/**
	 * Draw the string at the specified position with the specified width. 
	 * @param g
	 * @param text
	 * @param width
	 * @param x
	 * @param y
	 * @return The bounding box of the string as painted to the screen
	 */
	public static Rectangle2D drawBreakingString(Graphics2D g, Font font, String text, int width, int x, int y )
	{
		AttributedString string=new AttributedString(text);
		string.addAttribute(TextAttribute.FONT, font, 0, text.length());
		AttributedCharacterIterator styledText=string.getIterator();
		
		Point pen = new Point(x,y);
		FontRenderContext frc = g.getFontRenderContext();

		// let styledText be an AttributedCharacterIterator containing at least
		// one character

		LineBreakMeasurer measurer = new LineBreakMeasurer(styledText, frc);
		float wrappingWidth = width;
		double w=0; 
		while (measurer.getPosition() < text.length()) 
		{

			TextLayout layout = measurer.nextLayout(wrappingWidth);
			
			pen.y += (layout.getAscent());

			layout.draw(g, pen.x, pen.y);
			pen.y += layout.getDescent() + layout.getLeading();
			
			if(layout.getBounds().getWidth() > w)
				w=layout.getBounds().getWidth();
		}	
		
		return new Rectangle2D.Double(x-3, y, w+6, pen.y-y);
	}
	
	public static Rectangle2D drawBreakingString(Graphics2D g, Font font, String text, int width, int x, int y, int maxline)
	{
		AttributedString string=new AttributedString(text);
		string.addAttribute(TextAttribute.FONT, font, 0, text.length());
		AttributedCharacterIterator styledText=string.getIterator();
		
		Point pen = new Point(x,y);
		FontRenderContext frc = g.getFontRenderContext();

		// let styledText be an AttributedCharacterIterator containing at least
		// one character

		LineBreakMeasurer measurer = new LineBreakMeasurer(styledText, frc);
		float wrappingWidth = width;
		int l=0; 
		double w=0; 
		while (measurer.getPosition() < text.length()) 
		{
			if(l>= maxline)
				break;
			TextLayout layout = measurer.nextLayout(wrappingWidth);
			
			pen.y += (layout.getAscent());

			layout.draw(g, pen.x, pen.y);
			pen.y += layout.getDescent() + layout.getLeading();
			if(layout.getBounds().getWidth() > w)
				w=layout.getBounds().getWidth();
			
			++l;
		}
		
		return new Rectangle2D.Double(x-3, y, w+6, pen.y-y);
	}
	
	
	public static int breakingStringHeight(Graphics2D g, Font font, String text, int width, int x, int y )
	{
		AttributedString string=new AttributedString(text);
		string.addAttribute(TextAttribute.FONT, font, 0, text.length());
		AttributedCharacterIterator styledText=string.getIterator();
		
		Point pen = new Point(x,y);
		FontRenderContext frc = g.getFontRenderContext();

		// let styledText be an AttributedCharacterIterator containing at least
		// one character

		LineBreakMeasurer measurer = new LineBreakMeasurer(styledText, frc);
		float wrappingWidth = width;
//		float lastD=0;
		while (measurer.getPosition() < text.length()) 
		{
			TextLayout layout = measurer.nextLayout(wrappingWidth);
			pen.y += (layout.getAscent());
//			layout.draw(g, pen.x, pen.y);
			pen.y += layout.getDescent() + layout.getLeading();
//			lastD=(layout.getAscent())+layout.getDescent() + layout.getLeading();
		}
//		pen.y-=lastD;
		return pen.y;
	}
	
	/**
	 * Calculates a color which looks like the same color with alpha value 
	 * <code>alpha</code> above opaque white. This renders way faster than a java.awt.color with 
	 * alpha component. 
	 * In this function, the underlying color is considered to be opaque white. 
	 * @param c The color to be composed with opaque white. 
	 * @param alpha The alpha value in 0...255
	 * @return The alpha composition of the color and opaque white. 
	 */
	public static Color alphaColor(Color c, int alpha)
	{
		double a=alpha / 255.0;
		int r=(int)( (c.getRed()*a)+255*(1-a));
		int g=(int)( (c.getGreen()*a)+255*(1-a));
		int b=(int)( (c.getBlue()*a)+255*(1-a));
		return new Color(r, g, b);		
	}
	
	/**
	 * Calculates a color which looks like the alpha composition of color <code>ca </code>  with alpha value 
	 * <code>alphaA </code> and color <code>cb </code> with alpha value <code>alphaB </code>.
	 * @param ca
	 * @param alphaA
	 * @param cb
	 * @param alphaB
	 * @return the alpha composition of colors a and b. 
	 */
	public static Color alphaColor(Color ca, int alphaA, Color cb, int alphaB)
	{
		double aA=alphaA / 255.0;
		double aB=alphaB / 255.0;
		
		int r=(int)( (ca.getRed()*aA)+(cb.getRed()*aB)*(1-aA));
		int g=(int)( (ca.getGreen()*aA)+(cb.getGreen()*aB)*(1-aA));
		int b=(int)( (ca.getBlue()*aA)+(cb.getBlue()*aB)*(1-aA));
		return new Color(r, g, b);		
	}
	
	public static Color averageColor(Iterable<Color> colors)
	{
		int r=0,g=0,b=0;
		int i=0;
		for(Color c:colors)
		{
			r+=c.getRed();
			g+=c.getGreen();
			b+=c.getBlue();
			++i;
		}
		return new Color(r/i,g/i,b/i);		
	}
	
	public static void drawNameRow(Graphics2D g, List<String> names, Rectangle bounds)
	{
		AffineTransform tBak=g.getTransform();
		int sx=bounds.width/(names.size());
		
		g.rotate(Math.PI / 2.0d);
//		g.translate(bounds.x, bounds.y);
		g.setStroke(new BasicStroke(0));
		for(int i=0; i!= names.size(); ++i)
		{
			g.drawString(names.get(i), bounds.y, (int) ((-i-0.5)*sx));	
		}
		g.setTransform(tBak);
	}
	
	
	@SuppressWarnings("unchecked")
	public static void drawObjectNameRow(Graphics2D g, List objects, Rectangle bounds)
	{
		AffineTransform tBak=g.getTransform();
		int sx=bounds.width/(objects.size());

		
		g.rotate(Math.PI / 2.0d);
//		g.translate(bounds.x, bounds.y);
		g.setStroke(new BasicStroke(0));
		for(int i=0; i!= objects.size(); ++i)
		{
			g.drawString(objects.get(i).toString(), bounds.y, (int) ((-i-0.5)*sx));	
		}
		g.setTransform(tBak);
	}
	
	public static void drawNameColumn(Graphics2D g, List<String> names, Rectangle bounds)
	{
		AffineTransform tBak=g.getTransform();
		int sy=bounds.height/(names.size());
		
//		g.draw(bounds);
		g.setStroke(new BasicStroke(0));
		for(int i=0; i!= names.size(); ++i)
		{
			g.drawString(names.get(i), bounds.x, bounds.y+(int) ((i+0.5)*sy));			
		}
		g.setTransform(tBak);
	}
	
	@SuppressWarnings("unchecked")
	public static void drawObjectNameColumn(Graphics2D g, List objects, Rectangle bounds)
	{
		AffineTransform tBak=g.getTransform();
		int sy=bounds.height/(objects.size());
		g.setStroke(new BasicStroke(0));
		for(int i=0; i!= objects.size(); ++i)
		{
			g.drawString(objects.get(i).toString(), bounds.x, bounds.y+(int) ((i+0.5)*sy));			
		}
		g.setTransform(tBak);
	}
	
	public static Color wordToColor(String s)
	{
		if(s==null || s.length()==0)
		{
			return Color.LIGHT_GRAY;
		}
		
		char first=s.charAt(0);
		if(Character.isDigit(first))
		{
			return gray(0.75f - 0.05f* Integer.parseInt(""+first));
		}
		s=s.toLowerCase();
		
		float hue=letterValue(s,0);
		float saturation= 0.3f + 0.6f* letterValue(s,1);
		float brightness= 0.4f + 0.5f* letterValue(s,2);
		Color c=Color.getHSBColor(hue, saturation, brightness);
		
		return c;
	}
	
	public static Color gray(float d)
	{
		return new Color(d,d,d);
	}
	
	public static float letterValue(String s, int position)
	{
		if( s.length() <= position)
			return 0;
		int value=s.charAt(position)-'a';
		if(value <0)
			return 0;
		if(value > 26)
			return 1;
		return value/26.0f;
	}
	
	public static Polygon drawStar(Rectangle bounds)
	{
		int r=Math.min(bounds.width, bounds.height)-1;
		Polygon p=new Polygon();
	
		double r0=r*0.5;
		double r1=0.5*r0;
		int rc=(bounds.width-r)/2;		
		for(int i=0; i!=10; ++i)
		{
			double phi=i*(Math.PI*2)/10;
			if(i%2==0)
			{
				double x= 0*Math.cos(phi) - -r0*Math.sin(phi);
				double y= 0*Math.sin(phi) + -r0*Math.cos(phi);
				p.addPoint((int)(x+r0+rc),(int)(y+r0));
			}else
			{
				double x= 0*Math.cos(phi) - -r1*Math.sin(phi);
				double y= 0*Math.sin(phi) + -r1*Math.cos(phi);
				p.addPoint((int)(x+r0+rc),(int)(y+r0));
			}			
		}
		return p;
	}
	
	public static Polygon drawPolygon(Rectangle bounds, int sides)
	{
		int r=Math.min(bounds.width, bounds.height)-1;
		int rc=(bounds.width-r)/2;
		Polygon p=new Polygon();
	
		double r0=r*0.5;
				
		for(int i=0; i!=sides; ++i)
		{
			double phi=i*(Math.PI*2)/sides;
			double x= 0*Math.cos(phi) - -r0*Math.sin(phi);
			double y= 0*Math.sin(phi) + -r0*Math.cos(phi);
			p.addPoint((int)(x+r0+rc),(int)(y+r0));
			
		}
		return p;
	}
	
	public static Point2D rotate(double x, double y, double angle, Point2D center)
	{
		double xp= x*Math.cos(angle) - y*Math.sin(angle);
		double yp= x*Math.sin(angle) + y*Math.cos(angle);
	    
		Point2D res=new Point2D.Double(xp+center.getX(), yp+center.getY());
		return res;
	}
	
	public static  Point2D rotate(double x, double y, double angle, Point2D center, Point2D resultPoint)
	{
		double xp= x*Math.cos(angle) - y*Math.sin(angle);
		double yp= x*Math.sin(angle) + y*Math.cos(angle);
	    
		resultPoint.setLocation(xp+center.getX(), yp+center.getY());
		return resultPoint;
	}
	
	public static  Point2D rotate(double x, double y, double angle)
	{
		return rotate(x,y,angle, new Point2D.Double(0, 0));
	}
	

	
	
}
