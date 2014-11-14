package mayday.vis3.graph.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.typed.IntSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.algorithm.CycleRemoval;
import mayday.vis3.graph.model.GraphModel;

public class SugiyamaLayout extends CanvasLayouterPlugin
{
	
	private IntSetting margin=new IntSetting("Margin",null,50);	
	private IntSetting xSpace=new IntSetting("Horizontal Spacer",null,20);
	private IntSetting ySpace=new IntSetting("Vertical Spacer",null,30);
	
	public SugiyamaLayout() 
	{
		initSetting();
	}
	
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		Graph gr=model.getGraph();
		Graph g=Graphs.flatCopy(gr);
		new CycleRemoval().removeCycles(g);
		Map<Node,Integer> lambda=longestPathLayer(g);
		Map<Integer, Integer> lHeight=calculateHeight(lambda,model);
		// get max layer;
		int max=0;
		for(Node n:g.getNodes())
		{
			if(lambda.get(n) > max) 
				max=lambda.get(n);
		}
		Map<Node,Integer> heights=new HashMap<Node, Integer>();
		for(Node n:g.getNodes())
		{
//			heights.put(n, (max-lambda.get(n))*spacer);
//			heights.put(n, (max-lambda.get(n))*spacer-lHeight.get(lambda.get(n)));
			heights.put(n, lHeight.get(lambda.get(n)));
			if(lambda.get(n) > max) 
				max=lambda.get(n);
		}
		Map<Node,Double> x=layerOrdering(g, lambda,model);
		Map<Node,Double> x2=adjustLevels(x,lambda, xSpace.getIntValue(),model);
		
