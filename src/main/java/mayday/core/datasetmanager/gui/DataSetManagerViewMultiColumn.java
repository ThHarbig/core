package mayday.core.datasetmanager.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.DropMode;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.DataSet;
import mayday.core.EventFirer;
import mayday.core.StoreEvent;
import mayday.core.StoreListener;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.gui.PluginMenu;
import mayday.core.gui.dataset.DataSetOverview;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.meta.gui.MIGroupSelectionPanel;

/**
 * @author florian battke
 * 
 */
@SuppressWarnings("serial")
public class DataSetManagerViewMultiColumn extends JPanel
implements StoreListener,  DataSetManagerViewInterface
{
	private JMenu datasetMenu;
	private JMenu probelistMenu; // kept here because we have only one instance here but many ProbeListManagerViews

	private JSplitPane bigSplit, smallSplit; 	
	private JList dataSetList;
	private JPanel dataSetInfoPanel; 
	private JPanel dataSetViewPanel;

	private PLMVSelectedMouseListener plmvMouseListener = new PLMVSelectedMouseListener();

	private LinkedList<DataSet> currentSelection = new LinkedList<DataSet>();
	
	protected EventFirer<Object, ListSelectionListener> selectionFirer = new EventFirer<Object, ListSelectionListener>() {
		protected void dispatchEvent(Object event,
				ListSelectionListener listener) {
			listener.valueChanged(null);
		}
	};
	
	public DataSetManagerViewMultiColumn( ) {
		
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		dataSetList = new JList();
    	dataSetList.setModel(DataSetManager.singleInstance);    	
    	dataSetList.setCellRenderer(new DataSetCellRenderer());
		
		dataSetInfoPanel = new JPanel(new BorderLayout());
		dataSetViewPanel = new JPanel(new GridLayout(1,0));

		JScrollPane jsp = new JScrollPane(dataSetList);
		jsp.setBorder(BorderFactory.createTitledBorder("DataSets"));
		jsp.setMinimumSize(new Dimension(200,200));

		smallSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		smallSplit.setDividerLocation(.5);
		smallSplit.setOneTouchExpandable(true);    	
		smallSplit.setTopComponent(jsp);
		smallSplit.setBottomComponent(dataSetInfoPanel);
		smallSplit.setContinuousLayout(true);

//		rightPart = new JScrollPane(dataSetViewPanel);
		
		bigSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		bigSplit.setDividerLocation(.5);
		bigSplit.setOneTouchExpandable(true);
		bigSplit.setLeftComponent(smallSplit);
		bigSplit.setRightComponent(dataSetViewPanel);
		bigSplit.setContinuousLayout(true);
//		bigSplit.setRightComponent(rightPart);
		
		add(bigSplit, BorderLayout.CENTER);   

		dataSetList.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				LinkedList<DataSet> keepThese = new LinkedList<DataSet>();				
				for (int i = 0; i!=DataSetManager.singleInstance.getSize(); ++i) {
					if (dataSetList.getSelectionModel().isSelectedIndex(i))
						keepThese.add((DataSet)DataSetManager.singleInstance.get(i));
				}
				// remove items that are no longer selected
				currentSelection.retainAll(keepThese);
				// now add all new items in the front
				// -> ensure contract in DatasetManagerViewInterface.getSelectedDataSets();
				keepThese.removeAll(currentSelection);
				for (DataSet ds : keepThese)
					currentSelection.add(0, ds);
				// now update the view
				updateAfterSelectionChange();
			}

		});

		dataSetList.addMouseListener(new MouseAdapter() {
			public void mousePressed( MouseEvent event )
			{
				switch(event.getButton()) {
				case MouseEvent.BUTTON3:
					getPopupMenu().show( dataSetList, event.getX(), event.getY() );
					break;
				case MouseEvent.BUTTON1:
					if (event.getClickCount()==2)
						PropertiesDialogFactory.createDialog(getSelectedDataSets().toArray()).setVisible(true);
					break;
				}  
			}
		});
		
    	dataSetList.setDragEnabled(true);
    	dataSetList.setDropMode(DropMode.ON);
    	dataSetList.setTransferHandler(new DataSetTransferHandler());

		setActionsEnabled(false);

		updateAfterContentChange();

		DataSetManager.singleInstance.addStoreListener(this);
		
		setMinimumSize(new Dimension(500,500));
		setPreferredSize(getMinimumSize());
	}

	protected void setLeftBottomElement ( Component c, String title ) {
		JPanel bottomP = new JPanel(new BorderLayout());
		bottomP.setBorder(BorderFactory.createTitledBorder(title));
		bottomP.add(c, BorderLayout.CENTER);
		dataSetInfoPanel.add(bottomP, BorderLayout.CENTER);
	}

	protected void setLeftMiddleElement ( Component c, String title ) {
		JPanel middleP = new JPanel(new BorderLayout());
		middleP.setBorder(BorderFactory.createTitledBorder(title));
		middleP.add(c, BorderLayout.CENTER);
		dataSetInfoPanel.add(middleP, BorderLayout.NORTH);
	}

	protected void updateAfterContentChange() {		

		// remove old listeners
		for (Component cdsv : dataSetViewPanel.getComponents()) {
			if (cdsv instanceof DataSetView) {
				((DataSetView)cdsv).getProbeListManagerView().removeMouseListener(plmvMouseListener);
			}
		}
		// update datasets view
		dataSetViewPanel.removeAll();
		for (DataSet ds: DataSetManager.singleInstance.getDataSets()) {
			DataSetView c = ds.getDataSetView();
			c.getProbeListManagerView().addMouseListener(plmvMouseListener);
			c.setBorder(BorderFactory.createTitledBorder(ds.getName()));
			dataSetViewPanel.add(c);
		}			
		if (DataSetManager.singleInstance.getObjects().size()==0)
			dataSetViewPanel.add(new JPanel());
		
		invalidate();
		validate();
		
		updateAfterSelectionChange();
	}

	protected void updateAfterSelectionChange() {
		
		dataSetInfoPanel.removeAll();
		
		if (!currentSelection.isEmpty()) {	
			DataSet lastSelected = currentSelection.get(0);

			// mio panel 
			final MIGroupSelectionPanel selPanel = new MIGroupSelectionPanel(lastSelected.getMIManager());
			setLeftBottomElement(selPanel, "Meta Information");							
			// info panel
			setLeftMiddleElement(
					new JLabel("<html>"
							+lastSelected.getMasterTable().getNumberOfProbes()+" probes<br>"
							+lastSelected.getMasterTable().getNumberOfExperiments()+ " experiments"), 
			"DataSet Properties: "+lastSelected.getName());

			// update menus
        	if (lastSelected!=null && lastSelected.getProbeListManager().getProbeListManagerView()!=null)
        		lastSelected.getProbeListManager().getProbeListManagerView().updateMenu();
			setActionsEnabled(true);

		} else {						
			setLeftBottomElement(new JPanel(), "");
			setLeftMiddleElement(new JPanel(), "");
			setActionsEnabled(false);			
		}
		
		selectionFirer.fireEvent(this);
	}

	@SuppressWarnings("unchecked")
	public void setActionsEnabled(boolean enabled) {
		if (datasetMenu!=null)
			((PluginMenu)datasetMenu).setSelectionChanged();
//		if (datasetMenu!=null)
//			datasetMenu.setActivationState(enabled);
		if (probelistMenu!=null)
			probelistMenu.setEnabled(enabled);
	}    

	public void setDataSetMenu(JMenu DataSetMenu) {
		this.datasetMenu = DataSetMenu;
		setActionsEnabled(!currentSelection.isEmpty());
	}

	public JMenu getMenu() {
		return datasetMenu;
	}


	public JPopupMenu getPopupMenu() {    	
		return getMenu().getPopupMenu();
	}

	public JMenu getProbeListMenu() {
		if (probelistMenu==null) 
			return new JMenu("");
		else 
			return probelistMenu;
	}

	public void setProbeListMenu(JMenu plmenu) {
		probelistMenu = plmenu;
		setActionsEnabled(!currentSelection.isEmpty());
	}
	
	public void removeNotify() {	
		for (Component cdsv : dataSetViewPanel.getComponents()) {
			if (cdsv instanceof DataSetView) {
				((DataSetView)cdsv).getProbeListManagerView().removeMouseListener(plmvMouseListener);
			}
		}
		super.removeNotify();
	}
	
	public void objectAdded(StoreEvent event) {

		DataSet d = (DataSet)event.getObject();

		if(d.getDataSetView()==null) {
			new DataSetView(d);
			d.getDataSetView().setDataSetManagerView(this);
			//prepare dialog
			DataSetOverview overview=new DataSetOverview(d);
			overview.setVisible(true);  
			updateAfterContentChange();
		}
	}


	public void objectRemoved(StoreEvent event) {
		currentSelection.remove(event.getObject());    	
		updateAfterContentChange();
	}

	public void addDataSet(DataSet ds) {
		DataSetManager.singleInstance.addObjectAtBottom(ds);
	}

	public List<DataSet> getSelectedDataSets() {
		return Collections.unmodifiableList(currentSelection);
	}
	
	public void setSelectedDataSets(Collection<DataSet> ds) {
		DefaultListSelectionModel dlsm = (DefaultListSelectionModel)dataSetList.getSelectionModel();
		dlsm.setValueIsAdjusting(true);
		for (DataSet d : ds)
			dlsm.addSelectionInterval(DataSetManager.singleInstance.indexOf(d), DataSetManager.singleInstance.indexOf(d));
		dlsm.setValueIsAdjusting(false);
	}

	public void updateInfo(DataSet ds) {
		repaint(); // should be handled automatically by the jlist		
	}

	public void closeDataSet(DataSet ds) {
		DataSetManager.singleInstance.removeObject(ds );
		ds.propagateClosing();
	}

	public Component getGUIComponent() {
		return this;
	}
	
	protected class PLMVSelectedMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			Component c = (Component)e.getSource();
			for (DataSet d : DataSetManager.singleInstance.getDataSets()) {
				if (c==d.getProbeListManager().getProbeListManagerView().getComponent()) {
					dataSetList.setSelectedValue(d, true);
					return;
				}
			}
		}
	}

	public static class DSMVPlugin extends DataSetManagerViewPlugin {

		protected Class<? extends DataSetManagerViewInterface> getViewClass() {
			return DataSetManagerViewMultiColumn.class;
		}

		protected String getViewClassName() {
			return "Multi-Column List View";
		}
		
	}
	
	
	public void addSelectionListener(ListSelectionListener lsl) {
		selectionFirer.addListener(lsl);		
	}

	public void removeSelectionListener(ListSelectionListener lsl) {
		selectionFirer.removeListener(lsl);		
	}
	
	public void moveListenersTo(DataSetManagerViewInterface other) {
		for (ListSelectionListener lsl : selectionFirer.getListeners()) {
			other.addSelectionListener(lsl);
			removeSelectionListener(lsl);
		}
	}
	
	


}
