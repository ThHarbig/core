package mayday.vis3.graph.edges.router;

import java.awt.Container;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import mayday.core.structures.graph.Edge;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

/**
 * Edge Routers assign a shape (including arrow heads) to an edge. 
 * @author Stephan Symons
 * @version 1.0
 */
public interface EdgeRouter 
{
	/**
	 * @param e The edge to be routed
	 * @param style	The arrow style to be used
	 * @param container The container to be drawn to
	 * @param model the graph model the edge is from 
	 * @return
	 */
	public Path2D routeEdge(Edge e, Container container, GraphModel model);
	
	public EdgePoints getAdjustedPoints(CanvasComponent source, CanvasComponent target);
	
	public Point2D getSupportPoint(CanvasComponent source, CanvasComponent target);
}
