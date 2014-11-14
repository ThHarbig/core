package mayday.core.probelistmanager.gui;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.DelayedUpdateTask;
import mayday.core.ProbeList;
import mayday.core.StoreEvent;
import mayday.core.StoreListener;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.gui.GUIUtilities;
import mayday.core.gui.PluginMenu;
import mayday.core.gui.ProbeListImage;
import mayday.core.gui.components.ToolbarOverflowLayout;
import mayday.core.gui.probelist.ProbeListListbox;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.core.pluma.PluginInfo;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.probelistmanager.gui.cellrenderer.GraphicProbeListCellRenderer;
import mayday.core.probelistmanager.gui.cellrenderer.GraphicProbeListRenderComponent;
import mayday.core.probelistmanager.gui.cellrenderer.ProbeListCellRenderer;
import mayday.vis3.PlotPlugin;

/*
 * Created on Apr 6, 2003
 *
 */

/**
 * @author neil
 * @version 0.1
 */
@SuppressWarnings("serial")
public class ProbeListManagerViewDefault
extends ProbeListListbox
implements ProbeListManagerView, PropertyChangeListener, StoreListener 
{
	private ProbeListManager probeListManager;

	private RemoveSelectionAction removeSelectionAction;
	private MoveUpAction moveUpAction;
	private MoveDownAction moveDownAction;


	public ProbeListManagerViewDefault( ProbeListManager probeListManager ) {
		super();
		setProbeListManager( probeListManager );		
	}
	
	public void removeNotify() {
		probeListManager.removeStoreListener(this);
		super.removeNotify();
	}
	
	public void addNotify() {
		probeListManager.addStoreListener(this);
		super.addNotify();
	}

	public JToolBar getActionComponent() {
		JToolBar buttons = new JToolBar();
		buttons.setLayout(new ToolbarOverflowLayout(true, 3, true));		

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
		
		return buttons;
	}
	
	
	DelayedUpdateTask dut;
	
	public void super_repaint() {
		super.repaint();
	}
	
	public void repaint() {
		if (dut==null) {
			dut = new DelayedUpdateTask("PLMV repaint", 1500) {

					protected boolean needsUpdating() {
						return true;
					}

					protected void performUpdate() {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								super_repaint();
							}
						});
					}
					
				};
		}
		dut.trigger();
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


	public AbstractAction getRemoveSelectionAction(){
		return ( removeSelectionAction );
	}


	public AbstractAction getMoveUpAction(){
		return ( moveUpAction );
	}


	public AbstractAction getMoveDownAction(){
		return ( moveDownAction );
	}



	public void propertyChange( PropertyChangeEvent event )
	{
		if ( event.getPropertyName() == "enabled" ) {
			boolean l_activationMode = (((Boolean)event.getNewValue()).booleanValue() == true );
			updateMenu();
			JMenu g = getMenu();
			if (g!=null)
				g.setEnabled(l_activationMode);
		}
	}

	public ProbeListManager getProbeListManager() {
		return this.probeListManager;
	}

	
	public class MoveUpAction extends AbstractAction
	{
		public MoveUpAction( String text, Icon icon ) {			
			super( text, icon );
		}


		public MoveUpAction( String text ) {
			super( text );
		}


		public void actionPerformed( ActionEvent event ) {
			int[] l_selectedItems = getSelectedIndices();

			probeListManager.setSilent( true );

			for ( int i = 0; i < l_selectedItems.length; ++i )	{
				// finally notify listener about the changes
				if ( i == l_selectedItems.length - 1 )           
					probeListManager.setSilent( false );

				probeListManager.moveUpProbeList( l_selectedItems[i] );

				if ( l_selectedItems[i] > 0 )
					--l_selectedItems[i]; // move selection
				else
					l_selectedItems[i] = -1;
			}

//			setListData( probeListManager.getObjects().toArray() );
			setSelectedIndices( l_selectedItems );
		}
	}


	public class MoveDownAction	extends AbstractAction	{
		
		public MoveDownAction( String text, Icon icon ) {
			super( text, icon );
		}


		public MoveDownAction( String text ) {
			super( text );
		}


		public void actionPerformed( ActionEvent event ) {
			int[] l_selectedItems = getSelectedIndices();

			probeListManager.setSilent( true );

			for ( int i = l_selectedItems.length - 1; i >= 0; --i )
			{
				// finally notify listener about the changes
				if ( i == 0 )
					probeListManager.setSilent( false );

				probeListManager.moveDownProbeList( l_selectedItems[i] );

				if ( l_selectedItems[i] < getModel().getSize() - 1 )
					++l_selectedItems[i]; // move selection
				else
					l_selectedItems[i] = -1;
			}

//			setListData( probeListManager.getObjects().toArray() );
			setSelectedIndices( l_selectedItems );
		}
	}


	public class RemoveSelectionAction
	extends AbstractAction
	{
		public RemoveSelectionAction( final String text, final Icon icon ) {
			super( text, icon );			
		}

		public RemoveSelectionAction( final String text ) {
			super( text );
		}

		public RemoveSelectionAction() {
			super( "Close" );
		}

		public void actionPerformed( final ActionEvent event ) {		
			final Object[] l_selectedValues = getSelectedValues();

			probeListManager.setSilent( true );

			for ( int i = 0; i < l_selectedValues.length; ++i ) {
				// finally notify listeners
				if ( i == l_selectedValues.length - 1 )
					probeListManager.setSilent( false );

				probeListManager.removeObject( (ProbeList)l_selectedValues[i] );

				// clean up the probes (remove the probe list entries)
				((ProbeList)l_selectedValues[i]).propagateClosing();
			}

//			setListData( probeListManager.getObjects().toArray() );
		}
	}


	public class SelectionListener implements ListSelectionListener
	{
		public void valueChanged( ListSelectionEvent event ) {
			updateMenu();
		}
	}

