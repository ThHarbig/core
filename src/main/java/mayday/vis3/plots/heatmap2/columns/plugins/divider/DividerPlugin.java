package mayday.vis3.plots.heatmap2.columns.plugins.divider;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;

public class DividerPlugin extends AbstractColumnGroupPlugin {

	@Override
	public AbstractColumnGroupPlugin init(HeatmapStructure struct) {
		DividerColumn dc = new DividerColumn(struct);
		init(struct, dc, dc.setting);
		return this;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.Column.Divider",
				null,
				MC,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Add a divider column of a fixed width",
				"Divider"
				);
	}

}
