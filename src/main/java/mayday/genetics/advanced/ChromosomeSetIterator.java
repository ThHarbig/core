/**
 * 
 */
package mayday.genetics.advanced;

import java.util.Iterator;
import java.util.LinkedList;

import mayday.genetics.advanced.chromosome.AbstractLocusChromosome;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;

public class ChromosomeSetIterator implements Iterator<AbstractGeneticCoordinate>, Iterable<AbstractGeneticCoordinate> {

	public final static int ITERATE_UNSORTED = 0;
	public final static int ITERATE_BY_SIZE_ASCENDING = 1;
	public final static int ITERATE_BY_SIZE_DESCENDING = 2;
	public final static int ITERATE_BY_START = 3;
	public final static int ITERATE_BY_END = 4;
	public final static int ITERATE_BY_START_AND_END = 5;
	
	protected Iterator<AbstractGeneticCoordinate> chromeIt;
	protected LinkedList<Chromosome> chromes;
	protected int order;
	
	public ChromosomeSetIterator(ChromosomeSetContainer csc, int iterationOrder) {
		if (!(AbstractLocusChromosome.class.isAssignableFrom(csc.getChromosomeFactory().getChromosomeClass())))
			throw new RuntimeException("Can only iterate over a ChromosomeSetContainer containing AbstractLocusChromosomes.");
		chromes = new LinkedList<Chromosome>(csc.getAllChromosomes());
		order = iterationOrder;
		nextChrome();
	}
	
	public ChromosomeSetIterator(ChromosomeSetContainer csc) {
		this(csc, ITERATE_UNSORTED);
	}
	
	public boolean hasNext() {
		if (chromeIt!=null && chromeIt.hasNext())
			return true;
		chromeIt = null;
		nextChrome();
		return (chromeIt!=null);
	}

	public AbstractGeneticCoordinate next() {
		return chromeIt.next();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	@SuppressWarnings("unchecked")
	protected void nextChrome() {
		while (chromes.size()>0 && chromeIt==null) {
			AbstractLocusChromosome derChromosome = ((AbstractLocusChromosome)chromes.removeFirst());
			switch (order) {
			case ITERATE_BY_SIZE_ASCENDING:
				chromeIt = derChromosome.iterateByLocusSize(true);
				break;
			case ITERATE_BY_SIZE_DESCENDING:
				chromeIt = derChromosome.iterateByLocusSize(false);
				break;
			case ITERATE_BY_START:
				chromeIt = derChromosome.iterateStartPositions();
				break;
			case ITERATE_BY_END:
				chromeIt = derChromosome.iterateEndPositions();
				break;
			case ITERATE_BY_START_AND_END:
				chromeIt = derChromosome.iterateAllPositions();
				break;
			default:
				chromeIt = derChromosome.iterateUnsorted();
			}			
			if (!chromeIt.hasNext())
				chromeIt = null;
		}
	}

	public Iterator<AbstractGeneticCoordinate> iterator() {
		return this;
	}

}