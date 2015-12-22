package mayday.vis3.plots.heatmap2.columns.plugins.enhance;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;
import mayday.vis3.plots.heatmap2.columns.plugins.HasGradient;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.headers.column.ColumnNameHeader;

public class EnhancementPlugin extends AbstractColumnGroupPlugin implements HasGradient {

	@Override
	public AbstractColumnGroupPlugin init(HeatmapStructure struct) {
		EnhancementColumn dc = new EnhancementColumn(struct);
		init(struct, dc, dc.setting, new ColumnNameHeader().init(struct, this));
		return this;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.Column.Enhanced",
				null,
				MC,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Add a column colored by any information (meta information, for instance)",
				"Enhancement"
				);
	}

	@Override
	public ColorGradient getGradient() {
		return ((EnhancementColumn)getColumns().get(0)).getGradient();
	}

}
