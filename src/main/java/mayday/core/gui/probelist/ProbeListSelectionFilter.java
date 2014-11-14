package mayday.core.gui.probelist;

import mayday.core.ProbeList;

public interface ProbeListSelectionFilter {

	public boolean pass(ProbeList pl);
	
}
