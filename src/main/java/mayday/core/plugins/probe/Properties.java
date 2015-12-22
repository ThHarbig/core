package mayday.core.plugins.probe;

import java.util.Collection;
import java.util.HashMap;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbePlugin;

public class Properties extends AbstractPlugin implements ProbePlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.ProbeProperties",
				new String[0],
				Constants.MC_PROBE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Shows a property dialog for a Probe.",
				"Probe Properties"
				);
		pli.setMenuName("\0\0Properties...");
		return pli;
	}

	public void run(Collection<Probe> probes, MasterTable masterTable) {

		AbstractPropertiesDialog apd = PropertiesDialogFactory.createDialog(probes.toArray());
		apd.setVisible(true);
		
	}



}
