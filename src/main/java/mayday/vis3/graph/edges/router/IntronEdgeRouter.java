package mayday.vis3.graph.edges.router;

import java.awt.Container;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import mayday.core.structures.graph.Edge;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public abstract class IntronEdgeRouter extends SimpleEdgeRouter
{
	private boolean smooth;
		
	public IntronEdgeRouter(boolean smooth) {
		this.smooth = smooth;
	}

	@Override
	public Path2D routeEdge(Edge e, Container container, GraphModel model) 
	{	
		EdgePoints points=getAdjustedPoints(model.getComponent(e.getSource()), model.getComponent(e.getTarget()));
		Point2D p=getSupportPoint(model.getComponent(e.getSource()), model.getComponent(e.getTarget()));
		if(!smooth)
		{
			if(e.getSource()==e.getTarget())
			{
				return returnEdge(model.getComponent(e.getSource()), model.getComponent(e.getTarget()));
			}
			Path2D path=new Path2D.Double();			
			path.moveTo(points.source.getX(),points.source.getY());
			path.lineTo(p.getX(),p.getY());
			path.lineTo(points.target.getX(),points.target.getY());
			path.moveTo(points.target.getX(), points.target.getY());
			path.closePath();
			return path;
		}
		Path2D path=new Path2D.Double();
		path.moveTo(points.source.getX(),points.source.getY());
		double h= Math.max(points.source.getY(), points.target.getY())-model.getComponent(e.getSource()).getHeight();
		double w= points.target.getX() - points.source.getX();
		path.curveTo(
				points.source.getX()+w*0.25, points.source.getY(), // first ctrl point 
				points.source.getX(), h,
				points.source.getX()+w*0.25, h);
		path.lineTo(points.source.getX()+w*0.75, h);
		path.curveTo(
				points.target.getX(), h, // first ctrl point 
				points.source.getX()+w*0.75, points.target.getY(),
				points.target.getX(),points.target.getY());
		path.moveTo(points.target.getX(), points.target.getY());
		path.closePath();
		return path;
	}
	
	@Override
	public Point2D getSupportPoint(CanvasComponent source, 	CanvasComponent target) 
	{
		Point2D p=null;
		if(!smooth)
		{
			EdgePoints ep=getAdjustedPoints(source, target);
			double angle=angle(ep.source, ep.target);	
			double l=Point2D.distance(ep.source.getX(), ep.source.getY(), ep.target.getX(), ep.target.getY());
	        double x3 = ep.source.getX() - l *0.5 * Math.cos(angle);
	        double y3 = ep.source.getY() -l *0.5 * Math.sin(angle);
	        Point2D p0=new Point2D.Double(x3,y3);
			p=getX1(ep.source, p0, 20, Math.toRadians(90));
		}else
		{
			p= super.getSupportPoint(source, target);
		}
		return p;
	}
	
	protected double angle(Point2D source, Point2D target)
	{
		return Math.atan2 (target.getY() - source.getY(), target.getX() - source.getX()) + Math.PI;
	}
	
	protected Point2D getX1(Point2D source, Point2D target, int l, double alpha )
	{
		double ang=Math.atan2 (target.getY() - source.getY(), target.getX() - source.getX()) + Math.PI;
		double x1 = (target.getX() + l * Math.cos(ang+alpha));
	    double y1 = (target.getY() + l * Math.sin(ang+alpha));
	    return new Point2D.Double(x1,y1);
	}
	
}
