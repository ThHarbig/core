package mayday.dynamicpl.dataprocessor;

import java.util.HashMap;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;

public class ProbeNameAsString extends AbstractDataProcessor<Probe, String> {

	@Override
	public Class<?>[] getDataClass() {
		return new Class[]{String.class};
	}

	@Override
	public String toString() {
		return "Probe Name";
	}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.source.ProbeName",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Extract Probe Name for filtering",
				"Probe Name"
		);
		return pli;
	}

	@Override
	protected String convert(Probe value) {
		return value.getName();
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return (Probe.class.isAssignableFrom(inputClass[0]));
	}

}

