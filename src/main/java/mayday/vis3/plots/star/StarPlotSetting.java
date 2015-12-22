package mayday.vis3.plots.star;

import java.awt.Color;
import java.util.TreeSet;

import mayday.core.DataSet;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.vis3.RelevanceSetting;

public class StarPlotSetting extends HierarchicalSetting {
	
	protected BooleanSetting showDots, inferData, showCentroid, showCentroids, hideProfiles, globalMax, showAxes, showGuides;
	protected ColorSetting selectionColor;
	protected RelevanceSetting relevance;
	protected BooleanHierarchicalSetting useRelevance;
	protected DataSet ds;
	
	protected TreeSet<Integer> bk = new TreeSet<Integer>();
	
	public StarPlotSetting(DataSet ds) {
		super("Profile Plot");
		this.ds=ds;
		this
		.addSetting(showDots = new BooleanSetting("Indicate measurements",null,false))
		.addSetting(inferData = new BooleanSetting("Infer missing values",null,false))
		.addSetting(new HierarchicalSetting("Centroids")
			.addSetting(showCentroid = new BooleanSetting("Plot centroid profile",null, false))
			.addSetting(showCentroids = new BooleanSetting("Plot centroid for each probelist",null, false))
			.addSetting(hideProfiles = new BooleanSetting("Hide profiles (for use with centroids)",null, false))
		)
		.addSetting(globalMax = new BooleanSetting("Use global maximum/minimum",null, false))
		.addSetting(selectionColor = new ColorSetting("Selection color", null, Color.red))
		.addSetting(new HierarchicalSetting("Gridding")
			.addSetting(showAxes = new BooleanSetting("Show axes", null, true))
			.addSetting(showGuides = new BooleanSetting("Show guides", null, true))
		)
		.addSetting(useRelevance = new BooleanHierarchicalSetting("Add transparency from relevance",null,false)
		 	.addSetting(relevance = new RelevanceSetting(ds.getMIManager())))
		;		
	}

	public boolean useRelevance() {
		return useRelevance.getBooleanValue();
	}
	
	public RelevanceSetting getRelevanceSetting() {
		return relevance;
	}
	
	public BooleanSetting getShowDots() {
		return showDots;
	}

	public BooleanSetting getInferData() {
		return inferData;
	}

	public BooleanSetting getShowCentroid() {
		return showCentroid;
	}
	
	public BooleanSetting getShowCentroidForEachPL() {
		return showCentroids;
	}

	public BooleanSetting getHideProfiles() {
		return hideProfiles;
	}
	
	public BooleanSetting getUseGlobalMax() {
		return globalMax;
	}
	
	public BooleanSetting getShowAxes() {
		return showAxes;
	}
	
	public BooleanSetting getShowGuides() {
		return showGuides;
	}

	public ColorSetting getSelectionColor() {
		return selectionColor;
	}
	
	public StarPlotSetting clone() {
		StarPlotSetting ns = new StarPlotSetting(ds);
		ns.fromPrefNode(this.toPrefNode());
		return ns;
	}
	

}
