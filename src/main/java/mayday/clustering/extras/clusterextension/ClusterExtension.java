package mayday.clustering.extras.clusterextension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.tasks.AbstractTask;
import mayday.vis3.model.Visualizer;

public class ClusterExtension { 

	private final String UNCLUSTERED	= "unclustered"; 
	private final static String NAME_EXTENSION = " (extended)";

	private List<ProbeList> result;
	private ProbeList unclustered;
	private List<Probe> extraProbes;
	private AbstractTask cTask;
	
	
	public List<ProbeList> extendCluster(MasterTable masterTable, List<ProbeList> selectedPL, ClusterExtensionSetting settings){
		DataSet ds = masterTable.getDataSet();
		// Initialising
		result = new ArrayList<ProbeList>();
		// unclustered probelist added
		unclustered = new ProbeList(masterTable.getDataSet(), true);
		unclustered.setName(UNCLUSTERED);
					
		ProbeList input = settings.getProbeList();
		extraProbes = new ArrayList<Probe>();  

		for (Probe p: input) {
			boolean contained = false;
			for (ProbeList pl: selectedPL) {
				if (pl.contains(p)) {						
					contained = true;
				}
			}
			if (!contained)
				extraProbes.add(p);
		}				

		//copy of clusters for adding new probes
		HashMap<ProbeList,ProbeList> mapNewOldProbeList = new HashMap<ProbeList, ProbeList>();
		
		for(int j = selectedPL.size()-1; j >= 0 ; j--){
			ProbeList originalPL = selectedPL.get(j);
			ProbeList clonedPL = (ProbeList)originalPL.clone();
			result.add(clonedPL);
			
			String annotationString = "cluster extended, used distance = " + settings.getDistanceMeasure();
			String quickInfoString = "extended cluster: " + settings.getDistanceMeasure();
			
			
			if(settings.usePercentageThreshold()) {
				annotationString += ", percent threshold = " + settings.getPercentageThreshold() + "%";
				quickInfoString += ", pT=" + settings.getPercentageThreshold() + "%";
			}
			
			if(settings.useMaxThreshold()) {
				annotationString += ", max absolute distance = " + settings.getMaxThreshold();
				quickInfoString += ", maxT=" + settings.getMaxThreshold();
			}
			
			annotationString += ", used linkage method = " + settings.getLinkageMethod();
			quickInfoString += ", linkage=" + settings.getLinkageMethod();
			
			clonedPL.setAnnotation( new AnnotationMIO(annotationString, quickInfoString ));

			clonedPL.setName(originalPL.getName() + NAME_EXTENSION);
			mapNewOldProbeList.put(clonedPL,originalPL);
		}

		// settings after clicking ok
		DistanceMeasurePlugin distance = settings.getDistanceMeasure();
		String linkage = settings.getLinkageMethod();
		boolean doClone = settings.doCloneUnchanged();
		boolean usePercentageThreshold = settings.usePercentageThreshold();
		boolean useMaxThreshold = settings.useMaxThreshold();
		
		double percentageThreshold = Double.MAX_VALUE;
		double maxThreshold = Double.MAX_VALUE;
		
		//extending cluster with new probes
		cTask.writeLog("Extension parameters:\n");
		cTask.writeLog("\tDistqance measure=" + settings.getDistanceMeasure().toString()+"\n");
		
		if(usePercentageThreshold) {
			double newThreshold = 100. + settings.getPercentageThreshold();
			percentageThreshold = newThreshold / 100.;
			cTask.writeLog("\tPercentage Threshold="+settings.getPercentageThreshold()+"%\n");
		}
		
		if(useMaxThreshold) {
			maxThreshold = settings.getMaxThreshold();
			cTask.writeLog("\tMaximal probe distance="+settings.getMaxThreshold()+"\n");
		}
		
		HashMap<Probe, ProbeList> resultClusteringProbeToProbeList = clusterProbes(distance, linkage, percentageThreshold, maxThreshold);
		
		// adding probes into cluster
		int numAddedProbes = 0;
		for(Probe pb : extraProbes){
			ProbeList pl = resultClusteringProbeToProbeList.get(pb);
			if(!pl.getName().equals("Unclustered"))
				numAddedProbes++;
			pl.addProbe(pb);		
		}
		
		if(!doClone){
			// if clone=false, only return changed lists
			HashSet<ProbeList> extendedClusters = new HashSet<ProbeList>();
			extendedClusters.addAll(resultClusteringProbeToProbeList.values());
			List<ProbeList> tpl = new ArrayList<ProbeList>();
			for(ProbeList pl : result){
				if(extendedClusters.contains(pl))
					tpl.add(pl);				
			}
			result = tpl;
		}

		// extends annotationMio by adding number of added probes
		for (ProbeList pl : result) {
			ProbeList originalPL = mapNewOldProbeList.get(pl);
			int dif = pl.getNumberOfProbes() - originalPL.getNumberOfProbes();
			AnnotationMIO amio = pl.getAnnotation();
			String d = amio.getQuickInfo();
			amio.setQuickInfo(d + ", # added probes=" + dif );
		}
		
		result.add(unclustered);

		ResultFrame rf = new ResultFrame(new Visualizer(ds, result), resultClusteringProbeToProbeList);
		rf.setVisible(true);
		
		cTask.writeLog(numAddedProbes + " probes have been added to the clustering.\n");
		cTask.writeLog("Done.\n");

		return result;
	}
	
