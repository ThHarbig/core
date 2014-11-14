package mayday.vis3.plots.heatmap2.columns.plugins.boxsize;

import mayday.core.Probe;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;

public abstract class BoxSizeConfiguration implements ViewModelListener{


	protected HierarchicalSetting setting;
	protected BooleanSetting showSelection;	
	
	protected ViewModel vm;
	
	protected double min, max;
	protected HeatmapStructure data;	

	
	public abstract double getProbeValue(Probe pb, Integer column);
	public abstract int[] getColumn();
	public abstract String getName(Integer col);
	
	public BoxSizeConfiguration(HeatmapStructure struct, String title) {
		data = struct;
		
		setting = new HierarchicalSetting(title).setLayoutStyle(HierarchicalSetting.LayoutStyle.TABBED);
		vm = struct.getViewModel();
		
		showSelection = new BooleanSetting("Indicate selection",null, true);
		setting.addSetting(showSelection);

		vm.addViewModelListener(this);		

		int[] col = getColumn();
		min = vm.getMinimum(col, null);
		max = vm.getMaximum(col, null);
	}
	

	public HierarchicalSetting getSetting() {
		return setting;
	}
	
	public double getPercentage(Probe pb, int col) {
		double dvalue = getProbeValue(pb, col);
		double perc = (dvalue - min)/(max-min);
		return perc;
	}
	

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		if (vme.getChange()==ViewModelEvent.DATA_MANIPULATION_CHANGED || 
			vme.getChange()==ViewModelEvent.TOTAL_PROBES_CHANGED) {
			int[] col = getColumn();
			min = vm.getMinimum(col, null);
			max = vm.getMaximum(col, null);
		}
	}

	
	public boolean showSelection() {
		return showSelection.getBooleanValue();
	}
	
	public void dispose() {
		vm.removeViewModelListener(this);
	}
	
	
}
