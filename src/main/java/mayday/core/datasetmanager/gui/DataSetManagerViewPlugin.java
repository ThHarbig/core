package mayday.core.datasetmanager.gui;

import java.util.HashMap;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.GenericPlugin;

public abstract class DataSetManagerViewPlugin extends AbstractPlugin implements GenericPlugin {

	protected abstract Class<? extends DataSetManagerViewInterface> getViewClass();
	protected abstract String getViewClassName();

	public void init() {
	}

	public PluginInfo register() throws PluginManagerException {
		@SuppressWarnings("unused")
		Class<? extends DataSetManagerViewInterface> myClass = getViewClass();
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.alignedDS.SwitchView."+getViewClass().getCanonicalName(),
				new String[0],
				Constants.MC_SESSION,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Changes the current view to "+getViewClassName(),
				getViewClassName()
		);
		pli.addCategory("Appearance");
		return pli;
	}

	public void run() {
		DataSetManagerView.changeInstance(getViewClass());		
	}
}
