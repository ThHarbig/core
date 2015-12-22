package mayday.vis3.graph.edges.router;

import java.awt.Container;
import java.awt.geom.Path2D;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Edge;
import mayday.vis3.graph.model.GraphModel;

public class SmartEdgeRouter extends BezierEdgeRouter
{
	public SmartEdgeRouter() 
	{
		super(RoutingStyle.DIAGONAL);
	}
	
	public Path2D routeEdge(Edge e, Container container, GraphModel model) 
	{
		if(
				directEdge(model.getComponent(e.getSource()), model.getComponent(e.getTarget())) ||
				diagonalEdge(model.getComponent(e.getSource()), model.getComponent(e.getTarget()))
		)
		{
			EdgePoints points=getAdjustedPoints(model.getComponent(e.getSource()), model.getComponent(e.getTarget()));
			Path2D path=new Path2D.Double();
			path.moveTo(points.source.getX(),points.source.getY());
			path.lineTo(points.target.getX(),points.target.getY());
			path.moveTo(points.target.getX(), points.target.getY());
			path.closePath();
			return path;			
		}
		return super.routeEdge(e, container, model);		
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.EdgeRouter.SmartEdges",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Intelligently draw either lines or curves",
				"Smart"				
		);
		return pli;
	}
	

		
}
