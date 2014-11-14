package mayday.core.gui.tablespecials.SpanningCells;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableCellRenderer;

public class MergeTableUI extends BasicTableUI {

	public void paint(Graphics g, JComponent c) {
		Rectangle r=g.getClipBounds();
		Point topLeft1 = new Point(r.x,r.y);
		Point topLeft2 = new Point(r.x+r.width,r.y);

		Point botRight1 = new Point(r.x,r.y+r.height);
		Point botRight2 = new Point(r.x+r.width,r.y+r.height);
		
		int row_1a = table.rowAtPoint(topLeft1);
		int row_1b = table.rowAtPoint(topLeft2);
		int firstRow = Math.max(0, Math.min(row_1a, row_1b));
		
		int row_2a = table.rowAtPoint(botRight1);
		int row_2b = table.rowAtPoint(botRight2);
		int lastRow = Math.min(row_2a, row_2b);
		if (lastRow<0) 
			lastRow = table.getRowCount()-1;
		else 
			lastRow = Math.max(row_2a, row_2b);
		
		int col_1a = table.columnAtPoint(topLeft1);
		int col_1b = table.columnAtPoint(topLeft2);
		int firstCol = Math.max(0, Math.min(col_1a, col_1b));
		
		int col_2a = table.columnAtPoint(botRight1);
		int col_2b = table.columnAtPoint(botRight2);
		int lastCol = Math.min(col_2a, col_2b);
		if (lastCol<0) 
			lastCol = table.getColumnCount()-1;
		else 
			lastCol = Math.max(col_2a, col_2b);
		
		for (int i=firstRow; i<=lastRow; i++)
			paintRow(i,firstCol, lastCol, g);
	}
	
	private void paintRow(int row, int firstCol, int lastCol, Graphics g)	{
//		Rectangle r=g.getClipBounds();
		
		for (int i=firstCol; i<=lastCol;i++)
		{
			Rectangle r1=table.getCellRect(row,i,true);
		
			int[] sk=((MergeTable)table).cellMap.visibleCell(row,i);
			paintCell(sk[0],sk[1],g,r1);
			// increment the column counter
			i+=((MergeTable)table).cellMap.colSpan(sk[0],sk[1])-1;
		}
	}
	
	private void paintCell(int row, int column, Graphics g, Rectangle area)	{
		
		int verticalMargin = table.getRowMargin();
		int horizontalMargin  = table.getColumnModel().getColumnMargin();

		area.setBounds(area.x + horizontalMargin/2, 
				area.y + verticalMargin/2, 
				area.width - horizontalMargin, 
				area.height - verticalMargin);

		if (table.isEditing() && table.getEditingRow()==row &&
				table.getEditingColumn()==column) 
		{
			Component component = table.getEditorComponent();
			component.setBounds(area);
			component.validate();
		}
		else 
		{
			TableCellRenderer renderer = table.getCellRenderer(row, column);
			Component component = table.prepareRenderer(renderer, row, column);
			if (component.getParent() == null) 
				rendererPane.add(component);
			rendererPane.paintComponent(g, component, table, area.x, area.y,
					area.width, area.height, true);
		}
	}
}