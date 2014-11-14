package mayday.genetics.importer;

import java.util.List;

import mayday.genetics.locusmap.LocusMap;

public interface LocusFileImportPlugin {
	
	public LocusMap importFrom(List<String> files);

}
