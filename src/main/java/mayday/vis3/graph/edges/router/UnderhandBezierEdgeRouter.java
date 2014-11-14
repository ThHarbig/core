package mayday.vis3.graph.edges.router;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class UnderhandBezierEdgeRouter extends BezierEdgeRouter {

	public UnderhandBezierEdgeRouter() 
	{
		super(RoutingStyle.UNDERHAND);
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.EdgeRouter.Underhand",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Draw bezier curves (underhand style)",
				"Bezier (2)"				
		);
		return pli;
	}

}
