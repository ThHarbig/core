package mayday.core.pluma;

import java.io.File;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.prototypes.GenericPlugin;

public class CreatePluginList extends AbstractPlugin implements GenericPlugin {

	@Override
	public void init() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		if (!MaydayDefaults.isDebugMode())
			return null;
		
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.debug.PluginList",
				new String[]{},
				Constants.MC_SESSION,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Creates a text file of the plugins and their mastercomponents",
				"Create Plugin List"
		);
		pli.addCategory("DEBUG");
		return pli;		
		
	}
	
	public void run() {
		PluginManager.getInstance().dumpToFile(new File("plugins.txt"));
	}

}
