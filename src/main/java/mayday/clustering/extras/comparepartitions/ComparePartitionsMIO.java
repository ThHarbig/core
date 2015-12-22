package mayday.clustering.extras.comparepartitions;

import java.util.HashMap;
import java.util.TreeSet;

import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.NominalMIO;
import mayday.core.meta.plugins.AbstractMetaInfoPlugin;
import mayday.core.meta.plugins.MetaInfoPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class ComparePartitionsMIO
extends AbstractMetaInfoPlugin
{
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.clustering.comparePartitions",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Compares two partitions such as obtained by clustering the same data twice.",
				"Compare Partitions");
		pli.getProperties().put(MetaInfoPlugin.MULTISELECT_HANDLING, MetaInfoPlugin.MULTISELECT_HANDLE_INTERNAL);
		return pli;
	}

	@Override
	public void init() {
		registerAcceptableClass(NominalMIO.class);
	}
	
	
	public void run(MIGroupSelection<MIType> input, MIManager miManager) {

		MIGroup mg1 = input.get(0);
		MIGroup mg2 = input.get(1);

		Partition p1 = new Partition( mg1 );
		Partition p2 = new Partition( mg2 );
		
		// make sure all probes are present in each of the partitions
		TreeSet<Probe> probes = new TreeSet<Probe>();
		probes.addAll(p1.getProbes());
		probes.addAll(p2.getProbes());
		p1.addUnclustered(probes);
		p2.addUnclustered(probes);
		
		// compute the confusing matrix
		ConfusingMatrix cfm = new ConfusingMatrix(p1,p2);
		
		new ResultFrame(cfm).setVisible(true);		
	}
	
}
