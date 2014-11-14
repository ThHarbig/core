package mayday.vis3.tables;

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
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import mayday.core.Experiment;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.IntSetting;
import mayday.core.structures.linalg.impl.MasterTableColumn;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.vis3.SortedExperiments;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.model.Visualizer;

@SuppressWarnings("serial")
public class PercentileTableComponent extends JTable implements ListSelectionListener, ViewModelListener{

	protected Visualizer visualizer;
	protected IntSetting percentileStep;

	protected boolean isSilent; // indicates whether listeners are notified or not
	protected final static int EXPERIMENTCOL=1;
	
	protected boolean displayNames = true;

	protected SortedExperiments experiments;

	public PercentileTableComponent( Visualizer _visualizer )  {
		this.visualizer = _visualizer;
		
		this.isSilent = false;
		
		setDefaultEditor( Object.class, new TabularProbeListViewerCellEditor() );
		
		visualizer.getViewModel().addViewModelListener(this);
		
		percentileStep = new IntSetting("Percentile resolution",null,25);
		percentileStep.addChangeListener(new SettingChangeListener() {

			public void stateChanged(SettingChangeEvent e) {
				init();
				setSelectedExperiments(visualizer.getViewModel().getSelectedExperiments());
			}
			
		});
		
		experiments = new SortedExperiments( visualizer.getViewModel() );

		experiments.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				init();
				setSelectedExperiments(visualizer.getViewModel().getSelectedExperiments());
			}
		});
	
		init();

	}


	public void removeNotify() {
		super.removeNotify();
		visualizer.getViewModel().removeViewModelListener(this);
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
		l_tableData.addColumn( "Experiment" );

		int step = percentileStep.getIntValue();
		double count = Math.round(100/step);
		
		for (int p=0; p<=100; p+=step) 
			l_tableData.addColumn( p+"%" );
		
		
		Object[] row = new Object[l_tableData.getColumnCount()];
		
		MasterTable mata = visualizer.getViewModel().getDataSet().getMasterTable();
		
		String[] rn = null;
		
		
		ArrayList<String> myProbes = new ArrayList<String>();
		for (Probe pb : visualizer.getViewModel().getProbes())
			myProbes.add(pb.getName());
		
		for ( Experiment e : experiments ) {
			int i = e.getIndex();
			
			AbstractVector v = new MasterTableColumn(mata, i, rn);
			rn = ((MasterTableColumn)v).getRowNames();
			v = v.subset(myProbes);
			v.sort();
			
			row[0] = i+1;
			row[1] = e;

			for ( int pos=2; pos !=row.length; pos++ ) 
				row[pos] = v.quantile(count, pos-2, true);
			l_tableData.addRow( row );      
		}

		isSilent=true;
		setModel( l_tableData );
		isSilent=false;
		
		setAutoCreateRowSorter(true);
		
		TableColumn l_identifierColumn = getColumnModel().getColumn( EXPERIMENTCOL );
		l_identifierColumn.setCellRenderer( new IdentifierTableCellRenderer() );

	}

	public Setting[] getSettings() {
		return new Setting[]{experiments.getSetting(),percentileStep};
	}

	
	public mayday.vis3.model.ViewModel getViewModel() {
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

	/**
	 * This class provides a dummy cell editor that rejects all edit
	 * actions. 
	 * 
	 * @author Nils Gehlenborg
	 * @version 0.1
	 */
	protected class TabularProbeListViewerCellEditor extends DefaultCellEditor {
		TabularProbeListViewerCellEditor() {
			super( new JTextField() );
		}
		public boolean isCellEditable( EventObject eventObject ) {
			return ( false );
		}
	}
	
	public boolean isSilent() {
		return isSilent;
	}

	public void setSilent( boolean isSilent )	{
		this.isSilent = isSilent;
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
		case ViewModelEvent.TOTAL_PROBES_CHANGED:
			init();
			break;
		}
	}


	
}
