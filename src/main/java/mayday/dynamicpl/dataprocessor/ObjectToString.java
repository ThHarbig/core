package mayday.dynamicpl.dataprocessor;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;

@SuppressWarnings("unchecked")
public class ObjectToString extends AbstractDataProcessor<Object, String> {

	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return Object.class.isAssignableFrom(inputClass[0]) && !inputClass[0].equals(String.class);
	}

	@Override
	public String toString() {
		return " (string representation)";
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.filter.ObjectToString",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Convert to String representation",
				"Convert to String representation"
		);
		return pli;
	}
	
	@Override
	protected String convert(Object value) {
		return value.toString();
	}
	@Override
	public Class<?>[] getDataClass() {
		return new Class[]{String.class}; 
	};

}
