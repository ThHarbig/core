package mayday.core.probelistmanager.gui;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import mayday.core.ProbeList;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.gui.GUIUtilities;
import mayday.core.gui.PluginMenu;
import mayday.core.gui.ProbeListImage;
import mayday.core.gui.ProbeListImageStorage;
import mayday.core.gui.components.ToolbarOverflowLayout;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.core.pluma.PluginInfo;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.probelistmanager.ProbeListManagerTree;
import mayday.core.probelistmanager.UnionProbeList;
import mayday.core.probelistmanager.gui.cellrenderer.GraphicProbeListRenderComponent;
import mayday.core.probelistmanager.models.ListSelectionModelFromTreeSelectionModel;
import mayday.core.probelistmanager.models.ProbeListTreeListModel;
import mayday.vis3.PlotPlugin;

@SuppressWarnings("serial")
public class ProbeListManagerViewTree 
implements ProbeListManagerView {

	protected ProbeListManagerTree probeListManagerTree;
	protected ProbeListJTree tree;
	protected ProbeListTreeListModel model;
	protected ListSelectionModelFromTreeSelectionModel listSelectionModel;

	protected AbstractAction moveUpAction, moveDownAction, addGroupAction, removeSelectionAction;

	protected int GroupCounter = 0;


	public ProbeListManagerViewTree(ProbeListManager probeListManagerTree) {
		moveUpAction = new MoveUpAction();
		moveDownAction = new MoveDownAction();
		addGroupAction = new AddGroupAction();
		removeSelectionAction = new RemoveSelectionAction();

		// Ask for all probelist images right away -> will add them to the delayed update task
		for (ProbeList pl : probeListManagerTree.getProbeLists())
			ProbeListImageStorage.singleInstance().getImage(pl);

		setProbeListManager(probeListManagerTree);	
	}

	public ProbeListJTree getTree() {
		return tree;
	}

	public AbstractAction getRemoveSelectionAction() {
		return removeSelectionAction;
	}

	public AbstractAction getMoveUpAction() {
		return moveUpAction;
	}

	public AbstractAction getMoveDownAction() {
		return moveDownAction;
	}


	public Component getComponent() {
		return tree;
	}

	public JToolBar getActionComponent() {
		JToolBar buttons = new JToolBar();
		buttons.setLayout(new ToolbarOverflowLayout(true, 3, true));
		buttons.setFloatable(false);

		buttons.add(GUIUtilities.makeIconButton(
				getMoveUpAction(), 
				KeyEvent.VK_U, 
				"Increases priority of the selected probe lists", 
				PluginInfo.getIcon("mayday/images/up.png",20,20)));
		buttons.add(GUIUtilities.makeIconButton(
				getMoveDownAction(), 
				KeyEvent.VK_D, 
				"Decreases priority of the selected probe lists", 
				PluginInfo.getIcon("mayday/images/down.png",20,20)));		
		
		buttons.add(Box.createHorizontalStrut(10)); // spacer
		
		buttons.add(GUIUtilities.makeIconButton(
				addGroupAction, 
				KeyEvent.VK_G, 
				"Create a new group from the selected probe lists", 
				PluginInfo.getIcon("mayday/images/addgroup.png",20,20)));
		
		return buttons;
	}

	public JPopupMenu getPopupMenu() {
		return getMenu().getPopupMenu();
	}

	public JMenu getMenu(){    	
		if (!DataSetManagerView.instanceCreated())
			return new JMenu("");
		else
			return DataSetManagerView.getInstance().getProbeListMenu();
	}

	public ListModel getModel() {
		return model;
	}

	public ProbeListManager getProbeListManager() {
		return probeListManagerTree;
	}


	public ListSelectionModel getSelectionModel() {
		return listSelectionModel;
	}




	public void setProbeListManager(ProbeListManager probeListManager) {
		this.probeListManagerTree = (ProbeListManagerTree)probeListManager;
		tree = new ProbeListJTree(probeListManagerTree);
		model = (ProbeListTreeListModel)probeListManagerTree.getModel();

		tree.setDragEnabled(true);
		tree.setTransferHandler(new ProbeListTreeTransferHandler(this));		
		tree.getSelectionModel().setSelectionPath(new TreePath(model.getRoot()));

		tree.addMouseListener(new ProbeListMouseListener());

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				updateMenu();
			}
		});

		tree.setToggleClickCount(3); //double click is for properties / plots

		listSelectionModel = new ListSelectionModelFromTreeSelectionModel(tree.getSelectionModel(), model); 
	}

	public void updateCellRenderer() {
		tree.updateCellRenderer();
	}

	@SuppressWarnings("unchecked")
	public void updateMenu() {
		JMenu g = getMenu();
		if (g!=null && (g instanceof PluginMenu)) {
			((PluginMenu)g).setSelectionChanged();
		}
	}

	public ProbeList[] getSelectedValues() {
		return getSelectedValuesList().toArray(new ProbeList[0]);
	}

	public LinkedList<ProbeList> getSelectedValuesList() {
		ProbeListNode[] dmtns = tree.getSelectedNodes();
		LinkedList<ProbeList> lo = new LinkedList<ProbeList>();
		for (ProbeListNode node : dmtns)
			if (node.getProbeList()!=null)
				lo.add(node.getProbeList());
		return lo;
	}



	public void setSelectedValues(Object... values) {
		TreePath[] tps = new TreePath[values.length];
		int i=0;
		for (Object o : values)
			tps[i++] = new TreePath(model.nodeOf(o, ((ProbeListNode)model.getRoot())).getPath());
		tree.getSelectionModel().setSelectionPaths(tps);
	}


	public class MoveUpAction extends AbstractAction {

		public MoveUpAction() {
			super("Move up");
		}

		public void actionPerformed(ActionEvent e) {
			Object[] selectedObjects = getSelectedValues();

			probeListManagerTree.setSilent( true );

			for ( int i = 0; i != selectedObjects.length; ++i ) {
				// finally notify listener about the changes
				if ( i == selectedObjects.length-1)
					probeListManagerTree.setSilent( false );
				probeListManagerTree.moveUpProbeList( (ProbeList)selectedObjects[i] );
			}


			setSelectedValues(selectedObjects);
		}
	}

	public class MoveDownAction extends AbstractAction {

		public MoveDownAction() {
			super("Move down");
		}

		public void actionPerformed(ActionEvent e) {
			Object[] selectedObjects = getSelectedValues();

			probeListManagerTree.setSilent( true );

			for ( int i = selectedObjects.length - 1; i >= 0; --i )
			{
				// finally notify listener about the changes
				if ( i == 0 )
					probeListManagerTree.setSilent( false );

				probeListManagerTree.moveDownProbeList( (ProbeList)selectedObjects[i] );
			}

			setSelectedValues(selectedObjects);
		}
	}

	public String nextGroupName() {
		return "Group "+(++GroupCounter); 
	}

	public class AddGroupAction extends AbstractAction {

		public AddGroupAction() {
			super( "Add Group" );
		}

		public void actionPerformed(ActionEvent e) {	
			/* find parent to insert into.
			 * a) no selection: insert into root
			 * b) one selected PL, hierarchical: insert here, empty
			 * c) one selected PL, non-hierarchical: insert in parent, below selected pl, empty
			 * d) several selected PL: find common parent, insert and fill with selection
			 */
			ProbeListNode selectedNode = tree.getSelectedNode();
			ProbeList[] sels = getSelectedValues();
			int insertionIndex = 0;
			ProbeList parentList = null; // case a)			

			if (sels.length==1) { // case b,c			 	
				parentList = selectedNode.getProbeList();
				if (parentList!=null && selectedNode !=null && !(parentList instanceof UnionProbeList)) {
					// case c
					parentList = parentList.getParent();		
					insertionIndex = selectedNode.getParent().getIndex(selectedNode)+1;
					selectedNode = selectedNode.getParent();
				}
			}

			if (sels.length>1) { // case d
				LinkedList< LinkedList<ProbeList> > rootPaths = new LinkedList<LinkedList<ProbeList>>();
				// get all paths to the root
				for (ProbeList pl : sels) {
					LinkedList<ProbeList> rootPath = new LinkedList<ProbeList>();
					while (pl!=null) {
						rootPath.add(pl);
						pl = pl.getParent();
					}
					rootPaths.add(rootPath);
				}
				// find common parent
				LinkedList<ProbeList> commonNodes = rootPaths.get(0);
				for (int i=1; i!=rootPaths.size(); ++i)
					commonNodes.retainAll(rootPaths.get(i));
				if (commonNodes.size()>0)
					parentList = commonNodes.get(0);
				// insertionIndex stays 0
			}

			// Create a new group
			UnionProbeList newGroup = new UnionProbeList(probeListManagerTree.getDataSet(), null); // node will be set in a moment...			
			newGroup.setName(nextGroupName());										
			ProbeListNode insertedNode = model.insertProbeList(newGroup, parentList, insertionIndex); // this will set the node  

			// if more than one list was selected, place them all into the new node
			if (sels.length>1) {
				newGroup.beginLargeUpdate();
				for (ProbeList pl : sels)
					model.moveProbeList(pl, newGroup, Integer.MAX_VALUE);
				newGroup.endLargeUpdate();
			}
			if (insertedNode!=null) {
				tree.selectNode(model.nodeOf(newGroup, insertedNode));
				tree.expandPathTo(model.nodeOf(newGroup, insertedNode));
			} else {
				tree.selectNode(model.getRoot());
				tree.expandPathTo(model.getRoot());
			}
		}

	}

	public class RemoveSelectionAction extends AbstractAction
	{
		public RemoveSelectionAction() {
			super( "Close" );
		}

		public void actionPerformed( final ActionEvent event ) {

			// ask for conformation again!
			String message = "Do you really want to remove the selected Probelists?";
			if (JOptionPane.showConfirmDialog((Component)null, message, "Remove Probelists?",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)!=JOptionPane.YES_OPTION) {
				return;
			}

			final Object[] l_selectedValues = getSelectedValues();

			probeListManagerTree.setSilent( true );


			for ( int i = 0; i < l_selectedValues.length; ++i ) {
				// finally notify listeners
				if ( i == l_selectedValues.length - 1 )
					probeListManagerTree.setSilent( false );

				probeListManagerTree.removeObject( (ProbeList)l_selectedValues[i] );

			}
		}
	}



	public class ProbeListMouseListener extends MouseAdapter {
		public void mouseClicked( MouseEvent event )
		{
			// if graphical view is active and double click on the profile image is recieved, open profile plot if vis plugin present
			if ( event.getButton() == MouseEvent.BUTTON1 ) {
				if ( event.getClickCount() == 2 ) {
					if (tree.getSelectedNode()!=null) {
						boolean showingPlot = false;
						if (ProbeListImage.useGraphics.getBooleanValue()) {
							// map coordinates
							int x = event.getX();
							int y = event.getY();
							Rectangle r = tree.getPathBounds(new TreePath(tree.getSelectedNode().getPath()));
							x-= r.x;
							y-= r.y;
							// account for the inset                	  
							Rectangle image = new Rectangle(
									GraphicProbeListRenderComponent.INSETS.left,
									GraphicProbeListRenderComponent.INSETS.top,
									ProbeListImage.fetchWidth(),ProbeListImage.fetchHeight());
							if (image.contains(x,y)) {
								PlotPlugin plp = ProbeListImage.doubleclickplot.getInstance();
								if (plp!=null) {
									LinkedList<ProbeList> lpl   = new LinkedList<ProbeList>();
									lpl.add(tree.getSelectedNode().getProbeList());
									plp.run(lpl,lpl.get(0).getDataSet().getMasterTable());
									event.consume();	
									showingPlot=true;
								} else {
									System.err.println("Could not open plot plugin");
								}
							}
						}
						if (!showingPlot) {
							AbstractPropertiesDialog apd = PropertiesDialogFactory.createDialog(getSelectedValues());
							apd.setVisible(true);
							event.consume();
						}
					}
				}
			}
		}

		public void mousePressed( MouseEvent event ) {
			if ( event.getButton() == MouseEvent.BUTTON3 )
				getPopupMenu().show( tree, event.getX(), event.getY() );
		}
	}

	public void addMouseListener(MouseListener list) {
		if (tree!=null)
			tree.addMouseListener(list);
	}

	public void removeMouseListener(MouseListener list) {
		if (tree!=null)
			tree.removeMouseListener(list);
	}

	public void ensureIndexIsVisible(int index) 
	{
		if(index < 0 )
			return;

		TreePath tp=new TreePath(model.nodeOf(model.getElementAt(index), ((ProbeListNode)model.getRoot())).getPath());
		tree.expandPath(tp);
		tree.scrollPathToVisible(tp);

	}

	public void selectIndex(int index) {
		if(index < 0 )
			return;
		setSelectedValues(model.getElementAt(index));
	}

	public void addKeyListener(KeyListener kl) {
		tree.addKeyListener(kl);
	}

}
