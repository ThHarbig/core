package mayday.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.DoubleListSetting;
import mayday.core.settings.typed.MIGroupSetting;

public class MultiPValFilter extends AbstractPlugin implements ProbelistPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.MultiplePvalFilter",
				new String[0],
				Constants.MC_PROBELIST_CREATE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Create probelists by filtering according to one or more pvalue thresholds.",
				"Filter by p-value threshold(s)"
				);
		return pli;
	}

	public List<ProbeList> run(List<ProbeList> probelists, MasterTable masterTable) {

		ProbeList input = ProbeList.createUniqueProbeList(probelists);
				
		MIGroupSetting mgs = new MIGroupSetting("p-Values",null,null,masterTable.getDataSet().getMIManager(),false);
		mgs.setAcceptableClass(DoubleMIO.class);
		
		ArrayList<Double> threshlist = new ArrayList<Double>();
		threshlist.add(0.005);
		threshlist.add(0.01);
		threshlist.add(0.05);
		threshlist.add(0.10);
		DoubleListSetting threshs = new DoubleListSetting("Thresholds",null,threshlist);
		
		HierarchicalSetting hs = new HierarchicalSetting("Multiple p-Value filter").addSetting(mgs).addSetting(threshs);
		
		SettingDialog sd = new SettingDialog(null, hs.getName(), hs);
		sd.showAsInputDialog();
		
		if(sd.closedWithOK()) {
			
			LinkedList<ProbeList> pls = new LinkedList<ProbeList>();
			MIGroup filterGroup = mgs.getMIGroup();
			
			for (Double thresh : threshs.getDoubleListValue()) {
				ProbeList pl = new ProbeList(masterTable.getDataSet(), true);
				for (Probe pb : input) {
					MIType mt = filterGroup.getMIO(pb);
					if (mt!=null) {
						if (((DoubleMIO)mt).getValue()<thresh)
							pl.addProbe(pb);
					}					
				}
				pl.setName("Significant at "+thresh+" in "+mgs.getStringValue());
				pls.add(pl);
			}
			
			return pls;

		}
		
		return null;
		
    }



}
