package mayday.vis3.graph.arrows;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class OpenArrowPainter extends ArrowPainter 
{

	@Override
	public Shape paintArrow(Point2D source, Point2D target, int l,double alpha) 
	{
		Point p1=getX1(source, target, l, alpha);
		Point p2=getX2(source, target, l, alpha);
		
		Path2D p=new Path2D.Double();
		p.moveTo(target.getX(), target.getY());
		p.lineTo(p1.x, p1.y);
		p.moveTo(target.getX(), target.getY());
		p.lineTo(p2.x, p2.y);
		
		return p;
	}
	
	@Override
	public String toString() 
	{
		return "Open Arrow";
	}
}
