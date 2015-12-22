package mayday.vis3.model.manipulators;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.vis3.model.ManipulationMethod;

public class Ranking extends ManipulationMethod {

	public double[] manipulate(double[] input) {
		DoubleVector dv = new DoubleVector(input);
		return dv.rank().toArrayUnpermuted();
	}

	public String getName() {
		return "ranking";
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.manipulator.ranking",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Florian Battke",
					"battke@informatik.uni-tuebingen.de",
					"Ranking: f(x) = rank(x), i.e. each expression matrix column is ranked independently",
					getName()
					);
		return pli;
	}

	@Override
	public String getDataDescription() {
		return "ranked";
	}

}