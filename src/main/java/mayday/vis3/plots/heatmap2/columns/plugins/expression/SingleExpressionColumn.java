package mayday.vis3.plots.heatmap2.columns.plugins.expression;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;

public class SingleExpressionColumn extends MultiExpressionColumn {

	public SingleExpressionColumn(HeatmapStructure struct, SingleExpressionConfiguration sett) {
		super(struct, null, sett);
	}
	
	@Override
	public double getDesiredWidth() {
		return ((SingleExpressionConfiguration)config).getDesiredWidth();
	}

	@Override
	public void stateChanged(SettingChangeEvent e) {
		if (e.hasSource(((SingleExpressionConfiguration)config).getWidthSetting()))
			fireChange(UpdateEvent.SIZE_CHANGE);
		else
			fireChange(UpdateEvent.REPAINT);
	}

}
