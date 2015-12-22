package mayday.vis3.graph.arrows;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * @author Stephan Symons
 *
 */
public class DiamondArrowPainter extends ArrowPainter
{

	@Override
	public Shape paintArrow(Point2D source, Point2D target, int l,	double alpha)
	{		
	    double angle=angle(source, target);
		Point p1=getX1(source, target, l, alpha);
		Point p2=getX2(source, target, l, alpha);
        double x3 = target.getX() + l * 1.5 * Math.cos(angle);
        double y3 = target.getY() + l * 1.5 * Math.sin(angle);
        
		Polygon p=new Polygon();
		p.addPoint((int)target.getX(), (int)target.getY());
		p.addPoint((int)p1.getX(), (int)p1.getY());
		p.addPoint((int)x3, (int)y3);
		p.addPoint((int)p2.getX(), (int)p2.getY());
		p.addPoint((int)target.getX(), (int)target.getY());
		
		return p;
	}

	@Override
	public String toString() 
	{
		return "Diamond";
	}
}
