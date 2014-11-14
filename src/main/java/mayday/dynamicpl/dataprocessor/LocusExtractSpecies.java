package mayday.dynamicpl.dataprocessor;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;
import mayday.genetics.Locus;

public class LocusExtractSpecies extends AbstractDataProcessor<Locus, String> 
{
	
	@Override
	public Class<?>[] getDataClass() {
		return new Class[]{String.class};
	}

	@Override
	public String toString() {
		return "::species";
	}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.source.Locus2Species",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Extract Locus species",
				"Extract Locus species"
		);
		return pli;
	}

	@Override
	protected String convert(Locus value) {
		return value.getCoordinate().getChromosome().getSpecies().getName();
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return mayday.genetics.Locus.class.isAssignableFrom(inputClass[0]);
	}

	
}

