package mayday.core.plugins;

import java.util.HashMap;

import mayday.core.Mayday;
import mayday.core.MaydayDefaults;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.CorePlugin;

public class MaydayIcon extends AbstractPlugin implements CorePlugin {

	
	private static PluginInfo pli;
	
	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.MaydayIcon",
				new String[0],
				Constants.MC_CORE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Loads and sets Mayday's main icon",
				"Main Icon"
		);
		pli.setIcon(MaydayDefaults.PROGRAM_ICON_IMAGE);
		return pli;	
	}

	public void run() {		
		Mayday.Mayday_Icon = pli.getIcon();
		if (Mayday.Mayday_Icon!=null)
			Mayday.sharedInstance.setIconImage(Mayday.Mayday_Icon.getImage());

	}

}
