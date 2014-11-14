package mayday.clustering.qt;

import java.util.HashMap;
import java.util.List;

import mayday.clustering.qt.algorithm.QTPPluginBase;
import mayday.clustering.qt.algorithm.searchdiameter.QTSearchDiameter;
import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.structures.linalg.matrix.AbstractMatrix;
import mayday.core.tasks.AbstractTask;

/**
 * @author Sebastian Nagel
 * @author G&uuml;nter J&auml;ger
 * @version 0.1
 */
public class QTPSearchDiameterPlugin extends QTPPluginBase implements ProbelistPlugin{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				(Class)this.getClass(),
				"IT.clustering.qt.searchdiameter",
				new String[]{"IT.clustering.qt"}, 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"G\u00FCnter J\u00E4ger",
				"no e-mail provided",
				"Search for optimal diameter threshold.",
		"Quality-based (Search diameter for QT-Clustering)");
		pli.addCategory(CATEGORY);
		return pli;
	}

	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {		
		AbstractMatrix matrix = getClusterData(probeLists, masterTable);
		final QTSearchDiameter searchD = new QTSearchDiameter(matrix);
		
		searchD.setProbeLists(probeLists);
		searchD.setMasterTable(masterTable);
		
		//run search diameter plugin in an extra task
		AbstractTask task = new AbstractTask("Search diameter threshold ...") {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				searchD.setVisible(true);
			}
		};
		
		task.start();
		searchD.validate();
		
		return null;
	}
}
