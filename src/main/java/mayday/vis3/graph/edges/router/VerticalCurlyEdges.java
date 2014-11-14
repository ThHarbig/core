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

public class VerticalCurlyEdges extends AbstractEdgeRouter 
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
		case CanvasComponent.PORT_TOP_RIGHT: 
			sport=source.getPort(CanvasComponent.PORT_CENTER_RIGHT);
			tport=target.getPort(CanvasComponent.PORT_CENTER_LEFT);				
			break;

		case CanvasComponent.PORT_CENTER_RIGHT: 
			sport=source.getPort(CanvasComponent.PORT_CENTER_RIGHT);
			tport=target.getPort(CanvasComponent.PORT_CENTER_LEFT);			
			break;

		case CanvasComponent.PORT_BOTTOM_RIGHT: 
			sport=source.getPort(CanvasComponent.PORT_CENTER_RIGHT);
			tport=target.getPort(CanvasComponent.PORT_CENTER_LEFT);			
			break;	

		case CanvasComponent.PORT_BOTTOM_CENTER: 
			sport=source.getPort(CanvasComponent.PORT_BOTTOM_CENTER);
			tport=target.getPort(CanvasComponent.PORT_TOP_CENTER);			
			break;

		case CanvasComponent.PORT_BOTTOM_LEFT: 
			sport=source.getPort(CanvasComponent.PORT_CENTER_LEFT);
			tport=target.getPort(CanvasComponent.PORT_CENTER_RIGHT);				
			break;		

		case CanvasComponent.PORT_CENTER_LEFT: 
			sport=source.getPort(CanvasComponent.PORT_CENTER_LEFT);
			tport=target.getPort(CanvasComponent.PORT_CENTER_RIGHT);				
			break;	

		case CanvasComponent.PORT_TOP_LEFT: 
			sport=source.getPort(CanvasComponent.PORT_CENTER_LEFT);
			tport=target.getPort(CanvasComponent.PORT_CENTER_RIGHT);			
			break;

		case CanvasComponent.PORT_TOP_CENTER: 
			sport=source.getPort(CanvasComponent.PORT_TOP_CENTER);
			tport=target.getPort(CanvasComponent.PORT_CENTER_RIGHT);			
			break;


		case CanvasComponent.PORT_CENTER_CENTER: 
			sport=source.getPort(CanvasComponent.PORT_CENTER_CENTER);
			tport=source.getPort(CanvasComponent.PORT_CENTER_CENTER);			
			break;

		default:
			break;
		}
		return new EdgePoints(sport, tport);
	}

	@Override
	public Point2D getSupportPoint(CanvasComponent source, CanvasComponent target) 
	{

		int rel=relativePositionOfTarget(source, target,20);
		if(rel==CanvasComponent.PORT_CENTER_CENTER)
			rel=relativePositionOfTarget(source, target,0);

		EdgePoints points=getAdjustedPoints(source, target);		
		return new Point2D.Double( (points.source.getX()+points.target.getX())/2, (points.source.getY()+points.target.getY())/2 );
	}

	public Point2D getSupportPoint1(CanvasComponent source, CanvasComponent target) 
	{

		int rel=relativePositionOfTarget(source, target);
		if(rel==CanvasComponent.PORT_CENTER_CENTER)
			rel=relativePositionOfTarget(source, target,0);
		EdgePoints points=getAdjustedPoints(source, target);

		double dx= points.target.getX() - points.source.getX();
		return new Point2D.Double(points.source.getX()+dx/2.0 ,points.source.getY());	
	}

	public Point2D getSupportPoint2(CanvasComponent source, CanvasComponent target) 
	{

		int rel=relativePositionOfTarget(source, target);
		if(rel==CanvasComponent.PORT_CENTER_CENTER)
			rel=relativePositionOfTarget(source, target,0);
		EdgePoints points=getAdjustedPoints(source, target);

		double dx= points.target.getX() - points.source.getX();
		return new Point2D.Double(points.source.getX()+dx/2.0 ,points.target.getY());	
	}

	@Override
	public Path2D routeEdge(Edge e, Container container, GraphModel model) 
	{
		CanvasComponent source=model.getComponent(e.getSource());
		CanvasComponent target=model.getComponent(e.getTarget());

		EdgePoints points=getAdjustedPoints(source, target);		
		//		Point2D support=getSupportPoint(source, target);

		Path2D p=new Path2D.Double();
		p.moveTo(points.source.getX(), points.source.getY());

		Point2D s1=getSupportPoint1(source, target);
		Point2D s2=getSupportPoint2(source,target);

		p.curveTo(s1.getX(), s1.getY(), s2.getX(), s2.getY(), points.target.getX(), points.target.getY());
		return p;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.EdgeRouter.CurlyVertical",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Curly edges (vertical style)",
				"Curly (vertical)"				
		);
		return pli;
	}


}
