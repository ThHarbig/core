package mayday.genetics.locusmap;

import java.util.HashMap;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.plugins.MetaInfoPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ApplicableFunction;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.genetics.Locus;
import mayday.genetics.LocusMIO;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;

public class ImportLocusMIO extends AbstractPlugin implements MetaInfoPlugin, ApplicableFunction {
	
	public final PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.core.LocusMIOImport",
				new String[0],
				Constants.MC_METAINFO_PROCESS,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Add LocusMIO from files or existing locus mappings",
				"Add Locus Information"
		);
		pli.addCategory("Locus Data");
		return pli;
	}

	@Override
	public void init() {
	}

	public void run(MIGroupSelection<MIType> input, MIManager miManager) {
		
		LocusMapSetting lms = new LocusMapSetting();
		RestrictedStringSetting tgt = new RestrictedStringSetting("Map ids to",null,0, new String[]{"Probe Name","Probe Display Name"});
		tgt.setLayoutStyle(ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS);
		HierarchicalSetting all = new HierarchicalSetting("Locus Data addition");
		all.addSetting(tgt).addSetting(lms);
		
		SettingDialog sd = new SettingDialog(null, "Add locus data as meta information", all);
		sd.showAsInputDialog();
		if (sd.closedWithOK()) {
			LocusMap lm = lms.getLocusMap();
			boolean dname = tgt.getSelectedIndex()==1;
			MIGroup mg = miManager.newGroup(LocusMIO.myType, lm.getName(), "Locus Data");
			MasterTable mt = miManager.getDataSet().getMasterTable();
			for (Probe pb : mt.getProbes().values()) {
				String n = dname?pb.getDisplayName():pb.getName();
				AbstractGeneticCoordinate c = lm.get(n);
				if (c!=null) {
					LocusMIO lomi = new LocusMIO(new Locus(c));
					mg.add(pb, lomi);
				}
			}
			if (mg.size()==0) {
				miManager.removeGroup(mg);
				throw new RuntimeException("No matching identifiers found - no locus data attached to probes.");
			}
		}	
	}
	
	
	public boolean isApplicable(Object... o) {
		return true; //always possible
	}


}
