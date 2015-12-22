package mayday.core.plugins.dataset;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;

public class SortColumns extends AbstractPlugin implements DatasetPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.DatasetSortColumns",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Sorts the columns of the expression matrix by name",
				"Sort Experiments by Name"
		);
		pli.addCategory("Transform");
		return pli;
	}


	public List<DataSet> run(List<DataSet> datasets) {
		final DataSet ods = datasets.get(0);            

		int[] oldIndices = new int[ods.getMasterTable().getNumberOfExperiments()];
		for (int i=0; i!=oldIndices.length; ++i)
			oldIndices[i]=i;
		
		List<String> sortedExpNames = new LinkedList<String>(ods.getMasterTable().getExperimentNames());
		Collections.sort(sortedExpNames);		
		final int[] newIndices = new int[oldIndices.length];
		for (int i : oldIndices)
			newIndices[i] = ods.getMasterTable().getExperimentNames().indexOf(sortedExpNames.get(i));

		ods.getMasterTable().reorderExperiments(newIndices);

		return null;
	}



}
