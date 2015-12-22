package mayday.core.gui.tablespecials.SpanningCells;

public interface CellMap {
	
	public int colSpan(int row, int column);
	
	public int rowSpan(int row, int column);
	
	public int[] visibleCell(int row, int column);

}
