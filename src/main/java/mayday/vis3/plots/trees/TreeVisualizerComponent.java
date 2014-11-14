/*
 *  Created on Aug 29, 2004
 *
 */
package mayday.vis3.plots.trees;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.clustering.hierarchical.TreeInfo;
import mayday.clustering.hierarchical.TreeMIO;
import mayday.core.ClassSelectionModel;
import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.pluginrunner.ProbeListPluginRunner;
import mayday.core.plugins.probe.ProbeMenu;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.structures.trees.layout.Layout;
import mayday.core.structures.trees.layout.ObjectMapper;
import mayday.core.structures.trees.painter.INodePainter;
import mayday.core.structures.trees.painter.TreePainter;
import mayday.core.structures.trees.painter.node.LabelWithAngle;
import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.ITreePart;
import mayday.core.structures.trees.tree.Node;
import mayday.core.tasks.AbstractTask;
import mayday.genemining2.GeneminingPlugin;
import mayday.genemining2.cng.Bipartition;
import mayday.genemining2.cng.GeneminingCNGPlugin;
import mayday.vis3.ZoomController;
import mayday.vis3.categorical.ClassSelectionColoring;
import mayday.vis3.components.AntiAliasPlotPanel;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

@SuppressWarnings("serial")
public class TreeVisualizerComponent extends AntiAliasPlotPanel implements ViewModelListener {

	protected TreePainter painter;

	private PlotContainer plotContainer;

	private final static String TITLE = "Tree Visualizer";

	private ViewModel viewModel;
	private TreeInfo treeInfo;

	private TreeDelegate delegate;

	private TreePainterSetting setting;
	
	protected ClassSelectionColoring classColors;

	public TreeVisualizerComponent() {
		painter = new TreePainter();
	}

	@Override
	public String getName() {		
		return "Tree Visualizer";
	}
	
	private Dimension oldSize = getSize();

	@Override
	public void paintPlot(Graphics2D g) {
		g.setBackground(Color.white);
		g.clearRect(0, 0, getWidth(), getHeight());	
		if (treeInfo == null) {
			g.drawString("No TreeInfo found in this Visualizer. Add a ProbeList with a TreeInfo MIO.", 30, 30);
		} else {
			if(!oldSize.equals(getSize()))
				setPreferredSize(getSize());
			painter.paint(g, getSize());			
		}
	}

	@Override
	public void setup(PlotContainer plotContainer) {
		viewModel = plotContainer.getViewModel();
		this.plotContainer = plotContainer;
		plotContainer.setPreferredTitle(TITLE, this);

		ZoomController zoomController = new ZoomController();
		zoomController.setTarget(this);
		zoomController.setActive(true);
		zoomController.setAllowXOnlyZooming(true);
		zoomController.setAllowYOnlyZooming(true);

		findUsableTrees();

	}

