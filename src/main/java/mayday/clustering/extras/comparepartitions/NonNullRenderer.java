package mayday.clustering.extras.comparepartitions;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class NonNullRenderer extends DefaultTableCellRenderer {

	public NonNullRenderer() {
		this.setOpaque(true);
		this.setBackground(Color.WHITE);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		try {
			Integer i = (Integer)value;
			if (!isSelected) {
				if (i>0) 
					setBackground(Color.green);
				else 
					setBackground(Color.WHITE);
			}
			if (i>0) 
				setText(""+i);
			else 
				setText("");
		} catch (Exception i) {

		}
		return c;
	}


}
