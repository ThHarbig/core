package mayday.core.io.gude.prototypes;

import java.util.List;

public interface DatasetFileExportPlugin {
	
	public void exportTo(List<mayday.core.DataSet> datasets, String file);

}
