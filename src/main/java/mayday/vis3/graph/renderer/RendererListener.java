/**
 * 
 */
package mayday.vis3.graph.renderer;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.renderer.dispatcher.RendererPluginSetting;

public class RendererListener implements SettingChangeListener
{
	private GraphCanvas canvas;
	
	public RendererListener(GraphCanvas canvas) 
	{
		this.canvas=canvas;
	}
	
	public void stateChanged(SettingChangeEvent e) 
	{

		
		for(Object o:e.getAdditionalSources())
		{
			if (o instanceof RendererPluginSetting)
			{
				canvas.setRendererSilent(((RendererPluginSetting)o).getRenderer());
			}
		}
		
		
	}
}