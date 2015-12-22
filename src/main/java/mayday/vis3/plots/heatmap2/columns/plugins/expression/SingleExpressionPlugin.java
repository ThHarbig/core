package mayday.vis3.plots.heatmap2.columns.plugins.expression;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.plots.heatmap2.ColorEnhancementSetting;
import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;
import mayday.vis3.plots.heatmap2.columns.plugins.HasColorEnhancement;
import mayday.vis3.plots.heatmap2.columns.plugins.HasGradient;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.headers.column.ColumnNameHeader;

public class SingleExpressionPlugin extends AbstractColumnGroupPlugin implements HasGradient, HasColorEnhancement {

	protected SingleExpressionConfiguration exconf;
	
	@Override
	public AbstractColumnGroupPlugin init(HeatmapStructure struct) {
		exconf = new SingleExpressionConfiguration(struct);
		init(struct, new SingleExpressionColumn(struct, exconf), exconf.getSetting(), 
				new ColumnNameHeader().init(struct, this));
		return this;
	}

	public String getName() {
		return "Expression: "+exconf.getName(null);
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.Column.SingleExpression",
				null,
				MC,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Add a columns for one experiments",
				"Expression Column"
				);
	}

	@Override
	public ColorGradient getGradient() {
		return exconf.getColorGradient();
	}

	@Override
	public ColorEnhancementSetting getEnhancement() {
		return exconf.getEnhancementSetting();
	}
	
}
