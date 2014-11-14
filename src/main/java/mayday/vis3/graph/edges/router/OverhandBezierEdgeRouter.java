package mayday.vis3.graph.edges.router;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class OverhandBezierEdgeRouter extends BezierEdgeRouter {

	public OverhandBezierEdgeRouter() 
	{
		super(RoutingStyle.OVERHAND);
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.EdgeRouter.Overhand",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Draw bezier curves (overhand style)",
				"Bezier (1)"				
		);
		return pli;
	}

}
