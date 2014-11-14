package mayday.core.gui.dataset;

import mayday.core.DataSet;

public interface DataSetSelectionFilter {

	public boolean pass(DataSet ds);
	
}
