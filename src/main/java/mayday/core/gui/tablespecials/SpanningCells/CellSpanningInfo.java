package mayday.core.gui.tablespecials.SpanningCells;

import mayday.core.structures.maps.TwoKeyHashMap;

public class CellSpanningInfo implements CellMap {
	
	protected TwoKeyHashMap<Integer, Integer, int[]> spannedCells = new TwoKeyHashMap<Integer, Integer, int[]>();
	protected TwoKeyHashMap<Integer, Integer, int[]> backLinks = new TwoKeyHashMap<Integer, Integer, int[]>();

	protected int[] speedUpInstance = new int[2];
	
	public int colSpan(int row, int column) {		
		int[] span = spannedCells.get(row, column);
		if (span==null)
			return 1;
		else
			return span[1];
	}

	public int rowSpan(int row, int column) {
		int[] span = spannedCells.get(row, column);
		if (span==null)
			return 1;
		else
			return span[0];
	}

	public int[] visibleCell(int row, int column) {
		int[] realposition = backLinks.get(row, column);
		if (realposition==null) {
			realposition = speedUpInstance;
			realposition[0] = row;
			realposition[1] = column;
		}
		return realposition;
	}
	
	public void setSpan(int row, int col, int rowspan, int colspan) {
		spannedCells.put(row, col, new int[]{rowspan, colspan});
		int[] realpos=new int[]{row,col};
		for (int r=row; r<row+rowspan; r++) {
			for(int c=col; c<col+colspan; c++) {
				backLinks.put(r, c, realpos);
			}
		}
	}
	
	public void clear() {
		spannedCells.clear();
		backLinks.clear();
	}

}
