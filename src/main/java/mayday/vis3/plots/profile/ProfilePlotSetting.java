package mayday.vis3.plots.profile;

import java.awt.Color;
import java.util.List;
import java.util.TreeSet;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SortedExtendableConfigurableObjectListSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.vis3.RelevanceSetting;
import mayday.vis3.SortedExperiments;
import mayday.vis3.SortedExperimentsSetting;
import mayday.vis3.ValueProvider;
import mayday.vis3.model.ViewModel;
import mayday.vis3.plots.PlotTimepointSetting;

public class ProfilePlotSetting extends HierarchicalSetting {
	
	protected BreakSetting breaks;
	protected PlotTimepointSetting timepoints;
	protected BooleanSetting showDots, inferData, showCentroid, showCentroids, hideProfiles, globalMax;
	protected ColorSetting selectionColor;
	protected RelevanceSetting relevance;
	protected BooleanHierarchicalSetting useRelevance;
	protected SortedExtendableConfigurableObjectListSetting<ValueProvider> extraColumns;
	protected SortedExperiments experimentOrder;
	
	protected ViewModel vm;	
	protected ProfilePlotComponent ppc;
	
	protected TreeSet<Integer> bk = new TreeSet<Integer>();
	
	public ProfilePlotSetting(ViewModel vm, ProfilePlotComponent ppc) {
		super("Profile Plot");
		this.vm=vm;
		this.ppc = ppc;
		experimentOrder = new SortedExperiments(vm);	
		addSetting(experimentOrder.getSetting())
		.addSetting(timepoints = new PlotTimepointSetting("Time points", null, true))
		.addSetting(breaks = new BreakSetting("Breaks"))		
		.addSetting(showDots = new BooleanSetting("Indicate measurements",null,false))
		.addSetting(inferData = new BooleanSetting("Infer missing values",null,false))
		.addSetting(showCentroid = new BooleanSetting("Plot centroid profile",null, false))
		.addSetting(showCentroids = new BooleanSetting("Plot centroid for each probelist",null, false))
		.addSetting(hideProfiles = new BooleanSetting("Hide profiles (for use with centroids)",null, false))
		.addSetting(globalMax = new BooleanSetting("Use global maximum/minimum",null, false))
		.addSetting(selectionColor = new ColorSetting("Selection color", null, Color.red))
		.addSetting(useRelevance = new BooleanHierarchicalSetting("Add transparency from relevance",null,false)
		 	.addSetting(relevance = new RelevanceSetting(vm.getDataSet().getMIManager()))
		)
		.addSetting(extraColumns = new SortedExtendableConfigurableObjectListSetting<ValueProvider>(
				"Extra columns", null, new ProfilePlotExtraColumnsBridge(vm, ppc)				
		))
		;		
		breaks.addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				bk = null;
			}
		});
		timepoints.setDataSet(vm.getDataSet());
		
		// SortedExperiments automatically selects a tree-based ordering if a tree is in the viewmodel. Override.
		SortedExperimentsSetting ses = ((SortedExperimentsSetting)experimentOrder.getSetting());
		ses.setOrder(SortedExperimentsSetting.SORT_BY_INDEX);
		ses.setMode(SortedExperimentsSetting.SORT_ASCENDING);
	}

	public SortedExperiments getExperimentOrder() {
		return experimentOrder;
	}
	
	public boolean useRelevance() {
		return useRelevance.getBooleanValue();
	}
	
	public RelevanceSetting getRelevanceSetting() {
		return relevance;
	}
	
	public TreeSet<Integer> getBreakPositions() {
		if (bk==null) {
			bk = new TreeSet<Integer>();
			String preset = breaks.getBreaks().getStringValue();
			if (preset!=null) {
				String[] b = preset.trim().split(",");
				for (String bb : b)
					if (bb.trim().length()>0)
						bk.add(Integer.parseInt(bb.trim()));				
			}
		}
		return bk;
	}

	public PlotTimepointSetting getTimepoints() {
		return timepoints;
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

	public ColorSetting getSelectionColor() {
		return selectionColor;
	}
	
	public int getBreakType() {
		return breaks.getBreakType();
	}
	
	public ProfilePlotSetting clone() {
		ProfilePlotSetting ns = new ProfilePlotSetting(vm, ppc);
		ns.fromPrefNode(this.toPrefNode());
		return ns;
	}
	
	public List<ValueProvider> getExtraColumns() {
		return extraColumns.getElements();
	}

}
