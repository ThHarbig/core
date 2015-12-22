package mayday.vis3.graph.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Random;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.vis3.graph.model.GraphModel;

public class RandomLayout extends CanvasLayouterPlugin
{
	

	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		Random random=new Random();
		for(int i=0; i!= container.getComponentCount(); ++i)
		{
			int x=bounds.x+random.nextInt(bounds.width-container.getComponent(i).getWidth());
			int y=bounds.y+random.nextInt(bounds.height-container.getComponent(i).getHeight());			
			container.getComponent(i).setLocation(x, y);			
		}		
	}
	
	@Override
	protected void initSetting() 
	{			
	}
	
	@Override
	public Setting getSetting() 
	{
		return null;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.Random",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Places components randomly.",
				"Random"				
		);
		return pli;	
	}
	
}
