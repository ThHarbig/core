package mayday.vis3.tables;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import mayday.core.Probe;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.plugins.probe.ProbeMenu;
import mayday.core.settings.Setting;
import mayday.vis3.ColorProvider;
import mayday.vis3.SortedProbeList;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.model.Visualizer;

@SuppressWarnings("serial")
public abstract class AbstractTabularComponent extends JTable implements ListSelectionListener, ViewModelListener{

	protected Visualizer visualizer;

	protected boolean isSilent; // indicates whether listeners are notified or not

	protected SortedProbeList probes;
	protected ColorProvider coloring;

	protected final static int PROBECOL=1;

	protected boolean displayNames = true;
	
	public AbstractTabularComponent( Visualizer _visualizer )  {
		this.visualizer = _visualizer;
		
		this.isSilent = false;
		
		probes = new SortedProbeList( visualizer.getViewModel(), visualizer.getViewModel().getProbes());

		probes.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				init_with_columnorder();
				setSelectedProbes(visualizer.getViewModel().getSelectedProbes());
			}
		});
		
		coloring = new ColorProvider(visualizer.getViewModel());
		coloring.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				repaint();
			}
		});
				
		setDefaultEditor( Object.class, new TabularProbeListViewerCellEditor() );
//		setColumnModel( new TabularProbeListViewerTableColumnModel() );
		
		visualizer.getViewModel().addViewModelListener(this);
	
		init();

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton()==MouseEvent.BUTTON3) {
					ProbeMenu pm = new ProbeMenu(getViewModel().getSelectedProbes(), getViewModel().getDataSet().getMasterTable());
					pm.getPopupMenu().show(AbstractTabularComponent.this, e.getX(), e.getY());
				}
			}
		});
		
		setSelectedProbes(visualizer.getViewModel().getSelectedProbes());
		
	}


	// re-initialize the model, but try to keep the column order fixed 
	// this is not optimal as it runs in O(columns^2)
	protected void init_with_columnorder() {
		
		TableColumnModel cm = getColumnModel();
		
		HashMap<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
		for (int i=0; i!=cm.getColumnCount(); ++i) {
			TableColumn tc = cm.getColumn(i);
			indexMap.put(i,tc.getModelIndex());
		}
		
		init();
		// now re-order the columns to fit the index map
		
		// find out which column is where, then move them into position by only
		// moving them to the left to avoid shifting of already placed columns
		
		for (int tgtPosition=0; tgtPosition!=cm.getColumnCount(); ++tgtPosition) {
			Integer tgtModelIndex = indexMap.get(tgtPosition);
			if (tgtModelIndex==null)
				continue;
			for (int srcPosition=tgtPosition+1; srcPosition!=cm.getColumnCount(); ++srcPosition) {
				if (cm.getColumn(srcPosition).getModelIndex()==tgtModelIndex) {
					cm.moveColumn(srcPosition, tgtPosition);
					break;
				}
			}
		}
	}


	protected abstract void addColumns(DefaultTableModel tm);
	protected abstract void fillRow(Object[] row, Probe pb, int k);
	protected abstract String getToolTip(int column);

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
		
		for ( Probe pb : probes ) {		
			l_row[0] = ++row;
			l_row[PROBECOL] = pb; // displays name automatically
			fillRow(l_row, pb, PROBECOL+1);
			l_tableData.addRow( l_row );      
		}

		isSilent=true;
		setModel( l_tableData );
		isSilent=false;

		TableColumn l_identifierColumn = getColumnModel().getColumn( PROBECOL );
		l_identifierColumn.setCellRenderer( new IdentifierTableCellRenderer() );

		try { // try to modify table headers to show tooltops
			Method m = JTableHeader.class.getDeclaredMethod("createDefaultRenderer");
			m.setAccessible(true);
			JTableHeader jth = getTableHeader();
			
			for (int i=PROBECOL+1; i!=l_tableData.getColumnCount(); ++i) {
				TableColumn tc = getColumnModel().getColumn(i);
				JLabel jl = (JLabel)m.invoke(jth);
				jl.setToolTipText( getToolTip(i-(PROBECOL+1))) ;
				tc.setHeaderRenderer((TableCellRenderer)jl);			
			}	
		} catch (Exception dieKraetze) {
			// too bad, no tooltips for you sir.
		}
		
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount()==2 && e.getButton()==MouseEvent.BUTTON1) {
					int r = rowAtPoint(e.getPoint());
					Probe p = (Probe)getModel().getValueAt(r,PROBECOL);
					if (p!=null)
						PropertiesDialogFactory.createDialog(p).setVisible(true);
				}
			}
		});
	}


	
	public boolean goToProbe( String probeIdentifier )
	{  
		DefaultTableModel model = (DefaultTableModel)getModel();
		int row;
		for (row = 0; row!=model.getRowCount(); ++row) {
			Object opb = model.getValueAt(row, PROBECOL);
			if (opb instanceof Probe && (
				((Probe)opb).getName().equals(probeIdentifier)) || ((Probe)opb).getDisplayName().equals(probeIdentifier)
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
			LinkedList<Probe> newSelection = new LinkedList<Probe>();
			for ( int i : l_selectedRows ) 
				newSelection.add((Probe)getModel().getValueAt( i, PROBECOL ) );
			visualizer.getViewModel().removeViewModelListener(this);
			visualizer.getViewModel().setProbeSelection(newSelection);
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
		return new Setting[]{coloring.getSetting(), probes.getSetting()};
	}

	
	public mayday.vis3.model.ViewModel getViewModel() {
		return visualizer.getViewModel();
	}
	

	




	protected class IdentifierTableCellRenderer extends DefaultTableCellRenderer
	{
		public void setValue( Object value ) { 
			if ( value instanceof Probe ) {
				Probe l_probe = (Probe)value; 
				setForeground( coloring.getColor(l_probe) );
				if (displayNames)
					setText( l_probe.getDisplayName() );
				else 
					setText( l_probe.getName() );
			} else { 
				super.setValue(value); 
			} 
		}
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


	public void setSelectedProbes(Set<Probe> selection) {
		setSilent(true);
		getSelectionModel().clearSelection();
		for (int row=0; row!=getModel().getRowCount(); ++row) 
			if (selection.contains(getModel().getValueAt(row,PROBECOL)))
				getSelectionModel().addSelectionInterval(row,row);
		setSilent(false);
	}


	public void viewModelChanged(ViewModelEvent vme) {
		
		switch(vme.getChange()) {
		case ViewModelEvent.PROBE_SELECTION_CHANGED:
			setSelectedProbes(visualizer.getViewModel().getSelectedProbes());
			break;
		case ViewModelEvent.PROBELIST_ORDERING_CHANGED:
			repaint(); // change probe colors
			break;
		case ViewModelEvent.PROBELIST_SELECTION_CHANGED:
			probes.clear();
			probes.addAll(visualizer.getViewModel().getProbes());
			init_with_columnorder();
			break;
		case ViewModelEvent.DATA_MANIPULATION_CHANGED:
		case ViewModelEvent.TOTAL_PROBES_CHANGED:
				probes.clear();
				probes.addAll(visualizer.getViewModel().getProbes());
				init_with_columnorder();
			break;
		}
	}


	@Override
	public void removeNotify() {
		super.removeNotify();
		if (coloring!=null)
			coloring.removeNotify();
		visualizer.getViewModel().removeViewModelListener(this);
	}
	
	

	
}
