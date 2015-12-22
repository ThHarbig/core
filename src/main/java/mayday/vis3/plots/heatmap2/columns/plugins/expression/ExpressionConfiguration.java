package mayday.vis3.plots.heatmap2.columns.plugins.expression;

import java.awt.Color;

import mayday.core.Probe;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.plots.heatmap2.ColorEnhancementSetting;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;

public abstract class ExpressionConfiguration implements ViewModelListener, SettingChangeListener{


	protected HierarchicalSetting setting;
	protected ColorGradient coloring;
	protected ColorGradientSetting coloringSetting;
	protected ColorEnhancementSetting enhancement;
	protected BooleanSetting showSelection;	
	
	protected ViewModel vm;
	
	protected double min, max;
	protected HeatmapStructure data;	

	
	public abstract double getProbeValue(Probe pb, Integer column);
	public abstract int[] getColumn();
	public abstract String getName(Integer col);
	
	
	
	public ExpressionConfiguration(HeatmapStructure struct, String title) {
		data = struct;
		
		setting = new HierarchicalSetting(title).setLayoutStyle(HierarchicalSetting.LayoutStyle.TABBED);
		vm = struct.getViewModel();
		
		coloring = ColorGradient.createDefaultGradient(vm.getMinimum(null, null), vm.getMaximum(null, null));
		coloringSetting = new ColorGradientSetting("Heatmap colors",null,coloring).setLayoutStyle(ColorGradientSetting.LayoutStyle.FULL);		
		setting.addSetting(coloringSetting);		
		coloringSetting.addChangeListener(this);
		
		enhancement = new ColorEnhancementSetting(vm.getDataSet().getMIManager());
		enhancement.addChangeListener(this);
		setting.addSetting(enhancement);		
		
		showSelection = new BooleanSetting("Indicate selection",null, true);
		setting.addSetting(showSelection);

		vm.addViewModelListener(this);		

		int[] col = getColumn();
		min = vm.getMinimum(col, null);
		max = vm.getMaximum(col, null);
		coloring.setMin(min);
		coloring.setMax(max);
	}
	

	public HierarchicalSetting getSetting() {
		return setting;
	}
	
	public Color getColor(Probe pb, Integer col) {
		double dvalue = getProbeValue(pb, col);
		if ( !Double.isNaN(dvalue)) {
			Color l_color = coloring.mapValueToColor(dvalue);

			if ( enhancement.isActive() ) {
				int scaledRelevance = (int)(255 * enhancement.getProvider().getRelevance(pb));
				if ( enhancement.asColor() )	
					l_color = new Color( l_color.getRed(), l_color.getGreen(), Math.abs( l_color.getBlue() - scaledRelevance ) );
				else
					l_color = new Color( l_color.getRed(), l_color.getGreen(), l_color.getBlue(), scaledRelevance );
			}
			return l_color;
		} 
		return Color.white;
	}
	
	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		if (vme.getChange()==ViewModelEvent.DATA_MANIPULATION_CHANGED || 
			vme.getChange()==ViewModelEvent.TOTAL_PROBES_CHANGED) {
			int[] col = getColumn();
			min = vm.getMinimum(col, null);
			max = vm.getMaximum(col, null);
			coloring.setMin(min);
			coloring.setMax(max);
		}
	}

	@Override
	public void stateChanged(SettingChangeEvent e) {
		if (e.getSource()==coloringSetting) {
			coloring = coloringSetting.getColorGradient();
			int[] col = getColumn();
			min = vm.getMinimum(col, null);
			max = vm.getMaximum(col, null);
			coloring.setMin(min);
			coloring.setMax(max);
		}
	}
		
	public ColorGradient getColorGradient() {
		return coloring;
	}
	
	public ColorEnhancementSetting getEnhancementSetting() {
		return enhancement;
	}
	
	public boolean showSelection() {
		return showSelection.getBooleanValue();
	}
	
	public void dispose() {
		vm.removeViewModelListener(this);
	}
	
	
}
