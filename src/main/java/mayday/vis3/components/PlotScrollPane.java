package mayday.vis3.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class PlotScrollPane extends JScrollPane {
	
	public PlotScrollPane() {
		setBorder(BorderFactory.createEmptyBorder());
		setBackground(Color.WHITE);
		setViewportBorder(BorderFactory.createEmptyBorder());
	}
	
	public PlotScrollPane(Component c) {
		this();
		setViewportView(c);
	}
	
	@Override
	protected void paintChildren(Graphics g) {
		/* SVG export fails if scrollbars are visible during export.
		 * Also, we don't want scrollbars to show up in the exported image.
		 * During export, the exported component is set to invisible (ExportDialog.java)
		 * so we can use that criterion to find out if we're exported just now.
		 * btw: isShowing() != isVisible()
		 */
		if (!isShowing()) { 
			boolean hvis = getHorizontalScrollBar().isVisible();
			boolean vvis = getVerticalScrollBar().isVisible();
			getHorizontalScrollBar().setVisible(false);
			getVerticalScrollBar().setVisible(false);
			super.paintChildren(g);
			getHorizontalScrollBar().setVisible(hvis);
			getVerticalScrollBar().setVisible(vvis);
		} else {
			super.paintChildren(g);
		}
	}

}
