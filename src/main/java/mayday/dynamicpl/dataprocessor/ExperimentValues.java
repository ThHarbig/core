package mayday.dynamicpl.dataprocessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;

@SuppressWarnings("unchecked")
public class ExperimentValues extends AbstractDataProcessor<Probe, Collection> {

	@Override
	public Class<?>[] getDataClass() {
		return new Class[]{Collection.class, Double.class};
	}

	@Override
	public String toString() {
		return "Probe Values";
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.source.ExperimentValues",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Extract Probe Values for filtering",
				"Probe Experiment Values"
		);
		return pli;
	}

	@Override
	protected Collection convert(Probe value) {
		ArrayList<Double> al = new ArrayList<Double>();
		for (double d : value.getValues())
			al.add(d);
		return al;
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return (Probe.class.isAssignableFrom(inputClass[0]));
	}

}

