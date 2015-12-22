package mayday.core.plugins;

import java.util.HashMap;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.GenericPlugin;
import mayday.core.tasks.gui.TaskManagerFrame;

public class ShowTaskmanager extends AbstractPlugin implements GenericPlugin {

	
	private PluginInfo pli;
	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.ShowTaskmanager",
				new String[]{},
				Constants.MC_SESSION,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Shows the Mayday Task Manager",
				"Show Task Manager"
		);
		return pli;	
	}

	public void run() {
		TaskManagerFrame.getInstance().setVisible(true);
	}

}
