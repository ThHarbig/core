package mayday.vis3.plots.profilelogo.renderer;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

public interface ProfileLogoRenderer 
{
	public Path2D renderToShape(Rectangle2D rect);
	public Color getColor();
}
