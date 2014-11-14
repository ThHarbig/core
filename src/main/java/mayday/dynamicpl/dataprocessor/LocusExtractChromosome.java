package mayday.dynamicpl.dataprocessor;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;
import mayday.genetics.Locus;

public class LocusExtractChromosome extends AbstractDataProcessor<Locus, String> 
{
	
	@Override
	public Class<?>[] getDataClass() {
		return new Class[]{String.class};
	}

	@Override
	public String toString() {
		return "::chromosome";
	}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.source.Locus2Chromosome",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Extract Locus chromosome",
				"Extract Locus chromosome"
		);
		return pli;
	}

	@Override
	protected String convert(Locus value) {
		return value.getCoordinate().getChromosome().getId();
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return mayday.genetics.Locus.class.isAssignableFrom(inputClass[0]);
	}

	
}

