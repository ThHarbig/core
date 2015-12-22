package mayday.mpf.plumawrapper;

import java.util.HashMap;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.CorePlugin;
import mayday.mpf.FilterClassList;

public class PipelineRegistration extends AbstractPlugin implements CorePlugin {

	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(),
				"PAS.mpf.PipelineRegistration",
				new String[0],
				mayday.core.pluma.Constants.MC_CORE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Registers MPF pipelines with Mayday's PluginManager",
				"MPF Pipeline Registration"
				);
	}

	@Override
	public void run() {
		// wrap pipeline modules 
//		FilterClassList.dropInstance();
		for (FilterClassList.Item item : FilterClassList.getInstance().getValues()) {
			if (item.isComplex) {
				try {
					PluginManager.getInstance().addLatePlugin(WrappedMPFModule_Pipeline.producePluginInfo(item));					
				} catch (PluginManagerException e) {
					System.err.println("Can't register pipeline "+item+" with PluginManager:\n"+e.getMessage());
				}
			}
		}
	}

}
