package mayday.dynamicpl.dataprocessor;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;


public class NumberMatches_Double extends NumberMatches_Generic<Double> {

	@Override
	protected Boolean convert(Double v)  {
		switch(operation) {
		case 0: return v<number;
		case 1: return v<=number;
		case 2: return v==number;
		case 3: return v>=number;
		case 4: return v>number;
		}
		return null;
	}

	@Override
	protected Double parseNumber(String string) {
		return Double.parseDouble(string);
	} 

	@Override
	protected void initNumbers() {
		number = 0.0d;
	}
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.filter.NumberMatches",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Compare a number ("+number.getClass()+")",
				"Compare a number ("+number.getClass()+")"
		);
		return pli;
	}

}
