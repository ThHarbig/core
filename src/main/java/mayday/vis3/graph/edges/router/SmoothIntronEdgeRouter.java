package mayday.vis3.graph.edges.router;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class SmoothIntronEdgeRouter extends IntronEdgeRouter 
{
	public SmoothIntronEdgeRouter() 
	{
		super(true);
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.EdgeRouter.SmoothIntrons",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Smooth introns",
				"Intron (Smooth)"				
		);
		return pli;
	}
	
}
