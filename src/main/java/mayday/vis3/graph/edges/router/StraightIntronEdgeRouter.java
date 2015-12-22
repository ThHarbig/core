package mayday.vis3.graph.edges.router;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class StraightIntronEdgeRouter extends IntronEdgeRouter {

	public StraightIntronEdgeRouter() 
	{
		super(false);
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.EdgeRouter.Intron",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Classical Introns",
				"Intron"				
		);
		return pli;
	}

}
