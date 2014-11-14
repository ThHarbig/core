package mayday.dynamicpl;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.probelistmanager.MasterTableProbeList;
import mayday.dynamicpl.DataProcessors.Item;
import mayday.dynamicpl.dataprocessor.ContainedInPL;

public class NewDynamicProbelist extends AbstractPlugin implements ProbelistPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.new",
				new String[0],
				Constants.MC_PROBELIST_CREATE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Creates a new dynamic probelist for a Dataset",
				"New Dynamic Probelist"
				);
		pli.setMenuName("Dynamic ProbeList");
		return pli;
	}

	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {
        DynamicProbeList l_probeList = new DynamicProbeList(masterTable.getDataSet());
        
        RuleSet parent = l_probeList.getRuleSet();
        if (probeLists.size()>1) {
        	RuleSet rs = new RuleSet(l_probeList);
        	rs.setCombinationMode(RuleSet.COMBINE_OR);
        	parent.addSubRule(rs);
        	parent = rs;
        }
        Item i = DataProcessors.getProcessorByID("PAS.dynamicPL.source.ProbeListContained");
        for (ProbeList pl : probeLists) {
        	if (pl instanceof MasterTableProbeList)
        		continue; //no need to add this one
        	Rule r = new Rule(l_probeList);
        	ContainedInPL adp = (ContainedInPL)i.newInstance(l_probeList);
        	adp.setProbeList(pl);
        	r.addProcessor(adp);
        	parent.addSubRule(r);
        }

        l_probeList.setName("Dynamic ProbeList "+(++MaydayDefaults.s_globalProbeListCounter));
        l_probeList.setColor(Color.BLUE);
        AbstractPropertiesDialog apd = PropertiesDialogFactory.createDialog(l_probeList);
        apd.setModal(true);
        apd.setVisible(true);
    	LinkedList<ProbeList> ret = new LinkedList<ProbeList>();
        if (!apd.isCancelled()) {
        	if (l_probeList.getNumberOfProbes()>0) {
        		ret.add(l_probeList);
        	} else {
        		JOptionPane.showMessageDialog(null, "Your probelist was discarded because it contains no probes.");
        	}
        } else {
        	l_probeList.propagateClosing();
        }
        
		return ret;
	}
	
}
