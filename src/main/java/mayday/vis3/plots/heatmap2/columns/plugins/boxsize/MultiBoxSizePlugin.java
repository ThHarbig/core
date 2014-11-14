package mayday.vis3.plots.heatmap2.columns.plugins.boxsize;

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

public class MultiBoxSizePlugin extends AbstractColumnGroupPlugin 
implements HasExperimentTree {

	protected MultiBoxSizeConfiguration exconf;
	
	@Override
	public AbstractColumnGroupPlugin init(HeatmapStructure struct) {
		exconf = new MultiBoxSizeConfiguration(struct);		
		LinkedList<HeatmapColumn> cols = new LinkedList<HeatmapColumn>();		
		for (int i=0; i!=struct.getViewModel().getDataSet().getMasterTable().getNumberOfExperiments(); ++i)
			cols.add(new MultiBoxSizeColumn(struct, i, exconf)); 
		init(struct, cols, exconf.getSetting(), 
				new ClusterTreeHeader().init(struct, this), 
				new ColumnNameHeader().init(struct, this));
		return this;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.Column.BoxSize",
				null,
				MC,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Add scaled boxes for all experiments",
				"Expression Boxes"
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
