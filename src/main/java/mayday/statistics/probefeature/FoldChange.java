package mayday.statistics.probefeature;

import java.util.HashMap;
import java.util.List;

import mayday.core.ClassSelectionModel;
import mayday.core.MasterTable;
import mayday.core.Mayday;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ClassSelectionSetting;


public class FoldChange
extends AbstractProbeFeaturePlugin
implements ProbelistPlugin
{

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.statistics.FC",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Computes the fold change between two classes of experiments.",
		"Fold-change");
		pli.addCategory(MC);
		return pli;
	}

	public List<ProbeList> run( List<ProbeList> probeLists, MasterTable masterTable )	{
		
		ProbeList uniqueProbes = ProbeList.createUniqueProbeList(probeLists);

		ClassSelectionModel csm = new ClassSelectionModel(masterTable);
		ClassSelectionSetting css = new ClassSelectionSetting("Fold change classes",null,csm,2,2);
		
		BooleanSetting isLog = new BooleanSetting("Logarithmic data",
				"For logarithmic data, the fold change is computed as A-B\n" +
				"otherwise it is computed as A/B", true);
		BooleanSetting calcLog = new BooleanSetting("Calculate the log fold-change for unlogged data?", null, true);
		BooleanSetting replaceNAN = new BooleanSetting("Replace NA/NaN with 0?", "If this setting is checked NA or NaN values will be replaced with 0.", false);
		
		HierarchicalSetting sett = new HierarchicalSetting("Fold change").addSetting(css).addSetting(isLog).addSetting(calcLog).addSetting(replaceNAN);
		
		SettingDialog sd = new SettingDialog(Mayday.sharedInstance, sett.getName(), sett);
		sd.showAsInputDialog();
		if (!sd.closedWithOK())
			return null;
		
		csm=css.getModel();
		boolean log = isLog.getBooleanValue();
		boolean rpNA = replaceNAN.getBooleanValue();
		boolean logFC = calcLog.getBooleanValue();
		
		MIGroup mioGroup = masterTable.getDataSet().getMIManager().newGroup("PAS.MIO.Double", 
				"FC between "+csm.getClassNames().get(0)+" and "+csm.getClassNames().get(1),"/Probe Statistic/"
		);

		List<Integer> c1 = csm.toIndexList(0);
		List<Integer> c2 = csm.toIndexList(1);
		double l1 = c1.size();
		double l2 = c2.size();
		
		for (Probe pb : uniqueProbes.getAllProbes()) {
			double[] vals = pb.getValues();
			double m1=0, m2=0;
			for (int i : c1) 
				m1+=vals[i];				
			for (int i : c2) 
				m2+=vals[i];				
			m1/=l1;
			m2/=l2;
			
			double fc = 0.;
			
			if(log) {
				fc = (m2 - m1);
			} else {
				if(logFC) {
					double m1l = Math.log(m1)/Math.log(2);
					double m2l = Math.log(m2)/Math.log(2);
					fc = (m2l - m1l);
				} else {
					fc = m2 / m1;
				}
			}
			
			//check for NA / NaN
			if(Double.isNaN(fc)) {
				if(rpNA) {
					fc = 0.0;
				}
			}
			
			DoubleMIO mio = new DoubleMIO( fc );
			mioGroup.add( pb, mio );
		}

		return null;
	}

	@Override
	public void init() {	
	}
	

}
