package mayday.vis3.model.manipulators;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.model.ManipulationMethod;
import mayday.vis3.model.ManipulationMethodSingleValue;

public class None extends ManipulationMethod implements ManipulationMethodSingleValue {

	public String getName() {
		return "none";
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.manipulator.identity",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Florian Battke",
					"battke@informatik.uni-tuebingen.de",
					"Identity: f(x) = x",
					getName()
					);
		return pli;
	}
	
	public double[] manipulate(double[] input) {
		//attention! This is only a reference! May cause problems
		return input;
	}

	@Override
	public double manipulate(double input) {
		return input;
	}

	@Override
	public String getDataDescription() {		
		return ""; // data is unchanged
	}	
}