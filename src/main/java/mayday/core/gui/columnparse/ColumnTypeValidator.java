package mayday.core.gui.columnparse;

import java.util.List;

public interface ColumnTypeValidator<ColumnType>{
	
	public boolean isValid( List<ColumnType> columnTypes );
	
	public String getValidityHint();

}
