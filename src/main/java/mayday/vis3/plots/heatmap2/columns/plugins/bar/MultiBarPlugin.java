package mayday.vis3.plots.heatmap2.columns.plugins.bar;

import java.util.LinkedList;

import javax.swing.event.ChangeListener;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.trees.tree.Node;
import mayday.vis3.SortedExperimentsSetting;
import mayday.vis3.plots.heatmap2.columns.HeatmapColumn;
import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;
import mayday.vis3.plots.heatmap2.columns.plugins.HasExperimentTree;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.headers.column.ClusterTreeHeader;
import mayday.vis3.plots.heatmap2.headers.column.ColumnNameHeader;

public class MultiBarPlugin extends AbstractColumnGroupPlugin 
implements HasExperimentTree {

	protected MultiBarConfiguration exconf;
	
	@Override
	public AbstractColumnGroupPlugin init(HeatmapStructure struct) {
		exconf = new MultiBarConfiguration(struct);		
		LinkedList<HeatmapColumn> cols = new LinkedList<HeatmapColumn>();		
		for (int i=0; i!=struct.getViewModel().getDataSet().getMasterTable().getNumberOfExperiments(); ++i)
			cols.add(new MultiBarColumn(struct, i, exconf)); 
		init(struct, cols, exconf.getSetting(), 
				new ClusterTreeHeader().init(struct, this), 
				new ColumnNameHeader().init(struct, this));
		return this;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.Column.Bar",
				null,
				MC,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Add expression level bar charts for all experiments",
				"Expression Bars"
				);
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
