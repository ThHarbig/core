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

/**
 * @author gehlenbo
 *
 */
public class Mean
extends AbstractProbeFeaturePlugin
implements ProbelistPlugin
{

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.statistics.mean",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Nils Gehlenborg",
				"neil@mangojelly.org",
				"Compute mean of expression value across all experiments.",
				"Probe Mean");
		pli.addCategory(MC);
		return pli;
	}


	public List<ProbeList> run( List<ProbeList> probeLists, MasterTable masterTable )
	{
		List<Probe> pl = ProbeList.mergeProbeLists(probeLists, masterTable);

		// create MIO group
		MIGroup l_mioGroup = masterTable.getDataSet().getMIManager().newGroup("PAS.MIO.Double", 
				"Mean expression","/Probe Statistic/");

		// compute mean of all probes in the probe lists and attach the mean as
		// an mio to each probe
		for (Probe pb : pl) {
			Double val = pb.getMean();
			if ( val != null ) {
				DoubleMIO l_mio = new DoubleMIO( val );
				l_mioGroup.add( pb, l_mio );
			}
		}

		return null;
	}


	@Override
	public void init() {
	}
}
