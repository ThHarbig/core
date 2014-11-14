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

public class CoV
extends AbstractProbeFeaturePlugin
implements ProbelistPlugin
{

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.statistics.CoV",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Compute coefficient of variation across all experiments. (var/sd)",
		"Probe Coefficient of Variation");
		pli.addCategory(MC);
		return pli;
	}

	public List<ProbeList> run( List<ProbeList> probeLists, MasterTable masterTable )	{
		
		ProbeList uniqueProbes = ProbeList.createUniqueProbeList(probeLists);
		
		MIGroup mioGroup = masterTable.getDataSet().getMIManager().newGroup("PAS.MIO.Double", 
				"CoV","/Probe Statistic/"
		);

		for (Probe pb : uniqueProbes.getAllProbes()) {
			double mean = pb.getMean();
			double sd = pb.getStandardDeviation();

			DoubleMIO mio = new DoubleMIO( sd/mean );
			mioGroup.add( pb, mio );
		}

		return null;
	}

	@Override
	public void init() {	
	}
}
