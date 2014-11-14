package mayday.vis3.tables;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManagerEvent;
import mayday.core.meta.MIManagerListener;
import mayday.vis3.model.Visualizer;

@SuppressWarnings("serial")
public class MIOTableComponent extends AbstractTabularComponent implements MIManagerListener
{
	protected ArrayList<MIGroup> columns;	

	public MIOTableComponent( Visualizer visualizer )  {
		super(visualizer);
		visualizer.getViewModel().getDataSet().getMIManager().addMIManagerListener(this);
	}

	protected void findColumns() {
		columns = new ArrayList<MIGroup>();
		for (MIGroup mg : getViewModel().getDataSet().getMIManager().getGroups()) {
			for (Probe pb : probes) {
				if (mg.contains(pb)) {
					columns.add(mg);
					break;
				}
			}
		}		
	}
	
	protected void fillRow(Object[] row, Probe pb, int k) {
		for (MIGroup mg : columns) 
			row[k++] = mg.getMIO(pb);
	}
	
	protected void addColumns(DefaultTableModel tm) {
		findColumns();
		tm.addColumn( "Identifier" );		
		for ( MIGroup mg : columns ) 
			tm.addColumn( mg.getName() );	
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		visualizer.getViewModel().getDataSet().getMIManager().removeMIManagerListener(this);
	}
	
	public void miManagerChanged(MIManagerEvent event) {
		if (event.getChange()==MIManagerEvent.GROUP_ADDED || event.getChange()==MIManagerEvent.GROUP_DELETED)
			init();
	}

	@Override
	protected String getToolTip(int column) {
		MIGroup mg = columns.get(column);
		return mg.getPath()+"/"+mg.getName();
	}
}