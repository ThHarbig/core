package mayday.core.plugins.dataset;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.gui.MaydayFrame;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;

public class SetExperimentNames extends AbstractPlugin implements DatasetPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.DatasetSetExperimentNames",
				new String[0],
				Constants.MC_SUPPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Allows to change the names of experiments in a data set.",
				"Set Experiment Names"
				);
		pli.setMenuName("Experiment Names...");
		return pli;
	}

	public List<DataSet> run(List<DataSet> datasets) {
		
		DataSet ds = datasets.get(0);
		final MasterTable mt = ds.getMasterTable();
		
		Vector<String> currentNames = new Vector<String>();
		for (int i=0; i!=mt.getNumberOfExperiments(); ++i)
			currentNames.add(mt.getExperimentName(i));
		
	    final JFrame jf = new MaydayFrame();
	    jf.setTitle("Change Experiment Names for \""+ds.getName()+"\"");
	    jf.setLayout(new BorderLayout());
		
	    final ExperimentNameTableModel tm = new ExperimentNameTableModel(currentNames); 
		final JTable table = new JTable(tm);
	    JScrollPane scrollpane = new JScrollPane(table);
	 
	    Box buttonpane = Box.createHorizontalBox();
	    final JButton okButton = new JButton("Apply");
	    okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (table.getCellEditor()!=null)
					table.getCellEditor().stopCellEditing();
				for (int i=0; i!=mt.getNumberOfExperiments(); ++i)
					mt.setExperimentName(i, tm.getNames().get(i));
				jf.dispose();
			}
	    });
	    JButton cancelButton = new JButton("Cancel");
	    cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				jf.dispose();
			}
	    });
	    
	    buttonpane.add(cancelButton);
	    buttonpane.add(Box.createHorizontalGlue());
	    buttonpane.add(okButton);
	    
	    jf.add(scrollpane, BorderLayout.CENTER);
	    jf.add(buttonpane, BorderLayout.SOUTH);
	    
	    jf.pack();
	    jf.setVisible(true);
	    
		return new LinkedList<DataSet>();
    }
	

	
	@SuppressWarnings("serial")
	private class ExperimentNameTableModel extends AbstractTableModel {

		protected Vector<String> new_names = new Vector<String>();
		protected Vector<String> old_names;
		
		public ExperimentNameTableModel(Vector<String> currentNames) {
			old_names = currentNames;
			for (String name : old_names)
				new_names.add(name);
		}
		
		public int getColumnCount() {
			return 3;
		}

		public int getRowCount() {
			return old_names.size();			
		}
		
		public boolean isCellEditable(int arg0, int arg1) {
			return (arg1==2);
		}
		
		public Object getValueAt(int row, int col) {
			switch(col) {
			case 0:
				return "# "+row;
			case 1:
				return old_names.get(row);
			case 2:
				return new_names.get(row);
			}
			return null;
		}
		
		public void setValueAt(Object aValue, int row, int col) {
			if (col==2)
				new_names.set(row, aValue.toString());
		}

		
		public String getColumnName(int column) {
			switch(column) {
			case 0: return "Experiment Index";
			case 1: return "Current name";
			case 2: return "New name";			
			}
			return null;
		}
		
		public Vector<String> getNames() {
			return new_names;
		}
		
	}


}
