package mayday.vis3.graph.layout;

import java.awt.geom.Point2D;
import java.util.List;

import mayday.vis3.graph.components.CanvasComponent;

public class LayoutUtilities 
{
	public static final double TWO_PI=2.0*Math.PI;
	
	public static void placeOnAngleRadius(CanvasComponent comp, double radius, double angle, Point2D point)
	{
		double xp= 0.0*Math.cos(angle) - (-1.0*radius)*Math.sin(angle);
		double yp= 0.0*Math.sin(angle) + (-1.0*radius)*Math.cos(angle);
		xp+=point.getX();
		yp+=point.getY();
		comp.setLocation((int)xp, (int)yp);
	}
	
	public static void buildCircle(List<CanvasComponent> components, double radius, Point2D center)
	{
		double cc=components.size();
		double u=0;
				
		for(CanvasComponent c: components)
		{
			LayoutUtilities.placeOnAngleRadius(c, radius, u*(TWO_PI/cc), center);
			u++;
		}
	}

}
