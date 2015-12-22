package mayday.dynamicpl.dataprocessor;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;
import mayday.genetics.Locus;

public class LocusExtractStrand extends AbstractDataProcessor<Locus, String> 
{
	
	@Override
	public Class<?>[] getDataClass() {
		return new Class[]{String.class};
	}

	@Override
	public String toString() {
		return "::strand";
	}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dynamicPL.source.Locus2Strand",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Extract Locus strand",
				"Extract Locus strand"
		);
		return pli;
	}

	@Override
	protected String convert(Locus value) {
		return value.getCoordinate().getStrand().c+"";
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return mayday.genetics.Locus.class.isAssignableFrom(inputClass[0]);
	}

	
}

