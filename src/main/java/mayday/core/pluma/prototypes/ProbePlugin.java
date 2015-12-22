package mayday.core.pluma.prototypes;

import java.util.Collection;

public interface ProbePlugin {

	public abstract void run(
			Collection<mayday.core.Probe> probes, 
			mayday.core.MasterTable masterTable 
	);

	
}
