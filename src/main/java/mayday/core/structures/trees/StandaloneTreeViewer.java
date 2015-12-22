package mayday.core.structures.trees;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import mayday.core.structures.trees.io.LayoutedNewick;
import mayday.core.structures.trees.io.PlainNewick;
import mayday.core.structures.trees.layout.Layout;
import mayday.core.structures.trees.layouter.Circular;
import mayday.core.structures.trees.layouter.LeftRightDendrogram;
import mayday.core.structures.trees.layouter.TopDownDendrogram;
import mayday.core.structures.trees.layouter.TreeLayoutPlugin;
import mayday.core.structures.trees.layouter.UnrootedHorizontal;
import mayday.core.structures.trees.layouter.UnrootedVertical;
import mayday.core.structures.trees.painter.IEdgePainter;
import mayday.core.structures.trees.painter.INodePainter;
import mayday.core.structures.trees.painter.TreePainterPanel;
import mayday.core.structures.trees.painter.edge.DendrogramEdges;
import mayday.core.structures.trees.painter.edge.DirectEdgePainter;
import mayday.core.structures.trees.painter.edge.RadialEdgePainter;
import mayday.core.structures.trees.painter.node.LabelBelow;
import mayday.core.structures.trees.painter.node.LabelInside;
import mayday.core.structures.trees.painter.node.LabelWithAngle;
import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.ITreePart;
import mayday.core.structures.trees.tree.Node;
import mayday.vis3.ZoomController;




@SuppressWarnings("serial")
public class StandaloneTreeViewer extends JFrame {
	
	protected TreePainterPanel treePainter;
	protected Node tree;
	protected Layout layout;

	public StandaloneTreeViewer() {
		super("Tree Visualizer");
		setLayout(new BorderLayout());		
		setJMenuBar(createMenuBar());
		
		treePainter = new TreePainterPanel();
		treePainter.setBackground(Color.white);
		
		// Add zooming capabilities (mouse wheel + ctrl [+shift/alt])
		JScrollPane sp = new JScrollPane(treePainter);
		ZoomController zoomer = new ZoomController();
		zoomer.setTarget(treePainter);
		zoomer.setAllowXOnlyZooming(true);
		zoomer.setAllowYOnlyZooming(true);
		
		add(sp, BorderLayout.CENTER);
		pack();
		setSize(800,600);
	}
	
