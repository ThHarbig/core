package mayday.clustering.qt.algorithm;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import mayday.clustering.ClusterPlugin;
import mayday.clustering.ClusterTask;
import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.GUIUtilities;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.clustering.qt.algorithm.clustering.QTPClustering;

/**
 * @author Sebastian Nagel
 * @author Günter Jäger
 * @version 0.1
 */
public abstract class QTPPluginBase extends ClusterPlugin {
	
	protected static List<ProbeList> createResultProbeList(MasterTable masterTable,
			Object[] uniqueProbes, int[] ClusterIndices, int numOfClusters, QTPSettings settings) {
		
		Color[] colors = GUIUtilities.rainbow(numOfClusters, 0.75);
		List<ProbeList> Clustering = new ArrayList<ProbeList>();
		
		//for each cluster create a new probe list and set its annotation
		for(int i = 0; i < numOfClusters; i++)	{
			ProbeList tempList = new ProbeList(masterTable.getDataSet(), true);
			tempList.setName(settings.getClusterIdentifierPrefix() + " " + (numOfClusters-i));
			tempList.setAnnotation(new AnnotationMIO(
					
					"QT-Clustering, diameter threshold = "
					+ settings.getDiameterThreshold() + ", minimal number of elements per cluster = "
					+ settings.getMinNumOfElem(), "")); 
			
			tempList.setColor(colors[i]);
			Clustering.add(tempList);
		}
		
		//create the unclustered probe list and append it to end of the list of probe lists
		ProbeList unclustered = new ProbeList(masterTable.getDataSet(), true);
		unclustered.setName(settings.getClusterIdentifierPrefix() + " Unclustered");
		unclustered.setAnnotation(new AnnotationMIO(				
				"QT-Clustering, diameter threshold = "
				+ settings.getDiameterThreshold() + ", minimal number of elements per cluster = "
				+ settings.getMinNumOfElem(), ""));
		Clustering.add(unclustered);
		
		for(int i = 0; i < uniqueProbes.length; i++) {
			if(ClusterIndices[i] != -1)	{ //add probe to its resulting probe list as defined by the cluster index
				((ProbeList)Clustering.get(ClusterIndices[i])).addProbe((Probe)uniqueProbes[i]);
			}
			else { //add probe to unclustered probe list
				((ProbeList)Clustering.get(Clustering.size()-1)).addProbe((Probe)uniqueProbes[i]);
			}
		}
		return Clustering;
	}
	
	public List<ProbeList> cluster(List<ProbeList> probeLists, MasterTable masterTable, QTPSettings settings) {
		//Create unique ProbeList & unique ProbeArray
		ProbeList uniqueProbeList = ProbeList.createUniqueProbeList(probeLists);
		Object[] uniqueProbes = uniqueProbeList.toCollection().toArray();
		
		//fill matrix with expression values
		PermutableMatrix matrix = getClusterData(probeLists, masterTable);
		
		//new Instance of the cluster algorithm
		assert(settings != null);
		
		QTPClustering QT = new QTPClustering(matrix, settings);
		
		//run clustering in a cluster task
		ClusterTask cTask = new ClusterTask("QT-Clustering");
		cTask.setClAlg(QT);
		QT.setClusterTask(cTask);
		cTask.start();
		cTask.waitFor();
		
		//collect results and build resulting probe lists
		int [] ClusterIndices = cTask.getClResult();
		QT.setClusterTask(null);
		
		//no elements could be clustered
		if(ClusterIndices == null)	{
			return null;
		}
		
		int numOfClusters = QT.getNumOfClusters();
		
		//if no clusters were found
		if(numOfClusters == 0) {
			JOptionPane.showMessageDialog(null, "No Clusters were found!" +
					"\nPlease try again with different parameters.",
					MaydayDefaults.Messages.INFORMATION_TITLE, JOptionPane.INFORMATION_MESSAGE);
		}
		
		//create result probe lists
		List<ProbeList> Clustering = null;
		
		Clustering = createResultProbeList(masterTable, uniqueProbes,
				ClusterIndices, numOfClusters, settings);
		
		return Clustering;
	}

	public List<ProbeList> runWithSettings(List<ProbeList> probeLists, MasterTable masterTable, QTPSettings settings) {
		List<ProbeList> Clustering = null;
		
		if(settings != null) {
			//calculate the clustering
			Clustering = cluster(probeLists, masterTable, settings);
		}
		
		return Clustering;
	}
	
	public void init() {}
}
