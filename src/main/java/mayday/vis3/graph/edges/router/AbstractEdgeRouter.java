package mayday.vis3.graph.edges.router;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import mayday.core.pluma.AbstractPlugin;
import mayday.vis3.graph.components.CanvasComponent;

public abstract class AbstractEdgeRouter extends AbstractPlugin implements EdgeRouter
{
	public static final String MC="GraphViewer/EdgeRouter";
	
	public EdgePoints getAdjustedPoints(CanvasComponent source, CanvasComponent target)
	{
		double x= target.getBounds().getCenterX() -source.getBounds().getCenterX();
		double y= target.getBounds().getCenterY() -source.getBounds().getCenterY();
		
		Point2D start = null;
		Point2D end=null;
		
		if(x < y && -x < y)
		{
			start=new Point2D.Double(source.getBounds().getCenterX(), source.getBounds().getMaxY());
			end=new Point2D.Double(target.getBounds().getCenterX(), target.getBounds().getMinY());
		}
		
		if(x < y && -x >= y)
		{
			start=new Point2D.Double(source.getBounds().getMinX(), source.getBounds().getCenterY());
			end=new Point2D.Double(target.getBounds().getMaxX(), target.getBounds().getCenterY());
		}
		if(x >= y && -x >= y)
		{
			start=new Point2D.Double(source.getBounds().getCenterX(), source.getBounds().getMinY());
			end=new Point2D.Double(target.getBounds().getCenterX(), target.getBounds().getMaxY());
		}
		if(x >= y && -x < y)
		{
			start=new Point2D.Double(source.getBounds().getMaxX(), source.getBounds().getCenterY());
			end=new Point2D.Double(target.getBounds().getMinX(), target.getBounds().getCenterY());
		}
			
		return new EdgePoints(start,end);
	}
	
	public EdgePoints getArrowPoints(CanvasComponent source, CanvasComponent target)
	{
		return getAdjustedPoints(source, target);
	}
	
	protected boolean directEdge(CanvasComponent source, CanvasComponent target)
	{
		double x= target.getBounds().getCenterX() -source.getBounds().getCenterX();
		double y= target.getBounds().getCenterY() -source.getBounds().getCenterY();
		
		if( Math.abs(x) < 1.15* (Math.min(source.getBounds().width, target.getBounds().width)) )
		{
			return true;
		}
		
		if( Math.abs(y) < 1.15* (Math.min(source.getBounds().height, target.getBounds().height)) )
		{
			return true;
		}
		return false;		
	}
	
	protected boolean directEdge(EdgePoints points)
	{
		double x= points.target.getX() - points.source.getX();
		double y= points.target.getY() - points.source.getY();
		
		if( Math.abs(x) < 10 || Math.abs(y) <25 )
		{
			return true;
		}
		return false;		
	}
	
	protected boolean diagonalEdge(CanvasComponent source, CanvasComponent target)
	{
		double x= target.getBounds().getCenterX() -source.getBounds().getCenterX();
		double y= target.getBounds().getCenterY() -source.getBounds().getCenterY();
		
		return (Math.abs(x-y) < 10 || Math.abs(-x-y) < 10 );
	}
	
	protected Path2D returnEdge(CanvasComponent source, CanvasComponent target)
	{
		Path2D res=new Path2D.Double();
		res.moveTo(source.getBounds().getCenterX(), source.getBounds().getMaxY());
		res.curveTo(
				source.getBounds().getMaxX()+source.getBounds().getWidth()/2,source.getBounds().getMaxY()+source.getBounds().getHeight() , 
				source.getBounds().getMaxX()+source.getBounds().getWidth()/2,source.getBounds().getMaxY()+source.getBounds().getHeight(), 
				source.getBounds().getMaxX(), source.getBounds().getCenterY());
		return res;
	}
	
	@Override
	public void init() {	
		// do nothing; 
	}
	
	public static int relativePositionOfTarget(CanvasComponent sourceComp, CanvasComponent targetComp, float extension)
	{
		double xS=sourceComp.getBounds().getCenterX();
		double yS=sourceComp.getBounds().getCenterY();
		double wS=sourceComp.getBounds().getCenterY();
		double hS=sourceComp.getBounds().getCenterY();
		
		double xT=targetComp.getBounds().getCenterX();
		double yT=targetComp.getBounds().getCenterY();

		int x=5;
		int y=13;
		
		double dx= xT-xS;
		if(dx > extension*wS)
			x=7;
		if(dx < -extension*wS)
			x=3;
		
		double dy= yT-yS;
		if(dy > extension*hS)
			y=17;
		if(dy < -extension*hS)
			y=11;
		
		return x*y;
	}
	
	public static int relativePositionOfTarget(CanvasComponent sourceComp, CanvasComponent targetComp)
	{
		return relativePositionOfTarget(sourceComp, targetComp,2.0f);
	}	
	
}
