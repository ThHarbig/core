package mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures;

import mayday.genetics.basic.Species;
import mayday.vis3.plots.genomeviz.genomeorganisation.Chrome;

public class SpeciesChromePair implements Comparable<Object>{
 
	protected Species species;
	protected Chrome chrome;
	
	protected String speciesName = "";
	protected String chromeId = "";
	
	public SpeciesChromePair(Species species, Chrome chrome){
		this.species = species;
		this.chrome = chrome;
		this.speciesName = species.getName();
		this.chromeId = chrome.getId();
	}
	
	public Species getSpecies(){
		return this.species;
	}
	
	public Chrome getChrome(){
		return this.chrome;
	}


	public int compareTo(Object obj) {
		if(speciesName.equals(((SpeciesChromePair)obj).speciesName) 
				&& chromeId.equals(((SpeciesChromePair)obj).chromeId)
				&& species.equals(((SpeciesChromePair)obj).species)
				&& chrome.equals(((SpeciesChromePair)obj).chrome)){	
			return 0;
		} else{
			return -1;
		}
	}


	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		
		if(speciesName.equals(((SpeciesChromePair)obj).speciesName) 
				&& chromeId.equals(((SpeciesChromePair)obj).chromeId)
				&& species.equals(((SpeciesChromePair)obj).species)
				&& chrome.equals(((SpeciesChromePair)obj).chrome)){	
			return true;
		} else{
			return false;
		}

	}


	public int hashCode() {

		int result = 31 + ((species == null) ? 0 : speciesName.hashCode()); 
	    result = 31 * result + ((chrome == null) ? 0 : chromeId.hashCode());
	    result = 31 * result + ((species == null) ? 0: species.hashCode());
	    result = 31 * result + ((chrome == null) ? 0: chrome.hashCode());
	    return result; 
	}
}
