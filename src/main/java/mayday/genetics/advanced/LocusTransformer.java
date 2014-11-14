package mayday.genetics.advanced;

import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;

public interface LocusTransformer {

	public void addTransformedCoordinate(ChromosomeSetContainer target, AbstractGeneticCoordinate inputCoordinate);
	
	public AbstractGeneticCoordinate[] transform(AbstractGeneticCoordinate inputCoordinate, ChromosomeSetContainer csc);
	
}
