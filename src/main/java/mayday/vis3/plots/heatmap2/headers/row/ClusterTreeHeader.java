package mayday.vis3.plots.heatmap2.headers.row;

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.event.ChangeListener;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.structures.maps.BidirectionalHashMap;
import mayday.core.structures.trees.layout.Coordinate;
import mayday.core.structures.trees.layout.Layout;
import mayday.core.structures.trees.layout.ObjectMapper;
import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.structures.trees.screen.SelectionManager;
import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.ITreePart;
import mayday.core.structures.trees.tree.Node;
import mayday.vis3.HierarchicalSortedProbeList;
import mayday.vis3.HierarchicalSortedProbeListSetting;
import mayday.vis3.model.ViewModel;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.headers.RowHeaderElement;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;

public class ClusterTreeHeader extends AbstractClusterTreeHeader implements ChangeListener, RowHeaderElement {

	protected ViewModel vm;
	
	protected ObjectMapper om;
	protected LinkedSelectionManager lsm;

	public void produceLayout() {
		super.produceLayout();

		if (root==null)
			return;		
		
		ScreenLayout sl = painter.getScreenLayout();
		om = new ProbeNodeMapper(sl,vm.getDataSet().getMasterTable());			
		sl.setObjectMapper(om);
		sl.setSelectionManager(lsm);
		
		if (extendEdges.getBooleanValue()) {
			for (Probe pb : vm.getProbes()) {
				Coordinate c = sl.getUntransformedCoordinate(om.getNode(pb));
				c.x = 1.0;
			}
		}
	}
	
	protected void selectNode(Node n, boolean toggle, Set<Probe> selection) {
		Probe p = (Probe)painter.getScreenLayout().getObject(n);
		if (toggle) {
			if (selection.contains(p))
				selection.remove(p);
			else
				selection.add(p);
		} else {
			selection.add(p);
		}
	}
	
	protected void selectEdge(Edge e, boolean toggle, Set<Probe> selection) {
		Collection<Node> nodes = (e).getNode(1).getLeaves(e);
		List<Probe> pb = new LinkedList<Probe>();
		for (Node n : nodes)
			pb.add( (Probe)painter.getScreenLayout().getObject(n) );
		if (pb.size()==0)
			return;
		
		if (toggle) {
			if (selection.contains(pb.get(0))) {
				selection.removeAll(pb);
				painter.getScreenLayout().setSelected(e, false);
			} else { 
				selection.addAll(pb);
				painter.getScreenLayout().setSelected(e, true);
			}
		} else { 
			selection.addAll(pb);
			painter.getScreenLayout().setSelected(e, true);
		}
	}

	@Override
	public MouseListener getMouseListener() {
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				
				if (e.getButton()==MouseEvent.BUTTON1) {

					ITreePart t = painter.nearestObject(e.getX(),e.getY());
					
					Set<Probe> newSelection = new HashSet<Probe>();
					
					int CONTROLMASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
					boolean toggle = (e.getModifiers()&CONTROLMASK) == CONTROLMASK;
					
					if (toggle)
						newSelection.addAll(data.getViewModel().getSelectedProbes());
					else
						painter.getScreenLayout().clearSelected();
					
					if (t instanceof Edge) {
						selectEdge((Edge)t, toggle, newSelection);
					} else {
						selectNode((Node)t, toggle, newSelection);
					}
					
					data.getViewModel().setProbeSelection(newSelection);
				}
				
				
			}
		};
	}

	@Override
	public void render(Graphics2D g) {
		render0(g);
	}



	@Override
	protected long getCurrentModificationCount() {
		return data.getRowScalingModificationCount();
	}

	@Override
	protected Node getNewTree() {
		HierarchicalSortedProbeList probes = data.getSortedProbeList();
		if (probes.getMode()==HierarchicalSortedProbeListSetting.SORT_BY_TREE)
			return probes.getTreeInfo().getTree();
		return null;
	}

	


	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.RowHeader.Clustering",
				null,
				MC_ROW,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Add a clustering tree as row header",
				"Clustering Tree"
				);
	}


	@Override
	public RowHeaderElement init(HeatmapStructure struct) {
		data = struct;
		topDown = false;
		vm = data.getViewModel();
		data.getSortedProbeList().addChangeListener(this);
		lsm = new LinkedSelectionManager();
		extendEdges.addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				produceLayout();
				fireChange(UpdateEvent.REPAINT);
			}
		});
		stateChanged(null);
		return this;
	}
	
	
	public void dispose() { /* nada */ }
	
	
	public class ProbeNodeMapper implements ObjectMapper {

		public BidirectionalHashMap<Node,Probe> mapping = new BidirectionalHashMap<Node, Probe>();
		public Layout layout;

		public ProbeNodeMapper(Layout layout, MasterTable mt) {
			this.layout = layout;
			for (Node n : layout.getRoot().getLeaves(null)) {
				Probe pb = mt.getProbe(n.getLabel());
				if (pb!=null)
					mapping.put(n,pb);
			}
		}

		public Object getObject(Node n) {
			return (Probe)mapping.get(n);
		}

		public Node getNode(Object pb) {
			return (Node)mapping.get(pb);
		}

		public String getLabel(Node n) {
			return "";
		}

	}
	
	protected class LinkedSelectionManager extends SelectionManager {

		protected HashSet<Edge> extraEdges = new HashSet<Edge>();
		
		@Override
		public void clearSelection() {
			extraEdges.clear();
		}

		@Override
		public Set<ITreePart> getSelection() {
			System.out.println("getSelection");
			HashSet<ITreePart> selection = new HashSet<ITreePart>();
			selection.addAll(extraEdges);
			for (Probe pb : data.getViewModel().getProbes()) 
				selection.add(om.getNode(pb));
			return selection;
		}

		@Override
		public boolean isSelected(ITreePart object) {
			if (object instanceof Edge) 
				return extraEdges.contains(object);
			else 
				return data.getViewModel().isSelected((Probe)om.getObject((Node)object));
		}

		@Override
		public void setSelected(ITreePart object, boolean status) {
			if (object instanceof Edge) 
				if (status)
					extraEdges.add((Edge)object);
				else
					extraEdges.remove((Edge)object);
			else
				System.err.println("SetSELECTED");
		}
		
	}

	@Override
	protected int getFirstIndex() {
		return 0;
	}

}
