package mayday.clustering.kmeans;

import mayday.clustering.kmeans.searchK.SearchKSetting;
import mayday.core.math.average.AverageType;
import mayday.core.math.clusterinitializer.InitializerType;
import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.methods.DistanceMeasureSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.settings.typed.StringSetting;

public class KMeansSetting extends HierarchicalSetting {

	IntSetting numCluster;
	IntSetting cycleCount;
	DoubleSetting threshold;
	DistanceMeasureSetting distanceMeasure;
	RestrictedStringSetting initializer;
	RestrictedStringSetting centroidAlgorithm;
	StringSetting clusterdPrefix;
	
	public KMeansSetting() {
		super("Search K Setting");
		
		addSetting(clusterdPrefix = new StringSetting("Cluster Prefix", null, "k-Means"));
		
		addSetting(numCluster = new IntSetting("Number of clusters centroids", 
				"Number of initial cluster centroids", 9));
		addSetting(cycleCount = new IntSetting("Maximum number of iterations", 
				"An upper bound for the number of iteration that should be performed before the algorithm stops", 10000));
		addSetting(threshold = new DoubleSetting("Error threshold", null, 1.E-5));
		addSetting(distanceMeasure = new DistanceMeasureSetting("Distance Measure", null, DistanceMeasureManager.get("Euclidean")));
		
		String[] initializerStrings = {"kmeans++", "Random", "Random data point"};
		addSetting(initializer = new RestrictedStringSetting("Cluster centroid initializer", 
				"You can choose between different methods for cluster centroid initialization", 0, initializerStrings));
		
		String[] centroidAlgoStrings = {"mean", "median", "harmonic mean"};
		
		addSetting(centroidAlgorithm = new RestrictedStringSetting("Centroid calculation method", 
				"You can choose between different methods how centroids should be recalculated in each iteration", 0, centroidAlgoStrings));
	}
	
	public String getClusterPrefix() {
		return clusterdPrefix.getStringValue();
	}
	
	public int getNumCluster() {
		return this.numCluster.getIntValue();
	}
	
	public int getCycleCount() {
		return this.cycleCount.getIntValue();
	}
	
	public double getErrorThreshold() {
		return this.threshold.getDoubleValue();
	}
	
	public DistanceMeasurePlugin getDistanceMeasure() {
		return this.distanceMeasure.getInstance();
	}
	
	public InitializerType getInitializer() {
		switch(initializer.getSelectedIndex()) {
		case 0: return InitializerType.KMEANSPP;
		case 1: return InitializerType.RANDOM;
		case 2: return InitializerType.RANDOM_DATA_POINT;
		default: return InitializerType.KMEANSPP;
		}
	}
	
	public AverageType getCentroidAlgorithm() {
		switch(centroidAlgorithm.getSelectedIndex()) {
		case 0: return AverageType.MEAN;
		case 1: return AverageType.MEDIAN;
		case 2: return AverageType.HARM;
		default: return AverageType.MEAN;
		}
	}
	
	public void initializeSetting(SearchKSetting setting) {
		this.centroidAlgorithm.setSelectedIndex(setting.getCentroidAlgorithmIndex());
		this.cycleCount.setIntValue(setting.getCycleCount());
		this.distanceMeasure.setInstance(setting.getDistanceMeasure());
		this.initializer.setSelectedIndex(setting.getInitializerIndex());
		this.numCluster.setIntValue(setting.getMaxCluster());
		this.threshold.setDoubleValue(setting.getErrorThreshold());
	}
	
	public KMeansSetting clone() {
		return (KMeansSetting)this.reflectiveClone();
	}
}
