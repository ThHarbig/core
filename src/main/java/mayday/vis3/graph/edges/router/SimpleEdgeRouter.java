package mayday.vis3.graph.edges.router;

import java.awt.Container;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Edge;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class SimpleEdgeRouter extends AbstractEdgeRouter
{

	public Path2D routeEdge(Edge e, Container container, GraphModel model) 
	{
		if(e.getSource()==e.getTarget())
		{
			return returnEdge(model.getComponent(e.getSource()), model.getComponent(e.getTarget()));
		}
		EdgePoints points=getAdjustedPoints(model.getComponent(e.getSource()), model.getComponent(e.getTarget()));
		Path2D path=new Path2D.Double();
		path.moveTo(points.source.getX(),points.source.getY());
		path.lineTo(points.target.getX(),points.target.getY());
				
		path.moveTo(points.target.getX(), points.target.getY());
		path.closePath();
		return path;
	}
	
	public static SimpleEdgeRouter router=new SimpleEdgeRouter();
	
	public static EdgeRouter instance() 
	{
		return router;
	}
	
	public Point2D getSupportPoint(CanvasComponent source, CanvasComponent target) 
	{
		EdgePoints p=getAdjustedPoints(source, target);
		return new Point2D.Double( (p.source.getX() + p.target.getX()) / 2.0,  (p.source.getY() + p.target.getY()) / 2.0);

	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.EdgeRouter.SimpleEdges",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Draw straigt lines",
				"Lines"				
		);
		return pli;
	}

}
