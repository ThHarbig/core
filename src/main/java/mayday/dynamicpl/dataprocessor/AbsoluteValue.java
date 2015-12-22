package mayday.dynamicpl.dataprocessor;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;

@SuppressWarnings("unchecked")
public class AbsoluteValue extends AbstractDataProcessor<Double, Double> {

	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return Double.class.isAssignableFrom(inputClass[0]);
	}

	@Override
	public String toString() {
		return " (absolute)";
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.filter.AbsoluteValue",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Convert a number to its absolute value",
				"Absolute value"
		);
		return pli;
	}
	
	@Override
	protected Double convert(Double value) {
		return Math.abs(value);
	}
	@Override
	public Class<?>[] getDataClass() {
		return new Class[]{Double.class}; 
	};

}
