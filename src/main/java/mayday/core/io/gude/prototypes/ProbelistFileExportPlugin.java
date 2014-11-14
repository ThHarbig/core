package mayday.core.io.gude.prototypes;

import java.util.List;

public interface ProbelistFileExportPlugin {
	
	public void exportTo(List<mayday.core.ProbeList> probelist, String file);

}
