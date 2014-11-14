package mayday.core.plugins.pluggableUI;

import java.awt.Component;

import mayday.core.Mayday;
import mayday.core.gui.WindowListPanel;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.GenericPlugin;

public class WindowList extends AbstractPlugin implements GenericPlugin {

	protected static Component myComponent;
	
	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(), 
				"PAS.pluggableUI.windowlist", 
				null, 
				Constants.MC_PLUGGABLEVIEWS, 
				null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de",
				"Displays the list of open windows",
				"List of open windows"
		);
	}

	@Override
	public void run() {
		if (myComponent==null)
			myComponent = new WindowListPanel();
		Mayday.sharedInstance.addPluggableViewElement(myComponent, "Windows");
	}
		
	
}
