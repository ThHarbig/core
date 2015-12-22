package mayday.dynamicpl.dataprocessor;

import java.util.Collection;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;

@SuppressWarnings("unchecked")
public class AnyFromCollection extends AbstractDataProcessor<Collection, Object> {

	private Class[] innerClass;
	
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		if (inputClass[0]==Collection.class) {
			if (inputClass.length>1) {
				innerClass  = new Class[inputClass.length-1];
				System.arraycopy(inputClass, 1, innerClass, 0, inputClass.length-1);
			}				
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return ", at least one item ";
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.filter.AnyFromList",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"At least one element matches",
				"At least one element matches"
		);
		return pli;
	}
	
	/* Here we need a special implementation: We want to start the next chain element for ALL collection items
	 * until one is found to be true
	 */ 
	public Boolean processChain(Collection value) {
		if (nextInChain==null)
			return null;
		else {
			Boolean result = null;
			for (Object item : value) {
				result = nextInChain.processChain(item);
				if (result!=null && result==true)
					break;
			}
			return result;
		}
	}
	
	
	@Override
	protected Object convert(Collection value) {
		return value.iterator().next(); // dummy function
	}
	@Override
	public Class<?>[] getDataClass() {
		return innerClass; 
	};

}
