package mayday.dynamicpl.dataprocessor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;

@SuppressWarnings("unchecked")
public class ExtractValueFromMap extends AbstractDataProcessor<Map, Collection> {

	private Class[] innerClass;
	
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		if (Map.class.isAssignableFrom(inputClass[0])) {
			// get key class
			//Class keyClass = inputClass[1];
			// get value class
			Class valueClass = inputClass[2];
			innerClass = new Class[]{Collection.class, valueClass};
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return ", values, ";
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.filter.ValueFromMap",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Extract Values from Map",
				"Extract Values from Map"
		);
		return pli;
	}
	
	@Override
	protected Collection convert(Map value) {
		return value.values();
	}
	@Override
	public Class<?>[] getDataClass() {
		return innerClass; 
	};

}
