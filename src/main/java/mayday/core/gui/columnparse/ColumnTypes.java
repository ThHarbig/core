package mayday.core.gui.columnparse;

public interface ColumnTypes<ColumnType> {
	
	public ColumnType[] values();
	
	public ColumnType typeOf(String value);
	
	public int indexOf(ColumnType type);
		
}