	public void setClusterTask(AbstractTask cTask) {
		this.cTask = cTask;
	}
	
	private double centroidLinkageDistance(Probe pb, ProbeList pl, boolean useMean, DistanceMeasurePlugin distance) {
		return distance.getDistance(pb, useMean ? pl.getMean() : pl.getMedian()	);
	}
	
	private double averageLinkageDistance(Probe pb, ProbeList pl, DistanceMeasurePlugin distance) {
		double sum = 0.0;
		for(Probe pb2: pl.getAllProbes()){
			double curDist = distance.getDistance(pb, pb2);
			sum += curDist ;
		}
		return sum / (double) pl.getNumberOfProbes();
	}
			
	private double completeLinkageDistance(Probe pb, ProbeList pl, DistanceMeasurePlugin distance) {
		double maxDist = Double.NEGATIVE_INFINITY;
		for(Probe pb2: pl.getAllProbes()){
			double dist = distance.getDistance(pb, pb2);
			if(dist > maxDist){
				maxDist = dist;
			}
		}
		return maxDist;
	}
	
	private double singleLinkageDistance(Probe pb, ProbeList pl, DistanceMeasurePlugin distance) {
		double minDist = Double.POSITIVE_INFINITY;
		for(Probe pb2: pl.getAllProbes()){
			double dist = distance.getDistance(pb, pb2);
			if(dist < minDist){
				minDist = dist;
			}
		}
		return minDist;
	}
	
	private HashMap<Probe, ProbeList> clusterProbes(DistanceMeasurePlugin distance, String linkage,	 double percentageThreshold, double maxThreshold)	{
		
		HashMap<Probe, ProbeList> clustermapping = new HashMap<Probe, ProbeList>();
		
		for(Probe pb : extraProbes){
			double minDist = Double.MAX_VALUE;			
			ProbeList cluster = unclustered;
			for(ProbeList p : result) {
				double intraDist = getMaximalIntraDistance(p, distance);
				double thresholdDistance = intraDist * percentageThreshold;
				double dist = Double.POSITIVE_INFINITY; // never used
				
				if (linkage == ClusterExtensionSetting.CENTROID_MEAN_LINKAGE)
					dist = centroidLinkageDistance(pb, p, true, distance);
				else if (linkage == ClusterExtensionSetting.CENTROID_MEDIAN_LINKAGE)
					dist = centroidLinkageDistance(pb, p, false, distance);
				else if (linkage == ClusterExtensionSetting.AVERAGE_LINKAGE)
					dist = averageLinkageDistance(pb, p, distance);
				else if (linkage == ClusterExtensionSetting.COMPLETE_LINKAGE)
					dist = completeLinkageDistance(pb, p, distance);
				else if (linkage == ClusterExtensionSetting.SINGLE_LINKAGE)
					dist = singleLinkageDistance(pb, p, distance);
				
				//new best cluster found
				if(dist < minDist) {
					//thresholds satisfied?
					if(dist <= thresholdDistance && dist <= maxThreshold){
						minDist = dist;
						cluster = p;
					}
				}
			}
			
			clustermapping.put(pb, cluster);
		}
		return clustermapping;
	}

	private double getMaximalIntraDistance(ProbeList ppl, DistanceMeasurePlugin dist){
		ProbeList p = ppl;
		double intraDist = 0.0;
		for(int i = 0 ; i < p.getNumberOfProbes(); i++){
			for(int j = i+1; j < p.getNumberOfProbes(); j++){
				double d = dist.getDistance(p.getProbe(i), p.getProbe(j));
				if (d > intraDist){
					intraDist = d;
				}
			}
			
		}
		return intraDist;
	}
}
