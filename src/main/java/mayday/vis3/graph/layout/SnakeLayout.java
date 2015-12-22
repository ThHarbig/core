package mayday.vis3.graph.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.typed.IntSetting;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.algorithm.DepthFirstIterator;
import mayday.vis3.graph.model.GraphModel;

public class SnakeLayout extends CanvasLayouterPlugin
{

	private IntSetting xSpace=new IntSetting("Horizontal Spacer",null,20);
	private IntSetting ySpace=new IntSetting("Vertical Spacer",null,30);

	public SnakeLayout() 
	{
		initSetting();
	}

	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		// find first node: 
		Node n0=Graphs.firstNode(model.getGraph());
		DepthFirstIterator dfs=new DepthFirstIterator(model.getGraph(),n0);
		// group in lines and preliminarily place:
		List<List<Component>> lines=new ArrayList<List<Component>>();
		final int width=bounds.width;
		List<Component> current=new ArrayList<Component>();
		List<Integer> leftOver=new ArrayList<Integer>();

		int usedSpace=0;

		int xSpace=this.xSpace.getIntValue();
		int ySpace=this.ySpace.getIntValue();
		while(dfs.hasNext())
		{
			Component comp=model.getComponent(dfs.next());
			if(usedSpace+comp.getWidth()+xSpace < width)
			{				
				usedSpace+=comp.getWidth()+xSpace;				
				current.add(comp);
			}else
			{				
				lines.add(current);					
				leftOver.add(bounds.width-usedSpace);
				current=new ArrayList<Component>();
				current.add(comp);				
				usedSpace=comp.getWidth()+xSpace;
			}			
		}
		lines.add(current);	
		leftOver.add(bounds.width-usedSpace);

		//		 straighten out nodes:
		
		int yPos=bounds.y+ySpace;
		int maxY=0;
		
		for(int i=0; i!= lines.size(); ++i)
		{
			usedSpace=bounds.x+xSpace;
			if(lines.get(i).size()==1)
			{
				Component comp=lines.get(i).get(0);
				comp.setLocation(usedSpace,yPos);	
				continue;
			}
			int spaceToUse=leftOver.get(i)/(lines.get(i).size()-1);
			
			for(Component comp:lines.get(i))
			{
				comp.setLocation(usedSpace,yPos);				
				if(yPos+comp.getHeight() > maxY) maxY= yPos+comp.getHeight();	
				usedSpace+=comp.getWidth()+xSpace+spaceToUse;
			}
			yPos=maxY+ySpace;
		}

		// assign direction
		boolean forward=true;
		for(int i=0; i!= lines.size(); ++i)
		{
			if(forward)
			{		
				for(Component comp:lines.get(i))
				{
					comp.setLocation(bounds.x+comp.getX(), comp.getY());
				}
			}
			else
			{
				for(Component comp:lines.get(i))
				{
					comp.setLocation(bounds.x+xSpace+width-comp.getX()-comp.getWidth(), comp.getY());
				}	
			}
			forward=!forward;
		}
		
	}
	
	@Override
	protected void initSetting() 
	{
		setting.addSetting(xSpace).addSetting(ySpace);		
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.Snake",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Fill a line, return in the opposite direction",
				"Snake"				
		);
		return pli;	
	}



}

