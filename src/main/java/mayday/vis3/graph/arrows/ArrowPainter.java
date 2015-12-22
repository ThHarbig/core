package mayday.vis3.graph.arrows;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public abstract class ArrowPainter 
{
	/**
	 * Draws the arrow.
	 * @param source The source node
	 * @param target The target node where the head is drawn
	 * @param l The length of the arrow head
	 * @param alpha The angle of the arrow 
	 * @param fill Whether the arrow head should be filled
	 */
	public abstract Shape paintArrow(Point2D source, Point2D target, int l, double alpha);
	
	
	
	
	protected double angle(Point2D source, Point2D target)
	{
		return Math.atan2 (target.getY() - source.getY(), target.getX() - source.getX()) + Math.PI;
	}
	
	protected Point getX1(Point2D source, Point2D target, int l, double alpha )
	{
		double ang=Math.atan2 (target.getY() - source.getY(), target.getX() - source.getX()) + Math.PI;
		double x1 = (target.getX() + l * Math.cos(ang-alpha));
	    double y1 = (target.getY() + l * Math.sin(ang-alpha));
	    
	    return new Point((int)x1,(int)y1);
	}
	
	protected Point getX2(Point2D source, Point2D target,int l, double alpha)
	{
		double ang=Math.atan2 (target.getY() - source.getY(), target.getX() - source.getX()) + Math.PI;
		
		double x2 = (target.getX() + l * Math.cos(ang+alpha));
	    double y2 = (target.getY() + l * Math.sin(ang+alpha));
	    
	    return new Point((int)x2,(int)y2);
	}
	
	private static Map<ArrowStyle,ArrowPainter> painterMap;
	
	public static ArrowPainter painterForStyle(ArrowStyle style)
	{
		if(painterMap==null)
		{
			painterMap=new HashMap<ArrowStyle, ArrowPainter>();
			painterMap.put(ArrowStyle.ARROW_CIRCLE,new CircleArrowPainter());
			painterMap.put(ArrowStyle.ARROW_DIAMOND, new DiamondArrowPainter());
			painterMap.put(ArrowStyle.ARROW_OPEN,new OpenArrowPainter());
			painterMap.put(ArrowStyle.ARROW_TRIANGLE,new SolidArrowPainter());
			painterMap.put(ArrowStyle.ARROW_BAR,new BarArrowPainter());
			painterMap.put(ArrowStyle.ARROW_BOX,new BoxArrowPainter());
			painterMap.put(ArrowStyle.ARROW_BAR_AND_TRIANGLE,new BarAndSolidArrowPainter());
		}
		return painterMap.get(style);

	}
	
	
	
}
