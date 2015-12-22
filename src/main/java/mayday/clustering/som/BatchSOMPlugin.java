package mayday.clustering.som;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.clustering.ClusterPlugin;
import mayday.clustering.ClusterTask;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.GUIUtilities;
import mayday.core.math.clusterinitializer.IClusterInitializer;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.math.functions.IRotationalKernelFunction;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.SettingsDialog;
import mayday.core.structures.linalg.matrix.PermutableMatrix;

public class BatchSOMPlugin extends ClusterPlugin implements ProbelistPlugin {

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {

		PluginInfo pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.clustering.batchsom",
				new String[]{"LIB.JUNIT"}, 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Janko Dietzsch",
				"dietzsch@informatik.uni-tuebingen.de",
				"Kohonen's self-organizing map (SOM) clustering in the kind " + 
				"of the so-called batch SOM algorithm.",
		"Self-Organizing Map (SOM)");
		pli.addCategory(CATEGORY);
		return pli;
	}

	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {

		//		BatchSOMSettings setting=new BatchSOMSettings();

		SOMSettings setting=new SOMSettings();
		SettingsDialog dialog=new SettingsDialog(null, "SOM Settings", setting);
		dialog.showAsInputDialog();
		if(!dialog.closedWithOK())
			return null;


		//		BatchSOMSettings batchSOMsettings = null;
		List<ProbeList> clResults = null;
		//
		//		// Create and display the settings dialog
		//		SetupDialog dialog = new SetupDialog();
		//		dialog.setVisible(true);
		//		batchSOMsettings = dialog.getSettings();

		//		if (batchSOMsettings != null )
		//		{
		clResults = cluster(probeLists, masterTable, setting);
		//		};

		return clResults;
	}

	public List<ProbeList> cluster(List<ProbeList> probeLists, MasterTable masterTable, SOMSettings batchSOMsettings)
	throws RuntimeException {

		ProbeList uniqueProbeList = ProbeList.createUniqueProbeList(probeLists);
		// extract expression matrix from unique probe list

		// get array of probes from probe list
		Object[] probes = uniqueProbeList.toCollection().toArray();

		// new expression matrix
		PermutableMatrix matrix = getClusterData(probeLists, masterTable);
		// number of SOM units 
		int numberOfUnits = batchSOMsettings.getMapRows() * batchSOMsettings.getMapCols();

		// prepare algorithms for intialization, distance measure and kernel function
		IClusterInitializer clusterInit = 
			batchSOMsettings.getMapUnitInitializer().createInstance();
		DistanceMeasurePlugin distObj = batchSOMsettings.getDistanceMeasure(); 
		IRotationalKernelFunction kernel = 
			batchSOMsettings.getKernelFunction().createInstance(batchSOMsettings.getInitialKernelRadius());


		// creation of a BatchSOM-Object and preparation
		BatchSOMClustering bSOM = new BatchSOMClustering(matrix, batchSOMsettings.getMapTopology(), batchSOMsettings.getMapRows(), 
				batchSOMsettings.getMapCols(), batchSOMsettings.getCycles(), batchSOMsettings.getInitialKernelRadius(), 
				batchSOMsettings.getFinalKernelRadius(), kernel);
		bSOM.setClusterInitializer(clusterInit);
		bSOM.setDistanceMeasure(distObj);

		// normalize the used data to reach a better clustering - BUT changes the original data
		if (batchSOMsettings.isNormalizeData()) bSOM.normalizeDataMatrix();

		// Use a concurrent thread for clustering via class ClusterTask:
		ClusterTask clTask = new ClusterTask("Batch-SOM clustering");
		clTask.setClAlg(bSOM);
		bSOM.setProgressHook(clTask); // report the progress status

		// l_result = kmeans.runClustering();


		//start the process
		clTask.start();

		// wait for completion
		clTask.waitFor();

		// break up everything if the operation was canceled
		if (clTask.isCanceled()) return null;

		// get cluster results
		int[] result = clTask.getClResult();

		bSOM.setProgressHook(null); // remove the reference to to clTask

		// get colors for probe lists
		Color[] colors = GUIUtilities.rainbow(numberOfUnits, 0.75);

		// create a result probe lists
		ArrayList<ProbeList> list = new ArrayList<ProbeList>(); 
		for ( int i = 0; i < numberOfUnits; ++i ) {
			ProbeList temp = new ProbeList(masterTable.getDataSet(), true); 
			AnnotationMIO l_annotation = new AnnotationMIO( );
			l_annotation.setQuickInfo( "Self-Organizing Map: units = " + numberOfUnits + 
					", iterations = " + batchSOMsettings.getCycles() +
					", initialization = " + batchSOMsettings.getMapUnitInitializer().toString() );
			temp.setName( batchSOMsettings.getClusterIdentifierPrefix() + " " + (numberOfUnits - i) );
			temp.setAnnotation(l_annotation);
			temp.setColor(colors[i]);
			list.add(temp);
		}

		// copy results from unique probe list
		for (int i = 0; i < result.length; ++i)
		{
			((ProbeList)list.get(result[i])).addProbe((Probe)probes[i]);
		}

		return list;
	}


	public void init() {
	}

}

