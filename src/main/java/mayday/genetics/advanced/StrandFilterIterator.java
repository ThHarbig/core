package mayday.genetics.advanced;

import java.util.Iterator;

import mayday.genetics.basic.Strand;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;

public class StrandFilterIterator<X extends AbstractGeneticCoordinate> implements Iterator<X> {

	protected X next;
	protected Iterator<X> inIter;
	protected Strand strand;

	public StrandFilterIterator(Iterator<X> inIter, Strand strand) {
		this.inIter = inIter;
		this.strand=strand;
		next();
	}
	
	public boolean hasNext() {
		return next!=null;
	}

	public X next() {
		X ret = next;
		next=null;
		while (next==null && inIter.hasNext()) {
			next = inIter.next();
			if (!strand.similar(next.getStrand()))
				next = null;
		}
		return ret;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	

}
