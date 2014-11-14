package mayday.genetics;


import java.util.Collection;
import java.util.TreeMap;

import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.chromosome.Chromosome;

public abstract class ChromosomeBasedContainer<T> {

	private TreeMap<Chromosome, T> map = new TreeMap<Chromosome, T>();
	protected ChromosomeSetContainer csc;
	
	public ChromosomeBasedContainer(ChromosomeSetContainer csc) {
		this.csc = csc;
	}

	protected abstract T getUnknown();
	
	public T get(Chromosome c) {
		T cs = map.get(c);
		if (cs == null)
			cs = getUnknown();
		return cs;
	}
	
	public T get(String species, String chrome) {
		Chromosome c = csc.getChromosomeOrNull(SpeciesContainer.getSpecies(species), chrome);
		if (c == null)
			return getUnknown();
		return get(c);
	}
	
	public void add(Chromosome c, T s) {
		add(s, c.getSpecies().getName(), c.getId());
	}
	
	public void add(T s, String speciesName, String chromosomeName) {
		Chromosome c = csc.getChromosome(SpeciesContainer.getSpecies(speciesName), chromosomeName);
		map.put(c,s);
	}
	
	public void updateLength(String speciesName, String chromosomeName, long length) {
		csc.getChromosome(SpeciesContainer.getSpecies(speciesName), chromosomeName).updateLength(length);
	}
	
	public void updateLength(Chromosome c, long length) {
		updateLength(c.getSpecies().getName(), c.getId(), length);
	}
	
	public Collection<T> values() {
		return map.values();
	}
	
}