	public StandaloneTreeViewer(File treeFile) {
		this();
		try {
			load(treeFile);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public JMenuBar createMenuBar() {
		JMenuBar ret = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new LoadAction());
		fileMenu.add(new StoreAction(false));
		fileMenu.add(new StoreAction(true));
		fileMenu.add(new JSeparator());
		fileMenu.add(new AbstractAction("Exit") {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		ret.add(fileMenu);
		
		JMenu layoutMenu = new JMenu("Layout");
		layoutMenu.add(new ReLayoutAction(new TopDownDendrogram()));
		layoutMenu.add(new ReLayoutAction(new LeftRightDendrogram()));
		layoutMenu.add(new ReLayoutAction(new Circular()));
		layoutMenu.add(new ReLayoutAction(new UnrootedHorizontal()));
		layoutMenu.add(new ReLayoutAction(new UnrootedVertical()));
		ret.add(layoutMenu);
		
		JMenu epMenu = new JMenu("EdgePainter");
		epMenu.add(new DefaultEdgePainterAction(new DirectEdgePainter()));
		epMenu.add(new DefaultEdgePainterAction(new DendrogramEdges()));
		epMenu.add(new DefaultEdgePainterAction(new RadialEdgePainter()));
		
		JMenu npMenu = new JMenu("NodePainter");
		npMenu.add(new DefaultNodePainterAction(new LabelInside()));
		npMenu.add(new DefaultNodePainterAction(new LabelBelow()));
		npMenu.add(new DefaultNodePainterAction(new LabelWithAngle()));
		
		JMenu defaults = new JMenu("Defaults");
		defaults.add(epMenu);
		defaults.add(npMenu);
		defaults.add(new DefEdgeColorAction());
		defaults.add(new DefNodeColorAction());
		ret.add(defaults);
		
		JMenu ep2Menu = new JMenu("EdgePainter");
		ep2Menu.add(new EdgePainterAction(new DirectEdgePainter()));
		ep2Menu.add(new EdgePainterAction(new DendrogramEdges()));
		ep2Menu.add(new EdgePainterAction(new RadialEdgePainter()));
		
		JMenu np2Menu = new JMenu("NodePainter");
		np2Menu.add(new NodePainterAction(new LabelInside()));
		np2Menu.add(new NodePainterAction(new LabelBelow()));
		
		JMenu edgeMenu = new JMenu("Edge");
		edgeMenu.add(ep2Menu);
//		edgeMenu.add(new SplitAction());
		edgeMenu.add(new EdgeColorAction());
		
		JMenu nodeMenu = new JMenu("Node");
		nodeMenu.add(np2Menu);
		nodeMenu.add(new RenameAction());
		nodeMenu.add(new RerootAction());
		nodeMenu.add(new NodeColorAction());
		
		JMenu selection = new JMenu("Selection");
		selection.add(edgeMenu);
		selection.add(nodeMenu);
		ret.add(selection);
		
		return ret;
	}
	
	/**
	 * loads a tree either with or without layout from a file and displays it. 
	 * @param treeFile the file to load
	 * @author battke
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public void load(File treeFile) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		LayoutedNewick ln = new LayoutedNewick();
		BufferedReader br = new BufferedReader(new FileReader(treeFile));		
		layout = ln.parseWithLayout(br);
		tree = layout.getRoot();		
		treePainter.setLayoutForPainting(layout);
		repaint();
	}
	
	public void load(Node root, TreeLayoutPlugin layouter) {
		layout = layouter.doLayout(root);
		tree = root;
		treePainter.setLayoutForPainting(layout);
		repaint();
	}
	
	public final static void main(String[] args) {
		StandaloneTreeViewer m = new StandaloneTreeViewer();
		m.setDefaultCloseOperation(EXIT_ON_CLOSE);
		m.setVisible(true);
	}
	
	
	protected class StoreAction extends AbstractAction {
		
		protected boolean savelayout;
		
		public StoreAction(boolean withLayout) {
			super(!withLayout?"Save newick only...":"Save layouted tree...");
			savelayout = withLayout;
		}
		
		public void actionPerformed(ActionEvent e) {
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle("Save the tree "+(savelayout?"and layout":""));
			jfc.setDialogType(JFileChooser.SAVE_DIALOG);
			int returnVal = jfc.showSaveDialog(StandaloneTreeViewer.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File targetFile = jfc.getSelectedFile();
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(targetFile));
					if (savelayout)
						new LayoutedNewick().serialize(layout, null, bw);
					else
						new PlainNewick().serialize(tree, null, bw);
					bw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
	}
	
	protected class LoadAction extends AbstractAction {

		public LoadAction() {
			super("Open...");
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle("Load a tree");
			jfc.setDialogType(JFileChooser.OPEN_DIALOG);
			jfc.setMultiSelectionEnabled(true);
			int returnVal = jfc.showOpenDialog(StandaloneTreeViewer.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File firstFile = jfc.getSelectedFile();
				try {
					load(firstFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
				// load remaining files in new windows
				for (File extraFile : jfc.getSelectedFiles()) {
					if (extraFile!=firstFile)
						new StandaloneTreeViewer(extraFile).setVisible(true);
				}
			}
		}
	}
	
	protected class ReLayoutAction extends AbstractAction {

		TreeLayoutPlugin layouter;
		
		public ReLayoutAction(TreeLayoutPlugin Layouter) {
			super(Layouter.toString());
			layouter=Layouter;
		}

		public void actionPerformed(ActionEvent e) {
			if (tree!=null) {
				layout = layouter.doLayout(tree);
				treePainter.setLayoutForPainting(layout);
				repaint();
			}
		}
	}
	
	protected class DefaultEdgePainterAction extends AbstractAction {

		IEdgePainter painter;
		
		public DefaultEdgePainterAction(IEdgePainter Painter) {
			super(Painter.toString());
			painter=Painter;
		}

		public void actionPerformed(ActionEvent e) {
			if (layout!=null) {
				layout.setDefaultPainters(null, painter);
				repaint();
			}
		}
	}

	protected class DefEdgeColorAction extends AbstractAction {
		
		public DefEdgeColorAction() {
			super("Set default Edge Color");
		}
		
		public void actionPerformed(ActionEvent e) {
			ScreenLayout sl = treePainter.getLayoutForPainting();
			if(sl!=null) {
				Color c = JColorChooser.showDialog(new StandaloneTreeViewer(), "Choose new Color", Color.red);
				if(c!=null) {
					sl.getEdgeLayouts().getDefaultLayout().setColor(c);
					repaint();
				}
			}
		}
	}
	
	protected class DefNodeColorAction extends AbstractAction {
		
		public DefNodeColorAction() {
			super("Set default Node Color");
		}
		
		public void actionPerformed(ActionEvent e) {
			ScreenLayout sl = treePainter.getLayoutForPainting();
			if(sl!=null) {
				Color c = JColorChooser.showDialog(new StandaloneTreeViewer(), "Choose new Color", Color.red);
				if(c!=null) {
					sl.getNodeLayouts().getDefaultLayout().setColor(c);
					repaint();
				}
			}
		}
	}
	
	protected class EdgeColorAction extends AbstractAction {
		
		public EdgeColorAction() {
			super("Change Color");
		}
		
		public void actionPerformed(ActionEvent e) {
			ScreenLayout sl = treePainter.getLayoutForPainting();
			if(sl!=null) {
				if(sl.getSelected().size()!=0) {
					Color c = JColorChooser.showDialog(new StandaloneTreeViewer(), "Choose new Color", Color.red);
					if(c!=null) {
						for(ITreePart x: sl.getSelected()) {
								if(x instanceof Edge) {
									Edge edge = (Edge) x;
									if(layout.hasDefaultLayout(edge))
										layout.setLayout(edge, layout.getLayout(edge).clone());
									layout.getLayout(edge).setColor(c);
								}
							}
							}
					sl.clearSelected();
					repaint();
						}
				}
			}
		}
	
	protected class NodeColorAction extends AbstractAction {
		
		public NodeColorAction() {
			super("Change Color");
		}
		
		public void actionPerformed(ActionEvent e) {
			ScreenLayout sl = treePainter.getLayoutForPainting();
			if(sl!=null) {
				if(sl.getSelected().size()!=0) {
					Color c = JColorChooser.showDialog(new StandaloneTreeViewer(), "test", Color.red);
					if(c!=null) {
						for(ITreePart x: sl.getSelected()) {
							if(x instanceof Node) {
								Node node = (Node) x;
								if(layout.hasDefaultLayout(node))
									layout.setLayout(node, layout.getLayout(node).clone());
								layout.getLayout(node).setColor(c);
							}
						}
						}
					sl.clearSelected();
					repaint();
					}
				}
			}
		}
	
	protected class RenameAction extends AbstractAction {
		
		public RenameAction() {
			super("Rename");
		}
		
		public void actionPerformed(ActionEvent e) {
			if(treePainter.getLayoutForPainting()!=null) {
				Set<ITreePart> selected = treePainter.getLayoutForPainting().getSelected();
				if(selected.size()!=1 || !(selected.iterator().next() instanceof Node))
					System.out.println("USAGE: Select one Node!");
				else {
					Node node = (Node) selected.iterator().next();
					String name = JOptionPane.showInputDialog("New name:");
					if(name != null)
						node.setLabel(name);
					repaint();
				}
		}
	}
	}
	
	protected class RerootAction extends AbstractAction {
		
		public RerootAction() {
			super("Make Root");
		}
		
		public void actionPerformed(ActionEvent e) {
			if(treePainter.getLayoutForPainting()!=null) {
				Set<ITreePart> selected = treePainter.getLayoutForPainting().getSelected();
				if(selected.size()!=1 || !(selected.iterator().next() instanceof Node))
					System.out.println("USAGE: Select one Node!");
				else {
					Node node = (Node) selected.iterator().next();
					tree = node;
					node.makeRoot();
					new ReLayoutAction(treePainter.getLayoutForPainting().getLayouter()).actionPerformed(null);
					repaint();
				}
		}
	}
	}
	
//	protected class SplitAction extends AbstractAction {
//		
//		public SplitAction() {
//			super("Split tree");
//		}
//		
//		public void actionPerformed(ActionEvent e) {
//			if(treePainter.getScreenLayout()!=null) {
//				HashSet<ITreePart> selected = treePainter.getScreenLayout().getSelected();
//				if(selected.size()!=1 || !(selected.iterator().next() instanceof Edge))
//					System.out.println("USAGE: Select one Edge!");
//				else {
//					Edge ed = (Edge) selected.iterator().next();
//					ILayouter layouter = treePainter.getScreenLayout().getLayouter();
//					if(ed.isLeftEdge())
//						ed.getParent().setLeftedge(null);
//					else
//						ed.getParent().setRightedge(null);
//					Node child = ed.getChild();
//					child.setParentedge(null);
//					MainWindow m = new MainWindow();
//					m.setVisible(true);
//					m.load(child, layouter);
//					//alten Baum updaten
//					new ReLayoutAction(layouter).actionPerformed(e);
//					}
//				}
//			}
//	}
	
	protected class EdgePainterAction extends AbstractAction {

		IEdgePainter painter;
		
		public EdgePainterAction(IEdgePainter Painter) {
			super(Painter.toString());
			painter=Painter;
		}

		public void actionPerformed(ActionEvent e) {
			if (layout!=null) {
				for(ITreePart x: treePainter.getLayoutForPainting().getSelected()) {
					if(x instanceof Edge) {
						Edge edge = (Edge) x;
						if(layout.hasDefaultLayout(edge))
								layout.setLayout(edge, layout.getLayout(edge).clone());
						layout.getLayout(edge).setPainter(painter);
					}
				}
				repaint();
			}
		}
	}
	
	protected class NodePainterAction extends AbstractAction {

		INodePainter painter;
		
		public NodePainterAction(INodePainter Painter) {
			super(Painter.toString());
			painter=Painter;
		}

		public void actionPerformed(ActionEvent e) {
			if (layout!=null) {
				for(ITreePart x: treePainter.getLayoutForPainting().getSelected()) {
					if(x instanceof Node) {
						Node node = (Node) x;
						if(layout.hasDefaultLayout(node))
								layout.setLayout(node, layout.getLayout(node).clone());
						layout.getLayout(node).setPainter(painter);
					}
				}
				repaint();
			}
		}
	}

	
	protected class DefaultNodePainterAction extends AbstractAction {

		INodePainter painter;
		
		public DefaultNodePainterAction(INodePainter Painter) {
			super(Painter.toString());
			painter=Painter;
		}

		public void actionPerformed(ActionEvent e) {
			if (layout!=null) {
				layout.setDefaultPainters(painter, null);
				repaint();
			}
		}
	}
	
}
