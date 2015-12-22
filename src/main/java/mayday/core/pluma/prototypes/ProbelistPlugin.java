package mayday.core.pluma.prototypes;

import java.util.List;

public interface ProbelistPlugin {

	public abstract List<mayday.core.ProbeList> run(
			List<mayday.core.ProbeList>probeLists, 
			mayday.core.MasterTable masterTable 
	);

	
}
