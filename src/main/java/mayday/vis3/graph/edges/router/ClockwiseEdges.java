package mayday.vis3.graph.edges.router;

import java.awt.Container;
import java.awt.Point;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Edge;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class ClockwiseEdges extends AbstractEdgeRouter
{

	@Override
	public EdgePoints getAdjustedPoints(CanvasComponent source,	CanvasComponent target)
	{
		int rel=relativePositionOfTarget(source, target);
		if(rel==CanvasComponent.PORT_CENTER_CENTER)
			rel=relativePositionOfTarget(source, target,0);

		
		
		Point sport=null;
		Point tport=null;

		switch (rel) 
		{
		case CanvasComponent.PORT_TOP_LEFT: 
			sport=source.getPort(CanvasComponent.PORT_CENTER_LEFT);
			tport=target.getPort(CanvasComponent.PORT_BOTTOM_CENTER);			
			break;
		case CanvasComponent.PORT_TOP_CENTER: 
			sport=source.getPort(CanvasComponent.PORT_TOP_CENTER);
			tport=target.getPort(CanvasComponent.PORT_BOTTOM_CENTER);			
			break;
		case CanvasComponent.PORT_TOP_RIGHT: 
			sport=source.getPort(CanvasComponent.PORT_TOP_CENTER);
			tport=target.getPort(CanvasComponent.PORT_CENTER_LEFT);				
			break;

		case CanvasComponent.PORT_CENTER_LEFT: 
			sport=source.getPort(CanvasComponent.PORT_CENTER_LEFT);
			tport=target.getPort(CanvasComponent.PORT_CENTER_RIGHT);				
			break;		
		case CanvasComponent.PORT_CENTER_CENTER: 
			sport=source.getPort(CanvasComponent.PORT_CENTER_CENTER);
			tport=source.getPort(CanvasComponent.PORT_CENTER_CENTER);			
			break;
		case CanvasComponent.PORT_CENTER_RIGHT: 
			sport=source.getPort(CanvasComponent.PORT_CENTER_RIGHT);
			tport=target.getPort(CanvasComponent.PORT_CENTER_LEFT);			
			break;	

		case CanvasComponent.PORT_BOTTOM_LEFT: 
			sport=source.getPort(CanvasComponent.PORT_BOTTOM_CENTER);
			tport=target.getPort(CanvasComponent.PORT_CENTER_RIGHT);				
			break;		
		case CanvasComponent.PORT_BOTTOM_CENTER: 
			sport=source.getPort(CanvasComponent.PORT_BOTTOM_CENTER);
			tport=target.getPort(CanvasComponent.PORT_TOP_CENTER);			
			break;
		case CanvasComponent.PORT_BOTTOM_RIGHT: 
			sport=source.getPort(CanvasComponent.PORT_CENTER_RIGHT);
			tport=target.getPort(CanvasComponent.PORT_TOP_CENTER);			
			break;		

		default:
			break;
		}

//		System.out.println(rel+":"+sport+","+tport);
		return new EdgePoints(sport, tport);
	}

	@Override
	public Point2D getSupportPoint(CanvasComponent source, CanvasComponent target) 
	{
		
		int rel=relativePositionOfTarget(source, target);
		if(rel==CanvasComponent.PORT_CENTER_CENTER)
			rel=relativePositionOfTarget(source, target,0);

		EdgePoints points=getAdjustedPoints(source, target);		
		
		if(rel% 5==0 || rel % 13 ==0)
			return new Point2D.Double( (points.source.getX()+points.target.getX())/2, (points.source.getY()+points.target.getY())/2 );
			
		if(rel==CanvasComponent.PORT_TOP_RIGHT)
		{
			return new Point2D.Double(points.source.getX(), points.target.getY());
		}
		if(rel==CanvasComponent.PORT_BOTTOM_LEFT  )
		{
			return new Point2D.Double(points.source.getX(), points.target.getY());
		}
		if(rel==CanvasComponent.PORT_BOTTOM_RIGHT)
		{
			return new Point2D.Double(points.target.getX(), points.source.getY());
		}
		if(rel==CanvasComponent.PORT_TOP_LEFT )
		{
			return new Point2D.Double(points.target.getX(), points.source.getY());
		}
		return null;		
	}

	@Override
	public Path2D routeEdge(Edge e, Container container, GraphModel model) 
	{
		CanvasComponent source=model.getComponent(e.getSource());
		CanvasComponent target=model.getComponent(e.getTarget());
		
		EdgePoints points=getAdjustedPoints(source, target);		
		Point2D support=getSupportPoint(source, target);
		
		Path2D p=createCurve(points, support);
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
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.EdgeRouter.ClockwiseEdges",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Draw edges as clockwise curves",
				"Clockwise"				
		);
		return pli;
	}
}
