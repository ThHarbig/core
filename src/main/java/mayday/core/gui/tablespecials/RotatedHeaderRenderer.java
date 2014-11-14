package mayday.core.gui.tablespecials;

import java.awt.Color;

import javax.swing.table.DefaultTableCellRenderer;

import mayday.core.gui.components.VerticalLabel;

@SuppressWarnings("serial")
public class RotatedHeaderRenderer extends DefaultTableCellRenderer {

	public RotatedHeaderRenderer() {
		this.setOpaque(true);
		this.setBackground(Color.WHITE);
		this.setUI(new VerticalLabel.VerticalLabelUI(false));
	}

}
