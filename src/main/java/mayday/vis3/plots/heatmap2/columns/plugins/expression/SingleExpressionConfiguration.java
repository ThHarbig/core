package mayday.vis3.plots.heatmap2.columns.plugins.expression;

import mayday.core.Probe;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.ExperimentSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;

public class SingleExpressionConfiguration extends ExpressionConfiguration {

	protected ExperimentSetting experiment;
	
	protected IntSetting pxwidth;
	protected DoubleSetting relwidth;
	protected SelectableHierarchicalSetting wtype;
	
	public SingleExpressionConfiguration(HeatmapStructure struct) {
		super(struct, "Single expression column");
		
		pxwidth = new IntSetting   ("With in pixels",null,  3,  1, null, true, false);
		relwidth= new DoubleSetting("Relative width",null, 1d, .1, null, true, false);
		wtype = new SelectableHierarchicalSetting("Column width", null, 1, new Object[]{pxwidth, relwidth});
		
		experiment = new ExperimentSetting("Experiment",null,vm.getDataSet().getMasterTable());
		
		setting.addSetting(wtype).addSetting(experiment);
		
		experiment.addChangeListener(this);
		
		int[] col = getColumn();
		min = vm.getMinimum(col, null);
		max = vm.getMaximum(col, null);
		coloring.setMin(min);
		coloring.setMax(max);
	}
	
	public Setting getWidthSetting() {
		return wtype;
	}
	
	public double getProbeValue(Probe pb, Integer column /*ignored*/) {
		return vm.getProbeValues(pb)[experiment.getSelectedIndex()];
		
	}
	
	public int[] getColumn() {
		if (experiment!=null) 
			return new int[]{experiment.getSelectedIndex()};
		return null;
	}
	
	public double getDesiredWidth() {
		if (wtype.getObjectValue()==pxwidth)
			return -pxwidth.getIntValue();
		else
			return relwidth.getDoubleValue();
	}

	@Override
	public String getName(Integer col /*ignored*/) {
		return vm.getDataSet().getMasterTable().getExperimentDisplayName(experiment.getSelectedIndex());
	}
	
	public void stateChanged(SettingChangeEvent e) {
		if (e.getSource()==experiment) {
			int[] col = getColumn();
			min = vm.getMinimum(col, null);
			max = vm.getMaximum(col, null);
			coloring.setMin(min);
			coloring.setMax(max);
		}
		else super.stateChanged(e);
	}
	
}
