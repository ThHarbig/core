package mayday.vis3.plots.heatmap2.columns.plugins.expression;

import mayday.core.Experiment;
import mayday.core.Probe;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.generic.MultiselectObjectListSetting;
import mayday.core.structures.linalg.Algebra;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;

public class SubsetExpressionConfiguration extends ExpressionConfiguration {

	protected MultiselectObjectListSetting<Experiment> experiments;
	protected int[] cols; 
	
	public SubsetExpressionConfiguration(HeatmapStructure struct) {
		super(struct, "Subset expression column");
		
		experiments = new MultiselectObjectListSetting<Experiment>("Experiments","Select which experiments to display",vm.getDataSet().getMasterTable().getExperiments());
		experiments.setValueString("0");
		
		setting.addSetting(experiments);
		
		experiments.addChangeListener(this);
		
		cols = getColumn();
		min = vm.getMinimum(cols, null);
		max = vm.getMaximum(cols, null);
		coloring.setMin(min);
		coloring.setMax(max);
	}
	
	public double getProbeValue(Probe pb, Integer column) {
		return vm.getProbeValues(pb)[column];
		
	}
	
	public int[] getColumn() {
		if (experiments!=null) {
			if (experiments.getIntegerListValue().size()==0)
				return new int[0];
			return Algebra.<int[]>createNativeArray(experiments.getIntegerListValue().toArray());
		}
		return null;
	}

	@Override
	public String getName(Integer col) {
		if (col==null)
			return cols.length+" columns";
		else 
			return vm.getDataSet().getMasterTable().getExperimentDisplayName(col);
	}
	
	public void stateChanged(SettingChangeEvent e) {
		if (e.getSource()==experiments) {
			cols = getColumn();
			min = vm.getMinimum(cols, null);
			max = vm.getMaximum(cols, null);
			coloring.setMin(min);
			coloring.setMax(max);
		}
		else super.stateChanged(e);
	}
	
	public MultiselectObjectListSetting<Experiment> getExperimentSetting() {
		return experiments;
	}
	
}
