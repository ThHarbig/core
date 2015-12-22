package mayday.core.gui.listdialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

public final class TopOnlyBorder implements Border {
	
	public Insets getBorderInsets(Component c) {
		return new Insets(5,0,0,0);
	}

	public boolean isBorderOpaque() {
		return false;
	}

	public void paintBorder(Component c, Graphics g, int x, int y,
			int width, int height) {
		g.setColor(Color.gray);
		g.drawLine(x, y+2, x+width, y+2);
	}
}