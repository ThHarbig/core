package mayday.vis3.plots.heatmap2.columns.plugins.selection;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;

public class SelectionIndicatorPlugin extends AbstractColumnGroupPlugin {

	@Override
	public AbstractColumnGroupPlugin init(HeatmapStructure struct) {
		SelectionIndicatorColumn dc = new SelectionIndicatorColumn(struct);
		init(struct, dc, dc.setting);
		return this;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.Column.SelectionIndicator",
				null,
				MC,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Add a column of a fixed width which indicates selection",
				"Selection Indicator"
				);
	}

}
