package mayday.dynamicpl.dataprocessor;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;

@SuppressWarnings("unchecked")
public class IntToDouble extends AbstractDataProcessor<Integer, Double> {

	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return Integer.class.isAssignableFrom(inputClass[0]);
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.filter.IntToDouble",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Convert Int to Double",
				"Convert Int to Double"
		);
		return pli;
	}
	
	@Override
	protected Double convert(Integer value) {
		return new Double(value);
	}
	@Override
	public Class<?>[] getDataClass() {
		return new Class[]{Double.class}; 
	};

}
