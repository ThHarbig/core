package mayday.core.plugins.probelist;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import mayday.core.MasterTable;
import mayday.core.Mayday;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.structures.maps.MultiTreeMap;

public class NewForEachProbe extends AbstractPlugin implements ProbelistPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.NewForEachProbe",
				new String[0],
				Constants.MC_PROBELIST_CREATE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Creates a new probelist for each display name in the input",
				"Split input by display name"
				);
		return pli;
	}

	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {
		Collection<Probe> allprobes = ProbeList.mergeProbeLists(probeLists, masterTable);
        LinkedList<ProbeList> ret = new LinkedList<ProbeList>();
        MultiTreeMap<String, Probe> displayNameMap = new MultiTreeMap<String, Probe>();
        for (Probe pb : allprobes) {
        	displayNameMap.put(pb.getDisplayName(), pb);
        }
        
        if (JOptionPane.showConfirmDialog(Mayday.sharedInstance, "This will create "+displayNameMap.size()+" new ProbeLists\n" +
        		"Are you sure you want to continue?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)!=JOptionPane.YES_OPTION)
        	return null;
        
        
        for (String s : displayNameMap.keySet()) {
        	ProbeList pbl = new ProbeList(masterTable.getDataSet(), true);
        	pbl.setName(s);
        	for (Probe pb : displayNameMap.get(s))
        		pbl.addProbe(pb);
        	ret.add(pbl);
        }        
		return ret;
	}


}
