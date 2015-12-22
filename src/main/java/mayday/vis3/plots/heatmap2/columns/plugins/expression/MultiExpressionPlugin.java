package mayday.vis3.plots.heatmap2.columns.plugins.expression;

import java.util.LinkedList;

import javax.swing.event.ChangeListener;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.trees.tree.Node;
import mayday.vis3.SortedExperimentsSetting;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.plots.heatmap2.ColorEnhancementSetting;
import mayday.vis3.plots.heatmap2.columns.HeatmapColumn;
import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;
import mayday.vis3.plots.heatmap2.columns.plugins.HasColorEnhancement;
import mayday.vis3.plots.heatmap2.columns.plugins.HasExperimentTree;
import mayday.vis3.plots.heatmap2.columns.plugins.HasGradient;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.headers.column.ClusterTreeHeader;
import mayday.vis3.plots.heatmap2.headers.column.ColumnNameHeader;
import mayday.vis3.plots.heatmap2.headers.column.HeatmapGradientHeader;

public class MultiExpressionPlugin extends AbstractColumnGroupPlugin 
implements HasGradient, HasColorEnhancement, HasExperimentTree {

	protected MultiExpressionConfiguration exconf;
	
	@Override
	public AbstractColumnGroupPlugin init(HeatmapStructure struct) {
		exconf = new MultiExpressionConfiguration(struct);		
		LinkedList<HeatmapColumn> cols = new LinkedList<HeatmapColumn>();		
		for (int i=0; i!=struct.getViewModel().getDataSet().getMasterTable().getNumberOfExperiments(); ++i)
			cols.add(new MultiExpressionColumn(struct, i, exconf)); 
		init(struct, cols, exconf.getSetting(), 
				new HeatmapGradientHeader().init(struct, this), 
				new ClusterTreeHeader().init(struct, this), 
				new ColumnNameHeader().init(struct, this));
		return this;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.Column.Expression",
				null,
				MC,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Add columns for all experiments",
				"Expression Matrix"
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
	public Node getTree() {
		if (exconf.getExperimentOrder().getMode()==SortedExperimentsSetting.SORT_BY_TREE)
			return exconf.getExperimentOrder().getTreeInfo().getTree();		
		return null;
	}

	@Override
	public void addTreeChangeListener(ChangeListener cl) {
		exconf.getExperimentOrder().addChangeListener(cl);		
	}

}
