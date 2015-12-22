package mayday.clustering.qt.algorithm;

import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.settings.Settings;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.methods.DistanceMeasureSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.StringSetting;

/**
 * @author Sebastian Nagel
 * @author Günter Jäger
 * @version 0.1
 */
public class QTPSettings extends Settings {
	
	protected final static String DISTMEASURE = "Distance Measure";
	public final static String DIAMETER = "Diameter threshold";
	protected final static String ENHANCEMENT = "Dynamic Cluster Enlargement";
	protected final static String ENHANCEMENTTHRESHOLD = "Enlargement threshold";
	protected final static String MINDIAMETER = "Use diameter threshold as minimal diameter";
	protected final static String MAXENHANCEDIAMETER = "Maximal diameter";
	protected final static String MINSIZE = "Minimal cluster size";
	protected final static String CORECOUNT = "Thread count";
	protected final static String PREFIX = "Name prefix";
	protected final static String CREATELOG = "Create log";

	protected DistanceMeasureSetting distance;
	protected IntSetting minSize;
	protected ObjectSelectionSetting<Integer> coreCount;
	protected DoubleSetting diameter, enhancementThreshold, maxEnhancedDiameter;
	protected StringSetting prefix;
	protected BooleanSetting createLog, useMinDiameter;
	
	protected BooleanHierarchicalSetting enhancementSetting;
	
	public QTPSettings() {
		super(new HierarchicalSetting("QT Clustering"), null);
		root
		.addSetting(prefix = new StringSetting(PREFIX,null,"QT-Clust", false))
		.addSetting(distance = new DistanceMeasureSetting(DISTMEASURE,null,DistanceMeasureManager.get("Euclidean")))
		.addSetting(diameter = new DoubleSetting(DIAMETER, null, 2.5, 0.0, null, false, true))
		.addSetting(minSize = new IntSetting(MINSIZE, null, 2, 1, null, true, true));
		
		enhancementSetting = new BooleanHierarchicalSetting(ENHANCEMENT, "Use dynamic cluster enlargement to improve clustering by enlarging the cluster diameter succesively\nuntil the enlargement threshold or the maximal diameter is reached", false)
			.setLayoutStyle(BooleanHierarchicalSetting.LayoutStyle.PANEL_FOLDUP)
		.addSetting(useMinDiameter = new BooleanSetting(MINDIAMETER, "Add probes until diameter threshold is reached, only then use dynamic cluster enlargement threshold", true))
		.addSetting(enhancementThreshold = new DoubleSetting(ENHANCEMENTTHRESHOLD, "cluster enlargement threshold for adding a probe", 0.0, 0.0, null, true, true))
		.addSetting(maxEnhancedDiameter = new DoubleSetting(MAXENHANCEDIAMETER, "maximal cluster diameter that could be reached with cluster enlargement", 0.0, 0.0, null, true, true));
		
		root
		.addSetting(enhancementSetting)
		.addSetting(new HierarchicalSetting("Options", LayoutStyle.PANEL_HORIZONTAL, true)
				.addSetting(coreCount = new ObjectSelectionSetting<Integer>(CORECOUNT, null, Runtime.getRuntime().availableProcessors()-1, getProzessorCountList()))
				.addSetting(createLog = new BooleanSetting(CREATELOG,null,false))
		);
		
		connectToPrefTree(PluginInfo.getPreferences("PAS.clustering.qtp"));
	}
	
	public static Integer[] getProzessorCountList() {
		int prozessorCount = Runtime.getRuntime().availableProcessors();
		Integer[] threadCount = new Integer[prozessorCount];
		for (int i = 1; i <= prozessorCount; i++)
			threadCount[i-1] = i;
		return threadCount;
	}
	
	public boolean isEnableEnhancement() {
		return enhancementSetting.getBooleanValue();
	}

	public void setEnableEnhancement(boolean enableEnhancement) {
		enhancementSetting.setBooleanValue(enableEnhancement);
	}

	public boolean isUseMinDiameter() {
		return useMinDiameter.getBooleanValue();
	}

	public void setUseMinDiameter(boolean useMinDiameter) {
		this.useMinDiameter.setBooleanValue(useMinDiameter);
	}

	public void checkMaxDiameter() {
		if (getMaxDiameter() < getDiameterThreshold() + getEnhancementThreshold()) {
			setMaxDiameter(getDiameterThreshold() + getEnhancementThreshold());
		}
	}

	public String toString() {
		return root.toPrefNode().toDebugString();
	}
	
	public void setDistanceMeasure(DistanceMeasurePlugin distanceMeasure) {		
		if(distanceMeasure != null)
			distance.setInstance(distanceMeasure);
	}
	
	public DistanceMeasurePlugin getDistanceMeasure() {
		return distance.getInstance();
	}

	public double getEnhancementThreshold() {
		return enhancementThreshold.getDoubleValue();
	}

	public double getMaxDiameter() {
		return maxEnhancedDiameter.getDoubleValue();
	}
	
	public void setMaxDiameter(double maxDiameter) {
		this.maxEnhancedDiameter.setDoubleValue(maxDiameter);
	}

	public void setEnhancementThreshold(double enhancementThreshold) {
		this.enhancementThreshold.setDoubleValue(enhancementThreshold);
	}
	
	public int getCoreCount() {
		return coreCount.getObjectValue().intValue();
	}

	public void setDiameterThreshold(double diameterThreshold)	{
		diameter.setDoubleValue(diameterThreshold);
	}
	
	public double getDiameterThreshold() {
		return diameter.getDoubleValue();
	}
	
	public void setMinNumOfElem(int minNumOfElem) {
		minSize.setIntValue(minNumOfElem);
	}
	
	public int getMinNumOfElem() {
		return minSize.getIntValue();
	}
	
	public String getClusterIdentifierPrefix() {
		return getClusterNames();
	}
	
	public void setLog(boolean log)	{
		createLog.setBooleanValue(log);
	}
	
	public boolean getLog()	{
		return createLog.getBooleanValue();
	}
	
	public String getClusterNames()	{
		return prefix.getStringValue();
	}
	
	public void setClusterNames(String clusterNames) {
		prefix.setStringValue(clusterNames);
	}
}
