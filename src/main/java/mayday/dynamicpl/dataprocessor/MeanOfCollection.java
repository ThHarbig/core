package mayday.dynamicpl.dataprocessor;

import java.util.Collection;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;

@SuppressWarnings("unchecked")
public class MeanOfCollection extends AbstractDataProcessor<Collection, Object> {

	public boolean isAcceptableInput(Class<?>[] inputClass) {
		if (inputClass[0]==Collection.class) {
			if (inputClass.length>1 && Number.class.isAssignableFrom(inputClass[1])) {
				return true;
			}				
		}
		return false;
	}

	@Override
	public String toString() {
		return ", mean ";
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.filter.MeanOfList",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Mean of all elements",
				"Mean of all elements"
		);
		return pli;
	}
	
	@Override
	protected Double convert(Collection value) {
		double m=0;
		double c=0;
		for (Object o : value) {
			if (o instanceof Number) {
				m+=((Number)o).doubleValue();
				++c;
			}
		}
		return m/c;
	}
	
	@Override
	public Class<?>[] getDataClass() {
		return new Class[]{Double.class}; 
	};

}
