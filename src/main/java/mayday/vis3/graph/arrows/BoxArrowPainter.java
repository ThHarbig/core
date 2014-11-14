package mayday.vis3.graph.arrows;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class BoxArrowPainter extends ArrowPainter
{
	@Override
	public Shape paintArrow(Point2D source, Point2D target, int l, double alpha) 
	{
		Point p1=getX1(source, target, l/2, Math.toRadians(90) );
		Point p2=getX2(source, target, l/2, Math.toRadians(90) );
		
		Path2D p=new Path2D.Double();
		p.moveTo(target.getX(), target.getY());
		p.lineTo(p1.x, p1.y);
		p.moveTo(target.getX(), target.getY());
		p.lineTo(p2.x, p2.y);
		
	
		double angle=angle(source, target);	
        double x3 = target.getX() + l * 1 * Math.cos(angle);
        double y3 = target.getY() + l * 1 * Math.sin(angle);
        
        Point2D p0=new Point2D.Double(x3,y3);
        
		Point p3=getX1(source, p0, l/2, Math.toRadians(90));
		Point p4=getX2(source, p0, l/2, Math.toRadians(90));
		
		p.lineTo(p4.x, p4.y);
		p.lineTo(p3.x, p3.y);		
		p.lineTo(p1.x, p1.y);
		return p;
	}
	
	@Override
	public String toString() 
	{
		return "Box";
	}
}
