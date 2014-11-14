package mayday.core.plugins.mio;

import java.util.HashMap;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.plugins.AbstractMetaInfoPlugin;
import mayday.core.meta.plugins.MetaInfoPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class SetAsExperimentDisplayName
extends AbstractMetaInfoPlugin
{

	public void init() {
		pli.getProperties().put(MetaInfoPlugin.MULTISELECT_HANDLING, MetaInfoPlugin.MULTISELECT_HANDLE_INTERNAL);
		registerAcceptableClass(MIType.class);
	}
	
	
	public void run(MIGroupSelection<MIType> selection,MIManager miManager) {
		MIGroup mg = selection.get(0);
		mg.getMIManager().getDataSet().setExperimentDisplayNames( mg );
	}

	public PluginInfo register() throws PluginManagerException {
		pli= new PluginInfo(
				this.getClass(),
				"PAS.mio.setasexperimentdisplayname",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Set the selected MIGroup as the experiment display name group",
				"Use for experiment display names");
		return pli;
	}

}
