package mayday.clustering.dbscan;

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
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.structures.linalg.matrix.PermutableMatrix;

public class DBScanPlugin extends ClusterPlugin implements ProbelistPlugin {

	List<ProbeList> clusteringList;
	DBScanSettings Settings;


	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		//System.out.println("PL1: Register");		
		PluginInfo pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.clustering.dbscan",
				new String[]{"LIB.JUNIT"}, 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Implements a density-based clustering method.",
		"Density-based (DBSCAN)");
		pli.addCategory(CATEGORY);
		return pli;
	}

	public java.util.List<ProbeList> run( final java.util.List<ProbeList> probeLists, final MasterTable masterTable )
	{
		DBScanSettings l_settings = new DBScanSettings();
		List<ProbeList> l_result = null;


		SetupDialog l_setupDialog = new SetupDialog( l_settings );
		l_setupDialog.setVisible(true);
		l_settings = l_setupDialog.getSettings();

		if ( l_settings != null ) {
			l_result = cluster( probeLists, masterTable, l_settings );
		}

		return ( l_result );        
	}


	public java.util.List<ProbeList> cluster( java.util.List<ProbeList> probeLists, MasterTable masterTable, DBScanSettings settings  )
	throws RuntimeException 
	{
		ProbeList l_uniqueProbes = ProbeList.createUniqueProbeList(probeLists);
		PermutableMatrix matrix = getClusterData(probeLists, masterTable);

		int MinPts = settings.getMinPts();

		DistanceMeasurePlugin l_distMeasure = settings.getDistanceMeasure();

		// create new DBScan clustering object
		DBScanClustering dbscan = new DBScanClustering(matrix, MinPts, l_distMeasure);

		// Use a concurrent thread for clustering via class ClusterTask:
		ClusterTask clTask = new ClusterTask("DBScan clustering");
		clTask.setClAlg(dbscan);
		dbscan.setProgressHook(clTask); // report the progress status

		clTask.start();
		clTask.waitFor();

		// break up everything if the operation was canceled
		if (clTask.isCanceled()) return null;

		// get cluster results
		int[] l_result = clTask.getClResult();

		dbscan.setProgressHook(null); // remove the reference to to clTask

		int l_k = dbscan.getNumberOfClustersFound();

		// get colors for probe lists
		Color[] l_colors = GUIUtilities.rainbow( l_k, 0.75 );

		// create result probe lists
		ArrayList<ProbeList> l_list = new ArrayList<ProbeList>(); 
		for ( int i = 0; i < l_k; ++i )
		{
			ProbeList l_temp = new ProbeList( masterTable.getDataSet(), true );
			l_temp.setName(settings.getClusterIdentifierPrefix() + " " + ( l_k - i ));
			l_temp.setAnnotation( new AnnotationMIO( 
					"DBScan: MinPts = " +
					settings.getMinPts() +
					", Eps = " + dbscan.getEps() +
					", distance measure = " + settings.getDistanceMeasure(), "" ) );
			if (i==0) 
				l_temp.setName(settings.getClusterIdentifierPrefix() + " Noise");
			l_temp.setColor( l_colors[i] );
			l_list.add( l_temp );
		}


		if (l_result!=null)
		// copy results from unique probe list
			for ( int i = 0; i < l_result.length; ++i )
				if (l_result[i] >= 0) 
					((ProbeList)l_list.get( l_result[i] ))
					.addProbe( (Probe)l_uniqueProbes.getProbe(i) );

		return l_list;


	}

	public void init() {
	}

}

