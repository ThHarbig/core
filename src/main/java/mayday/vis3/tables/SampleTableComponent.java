package mayday.vis3.tables;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import mayday.core.Experiment;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManagerEvent;
import mayday.core.meta.MIManagerListener;
import mayday.core.settings.Setting;
import mayday.vis3.SortedExperiments;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.model.Visualizer;

@SuppressWarnings("serial")
public class SampleTableComponent extends JTable implements MIManagerListener, ViewModelListener
{
	protected ArrayList<MIGroup> columns;	
	protected Visualizer visualizer;

	protected boolean isSilent; // indicates whether listeners are notified or not

	protected SortedExperiments experiments;

	protected final static int EXPERIMENTCOL=1;

	protected boolean displayNames = true;
	
	public SampleTableComponent( Visualizer _visualizer )  {
		
		this.visualizer = _visualizer;
		
		this.isSilent = false;
		
		experiments = new SortedExperiments( visualizer.getViewModel() );

		experiments.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				init();
				setSelectedExperiments(visualizer.getViewModel().getSelectedExperiments());
			}
		});
		
		visualizer.getViewModel().getDataSet().getMIManager().addMIManagerListener(this);
		displayNames = false;
				
		setDefaultEditor( Object.class, new TabularExperimentViewerCellEditor() );
//		setColumnModel( new TabularProbeListViewerTableColumnModel() );
		
		visualizer.getViewModel().addViewModelListener(this);
	
		init();
		
		setSelectedExperiments(visualizer.getViewModel().getSelectedExperiments());
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount()==2 && e.getButton()==MouseEvent.BUTTON1) {
					int r = rowAtPoint(e.getPoint());
					Experiment ex = (Experiment)getModel().getValueAt(r,EXPERIMENTCOL);
					if (ex!=null)
						PropertiesDialogFactory.createDialog(ex).setVisible(true);
				}
			}
		});
	}


	/**
	 * Initializes the view.
	 */
	protected void init()
	{  	
		// columns are set to their preferred width
		setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		
		DefaultTableModel l_tableData = new DefaultTableModel(); // first index: row, second index: column
		
		// add columns to the model
		l_tableData.addColumn( "Row #" );		
		addColumns(l_tableData);
		
		int row=0;
		Object[] l_row = new Object[l_tableData.getColumnCount()];
		
		for ( Experiment e : experiments ) {		
			l_row[0] = ++row;
			l_row[EXPERIMENTCOL] = e; // displays name automatically
			fillRow(l_row, e, EXPERIMENTCOL+1);
			l_tableData.addRow( l_row );      
		}

		isSilent=true;
		setModel( l_tableData );
		isSilent=false;

		TableColumn l_identifierColumn = getColumnModel().getColumn( EXPERIMENTCOL );
		l_identifierColumn.setCellRenderer( new IdentifierTableCellRenderer() );

	}


	
	public boolean goToExperiment( String experimentIdentifier )
	{  
		DefaultTableModel model = (DefaultTableModel)getModel();
		int row;
		for (row = 0; row!=model.getRowCount(); ++row) {
			Object oex = model.getValueAt(row, EXPERIMENTCOL);
			if (oex instanceof Experiment && (
				((Experiment)oex).getName().equals(experimentIdentifier)) || ((Experiment)oex).getDisplayName().equals(experimentIdentifier)
				)
				break;
		}
		
		if (row<model.getRowCount()) {			
//			if (row+5<model.getRowCount())
//				row+=5;
			scrollRectToVisible(this.getCellRect(row, 0, true));
			return true;
		}
		
		return false;
	}


	@Override
	public void valueChanged( ListSelectionEvent event )
	{            
		if ( !isSilent() ) {
			int[] l_selectedRows = getSelectedRows();
			LinkedList<Experiment> newSelection = new LinkedList<Experiment>();
			for ( int i : l_selectedRows ) 
				newSelection.add((Experiment)getModel().getValueAt( i, EXPERIMENTCOL ) );
			visualizer.getViewModel().removeViewModelListener(this);
			visualizer.getViewModel().setExperimentSelection(newSelection);
			visualizer.getViewModel().addViewModelListener(this);
		}
		// perform default actions associated with the table
		super.valueChanged( event );        
	}



	public boolean isSilent() {
		return isSilent;
	}


	public void setSilent( boolean isSilent )	{
		this.isSilent = isSilent;
	}

	public Setting[] getSettings() {
		return new Setting[]{experiments.getSetting()};
	}

	
	public ViewModel getViewModel() {
		return visualizer.getViewModel();
	}
	
	protected class IdentifierTableCellRenderer extends DefaultTableCellRenderer
	{
		public void setValue( Object value ) { 
			if ( value instanceof Experiment ) {
				Experiment experiment = (Experiment)value; 
				if (displayNames)
					setText( experiment.getDisplayName() );
				else 
					setText( experiment.getName() );
			} else { 
				super.setValue(value); 
			} 
		}
	}


	protected class TabularExperimentViewerCellEditor extends DefaultCellEditor {
		TabularExperimentViewerCellEditor() {
			super( new JTextField() );
		}
		public boolean isCellEditable( EventObject eventObject ) {
			return ( false );
		}
	}


	public void setSelectedExperiments(Set<Experiment> selection) {
		setSilent(true);
		getSelectionModel().clearSelection();
		for (int row=0; row!=getModel().getRowCount(); ++row) 
			if (selection.contains(getModel().getValueAt(row,EXPERIMENTCOL)))
				getSelectionModel().addSelectionInterval(row,row);
		setSilent(false);
	}


	public void viewModelChanged(ViewModelEvent vme) {		
		switch(vme.getChange()) {
		case ViewModelEvent.EXPERIMENT_SELECTION_CHANGED:
			setSelectedExperiments(visualizer.getViewModel().getSelectedExperiments());
			break;
		}		
	}


	@Override
	public void removeNotify() {
		super.removeNotify();
		visualizer.getViewModel().removeViewModelListener(this);
		visualizer.getViewModel().getDataSet().getMIManager().removeMIManagerListener(this);
	}
	
	protected void findColumns() {
		columns = new ArrayList<MIGroup>();
		for (MIGroup mg : getViewModel().getDataSet().getMIManager().getGroups()) {
			for (Experiment e : experiments) {
				if (mg.contains(e)) {
					columns.add(mg);
					break;
				}
			}
		}		
	}
	
	protected void fillRow(Object[] row, Experiment e, int k) {
		for (MIGroup mg : columns) 
			row[k++] = mg.getMIO(e);
	}
	
	protected void addColumns(DefaultTableModel tm) {
		findColumns();
		tm.addColumn( "Identifier" );		
		for ( MIGroup mg : columns ) 
			tm.addColumn( mg.getName() );	
	}
	
	public void miManagerChanged(MIManagerEvent event) {
		if (event.getChange()==MIManagerEvent.GROUP_ADDED || event.getChange()==MIManagerEvent.GROUP_DELETED)
			init();
	}
}