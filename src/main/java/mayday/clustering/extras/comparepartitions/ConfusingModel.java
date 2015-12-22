package mayday.clustering.extras.comparepartitions;

import java.util.Collection;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class ConfusingModel extends AbstractTableModel {
	
	protected ConfusingMatrix cm;
	
	protected String[] colNames;
	protected String[] rowNames;
	
	public ConfusingModel(ConfusingMatrix cm) {
		this.cm = cm;
		Collection<String> cn = cm.p2.getPartitionNames();
		colNames = cn.toArray(new String[0]);
		cn = cm.p1.getPartitionNames();
		rowNames = cn.toArray(new String[0]);
	} 

	public int getColumnCount() {
		return colNames.length+1;
	}

	public String getColumnName(int columnIndex) {
		if (columnIndex==0) return "";
		columnIndex--;
		return colNames[columnIndex];
	}

	public int getRowCount() {
		return rowNames.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex==0)
			return rowNames[rowIndex];		
		return cm.data[rowIndex][columnIndex-1];
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
	}

}
