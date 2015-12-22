package mayday.vis3.graph.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.algorithm.NodeDegreeComparator;
import mayday.core.structures.maps.MultiHashMap;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.model.GraphModel;

public class GridLayouter extends CanvasLayouterPlugin
{
	public static final int FILL=0;
	public static final int HORIZONTAL=1;
	public static final int VERTICAL=2;

	private RestrictedStringSetting modeSetting=new RestrictedStringSetting("Mode", "How the components should be placed:\n" +
			"fill= fill the up the space\n, " +
			"horizontal: place all components on a horizontal line\n" +
			"vertical: place all components on a vertical line", 0, new String[]{"fill","horizontal","vertical"});

	private IntSetting xSpace=new IntSetting("Horizontal Spacer",null,20);
	private IntSetting ySpace=new IntSetting("Vertical Spacer",null,30);

	private IntSetting spacer=new IntSetting("Between Groups Space", null, 50);

	private BooleanHierarchicalSetting sortSetting=new BooleanHierarchicalSetting("Sort", "sort the components", false);
	private BooleanHierarchicalSetting groupSetting=new BooleanHierarchicalSetting("Group By", "group the components", false);

	private static final String[] sortingStyles={"Node name", "Node Degree", "Node InDegree" , "Node OutDegree"};
	private static final String[] groupingStyles={"Connected Component", "ProbeList", "DataSet"};


	private RestrictedStringSetting sortingStyle=new RestrictedStringSetting("Sort by", null, 0, sortingStyles);
	private RestrictedStringSetting groupingStyle=new RestrictedStringSetting("Group by", null, 1, groupingStyles);




	public GridLayouter() 
	{	
		initSetting();
	}

	public GridLayouter(int mode) 
	{		
		this.modeSetting.setSelectedIndex(mode);
		initSetting();
	}

	@Override
	protected void initSetting() 
	{
		setting.addSetting(modeSetting).addSetting(xSpace).addSetting(ySpace);
		sortSetting.addSetting(sortingStyle);
		groupSetting.addSetting(groupingStyle).addSetting(spacer);

		setting.addSetting(sortSetting).addSetting(groupSetting);

	}

	public void layout(Container container, Rectangle bounds, GraphModel model)
	{
		// 1st: group
		MultiHashMap<Object, Node> groups=new MultiHashMap<Object, Node>();
		if(groupSetting.getBooleanValue())
		{
			switch(groupingStyle.getSelectedIndex())
			{
			case 0: groups=groupByConnectedComponent(model);
			break;
			case 1: groups=groupByProbeList(model);
			break;
			case 2: groups=groupByDataSet(model);
			break;
			}
		}else
		{
			Object o=new Object();
			for(CanvasComponent cc: model.getComponents())
			{
				if(cc instanceof NodeComponent)
					groups.put(o, ((NodeComponent)cc).getNode());
			}			
		}
				
		// 2nd: sort and deploy
		int grp=0;
		for(Object o: groups.keySet())
		{
			List<Node> nodes=new ArrayList<Node>(groups.get(o,true));
			switch(groupingStyle.getSelectedIndex())
			{
			case 0: 
				Collections.sort(nodes);				
				break;
			case 1: 
				Collections.sort(nodes, new NodeDegreeComparator(NodeDegreeComparator.DEGREE));
				break;
			case 2: 
				Collections.sort(nodes, new NodeDegreeComparator(NodeDegreeComparator.INDEGREE));
				break;
			case 3: 
				Collections.sort(nodes, new NodeDegreeComparator(NodeDegreeComparator.OUTDEGREE));
				break;				
			}
			
			switch (modeSetting.getSelectedIndex()) 
			{
			case 0:
				grp=layoutFill(container, 
						new Rectangle(bounds.x, bounds.y+grp, bounds.width , bounds.height), model, nodes )+spacer.getIntValue();
				break;
			case 1:
				grp=layoutHorizontal(container, new Rectangle(bounds.x, bounds.y+grp, bounds.width , bounds.height), model, nodes);	
				break;
			case 2:
				grp=layoutVertical(container, new Rectangle(bounds.x+grp, bounds.y, bounds.width , bounds.height), model, nodes);	
				break;
			default:
				throw new IllegalArgumentException("Wrong layout model");
			}

		}



	}

