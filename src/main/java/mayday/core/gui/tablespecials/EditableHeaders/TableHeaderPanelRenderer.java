package mayday.core.gui.tablespecials.EditableHeaders;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class TableHeaderPanelRenderer implements TableCellRenderer {

	TableHeaderPanel thePanel;

	public TableHeaderPanelRenderer(TableHeaderPanel t) {
		thePanel = t;
	}

	public Component getTableCellRendererComponent(
			JTable table, Object value,
			boolean isSelected, boolean hasFocus, 
			int row, int column) {
		thePanel.setColumnIndex(column);
		thePanel.setTitle(table.getModel().getColumnName(column));
		thePanel.setValue(value);
		return thePanel;
	}

	public TableHeaderPanel getTableHeaderPanel() {
		return thePanel;
	}

}