package mayday.core.gui.tablespecials.EditableHeaders;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public  class TableHeaderPanelEditor extends DefaultCellEditor {

		TableHeaderPanelRenderer renderer;
		
		public TableHeaderPanelEditor(TableHeaderPanel pnl) {
			super(new JTextField());
			renderer=new TableHeaderPanelRenderer(pnl);
		}
		
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {			
			return renderer.getTableCellRendererComponent(table, value, isSelected, true,  row, column);
		}

		public Object getCellEditorValue() {
			return renderer.getTableHeaderPanel().getValue();
		}
		
		public boolean isCellEditable(EventObject anEvent) {
			return true;
		}
		
	}