package mayday.vis3.tables;
import javax.swing.table.DefaultTableModel;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.vis3.model.Visualizer;

@SuppressWarnings("serial")
public class ExpressionTableComponent extends AbstractTabularComponent implements ProbeListListener
{
	public ExpressionTableComponent( Visualizer visualizer )  {
		super(visualizer);
//		setColumnModel( new TabularProbeListViewerTableColumnModel() );
		// listen to all pl's for coloring
		visualizer.getViewModel().addRefreshingListenerToAllProbeLists(this, false);
	}

	protected void addColumns(DefaultTableModel tm) {
		tm.addColumn( "Display Name" );
		MasterTable masterTable = visualizer.getViewModel().getDataSet().getMasterTable();
		for ( int i = 0; i < masterTable.getNumberOfExperiments(); ++i ) 
			tm.addColumn( masterTable.getExperimentDisplayName( i ) );		
	}
	
	protected void fillRow(Object[] row, Probe pb, int k) {
		double[] vals = getViewModel().getProbeValues(pb);
		for ( int i=0; i!=vals.length;++i ) 
			row[k++] = vals[i];       

	}

//	protected class TabularProbeListViewerTableColumnModel extends DefaultTableColumnModel
//	{     
//		/**
//		 * Overrides the original function from DefaultTableColumnModel.
//		 * This is to prevent that columns are moved in a TabularProbeListViewer.
//		 * 
//		 * @param source The source index (where the column was located before the move).
//		 * @param target The target index (where the column is located now).
//		 */
//		public void moveColumn( int source, int target ) {
//			// do nothing (at least for time being)       
//			// NOTE: in future releases this may implement the re-ordering of experiments in the master table  
//		}
//	}
	

	public void probeListChanged(ProbeListEvent event) {
		if (event.getChange()==ProbeListEvent.LAYOUT_CHANGE)
			repaint();
		/* Handle content change differently, by listening to the viewmodel */
	}
	
	@Override
	public void removeNotify() {
		super.removeNotify();
		visualizer.getViewModel().removeRefreshingListenerToAllProbeLists(this);	
	}

	@Override
	protected String getToolTip(int column) {
		return visualizer.getViewModel().getDataSet().getMasterTable().getExperimentName(column);
	}
}