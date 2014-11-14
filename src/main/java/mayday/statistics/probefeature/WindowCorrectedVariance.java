/*
 * Created on Dec 8, 2004
 *
 */
package mayday.statistics.probefeature;

import java.util.HashMap;
import java.util.List;

import mayday.core.MasterTable;
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
import mayday.core.settings.typed.AveragingSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.structures.linalg.vector.DoubleVector;

/**
 * @author gehlenbo
 *
 */
public class WindowCorrectedVariance
extends AbstractProbeFeaturePlugin
implements ProbelistPlugin
{

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		//System.out.println("PL1: Register");		
		PluginInfo pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.statistics.wincorrectedvar",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Compute expression value variance across all experiments and normalize by the minimal variance computed over a window size.",
				"Probe Window Corrected Variance");
		pli.addCategory(MC);
		return pli;
	}

	protected IntSetting window_size;
	protected AveragingSetting noiseaverager;
	protected HierarchicalSetting sett;


	public List<ProbeList> run( List<ProbeList> probeLists, MasterTable masterTable )
	{
		window_size = new IntSetting("Window size", null, 3, 2, null, true, false);
		noiseaverager = new AveragingSetting();
		sett = new HierarchicalSetting("Window-corrected variance").addSetting(window_size).addSetting(noiseaverager);

		SettingDialog sd = new SettingDialog(null, "Window-corrected variance", sett);
		sd.showAsInputDialog();
		if (sd.canceled())
			return null;

		ProbeList pl = ProbeList.createUniqueProbeList(probeLists);
		MIGroup mioGroup = masterTable.getDataSet().getMIManager().newGroup("PAS.MIO.Double", "Window-corrected variance","/Probe Statistic/");

		int winsize = window_size.getIntValue();
		int startindex=0;
		int endindex = masterTable.getNumberOfExperiments()-winsize;
		DoubleVector part = new DoubleVector(winsize);

		for ( Probe pb : pl.getAllProbes()) {

			double var = pb.getVariance();
			DoubleVector dv = new DoubleVector(endindex-startindex);

			for (int i=startindex; i!=endindex; ++i) {
				for (int j=i; j!=i+winsize; ++j) {
					part.set(j-i, pb.getValues()[j]);
				}				
				double winvar = Math.pow(part.sd(),2);
				dv.set(i-startindex, winvar);
			}
			double minwinvar = noiseaverager.getSummaryFunction().getAverage(dv);

			mioGroup.add( pb, new DoubleMIO( var/minwinvar) );
		}

		return null;
	}


	@Override
	public void init() {
	}
}
