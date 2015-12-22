package mayday.clustering.kmeans.searchK;

import mayday.core.math.average.AverageType;
import mayday.core.math.clusterinitializer.InitializerType;
import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.methods.DistanceMeasureSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

public class SearchKSetting extends HierarchicalSetting {
	
	IntSetting maxCluster;
	IntSetting cycleCount;
	DoubleSetting threshold;
	DistanceMeasureSetting distanceMeasure;
	RestrictedStringSetting initializer;
	RestrictedStringSetting centroidAlgorithm;
	
	public SearchKSetting() {
		super("Search K Setting");
		
		addSetting(maxCluster = new IntSetting("Maximum number of clusters", 
				"An upper bound for the number of clusters that should be evaluated", 9));
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
	
	public int getMaxCluster() {
		return this.maxCluster.getIntValue();
	}
	
	public void setMaxCluster(int maxCluster) {
		this.maxCluster.setIntValue(maxCluster);
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
	
	public int getInitializerIndex() {
		return this.initializer.getSelectedIndex();
	}
	
	public AverageType getCentroidAlgorithm() {
		switch(centroidAlgorithm.getSelectedIndex()) {
		case 0: return AverageType.MEAN;
		case 1: return AverageType.MEDIAN;
		case 2: return AverageType.HARM;
		default: return AverageType.MEAN;
		}
	}
	
	public int getCentroidAlgorithmIndex() {
		return this.centroidAlgorithm.getSelectedIndex();
	}
	
	public SearchKSetting clone() {
		return (SearchKSetting)this.reflectiveClone();
	}
}
