package mayday.clustering.extras.clusterextension;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.methods.DistanceMeasureSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.ProbeListSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

public class ClusterExtensionSetting extends HierarchicalSetting { 

	private ProbeListSetting addSetting;
	private RestrictedStringSetting linkage;
	private DistanceMeasureSetting dist;
	private BooleanHierarchicalSetting usePercentThreshold;
	private DoubleSetting percentThreshold;
	private BooleanSetting clone;
	private BooleanHierarchicalSetting useMaxThreshold;
	private DoubleSetting maxThreshold;
	private DataSet data;
	
	public final static String SINGLE_LINKAGE = "single";
	public final static String AVERAGE_LINKAGE = "average";
	public final static String COMPLETE_LINKAGE = "complete";
	public final static String CENTROID_MEAN_LINKAGE = "centroid (mean)";
	public final static String CENTROID_MEDIAN_LINKAGE = "centroid (median)";
	
	private final static String[] linkages = {SINGLE_LINKAGE, AVERAGE_LINKAGE, COMPLETE_LINKAGE, CENTROID_MEAN_LINKAGE, CENTROID_MEDIAN_LINKAGE};
	
	public ClusterExtensionSetting(DataSet data) {
		super("Cluster Extension Settings");
		
		this.data = data;
		addSetting = new ProbeListSetting("Select additional probelist", null, null, this.data, false);
		dist = new DistanceMeasureSetting("Distance", null, DistanceMeasureManager.get("Euclidean"));
				
		usePercentThreshold = new BooleanHierarchicalSetting("Maximal cluster enlargement in %", "The maximal enlargement of the intra cluster distance", true)
		.addSetting(percentThreshold = new DoubleSetting(null, null, 50));
		useMaxThreshold = new BooleanHierarchicalSetting("Maximal absolute distance", "The maximal pairwise distance allowed between probes contained in the clustering and probes from the extension probe list", true)
			.addSetting(maxThreshold = new DoubleSetting(null, null, 30));
		linkage = new RestrictedStringSetting("Linkage", null, 0, linkages);
		clone = new BooleanSetting("Also clone unchanged ProbeLists", null, true);
				
		this.addSetting(addSetting);
		this.addSetting(clone);
		this.addSetting(dist);
		this.addSetting(linkage);
		this.addSetting(usePercentThreshold);
		this.addSetting(useMaxThreshold);
	}
	
	public ClusterExtensionSetting clone() {
		ClusterExtensionSetting cl = new ClusterExtensionSetting(this.data);
		cl.fromPrefNode(this.toPrefNode());
		return cl;
	}
	
	public DistanceMeasurePlugin getDistanceMeasure() {
		return this.dist.getInstance();
	}
	
	public double getMaxThreshold() {
		return maxThreshold.getDoubleValue();
	}
	
	public double getPercentageThreshold() {
		return percentThreshold.getDoubleValue();
	}
	
	public String getLinkageMethod() {
		return this.linkage.getStringValue();
	}
	
	public void setLinkage (String linkage) { 
		this.linkage.setObjectValue(linkage);
	}
	
	public boolean doCloneUnchanged() {
		return clone.getBooleanValue();
	}
	
	public boolean useMaxThreshold() {
		return useMaxThreshold.getBooleanValue();
	}
	
	public boolean usePercentageThreshold() {
		return usePercentThreshold.getBooleanValue();
	}
	
	public ProbeList getProbeList() {
		return addSetting.getProbeList();
	}
}
