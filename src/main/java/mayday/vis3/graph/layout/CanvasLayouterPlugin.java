package mayday.vis3.graph.layout;

import mayday.core.gui.PreferencePane;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.model.ViewModel;

public abstract class CanvasLayouterPlugin extends AbstractPlugin implements CanvasLayouter
{
	public static final String  MC = "GraphViewer/Layout";
		
	protected HierarchicalSetting setting=new HierarchicalSetting("Layouter Settings");
	
	protected abstract void initSetting();

	@Override
	public void init() 
	{			
	}
	
	@Override
	public Setting getSetting() 
	{
		return setting;
	}
	
	/**
	 * This methods allows layouters to use informations form view models. The method is empty here, and is to be overloaded in implementing classes. 
	 * @param model
	 */
	public void setViewModel(ViewModel model)
	{
		// do nothing
	}
	
	@Override
	public PreferencePane getPreferencesPanel() {
		return null;
	}
	
}
