package mayday.core.pluma.prototypes;

import java.util.List;

public interface DatasetPlugin {

	public List<mayday.core.DataSet> run(
			List<mayday.core.DataSet> datasets 
	);

	
}
