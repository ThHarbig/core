package mayday.clustering.kmeans;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.clustering.ClusterAlgorithms;
import mayday.clustering.ClusterPlugin;
import mayday.clustering.ClusterTask;
import mayday.core.MasterTable;
import mayday.core.Mayday;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.GUIUtilities;
import mayday.core.math.average.AverageType;
import mayday.core.math.average.IAverage;
import mayday.core.math.clusterinitializer.IClusterInitializer;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.SettingDialog;
import mayday.core.structures.linalg.matrix.PermutableMatrix;

public class KMeansPlugin extends ClusterPlugin implements ProbelistPlugin {

	List<ProbeList> clusteringList;
	KMeansSetting Settings;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PluginInfo register() throws PluginManagerException {
		//System.out.println("PL1: Register");		
		PluginInfo pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.clustering.kmeans",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Janko Dietzsch",
				"dietzsch@informatik.uni-tuebingen.de",
				"k-Means is a common clustering technique used in microarray data analysis.",
		"Partitioning (k-Means)");
		pli.addCategory(CATEGORY);
		return pli;
	}
	/*
	 * GJ (13.12.2013): Changed to hierarchical settings
	 */
	public java.util.List<ProbeList> run( final java.util.List<ProbeList> probeLists, final MasterTable masterTable ) {
		KMeansSetting l_settings;
		
		if(this.Settings != null) {
			l_settings = this.Settings;
		} else {
			l_settings = new KMeansSetting();
		}
		
		List<ProbeList> l_result = null;

		//GJ (13.12.2013 - Using SettingDialog for hierachical settings)
		SettingDialog sd = new SettingDialog(Mayday.sharedInstance, "k-Means Settings", l_settings);
		sd.showAsInputDialog();
		
		if(sd.closedWithOK()) {
			l_result = cluster( probeLists, masterTable, l_settings );
		}
		
		return ( l_result );        
	}
	
	public void setSettings(KMeansSetting settings) {
		this.Settings = settings;
	}

	public java.util.List<ProbeList> cluster( java.util.List<ProbeList> probeLists, MasterTable masterTable, KMeansSetting settings  )
	throws RuntimeException {
		ProbeList l_uniqueProbeList = ProbeList.createUniqueProbeList(probeLists);

		Object[] l_probes = l_uniqueProbeList.toArray();

		// new expression matrix
		PermutableMatrix matrix = getClusterData(probeLists, masterTable); 

		int l_k = settings.getNumCluster();

		int l_cycles = settings.getCycleCount();

		double l_threshold = settings.getErrorThreshold();

		IAverage l_centroidAverage = settings.getCentroidAlgorithm().createInstance();

		DistanceMeasurePlugin l_distMeasure = settings.getDistanceMeasure();

		IClusterInitializer l_initCluster = settings.getInitializer().createInstance();

		ClusterAlgorithms kmeans = null;

		// create new k-means clustering object
		if (settings.getCentroidAlgorithm() == AverageType.MEAN) {
			// If the normal mean is selected -> use the specialized algorithm wich is much faster and leaner
			kmeans = new KMeansClustering(matrix, l_k, l_cycles, l_threshold, l_distMeasure, l_initCluster);
		} else {
			// If the generic version is necessary
			kmeans = new KCentroidsClustering(matrix, l_k, l_cycles, l_threshold, l_centroidAverage, l_distMeasure, l_initCluster);
		}
		
		// Use a concurrent thread for clustering via class ClusterTask:
		ClusterTask clTask = new ClusterTask("K-means clustering");
		clTask.setClAlg(kmeans);
		kmeans.setProgressHook(clTask); // report the progress status

		//start the process
		clTask.start();

		// wait for completion
		clTask.waitFor();

		// break up everything if the operation was canceled
		if (clTask.isCanceled()) 
			return null;

		// get cluster results
		int[] l_result = clTask.getClResult();

		kmeans.setProgressHook(null); // remove the reference to to clTask

		// get colors for probe lists
		Color[] l_colors = GUIUtilities.rainbow( l_k, 0.75 );

		// create result probe lists
		ArrayList<ProbeList> l_list = new ArrayList<ProbeList>(); 
		for ( int i = 0; i < l_k; ++i ) {
			ProbeList l_temp = new ProbeList( masterTable.getDataSet(), true );
			l_temp.setName(settings.getClusterPrefix() + " " + ( l_k - i ));
			l_temp.setAnnotation( new AnnotationMIO( 
					"k-Means: k = " +
					settings.getNumCluster() +
					", iterations = " + settings.getCycleCount() +
					", error threshold = " + settings.getErrorThreshold() + 
					", initialization = " + settings.getInitializer().toString(), "" ) ); 
			l_temp.setColor( l_colors[i] );
			l_list.add( l_temp );
		}

		if (l_result==null)
			return null;

		// copy results from unique probe list
		for ( int i = 0; i < l_result.length; ++i ) {
			((ProbeList)l_list.get( l_result[i] )).addProbe( (Probe)l_probes[i] );
		}

		return ( l_list );
	}
	
	public void init() {}
}

