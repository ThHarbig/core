package mayday.dynamicpl.dataprocessor;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;
import mayday.genetics.Locus;

public class LocusExtractTo extends AbstractDataProcessor<Locus, Long> 
{
	
	@Override
	public Class<?>[] getDataClass() {
		return new Class[]{Long.class};
	}

	@Override
	public String toString() {
		return "::start position";
	}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.source.Locus2To",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Extract Locus end position",
				"Extract Locus end position"
		);
		return pli;
	}

	@Override
	protected Long convert(Locus value) {
		return value.getCoordinate().getTo();
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return mayday.genetics.Locus.class.isAssignableFrom(inputClass[0]);
	}

	
}

