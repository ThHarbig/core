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
import mayday.core.pluma.prototypes.ApplicableFunction;
import mayday.core.pluma.prototypes.ProbelistPlugin;

public class CloneProbelist extends AbstractPlugin implements ProbelistPlugin, ApplicableFunction {

	public void init() {
	}

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.core.CloneProbeList",
				new String[0],
				Constants.MC_PROBELIST,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Creates a new ProbeList by cloning an existing list",
				"Clone ProbeList"
				);
		pli.setMenuName("\0Clone...");
		return pli;
	}

	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {
		ProbeList l_probeList = probeLists.get(0);
		LinkedList<ProbeList> ret = new LinkedList<ProbeList>();
		ProbeList pl = (ProbeList)l_probeList.clone();
		AbstractPropertiesDialog apd = PropertiesDialogFactory.createDialog(pl);
		apd.setModal(true);
		apd.setVisible(true);
		if (!apd.isCancelled())
			ret.add(pl);		
		return ret;
	}

	@Override
	public boolean isApplicable(Object... o) {
		return (o.length==1 && (o[0] instanceof ProbeList));
	}
        
}
