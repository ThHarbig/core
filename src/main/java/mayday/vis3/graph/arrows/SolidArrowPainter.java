package mayday.vis3.graph.arrows;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Point2D;

public class SolidArrowPainter extends ArrowPainter
{

	@Override
	public Shape paintArrow(Point2D source, Point2D target, int l,	double alpha) 
	{
		Point p1=getX1(source, target, l, alpha);
		Point p2=getX2(source, target, l, alpha);
		
		Polygon p=new Polygon();
		p.addPoint((int)target.getX(), (int)target.getY());
		p.addPoint((int)p1.getX(), (int)p1.getY());
		p.addPoint((int)p2.getX(), (int)p2.getY());
		p.addPoint((int)target.getX(), (int)target.getY());
		
		return p;		
	}

	@Override
	public String toString() 
	{
		return "Solid Arrow";
	}
}
