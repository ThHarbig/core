package mayday.core.gui.tablespecials.SpanningCells;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class MergeTableHeaderUI extends BasicTableHeaderUI {

	public void paint(Graphics g, JComponent c) {
		CellMap cm = null;
		if (header.getTable() instanceof MergeTable)
			cm = ((MergeTable)header.getTable()).cellMap;

		if (cm==null) {
			super.paint(g, c);
			return;
		}

		Rectangle clipBounds = g.getClipBounds();

		Dimension size = header.getSize();			

		Rectangle cellRect  = new Rectangle(0, 0, size.width, size.height);

		int columnMargin = header.getColumnModel().getColumnMargin();

		for(int i=0; i!=header.getColumnModel().getColumnCount(); ) {
			int colSpan = cm.colSpan(-1, i);
			cellRect.width = 0;
			for (int j=0; j!=colSpan; ++j)
				cellRect.width += header.getColumnModel().getColumn(i+j).getWidth();
			cellRect.width += (colSpan-1)*columnMargin;
			if (cellRect.intersects(clipBounds)) {
				paintCell(g, cellRect, i);
			}
			i+=colSpan;
			cellRect.x += cellRect.width;
		}

	}

	private void paintCell(Graphics g, Rectangle cellRect, int columnIndex) {
		TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
		TableCellRenderer renderer = aColumn.getHeaderRenderer();
		Component component = renderer.getTableCellRendererComponent(
				header.getTable(), aColumn.getHeaderValue(),false, false, -1, columnIndex);
		rendererPane.add(component);
		rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y,
				cellRect.width, cellRect.height, true);
	}

}
