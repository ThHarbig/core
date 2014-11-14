package mayday.core.io.gudi.prototypes;

import java.util.List;

import mayday.core.DataSet;

public interface ProbelistFileImportPlugin {
	
	public List<mayday.core.ProbeList> importFrom(List<String> files, DataSet ds);

}
