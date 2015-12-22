package mayday.vis3.plots.heatmap2.columns.plugins.boxsize;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.Probe;
import mayday.vis3.SortedExperiments;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;

public class MultiBoxSizeConfiguration extends BoxSizeConfiguration implements ChangeListener {

	protected SortedExperiments expSorting;
	
	double[] cache;
	Probe cachedProbe=null;

	public MultiBoxSizeConfiguration(HeatmapStructure struct) {
		super(struct, "Expression columns");

		expSorting = new SortedExperiments(vm);
		setting.addSetting(expSorting.getSetting());
		expSorting.addChangeListener(this);
	}
	
	public double getProbeValue(Probe pb, Integer col) {
		if (pb!=cachedProbe) {
			cache = vm.getProbeValues(pb);
			cachedProbe = pb;
		}	
		col = expSorting.mapColumn(col);
		return cache[col];
	}
	
	public String getName(Integer col) {
		col = expSorting.mapColumn(col);
		return vm.getDataSet().getMasterTable().getExperimentDisplayName(col);		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// Experiment Sorting
		data.triggerInvalidate();	
	}

	@Override
	public int[] getColumn() {
		return null;
	}
	
	public SortedExperiments getExperimentOrder() {
		return expSorting;
	}
	
	public void dispose() {
		super.dispose();
		expSorting.dispose();
	}
	
	
}
