package mayday.vis3.plots.heatmap2.headers.column;

import java.awt.Graphics2D;

import javax.swing.event.ChangeListener;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.structures.trees.layout.Coordinate;
import mayday.core.structures.trees.layout.ObjectMapper;
import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.structures.trees.tree.Node;
import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;
import mayday.vis3.plots.heatmap2.columns.plugins.HasExperimentTree;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.headers.ColumnHeaderElement;
import mayday.vis3.plots.heatmap2.headers.row.AbstractClusterTreeHeader;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;

public class ClusterTreeHeader extends AbstractClusterTreeHeader implements ChangeListener, ColumnHeaderElement {

	protected HasExperimentTree expTreeSource;
	protected int firstIndex=-1;

	@Override
	public void render(Graphics2D g, AbstractColumnGroupPlugin group) {
		double h=g.getClipBounds().x;
		int nowIndex = data.getColumns().indexOf(group.getColumns().get(0));
		if (nowIndex!=firstIndex) {
			firstIndex = nowIndex;
			produceLayout();
		}
		g.translate(h, 0);
		render0(g);		 
	}
	
	public void produceLayout() {
		super.produceLayout();

		if (root==null)
			return;		
		
		ScreenLayout sl = painter.getScreenLayout();
		ObjectMapper om = new ExperimentNodeMapper();			
		sl.setObjectMapper(om);
		
		if (extendEdges.getBooleanValue()) {
			for (Node n : root.getLeaves(null)) {
				Coordinate c = sl.getUntransformedCoordinate(n);
				c.y = 1.0;
			}
		}
	}

	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.ColumnHeader.Clustering",
				null,
				MC_COL,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Add a clustering tree as column header",
				"Clustering Tree"
				);
	}

	@Override
	public ColumnHeaderElement init(HeatmapStructure struct, AbstractColumnGroupPlugin group) {
		data = struct;
		topDown = true;
		
		if (group instanceof HasExperimentTree && expTreeSource==null) {
			expTreeSource = (HasExperimentTree)group;
			expTreeSource.addTreeChangeListener(this);
		}
		
		extendEdges.addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				produceLayout();
				fireChange(UpdateEvent.REPAINT);
			}
		});
		
		stateChanged(null);
		return this;
	}

	@Override
	protected long getCurrentModificationCount() {
		return data.getColumnScalingModificationCount();
	}

	@Override
	protected Node getNewTree() {
		if (expTreeSource!=null)
			return expTreeSource.getTree();
		return null;
	}
	
	@Override
	protected int getFirstIndex() {
		if (firstIndex<0)
			return 0;
		return firstIndex;
	}

	
	/** simply hide labels*/
	public class ExperimentNodeMapper implements ObjectMapper {

		public ExperimentNodeMapper() {}

		public Object getObject(Node n) {
			return  null;
		}

		public Node getNode(Object pb) {
			return null;
		}

		public String getLabel(Node n) {
			return "";
		}

	}

	public void dispose() { /* nada */ }
}
