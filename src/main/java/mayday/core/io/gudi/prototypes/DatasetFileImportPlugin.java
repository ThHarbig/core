package mayday.core.io.gudi.prototypes;

import java.util.List;

public interface DatasetFileImportPlugin {
	
	public List<mayday.core.DataSet> importFrom(List<String> files);

}
