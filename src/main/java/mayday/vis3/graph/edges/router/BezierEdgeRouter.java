package mayday.vis3.graph.edges.router;

import java.awt.Container;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public abstract class BezierEdgeRouter extends AbstractEdgeRouter
{
	private RoutingStyle style;
	
	public BezierEdgeRouter() 
	{
		style=RoutingStyle.DIAGONAL;
	}
	
	public BezierEdgeRouter(RoutingStyle style) 
	{
		this.style=style;
	}
	
	public Path2D routeEdge(Edge e, Container container, GraphModel model) 
	{
		CanvasComponent source=model.getComponent(e.getSource());
		CanvasComponent target=model.getComponent(e.getTarget());
		
		if(directEdge(source, target))
		{
			return SimpleEdgeRouter.router.routeEdge(e, container, model);
		}
		
		EdgePoints points=getAdjustedPoints(source, target);
		
		Point2D support=null;
		switch (style) 
		{
			case DIAGONAL: support=diagonalSupportPoint(points); 
			break;
			case OVERHAND: support=overhandSupportPoint(points); break;
			case UNDERHAND: support=underhandSupportPoint(points); break;
			case SMART: 
				Node sourceNode=model.getNode(source);
				Node targetNode=model.getNode(target);
				support=smartSupportPoint(points,sourceNode, targetNode,container,model ); break;
	
			default:
				break;
		}
		Path2D p=createCurve(points, support);
//		p.append(createCurve(points.swapped(), support), false);
//		p.moveTo(p.getCurrentPoint().getX(),p.getCurrentPoint().getY());
//		p.closePath();
		return p;
	}
	
	protected Path2D createCurve(EdgePoints p, Point2D support)
	{
		Path2D path=new Path2D.Double();
		path.moveTo(p.source.getX(), p.source.getY());
		path.curveTo(
				support.getX(),		support.getY(),
				support.getX(),		support.getY(),
				p.target.getX(),	p.target.getY()); 
		
		return path;
	}
	
	protected Point2D smartSupportPoint(EdgePoints p, Node source, Node target, Container parent, GraphModel model)
	{
		// start diagonal;
		Point2D support= diagonalSupportPoint(p);
		if(!intersects(p, support, source, target, parent, model))
		{
			return support;
		}
		// overhand
		support= overhandSupportPoint(p);
		if(!intersects(p, support, source, target, parent, model))
		{
			return support;
		}
		// underhand
		support= underhandSupportPoint(p);
		if(!intersects(p, support, source, target, parent, model))
		{
			return support;
		}
		// straight.
		support= new Point2D.Double(
			(p.source.getX()+p.target.getX())/2.0,
			(p.source.getY()+p.target.getY())/2.0);
		return support;
	}
	
	/* (non-Javadoc)
	 * @see mayday.canvas.layout.edges.AbstractEdgeRouter#getAdjustedPoints(mayday.canvas.components.CanvasComponent, mayday.canvas.components.CanvasComponent)
	 */
	@Override
	public EdgePoints getAdjustedPoints(CanvasComponent source,
			CanvasComponent target)
	{
		if(directEdge(source, target))
		{
			return SimpleEdgeRouter.router.getAdjustedPoints(source, target);
		}
		
		switch (style) {
		case DIAGONAL: return super.getAdjustedPoints(source, target);
		case OVERHAND: return overhandAdjustedPoints(source, target);	
		case UNDERHAND: return underhandAdjustedPoints(source, target);		
		case SMART: return super.getAdjustedPoints(source, target);
		default: return super.getAdjustedPoints(source, target);
		}
	}
	
	protected EdgePoints overhandAdjustedPoints(CanvasComponent source, CanvasComponent target)
	{
		double x= target.getBounds().getCenterX() -source.getBounds().getCenterX();
		double y= target.getBounds().getCenterY() -source.getBounds().getCenterY();	
		
		if (directEdge(source, target))
		{
			return super.getAdjustedPoints(source, target);
		}
		Point2D start = null;
		Point2D end=null;		
		if(x >= 0 && y >= 0 )
		{
			start=new Point2D.Double(source.getBounds().getCenterX(), source.getBounds().getMaxY());
			end=new Point2D.Double(target.getBounds().getMinX(), target.getBounds().getCenterY());
		}
		
		if(x >= 0 && y < 0)
		{
			start=new Point2D.Double(source.getBounds().getCenterX(), source.getBounds().getMinY());
			end=new Point2D.Double(target.getBounds().getMinX(), target.getBounds().getCenterY());
		}
		if(x < 0 && y >=0)
		{
			start=new Point2D.Double(source.getBounds().getCenterX(), source.getBounds().getMaxY());
			end=new Point2D.Double(target.getBounds().getMaxX(), target.getBounds().getCenterY());
		}
		if(x < 0 && y < 0)
		{
			start=new Point2D.Double(source.getBounds().getCenterX(), source.getBounds().getCenterY());
			end=new Point2D.Double(target.getBounds().getMaxX(), target.getBounds().getCenterY());
		}		
		return new EdgePoints(start,end);
	}
	
	protected EdgePoints underhandAdjustedPoints(CanvasComponent source, CanvasComponent target)
	{
		if (directEdge(source, target))
		{
			return super.getAdjustedPoints(source, target);
		}
		
		double x= target.getBounds().getCenterX() -source.getBounds().getCenterX();
		double y= target.getBounds().getCenterY() -source.getBounds().getCenterY();		
		Point2D start = null;
		Point2D end=null;		
		if(x >= 0 && y >= 0 )
		{
			start=new Point2D.Double(source.getBounds().getMaxX(), source.getBounds().getCenterY());
			end=new Point2D.Double(target.getBounds().getCenterX(), target.getBounds().getMinY());
		}
		
		if(x >= 0 && y < 0)
		{
			start=new Point2D.Double(source.getBounds().getMaxX(), source.getBounds().getCenterY());
			end=new Point2D.Double(target.getBounds().getCenterX(), target.getBounds().getMaxY());
		}
		if(x < 0 && y >=0)
		{
			start=new Point2D.Double(source.getBounds().getMinX(), source.getBounds().getCenterY());
			end=new Point2D.Double(target.getBounds().getCenterX(), target.getBounds().getMinY());
		}
		if(x < 0 && y < 0)
		{
			start=new Point2D.Double(source.getBounds().getMinX(), source.getBounds().getCenterY());
			end=new Point2D.Double(target.getBounds().getCenterX(), target.getBounds().getMaxY());
		}		
		return new EdgePoints(start,end);
	}
	
	protected Point2D overhandSupportPoint(EdgePoints points)
	{
		if(directEdge(points))
		{
			return points.source;
		}
		return new Point2D.Double(points.source.getX(), points.target.getY());
	}
	
	protected Point2D underhandSupportPoint(EdgePoints points)
	{
		if(directEdge(points))
		{
			return points.source;
		}
		return new Point2D.Double(points.target.getX(), points.source.getY());
	}
	
	protected Point2D diagonalSupportPoint(EdgePoints points)
	{
		if(directEdge(points))
		{
			return points.source;
		}
		double x= points.target.getX() - points.source.getX();
		double y= points.target.getY() - points.source.getY();	
		double c= Math.min(Math.abs(x), Math.abs(y));
		
		return new Point2D.Double(points.source.getX()+Math.signum(x)*c, points.source.getY()+Math.signum(y)*c);
	}
	
	protected boolean intersects(EdgePoints p, Point2D support, Node source, Node target, Container parent, GraphModel model)
	{
		Line2D.Double line1=new Line2D.Double(p.source,support);
		Line2D.Double line2=new Line2D.Double(support,p.target);
		
		for(int i=0; i!= parent.getComponentCount(); ++i)
		{
			if( 
					line1.intersects(parent.getComponent(i).getBounds()) || 
					line2.intersects(parent.getComponent(i).getBounds()) && 
					(model.getNode((CanvasComponent)parent.getComponent(i))!=source && 
					model.getNode((CanvasComponent)parent.getComponent(i))!=target) )
			{				
				return true;				
			}
		}
		return false;
	}
	
	public static enum RoutingStyle
	{
		OVERHAND, UNDERHAND, SMART, DIAGONAL
	}


	public EdgePoints getArrowPoints(CanvasComponent source, CanvasComponent target)
	{
		EdgePoints p=new EdgePoints(source.getLocation(),target.getLocation());
		
		Point2D pR=null;
		switch (style) {
		case DIAGONAL: 
			pR= diagonalSupportPoint(p); 
			break;
		case OVERHAND:
			pR= overhandSupportPoint(p); 
			break;
		case UNDERHAND: 
			pR= underhandSupportPoint(p); 
			break;
		case SMART:
			pR=diagonalSupportPoint(p); 
			break;
		default: 
		}
		return new EdgePoints(pR,p.target);
		
	}


	/**
	 * @return the style
	 */
	public RoutingStyle getStyle() {
		return style;
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(RoutingStyle style) {
		this.style = style;
	}

	public Point2D getSupportPoint(CanvasComponent source, CanvasComponent target) 
	{
		if(directEdge(source, target))
		{
			return SimpleEdgeRouter.router.getSupportPoint(source, target);
		}
		EdgePoints p=new EdgePoints(source.getLocation(),target.getLocation());
		Point2D res=null;
		switch (style) {
		case DIAGONAL: 
			res= diagonalSupportPoint(p);
			break;
		case OVERHAND:
			res=  overhandSupportPoint(p); 
			break;
		case UNDERHAND: 
			res= underhandSupportPoint(p); 
			break;
		case SMART:
			res= diagonalSupportPoint(p); 
			break;
		default: 
			res= diagonalSupportPoint(p);
		}
		return new Point((int)res.getX(),(int)res.getY());
	}
}
