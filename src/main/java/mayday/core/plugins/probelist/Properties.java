package mayday.core.plugins.probelist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;

public class Properties extends AbstractPlugin implements ProbelistPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.ProbelistProperties",
				new String[0],
				Constants.MC_PROBELIST,
				new HashMap<String, Object>(),
				"Nils Gehlenborg",
				"neil@mangojelly.org",
				"Shows a property dialog for a ProbeList.",
				"ProbeList Properties"
				);
		pli.setMenuName("\0\0Properties...");
		return pli;
	}

	public List<ProbeList> run(List<ProbeList> probelists, MasterTable masterTable) {

		AbstractPropertiesDialog apd = PropertiesDialogFactory.createDialog(probelists.toArray());
		apd.setVisible(true);
		
        return new LinkedList<ProbeList>();
    }



}
