package mayday.core.gui.tablespecials.SpanningCells;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * A JTable implementation with cells spanning multiple rows/columns
 * To define the header cells' column spanning, use row=-1
 * Example :
 * 		CellSpanningInfo cellSpanning = new CellSpanningInfo();
  		cellSpanning.setSpan(0, 2, 4, 9); // content cell spanning		
		cellSpanning.setSpan(-1, 2, 1, 9); // column header cell spanning (row=-1)		
		cellSpanning.setSpan(0, 0, 4, 1); // row header cell spanning (col=0)
		MergeTable mt = new MergeTable(cellSpanning);

 * @author battke
 *
 */

@SuppressWarnings("serial")
public class MergeTable extends JTable {

	protected CellMap cellMap;

	public MergeTable(CellMap cmap) {
		this();
		cellMap = cmap;
	}
	
	public MergeTable(CellMap cmap, TableModel tbl) {
		this(cmap);
		setModel(tbl);
	}
	
	public MergeTable() {
		super();
		setUI(new MergeTableUI());
		getTableHeader().setUI(new MergeTableHeaderUI());
	}
	
	public void setCellMap(CellMap cm) {
		cellMap = cm;
	}
	
	public Rectangle getCellRect(int row, int column, boolean includeSpacing){
		// required because getCellRect is used in JTable constructor
		if (cellMap==null) 
			return super.getCellRect(row,column, includeSpacing);

		// get the correct cell spanning this position
		int[] sk=cellMap.visibleCell(row,column);	    
		Rectangle r1=super.getCellRect(sk[0],sk[1],includeSpacing);

		// add widths of all spanned logical cells
		if (cellMap.colSpan(sk[0],sk[1])!=1) {	    	
			for (int i=1; i<cellMap.colSpan(sk[0],sk[1]); i++){
				r1.width+=getColumnModel().getColumn(sk[1]+i).getWidth();
			}	    	
		}

		// add heights of all spanned logical cells
		if (cellMap.rowSpan(sk[0],sk[1])!=1) {	    	
			for (int i=1; i<cellMap.rowSpan(sk[0],sk[1]); i++){
				r1.height+=getRowHeight(sk[0]+i);
			}	    	
		}

		return r1;
	}

	public int columnAtPoint(Point p) {
		int x=super.columnAtPoint(p);
		// -1 is returned by columnAtPoint if the point is not in the table
		if (cellMap==null || x<0)
			return x;
		int y=super.rowAtPoint(p);
		return cellMap.visibleCell(y,x)[1];
	}

	public int rowAtPoint(Point p) {
		int y=super.rowAtPoint(p);
		// -1 is returned by columnAtPoint if the point is not in the table
		if (cellMap==null || y<0)
			return y;
		int x=super.columnAtPoint(p);
		return cellMap.visibleCell(y,x)[0];
	}


}
