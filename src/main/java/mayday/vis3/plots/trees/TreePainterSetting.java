package mayday.vis3.plots.trees;

import mayday.core.ClassSelectionModel;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ClassSelectionSetting;
import mayday.core.structures.trees.layouter.TreeLayoutPlugin;
import mayday.core.structures.trees.layouter.UnrootedHorizontal;
import mayday.vis3.ColorProvider;
import mayday.vis3.model.ViewModel;

public class TreePainterSetting extends HierarchicalSetting {

	protected PluginTypeWithoutOptionsSetting<TreeLayoutPlugin> layouter;
	protected BooleanSetting showNodeLabels, showEdgeLabels, useHeatmapLabels, ignoreEdgeLengths;
	protected BooleanHierarchicalSetting colorExperiments;
	protected ClassSelectionSetting experimentClasses;
	protected ColorProvider cp;
	protected ViewModel vm;
	
	public TreePainterSetting(String Name, ViewModel vm, boolean probeDimension) {
		super(Name);
		addSetting(layouter = new PluginTypeWithoutOptionsSetting<TreeLayoutPlugin>("Layout Algorithm", null, new UnrootedHorizontal(), TreeLayoutPlugin.MC));
		addSetting(ignoreEdgeLengths = new BooleanSetting("Ignore edge lengths", 
				"If activated, draw all edges with the same length.\nIf deactivated, use edge lengths stored in the tree",false));
		addSetting(showNodeLabels = new BooleanSetting("Show Node Labels", null, true));
		addSetting(showEdgeLabels = new BooleanSetting("Show Edge Labels", null, true));
		if (probeDimension) {
			addSetting(useHeatmapLabels = new BooleanSetting("Show heatmaps as labels", null, false));
			if (vm!=null) {
				cp = new ColorProvider(vm);
				addSetting(cp.getSetting());
			}
		} else {
			experimentClasses = new ClassSelectionSetting("Classes", null, new ClassSelectionModel(vm.getDataSet().getMasterTable()), 1, 
					vm.getDataSet().getMasterTable().getNumberOfExperiments(), vm.getDataSet());
			colorExperiments = new BooleanHierarchicalSetting("Color by classes",null,false)
			.addSetting(experimentClasses);
			addSetting(colorExperiments);
		}
		this.vm = vm;
	}
	
	public TreeLayoutPlugin getLayouter() {
		TreeLayoutPlugin tlp = layouter.getInstance();
		tlp.setIgnoreEdgeLengths(getIgnoreEdgeLengths().getBooleanValue());
		return tlp;
	}
	
	public void setLayouter(TreeLayoutPlugin layouter) {
		this.layouter.setInstance(layouter);
	}
	
	public PluginTypeWithoutOptionsSetting<TreeLayoutPlugin> getLayouterSetting() {
		return layouter;
	}
	
	public BooleanSetting getIgnoreEdgeLengths() {
		return ignoreEdgeLengths;
	}
	
	public BooleanSetting getNodeLabelSetting() {
		return showNodeLabels;
	}
	
	public BooleanSetting getEdgeLabelSetting() {
		return showEdgeLabels;
	}
	
	

	public BooleanSetting getHeatmapLabelSetting() {
		return useHeatmapLabels;
	}
	
	protected ColorProvider getColorProvider() {
		return cp;
	}
	
	public boolean useClasses() {
		return colorExperiments.getBooleanValue();
	}
	
	public ClassSelectionModel getExperimentClasses() {
		return experimentClasses.getModel();
	}
	
	public BooleanHierarchicalSetting getExperimentClassColoringSetting() {
		return colorExperiments;
	}
	
	public TreePainterSetting clone() {
		TreePainterSetting tps = new TreePainterSetting(getName(), vm, useHeatmapLabels!=null);
		tps.fromPrefNode(this.toPrefNode());
		return tps;
	}
}
