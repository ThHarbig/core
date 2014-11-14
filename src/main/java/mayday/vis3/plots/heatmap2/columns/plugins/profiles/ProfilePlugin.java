package mayday.vis3.plots.heatmap2.columns.plugins.profiles;

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
import mayday.vis3.plots.heatmap2.headers.column.ColumnNameHeader;

public class ProfilePlugin extends AbstractColumnGroupPlugin implements HasExperimentTree {

	ProfileConfiguration exconf;

	
	@Override
	public AbstractColumnGroupPlugin init(HeatmapStructure struct) {

		exconf = new ProfileConfiguration(struct);
		
		LinkedList<HeatmapColumn> cols = new LinkedList<HeatmapColumn>();
		
		for (int i=0; i!=struct.getViewModel().getDataSet().getMasterTable().getNumberOfExperiments(); ++i)
			cols.add(new ProfileColumn(struct, i, exconf)); 
		
		init(struct, cols, exconf.getSetting(), new ColumnNameHeader().init(struct, this));
		
		return this;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.Column.ExpressionProfile",
				null,
				MC,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Add columns with a profile over all experiments",
				"Expression Profiles"
				);
	}

	@Override
	public Node getTree() {
		if (exconf.getSortOrder().getMode()==SortedExperimentsSetting.SORT_BY_TREE)
			return exconf.getSortOrder().getTreeInfo().getTree();		
		return null;
	}

	@Override
	public void addTreeChangeListener(ChangeListener cl) {
		exconf.getSortOrder().addChangeListener(cl);		
	}

}
