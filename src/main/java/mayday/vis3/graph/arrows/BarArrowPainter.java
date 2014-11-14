package mayday.vis3.graph.arrows;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class BarArrowPainter extends ArrowPainter
{
	@Override
	public Shape paintArrow(Point2D source, Point2D target, int l, double alpha) 
	{
		Path2D res=new Path2D.Float();
			
		double angle=angle(source, target);	
        double x3 = target.getX() + l *0.8 * Math.cos(angle);
        double y3 = target.getY() + l *0.8 * Math.sin(angle);
        
        Point2D p0=new Point2D.Double(x3,y3);
        
		Point p1=getX1(source, p0, 8, Math.toRadians(90));
		Point p2=getX2(source, p0, 8, Math.toRadians(90));
		
		res.append(new Line2D.Float((float)p0.getX(),(float)p0.getY(),(float)p1.getX(),(float)p1.getY() ),false);
		res.append(new Line2D.Float((float)p0.getX(),(float)p0.getY(),(float)p2.getX(),(float)p2.getY() ),false);
		
		return res;		
	}
	
	@Override
	public String toString() 
	{
		return "Bar";
	}
}
