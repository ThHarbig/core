package mayday.vis3.plots.heatmap2.columns.plugins.expression;

import java.util.LinkedList;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.plots.heatmap2.ColorEnhancementSetting;
import mayday.vis3.plots.heatmap2.columns.HeatmapColumn;
import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;
import mayday.vis3.plots.heatmap2.columns.plugins.HasColorEnhancement;
import mayday.vis3.plots.heatmap2.columns.plugins.HasGradient;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.headers.column.ColumnNameHeader;
import mayday.vis3.plots.heatmap2.headers.column.HeatmapGradientHeader;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;

public class SubsetExpressionPlugin extends AbstractColumnGroupPlugin implements HasGradient, HasColorEnhancement, SettingChangeListener {

	protected SubsetExpressionConfiguration exconf;
	protected HeatmapStructure struct;
	
	@Override
	public AbstractColumnGroupPlugin init(HeatmapStructure struct) {
		exconf = new SubsetExpressionConfiguration(struct);
		LinkedList<HeatmapColumn> cols = new LinkedList<HeatmapColumn>();		
		for (int i : exconf.getColumn() )
			cols.add(new MultiExpressionColumn(struct, i, exconf)); 
		init(struct, cols, exconf.getSetting(), 
				new HeatmapGradientHeader().init(struct, this), 
				new ColumnNameHeader().init(struct, this));
		this.struct = struct;
		exconf.getExperimentSetting().addChangeListener(this);
		return this;

	}

	public String getName() {
		return "Expression matrix subset: "+exconf.getName(null);
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.Column.SubsetExpression",
				null,
				MC,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Add a columns for a subset of experiments of a dataset",
				"Expression Columns (subset)"
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

	@Override
	public void stateChanged(SettingChangeEvent e) {
		columns.clear();
		LinkedList<HeatmapColumn> cols = new LinkedList<HeatmapColumn>();		
		for (int i : exconf.getColumn() )
			cols.add(new MultiExpressionColumn(struct, i, exconf)); 
		columns.addAll(cols);
		
		for (HeatmapColumn col : columns)
			col.setGroup(this);
		
		struct.elementNeedsUpdating(new UpdateEvent(this, UpdateEvent.COLUMNS_CHANGE));
	}
	
}
