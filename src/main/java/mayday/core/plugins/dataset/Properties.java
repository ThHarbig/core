package mayday.core.plugins.dataset;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;

public class Properties extends AbstractPlugin implements DatasetPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.DatasetProperties",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Nils Gehlenborg",
				"neil@mangojelly.org",
				"Shows a property dialog for a Dataset.",
				"DataSet Properties"
				);
		pli.setMenuName("\0\0Properties...");
		return pli;
	}

	public List<DataSet> run(List<DataSet> datasets) {
		
		AbstractPropertiesDialog apd = PropertiesDialogFactory.createDialog(datasets.toArray());
		apd.setVisible(true);
	
        return new LinkedList<DataSet>();
    }


}
