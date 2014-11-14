package mayday.mpf;

import java.util.HashMap;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.GenericPlugin;

public class MPFDesigner extends AbstractPlugin implements GenericPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
			PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.mpf.designer",
					new String[0],
					Constants.MC_SESSION,
					new HashMap<String, Object>(),
					"Florian Battke",
					"battke@informatik.uni-tuebingen.de",
					"Visual tool for creating and editing MPF processing pipelines.",
					"Show MPF Designer"
					);
			return pli;
		}

	public void run() {
		Designer d = new Designer();
		d.setVisible(true);
	}

}