	public void findUsableTrees() {
		// find out how many trees are present in the viewmodel
		final HashMap<ProbeList,TreeInfo> candidates = new HashMap<ProbeList,TreeInfo>();

		for (ProbeList pl : viewModel.getProbeLists(false)) {
			MIGroupSelection<MIType> mgs = pl.getDataSet().getMIManager().getGroupsForType("PAS.MIO.HierarchicalClusteringTree");
			MIType mt=null;
			for (MIGroup mg : mgs) {
				mt = mg.getMIO(pl);
				if (mt!=null)
					break;
			}
			if (mt!=null) {
				TreeInfo ti = ((TreeMIO)mt).getValue();
				candidates.put(pl, ti);
			}
		}

		treeInfo = null;

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				if (candidates.isEmpty()) {
					updatePlot();
					return;
				}
				if (candidates.size()==1) {
					adoptTree(candidates.values().iterator().next(), plotContainer, candidates.keySet().iterator().next());
				} else {
					Object result = JOptionPane.showInputDialog((Component)null, "Please select a tree to display", 
							"Multiple trees found", JOptionPane.QUESTION_MESSAGE, null, 
							candidates.keySet().toArray(), candidates.keySet().iterator().next());
					if (result==null)
						treeInfo=null;
					else {
						adoptTree(candidates.get(result), plotContainer, (ProbeList)result);
					}
				}
				
			}

		});
	}

	public void adoptTree(TreeInfo ti, PlotContainer plotContainer, ProbeList pl) {
		if (treeInfo==ti)
			return;

		boolean nullBefore = treeInfo==null;

		if (!nullBefore) {
			removeMouseListener(delegate.getMouseListener());
		}

		treeInfo = ti;

		setting = new TreePainterSetting("Tree Settings", viewModel, !treeInfo.getSettings().isMatrixTransposed());
		setting.addChangeListener(new SettingChangeListener() {

			public void stateChanged(SettingChangeEvent e) {
				updatePlot();				
			}			
		});

		try {
			if (!treeInfo.getSettings().isMatrixTransposed()) {
				delegate = new TreeDelegateProbes(treeInfo.getTree(), setting, painter);
			} else {
				delegate = new TreeDelegateExperiments(treeInfo.getTree(), setting, painter);
			}			
			delegate.getScreenLayout().getSelectionManager().addListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					updatePlot(); // not very efficient, but ok
				}
			});
			delegate.setupMenus(plotContainer, this);
			addMouseListener(delegate.getMouseListener());

		} catch (Exception e) {
			treeInfo=null;
			delegate=null;
			e.printStackTrace();
		}

		if (nullBefore)
			viewModel.addViewModelListener(this);

		if (nullBefore & treeInfo!=null) {
			for (Setting sub : setting.getChildren())
				plotContainer.addViewSetting(sub, this);
		}

		updatePlot();
	}


	public void viewModelChanged(ViewModelEvent vme) {
		if (vme.getChange()==ViewModelEvent.PROBELIST_SELECTION_CHANGED) {
			setup(plotContainer);
		}

		if (treeInfo==null || treeInfo.getSettings().isMatrixTransposed())
			return;

		if (vme.getChange()==ViewModelEvent.PROBE_SELECTION_CHANGED 
				|| vme.getChange()==ViewModelEvent.EXPERIMENT_SELECTION_CHANGED) {
			// make sure the changed selection is reflected in probe colors
			updatePlot();
		}

	}

	public void removeNotify() {
		viewModel.removeViewModelListener(this);
		if (classColors!=null)
			classColors.hideColorAssignmentWindow();
		super.removeNotify();
	}




	public class TreeDelegateProbes extends TreeDelegate {

		protected ViewModelProbeSelectionManager smgr;

		public TreeDelegateProbes(Node tree, TreePainterSetting setting, TreePainter painter) {
			super(tree, setting, painter);
			setting.getHeatmapLabelSetting().addChangeListener(new SettingChangeListener() {
				public void stateChanged(SettingChangeEvent e) {
					setPainter();
				}
			});
		}

		protected void setLayoutForPainting(Layout l) {
			super.setLayoutForPainting(l);
			if (smgr!=null)
				viewModel.removeViewModelListener(smgr);
			// now add an object mapper
			ObjectMapper om = new ProbeNodeMapper(sl, viewModel.getDataSet().getMasterTable());			
			sl.setObjectMapper(om);
			setPainter();			
			// now connect tree and viewmodel
			smgr = new ViewModelProbeSelectionManager(viewModel, sl);
			sl.setSelectionManager(smgr);		
		}

		public void handleButton3(MouseEvent e) {
			ProbeMenu pm = new ProbeMenu(viewModel.getSelectedProbes(), viewModel.getDataSet().getMasterTable());
			pm.getPopupMenu().add(new ChangeLabelAction());
			pm.getPopupMenu().add(new RerootAction());
			pm.getPopupMenu().add(new AddRootAction());
			pm.getPopupMenu().add(new SwapChildrenAction());
			pm.getPopupMenu().show(TreeVisualizerComponent.this, e.getX(), e.getY());
		}

		protected boolean handleEdgeSelection(Edge e) {
			// Select all nodes below
			boolean eWasSelected = painter.getScreenLayout().isSelected(e);
			Collection<Node> nodes ;
			if (!eWasSelected) 
				nodes = e.getNode(1).getLeaves(e);
			else
				nodes = e.getNode(0).getLeaves(e);
			painter.getScreenLayout().clearSelected();
			for (Node n : nodes)
				painter.getScreenLayout().setSelected(n, true);
			painter.getScreenLayout().setSelected(e, !eWasSelected);
			return true;
		}
		

		public void setPainter() {
			sl.getNodeLayouts().getDefaultLayout().setPainter(
					setting.getHeatmapLabelSetting().getBooleanValue()
					?
							new HeatmapLabel(setting.getColorProvider(), sl.getObjectMapper(), viewModel)
					:
						new ColorizedProbeLabelWithAngle(setting.getColorProvider(),  sl.getObjectMapper())
			);
		}

	}

	

	protected class RerootAction extends AbstractAction {

		public RerootAction() {
			super("Set this node as root node");
		}

		public void actionPerformed(ActionEvent e) {
			if(painter.getScreenLayout()!=null) {
				Set<ITreePart> selected = painter.getScreenLayout().getSelected();
				if(selected.size()!=1 || !(selected.iterator().next() instanceof Node))
					JOptionPane.showMessageDialog(TreeVisualizerComponent.this, 
							"Please select exactly one node as root.", 
							"Incorrect selection", 
							JOptionPane.ERROR_MESSAGE);
				else {
					Node node = (Node) selected.iterator().next();				
					if (node.isLeaf())
						node = node.getEdges().iterator().next().getOtherNode(node);
					node.makeRoot();
					delegate.replaceTree(node);
					treeInfo.setTree(node);
					updatePlot();
				}
			}
		}
	}
	
	protected class ChangeLabelAction extends AbstractAction {

		public ChangeLabelAction() {
			super("Edit node label");
		}

		public void actionPerformed(ActionEvent e) {
			if(painter.getScreenLayout()!=null) {
				Set<ITreePart> selected = painter.getScreenLayout().getSelected();
				boolean correct = selected.size()==1;
				if (correct) 
					correct &= (selected.iterator().next() instanceof Node);
				if (correct) {
					Node n = (Node)selected.iterator().next(); 
					if (n.isLeaf())
						correct = false;
					if (correct) {
						String oldName = n.getLabel();
						if (oldName==null)
							oldName="";
						String newName = (String)JOptionPane.showInputDialog(
								TreeVisualizerComponent.this, 
								"Enter a node name",
								"Node label",								
								JOptionPane.QUESTION_MESSAGE,
								null,null,oldName);
						if (newName!=null && !newName.equals(oldName)) {
							n.setLabel(newName);
							updatePlot(); 
						}
					}
				}				
				if (!correct) 
					JOptionPane.showMessageDialog(TreeVisualizerComponent.this, 
							"Please select exactly one node internal node", 
							"Incorrect selection", 
							JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	protected class AddRootAction extends AbstractAction {

		public AddRootAction() {
			super("Add root between selected nodes");
		}

		public void actionPerformed(ActionEvent e) {
			if(painter.getScreenLayout()!=null) {
				Set<ITreePart> selected = painter.getScreenLayout().getSelected();
				boolean correct = selected.size()==2;
				if (correct) {
					Iterator<ITreePart> itp = selected.iterator();
					correct = ((itp.next() instanceof Node) && (itp.next() instanceof Node));
					if (correct) {
						itp = selected.iterator();
						Node n1 = (Node)itp.next();
						Node n2 = (Node)itp.next();
						Edge connector=null;
						correct = false;						
						for (Edge edg : n1.getEdges()) {
							if (edg.getOtherNode(n1)==n2) { 
								correct = true;
								connector = edg; 
								break;
							}
						}
						if (connector!=null) {
							// remove existing edge
							Node newRoot = new Node("",null);
							Edge newEdge1 = new Edge(connector.getLength()/2d,newRoot,n1);
							Edge newEdge2 = new Edge(connector.getLength()/2d,newRoot,n2);
							newRoot.addEdge(newEdge1);
							newRoot.addEdge(newEdge2); 
							n1.replaceEdge(connector, newEdge1);
							n2.replaceEdge(connector, newEdge2);
							newRoot.makeRoot();
							delegate.replaceTree(newRoot);
							treeInfo.setTree(newRoot);
							updatePlot();
						}
					}
					
				}
				if (!correct) 
					JOptionPane.showMessageDialog(TreeVisualizerComponent.this, 
							"Please select exactly two neighboring nodes.", 
							"Incorrect selection", 
							JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	protected class SwapChildrenAction extends AbstractAction {

		public SwapChildrenAction() {
			super("Swap children selected nodes");
		}

		public void actionPerformed(ActionEvent e) {
			if(painter.getScreenLayout()!=null) {
				Set<ITreePart> selected = painter.getScreenLayout().getSelected();
				for (ITreePart itp : selected) {
					if (itp instanceof Edge)
						continue;
					Node node = (Node)itp;				
					if (!node.isLeaf() && node.getEdges().size()>2)
						node.swapChildren(1, 2);
					delegate.replaceTree(painter.getScreenLayout().getRoot());
					updatePlot();
				}
			}
		}
	}

	public class TreeDelegateExperiments extends TreeDelegate { 
		
		protected ViewModelExperimentSelectionManager smgr;
		
		public TreeDelegateExperiments(Node tree, TreePainterSetting setting, TreePainter painter) {
			super(tree,setting,painter);
			setting.getExperimentClassColoringSetting().addChangeListener(new SettingChangeListener() {
				public void stateChanged(SettingChangeEvent e) {
					setPainter();
				}
			});
		}

		@SuppressWarnings("deprecation")
		public void setupMenus(PlotContainer pc, PlotComponent askingObject) {
			super.setupMenus(pc, askingObject);
			pc.addMenu(new JMenu("Gene Mining"));
			JMenu mnu = pc.getMenu("Gene Mining", askingObject);
			mnu.add(new GeneminingAction("... start with selected edge"));
			mnu.add(new GeneminingCompareAction("... start with selected edge - compare number of genes"));
		}
		
		protected void setLayoutForPainting(Layout l) {
			super.setLayoutForPainting(l);
			if (smgr!=null)
				viewModel.removeViewModelListener(smgr);
			// now add an object mapper
			ObjectMapper om = new ExperimentNodeMapper(sl, viewModel.getDataSet().getMasterTable());			
			sl.setObjectMapper(om);
			setPainter();	
			// now connect tree and viewmodel
			smgr = new ViewModelExperimentSelectionManager(viewModel, sl);
			sl.setSelectionManager(smgr);		
		}

		protected boolean handleEdgeSelection(Edge e) {
			// Select all nodes below, creates a bipartition for genemining
			boolean eWasSelected = painter.getScreenLayout().isSelected(e);
			Collection<Node> nodes ;
			if (!eWasSelected) 
				nodes = e.getNode(1).getLeaves(e);
			else
				nodes = e.getNode(0).getLeaves(e);
			painter.getScreenLayout().clearSelected();
			for (Node n : nodes)
				painter.getScreenLayout().setSelected(n, true);
//			updatePlot();
			painter.getScreenLayout().setSelected(e, !eWasSelected);
			return true;
		}
		
		public void setPainter() {
			
			INodePainter inp;
			if (classColors!=null)
				classColors.hideColorAssignmentWindow();				
			
			if (setting.useClasses()) {
				classColors = new ClassSelectionColoring(setting.getExperimentClasses(), viewModel, "Class Labels");
				classColors.showColorAssignmentWindow();
				classColors.addListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						updatePlot();
					}
				});
				inp = new ColorizedExperimentLabelWithAngle(classColors,  sl.getObjectMapper());
			} else {
				inp = new LabelWithAngle();
			}

			sl.getNodeLayouts().getDefaultLayout().setPainter( inp );						
		}

		protected void handleButton1(MouseEvent e) {
			super.handleButton1(e);
			updatePlot();
		}
		
		public void handleButton3(MouseEvent e) {
			JPopupMenu pm = new JPopupMenu();			
			pm.add(new ChangeLabelAction());
			pm.add(new RerootAction());
			pm.add(new AddRootAction());
			pm.add(new SwapChildrenAction());
			pm.show(TreeVisualizerComponent.this, e.getX(), e.getY());
		}


	}

	protected class GeneminingAction extends AbstractAction {

		public GeneminingAction( String text ) {
			super( text );
		}

		public void actionPerformed( ActionEvent event ) {
			AbstractTask at = new AbstractTask("GeneMining from Tree Visualizer") {
				
				@Override
				protected void initialize() {
				}
				
				@Override
				protected void doWork() throws Exception {
					List<String> partition = new ArrayList<String>();
					List<String> objectNames = new ArrayList<String>();

					MasterTable masterTable = viewModel.getDataSet().getMasterTable();
					List<ProbeList> probeLists = viewModel.getProbeLists(false); 

					for ( Node e : painter.getScreenLayout().getRoot().getLeaves(null) ) {
						objectNames.add(painter.getScreenLayout().getLabel(e));
						partition.add( painter.getScreenLayout().isSelected(e) ? "+1" : "-1");
					}
					
					ClassSelectionModel classSelectionModel = new ClassSelectionModel( objectNames, partition );
					Bipartition bipartition = treeInfo.getSettings().getBipartition();
					bipartition.set(classSelectionModel);
					
					//run gene-mining with classSelectionModel generated from tree
					GeneminingPlugin genemining = new GeneminingPlugin();
					
					List<ProbeList> result = genemining.run(probeLists, masterTable, classSelectionModel);
					
					ProbeListPluginRunner.insertProbeListsIntoProbeListManager(probeLists, result, masterTable.getDataSet().getProbeListManager(), "Gene Mining from tree");
				}
			};
			at.start();
			
			
		}
	}
	
	protected class GeneminingCompareAction extends AbstractAction {

		public GeneminingCompareAction( String text ) {
			super( text );
		}

		public void actionPerformed( ActionEvent event ) {
			List<String> partition = new ArrayList<String>();
			List<String> objectNames = new ArrayList<String>();

			MasterTable masterTable = viewModel.getDataSet().getMasterTable();
			List<ProbeList> probeLists = viewModel.getProbeLists(false); 

			for ( Node e : painter.getScreenLayout().getRoot().getLeaves(null) ) {
				objectNames.add(painter.getScreenLayout().getLabel(e));
				partition.add( painter.getScreenLayout().isSelected(e) ? "+1" : "-1");
			}
			
			ClassSelectionModel classSelectionModel = new ClassSelectionModel( objectNames, partition );
			Bipartition bipartition = treeInfo.getSettings().getBipartition();
			bipartition.set(classSelectionModel);
			
			//run gene-mining with classSelectionModel generated from tree
			GeneminingCNGPlugin genemining = new GeneminingCNGPlugin();
			genemining.run(probeLists, masterTable, classSelectionModel, 
					treeInfo.getSettings());
		}
	}
}
