package mayday.genetics.basic;

import mayday.genetics.basic.chromosome.Chromosome;

public interface ChromosomeFactory {

	public Chromosome createChromosome(Species s, String id, long length);
	
	public Class<? extends Chromosome> getChromosomeClass();
	
}