		for(Node n:g.getNodes())
		{
//			model.getComponent(n).setLocation(margin+x2.get(n).intValue(), margin+heights.get(n));
			model.getComponent(n).setLocation(x2.get(n).intValue(), margin.getIntValue()+heights.get(n));
		}
		
	}
	
	private Map<Integer, Integer> calculateHeight(Map<Node, Integer> lambda,GraphModel model) 
	{
		Map<Integer, Integer> res=new HashMap<Integer, Integer>();
		for(Integer i:lambda.values())
			res.put(i, 0);
		res.put(-1,0);
		
		int lMax=Collections.max(lambda.values());
		res.put(lMax+1,0);
		for(Node n:lambda.keySet())
		{
			if(model.getComponent(n).getHeight() > res.get(lambda.get(n)))
			{
				res.put(lambda.get(n), model.getComponent(n).getHeight());
			}
			
		}
		Map<Integer, Integer> resf=new HashMap<Integer, Integer>();
		int h=ySpace.getIntValue(); 
		for(int i=lMax; i>=0; --i)
		{
			resf.put(i, h);
			h+=res.get(i)+ySpace.getIntValue();
		}
		return resf;
	}

	/**
	 * Calculates the layer of each node in the graph. 
	 * @see Eades and Sugiyama, J Inf Proc 13(4) (1990) p. 424-437 
	 * @param  The graph to be layered.
	 * @return A Map (Node-->int) in which the value is the layer of the node. 
	 */
	public Map<Node,Integer> longestPathLayer(Graph g)
	{
		// 1. topologically sort the nodes of g; 
		List<Node> nodes= Graphs.topologicalSort(g);
		// 2. FOREACH NODE v DO lambda(v) <- 0;
		Map<Node, Integer> lambda = new HashMap<Node, Integer>();
		for(Node v:nodes)
		{
			lambda.put(v, 0);
		}
		// 3. FOREACH node v in reverse topological order DO
		Collections.reverse(nodes);
		//   FOREACH node u in inEdges(v) DO
		for(Node v:nodes)
		{
			for(Node u:g.getInNeighbors(v))
			{
				int lu = Math.max(lambda.get(u), lambda.get(v)+1);
				lambda.put(u, lu);
			}
		}
		return lambda;
	}
	
	public Map<Node,Double> layerOrdering(Graph g, Map<Node, Integer> lambda, GraphModel model)
	{
		Map<Node,Double> xCoords=new HashMap<Node, Double>();
		List<Node> layerZero=new ArrayList<Node>();
		for(Node n:g.getNodes())
		{
			if(lambda.get(n)==0)
				layerZero.add(n);
		}
		xCoords.putAll(initialOrdering(layerZero,model));
		int l=1;
		while(true)
		{
			List<Node> layer=new ArrayList<Node>();
			for(Node n:g.getNodes())
			{
				if(lambda.get(n)==l)
					layer.add(n);
			}
			if(layer.isEmpty()) break;
			
			xCoords.putAll(barycenter(g, layer, xCoords,model));
			
		
			l++;
		}		
		return xCoords;
	}
	
	public Map<Node,Double> barycenter(Graph g, List<Node> layer, Map<Node,Double> xCoords, GraphModel model)
	{
		Map<Node,Double> xNew=new HashMap<Node, Double>();
		for(int i=0; i!=layer.size(); ++i)
		{
			double bc=0;
			int nCount=0;
			for(Node u:g.getOutNeighbors(layer.get(i)))
			{
				bc+=xCoords.get(u)+model.getComponent(u).getWidth()+xSpace.getIntValue();
				nCount++;
			}			
			xCoords.put(layer.get(i), bc/nCount);
		}		
		return xNew;
	}
	
	public Map<Node,Double> barycenter2(Graph g, List<Node> layer, Map<Node,Double> xCoords)
	{
		Map<Node,Double> xNew=new HashMap<Node, Double>();
		for(int i=0; i!=layer.size(); ++i)
		{
			double bc=0;
			int nCount=0;
			for(Node u:g.getOutNeighbors(layer.get(i)))
			{
				bc+=xCoords.get(u);
				nCount++;
			}			
			xCoords.put(layer.get(i), bc/nCount);
		}		
		return xNew;
	}
	
	public Map<Node,Double> adjustLevels(Map<Node,Double> x, Map<Node,Integer> lambda, int space, GraphModel model)
	{
		Map<Node,Double> xCoords=new HashMap<Node, Double>();
		int l=0;
		// calculate overall widths of levels:
		Map<Integer,Integer> lWidths=new HashMap<Integer, Integer>();
		int max=0;
		while(true)
		{
			List<Node> layer=new ArrayList<Node>();
			for(Node n:lambda.keySet())
			{
				if(lambda.get(n)==l)
					layer.add(n);
			}
			if(layer.isEmpty()) break;					
			
			int w=0;
			for(Node n:layer)
			{
				w+=model.getComponent(n).getWidth()+xSpace.getIntValue();
			}
			lWidths.put(l, w);
			if(w > max)
				max=w;
			l++;
		}
		
		l=0;
		XRankComparator xc=new XRankComparator(x);
		while(true)
		{
			List<Node> layer=new ArrayList<Node>();
			for(Node n:lambda.keySet())
			{
				if(lambda.get(n)==l)
					layer.add(n);
			}
			if(layer.isEmpty()) break;			
			Collections.sort(layer, xc);
			
			int width=lWidths.get(l);	// width of layer
			int x0=(max-width)/2+xSpace.getIntValue(); // leftover space
			int i=0;
			for(Node n:layer)
			{
				xCoords.put(n,(double)x0 );
				x0+=model.getComponent(n).getWidth()+xSpace.getIntValue();
				++i;
			}
			l++;
		}		
		return xCoords;
	}
	
	public Map<Node,Double> adjustLevels2(Map<Node,Double> x, Map<Node,Integer> lambda, int space)
	{
		Map<Node,Double> xCoords=new HashMap<Node, Double>();
		int l=0;
		XRankComparator xc=new XRankComparator(x);
		while(true)
		{
			List<Node> layer=new ArrayList<Node>();
			for(Node n:lambda.keySet())
			{
				if(lambda.get(n)==l)
					layer.add(n);
			}
			if(layer.isEmpty()) break;			
			Collections.sort(layer, xc);
			
			int i=0;
			for(Node n:layer)
			{
				xCoords.put(n,(double)(i*space) );
				++i;
			}
			l++;
		}
		
		return xCoords;
	}
	
	private class XRankComparator implements Comparator<Node>
	{
		private Map<Node,Double> lambda;
		
		public XRankComparator(Map<Node,Double> lambda) 
		{
			this.lambda=lambda;
		}
		
		public int compare(Node o1, Node o2) 
		{
			return lambda.get(o1).compareTo(lambda.get(o2));
			
		}
	}
	
	public Map<Node,Double> initialOrdering(List<Node> nodes, GraphModel model)
	{
//		Collections.shuffle(nodes);
		Map<Node,Double> xCoords=new HashMap<Node, Double>();
		
		int x=xSpace.getIntValue();
		
		for(int i=0; i!=nodes.size(); ++i)
		{
//			xCoords.put(nodes.get(i), model.getComponent(nodes.get(i)).getWidth()+1.0*xSpace*i);
			xCoords.put(nodes.get(i), 1.0*x);
			x+=model.getComponent(nodes.get(i)).getWidth()+xSpace.getIntValue();
		}		
		return xCoords;
	}
	
	@Override
	protected void initSetting() 
	{
		setting.addSetting(xSpace).addSetting(ySpace).addSetting(margin);		
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.Sugiyama",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Hierarchical Layout using the Sugiyama framework",
				"Hierarchical"				
		);
		return pli;	
	}
	
	

}
