package mayday.vis3.graph.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.HashMap;

import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.typed.IntSetting;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.model.ViewModel;

public class ProbeListLayout extends CanvasLayouterPlugin
{
	private ViewModel viewModel;
	private IntSetting xSpace=new IntSetting("Horizontal Spacer",null,20);
	private IntSetting ySpace=new IntSetting("Vertical Spacer",null,30);
	
	public ProbeListLayout()
	{
		initSetting();
	}
	
	public ProbeListLayout(ViewModel model)
	{
		initSetting();
		this.viewModel=model;		
	}

	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		// partition probes
		MultiTreeMap<ProbeList, CanvasComponent> map=new MultiTreeMap<ProbeList, CanvasComponent>();
		for(CanvasComponent c:model.getComponents())
		{
			if(c instanceof MultiProbeComponent)
			{
				map.put(viewModel.getTopPriorityProbeList(((MultiProbeComponent) c).getFirstProbe()),c);
			}
		}
		
		int usedSpace=bounds.x+xSpace.getIntValue();
		int maxY=0;
		int yPos=bounds.y+ySpace.getIntValue();
		
		for(ProbeList pl:map.keySet())
		{
			for(CanvasComponent comp:map.get(pl))
			{
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
			usedSpace=bounds.x+xSpace.getIntValue();
			maxY+=ySpace.getIntValue();
			yPos=maxY;
		}
	}
	
	@Override
	public void setViewModel(ViewModel model) 
	{
		viewModel=model;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.ProbeList",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Arranges components grouped by probe list",
				"Probe List"				
		);
		return pli;	
	}
	
	@Override
	protected void initSetting() 
	{
		setting.addSetting(xSpace).addSetting(ySpace);		
	}
	

}