	private MultiHashMap<Object, Node> groupByConnectedComponent(GraphModel model)
	{
		Graph g =model.getGraph();
		List<List<Node>> comps=Graphs.calculateComponents(g);
		MultiHashMap<Object, Node> res=new MultiHashMap<Object, Node>();
		int i=0;
		for(List<Node> comp: comps)
		{
			for(Node node:comp)
			{
				res.put(i,node);
			}
			++i;
		}
		return res;
	}

	private MultiHashMap<Object, Node> groupByProbeList(GraphModel model)
	{
		MultiHashMap<Object, Node> res=new MultiHashMap<Object, Node>();
		Object empty=new Object();
		for(CanvasComponent cc: model.getComponents())
		{
			if(cc instanceof MultiProbeComponent)
			{
				List<ProbeList> pls=((MultiProbeComponent) cc).getProbes().get(0).getProbeLists();
				Collections.sort(pls, Collections.reverseOrder(new ProbeListSizeComparator()));				
				res.put(pls.get(0), ((MultiProbeComponent) cc).getNode());				
			}else
			{
				res.put(empty, ((NodeComponent)cc).getNode());
			}
		}
		return res;
	}

	private MultiHashMap<Object, Node> groupByDataSet(GraphModel model)
	{
		MultiHashMap<Object, Node> res=new MultiHashMap<Object, Node>();
		Object empty=new Object();
		for(CanvasComponent cc: model.getComponents())
		{
			if(cc instanceof MultiProbeComponent)
			{
				res.put(((MultiProbeComponent) cc).getProbes().get(0).getMasterTable().getDataSet(), ((MultiProbeComponent) cc).getNode());				
			}else
			{
				res.put(empty, ((NodeComponent)cc).getNode());
			}
		}
		return res;
	}

	public int layoutVertical(Container container, Rectangle bounds, GraphModel model, List<Node> group) 
	{
		int usedSpace=bounds.y+xSpace.getIntValue();
		int xPos=bounds.x+ySpace.getIntValue();
		for(Node n: group)
		{		
			CanvasComponent comp=model.getComponent(n);
			comp.setLocation(xPos, usedSpace );
			usedSpace+=xSpace.getIntValue()+comp.getHeight();					
		}
		return usedSpace;
	}

	public int layoutHorizontal(Container container, Rectangle bounds, GraphModel model, List<Node> group) 
	{
		int usedSpace=bounds.x+xSpace.getIntValue();
		int yPos=bounds.y+ySpace.getIntValue();

		for(Node n: group)
		{
			CanvasComponent comp=model.getComponent(n);
			comp.setLocation(usedSpace,yPos );
			usedSpace+=xSpace.getIntValue()+comp.getWidth();		
		}
		return usedSpace;
	}

	public int layoutFill(Container container, Rectangle bounds, GraphModel model, List<Node> group) 
	{
		int usedSpace=bounds.x+xSpace.getIntValue();
		int maxY=0;
		int yPos=bounds.y+ySpace.getIntValue();
		for(Node n : group)
		{			
			CanvasComponent comp=model.getComponent(n);
			if(usedSpace+comp.getWidth() > bounds.x+bounds.getWidth() )
			{
				usedSpace=bounds.x+xSpace.getIntValue();
				maxY+=ySpace.getIntValue();
				yPos=maxY;
			}
			comp.setLocation(usedSpace,yPos );
			usedSpace+=xSpace.getIntValue()+comp.getWidth();
			if(yPos+comp.getHeight() > maxY) maxY= yPos+comp.getHeight();			
		}		
		return yPos;
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.Grid",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Place components on a grid, or on one axis",
				"Grid"				
		);
		return pli;	
	}

	private class ProbeListSizeComparator implements Comparator<ProbeList>
	{
		@Override
		public int compare(ProbeList o1, ProbeList o2) 
		{
			return o1.getNumberOfProbes() - o2.getNumberOfProbes();
		}
	}
	
}