//	private DelayedUpdateTask updateTask = new DelayedUpdateTask("PLMV") {
//
//		private long lastHash = 0;
//		
//		protected boolean needsUpdating() {
//			long newHash = probeListManager.getObjects().hashCode();
//			return newHash!=lastHash;
//		}
//
//		protected void performUpdate() {
//			setListData( probeListManager.getObjects().toArray() );
//			lastHash = probeListManager.getObjects().hashCode();
//		}
//		
//	};
	
//	public synchronized void probeListManagerChanged( ProbeListManagerEvent event )	{
//		// whatever happens, update the whole list, except for reordering (which will be a result of our own actions anyway)
//		if (event.getChange()!=ProbeListManagerEvent.ORDER_CHANGE)
//			updateTask.trigger();
//	}


	public void updateCellRenderer() {
		if (ProbeListImage.useGraphics.getBooleanValue())
			setCellRenderer( new GraphicProbeListCellRenderer() );
		else
			setCellRenderer( new ProbeListCellRenderer());
		repaint();
	}

//	public void addNotify() {
//		updateTask.trigger();
//		super.addNotify();
//	}

	private class ProbeListTransferHandler extends TransferHandler {
		private int[] indices = null;

		public boolean canImport(TransferHandler.TransferSupport info) {
			// Check for String flavor
			if (indices==null) {
				return false;
			}
			// show where it would be dropped
			JList.DropLocation dl = (JList.DropLocation)info.getDropLocation();
			int index = dl.getIndex();
			int delta = index-indices[0];

			if (delta!=0) {
				setSelectedIndices(indices);

				System.out.print("Drop target: "+index+"  selected: ");
				for (int i : indices)
					System.out.print(i+" ");
				System.out.println("\nDelta: "+delta);

				for (int i=0; i!=indices.length; ++i) {
					indices[i]+=delta;
				}

				// move all dragged items by delta positions
				while (delta>0) {
					moveDownAction.actionPerformed(null);
					--delta;
				}
				while (delta<0) {
					moveUpAction.actionPerformed(null);
					++delta;
				}

			}

			return true;
		}

		protected Transferable createTransferable(JComponent c) {
			return new StringSelection(exportString(c));
		}

		public int getSourceActions(JComponent c) {
			return TransferHandler.COPY_OR_MOVE;
		}

		public boolean importData(TransferHandler.TransferSupport info) {
			if (!info.isDrop()) {
				return false;
			}
			return true;
		}

		protected void exportDone(JComponent c, Transferable data, int action) {
			indices=null;
		}

		//Bundle up the selected items in the list
		//as a single string, for export.
		protected String exportString(JComponent c) {
			JList list = (JList)c;
			indices = list.getSelectedIndices();
			Object[] values = list.getSelectedValues();

			StringBuffer buff = new StringBuffer();

			for (int i = 0; i < values.length; i++) {
				Object val = values[i];
				buff.append(val == null ? "" : val.toString());
				if (i != values.length - 1) {
					buff.append("\n");
				}
			}

			return buff.toString();
		}


	}

	public void objectAdded(StoreEvent event) {
		((ProbeList)event.getObject()).addProbeListListener(this);
	}

	public void objectRemoved(StoreEvent event) {
		((ProbeList)event.getObject()).removeProbeListListener(this);
	}

	public void setProbeListManager( ProbeListManager probeListManager ) {
		setModel(probeListManager.getModel());
		this.probeListManager = probeListManager;

		updateCellRenderer();

		// Enable drag and drop
		this.setDragEnabled(true);
		this.setTransferHandler(new ProbeListTransferHandler());

		// create new actions
		this.removeSelectionAction = new RemoveSelectionAction( "Close" );
		this.moveUpAction = new MoveUpAction( "Move Up" );
		this.moveDownAction = new MoveDownAction( "Move Down" );

		addMouseListener(new MouseAdapter() {
			public void mouseClicked( MouseEvent event )
			{
				// if graphical view is active and double click on the profile image is recieved, open profile plot if vis plugin present
				if ( event.getButton() == MouseEvent.BUTTON1 ) {
					if ( event.getClickCount() == 2 ) {
						if ( getSelectedValue() != null ) {
							boolean showingPlot = false;
							if (ProbeListImage.useGraphics.getBooleanValue()) {
								// map coordinates
								int x = event.getX();
								int y = event.getY();
								Rectangle r = getCellBounds(getSelectedIndex(),getSelectedIndex());
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
										lpl.add((ProbeList)getSelectedValue());
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
							}
						}
					}
				}
			}
			
			public void mousePressed( MouseEvent event ) {
				if ( event.getButton() == MouseEvent.BUTTON3 )
					getPopupMenu().show( getComponent(), event.getX(), event.getY() );
			}
		});
		
		updateMenu();
		addPropertyChangeListener( this );
		addListSelectionListener( new SelectionListener() );
		probeListManager.setProbeListManagerView(this);		
	}
	
	public Component getComponent() {
		return this;
	}

	@SuppressWarnings("unchecked")
	public void updateMenu() {
		JMenu g = getMenu();
		if (g!=null && (g instanceof PluginMenu)) {
			((PluginMenu)g).setSelectionChanged();
		}
	}

	public void selectIndex(int index) 
	{
		setSelectedIndex(index);		
	}
	
	
	
}
