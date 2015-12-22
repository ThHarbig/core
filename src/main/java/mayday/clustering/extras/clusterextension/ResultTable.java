package mayday.clustering.extras.clusterextension;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.structures.maps.MultiHashMap;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.Visualizer;
import mayday.vis3.plots.profile.ProfilePlot;

@SuppressWarnings("serial")
public class ResultTable extends JTable implements ListSelectionListener {

	DefaultTableModel l_tableData;
	MultiHashMap<ProbeList, Probe> mapProbeListProbe;
	protected boolean isSilent = false; // indicates whether listeners are notified or
	
	public ResultTable(final Visualizer visualizer, HashMap<Probe, ProbeList> mapProbeProbeList){

		
		addMouseListener(new MouseAdapter() {
			
			public void mouseClicked( MouseEvent event )
			{
				if(event.getClickCount() == 2){
					ProfilePlot plot = new ProfilePlot();
					DataSet ds = visualizer.getViewModel().getDataSet();
					Component pc = plot.getComponent();
					List<ProbeList> lpl = new ArrayList<ProbeList>();
					//System.out.println((String)getModel().getValueAt(getSelectedRow(), getSelectedColumn()));
					//System.out.println("welche PL? " + indexToProbeList.get(getSelectedRow()));
					
					ProbeList pl = (ProbeList)l_tableData.getValueAt(getSelectedRow(), 0); 
					lpl.add(pl);
					
					Visualizer v = Visualizer.createWithPlot(ds, lpl, pc);
					ViewModel vm = v.getViewModel();
					vm.setProbeSelection(mapProbeListProbe.get(pl));
				}
			}
		});
		
		l_tableData = new DefaultTableModel();
		l_tableData.addColumn("Cluster");
		l_tableData.addColumn(" # Added Probe");
		
		// Mapping from HashMap<Probe, ProbeList> to Hashmap<ProbeList, List<Probe>>
		mapProbeListProbe = new MultiHashMap<ProbeList, Probe>();

		for(Probe pb: mapProbeProbeList.keySet()){			
			ProbeList pl = mapProbeProbeList.get(pb);
			mapProbeListProbe.put(pl, pb);
		}
		
		// presenting result
		Object[] row = new Object[2];
		for(ProbeList pl : mapProbeListProbe.keySet()){	
			row[0] = pl;
			row[1] = mapProbeListProbe.get(pl).size();
			l_tableData.addRow(row);	
		}
		
		setDefaultEditor( Object.class, new TabularProbeListViewerCellEditor() );
		isSilent = true;
		setModel(l_tableData);
		isSilent = false;
		setVisible(true);
	}

	public boolean isSilent() {
		return isSilent;
	}

	public void setSilent(boolean isSilent) {
		this.isSilent = isSilent;
	}
	
	protected class TabularProbeListViewerCellEditor extends DefaultCellEditor {
		TabularProbeListViewerCellEditor() {
			super( new JTextField() );
		}
		public boolean isCellEditable( EventObject eventObject ) {
			return ( false );
		}
	}
	
}
