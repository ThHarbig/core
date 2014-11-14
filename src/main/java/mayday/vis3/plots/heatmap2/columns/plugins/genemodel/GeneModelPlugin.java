package mayday.vis3.plots.heatmap2.columns.plugins.genemodel;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.headers.column.ColumnNameHeader;

public class GeneModelPlugin extends AbstractColumnGroupPlugin {

	@Override
	public AbstractColumnGroupPlugin init(HeatmapStructure struct) {
		GeneModelColumn dc = new GeneModelColumn(struct);
		init(struct, dc, dc.setting, new ColumnNameHeader().init(struct, this));
		return this;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.Column.GeneModel",
				null,
				MC,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Add a column colored showing gene models from locus data",
				"Gene Model"
				);
	}

}
