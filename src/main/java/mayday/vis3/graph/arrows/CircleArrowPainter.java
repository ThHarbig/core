package mayday.vis3.graph.arrows;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class CircleArrowPainter extends ArrowPainter
{

	@Override
	public Shape paintArrow(Point2D source, Point2D target, int l,double alpha) 
	{
        double angle=angle(source, target);
        
        double x3 = target.getX() + l * 0.5 * Math.cos(angle);
        double y3 = target.getY() + l * 0.5 * Math.sin(angle);
                
        Ellipse2D e=new Ellipse2D.Double(x3-(l/2),y3-(l/2),l, l);
        return e;      

	}
	
	@Override
	public String toString() 
	{
		return "Circle";
	}
	
}
