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

public class CurvyEdges extends AbstractEdgeRouter
{
	private double factor=0.5d;
	private double alpha=Math.PI/3;
	public static CurvyEdges router=new CurvyEdges();
			
	public static EdgeRouter instance() 
	{
		return router;
	}
	
	public Point2D getSupportPoint(CanvasComponent source, CanvasComponent target) 
	{
		EdgePoints p=getAdjustedPoints(source, target);
		return calculateSupportPoint(p.source, p.target, alpha, factor);		
	}
	
	/**
	 * @param s source point
	 * @param t target point
	 * @param angle angle of the support point (-2pi ... 2pi)
	 * @param factor fraction of the distance between s and t for placing the support point (usually 0...1). 
	 */
	public Point2D calculateSupportPoint(Point2D s, Point2D t, double alpha, double factor)
	{
		double l=t.distance(s)*factor;
		double ang=Math.atan2 (s.getY() - t.getY(), s.getX() - t.getX()) + Math.PI;
		double x1 = (s.getX() + l * Math.cos(ang+alpha));
	    double y1 = (s.getY() + l * Math.sin(ang+alpha));
	    
	    return new Point2D.Double(x1,y1);
		
	}
	
	public Path2D routeEdge(Edge e, Container container, GraphModel model) 
	{
		if(e.getSource()==e.getTarget())
		{
			return returnEdge(model.getComponent(e.getSource()), model.getComponent(e.getTarget()));
		}
		EdgePoints points=getAdjustedPoints(model.getComponent(e.getSource()), model.getComponent(e.getTarget()));
		Path2D path=new Path2D.Double();
		Point2D cp=calculateSupportPoint(points.source, points.target, alpha, factor);
		
		path.moveTo(points.source.getX(),points.source.getY());
		path.quadTo(cp.getX(), cp.getY(), points.target.getX(),points.target.getY());				
		path.moveTo(points.target.getX(), points.target.getY());
		
		path.closePath();
		return path;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.EdgeRouter.CurvyEdges",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Draw curves as edges",
				"Curvy"				
		);
		return pli;
	}
	
	


}
