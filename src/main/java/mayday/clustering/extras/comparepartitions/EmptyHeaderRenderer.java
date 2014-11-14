package mayday.clustering.extras.comparepartitions;

import java.awt.Color;

import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class EmptyHeaderRenderer extends DefaultTableCellRenderer {

	public EmptyHeaderRenderer() {
		this.setOpaque(true);
		this.setBackground(Color.WHITE);
		this.setForeground(Color.WHITE);
	}
	

}
