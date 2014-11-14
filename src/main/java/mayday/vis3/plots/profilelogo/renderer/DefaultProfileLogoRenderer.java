package mayday.vis3.plots.profilelogo.renderer;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;


public class DefaultProfileLogoRenderer implements ProfileLogoRenderer {

	private Color color;
	
	public DefaultProfileLogoRenderer()
	{
		color=Color.black;
	}
	
	public DefaultProfileLogoRenderer(Color col)
	{
		color=col;
	}
	
	public Path2D renderToShape(Rectangle2D rect) 
	{
		Path2D p=new Path2D.Double(rect);
		return p;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	

}
