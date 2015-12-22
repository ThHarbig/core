package mayday.genetics.advanced;

import java.util.Iterator;
import java.util.LinkedList;

import mayday.genetics.basic.Strand;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;

public class StrandSplitIterator<X extends AbstractGeneticCoordinate> {

	protected X nextPLUS;
	protected X nextMINUS;
	protected Iterator<X> inIter;
	
	LinkedList<X> cachedPlus = new LinkedList<X>();
	LinkedList<X> cachedMinus= new LinkedList<X>();;

	public StrandSplitIterator(Iterator<X> inIter) {
		this.inIter = inIter;
		populate();
	}
	
	public void populate() {
		if (nextPLUS==null && cachedPlus.size()>0)
			nextPLUS = cachedMinus.removeFirst();
		if (nextMINUS ==null && cachedMinus.size()>0)
			nextMINUS = cachedMinus.removeFirst();
		while ((nextPLUS==null || nextMINUS==null) && inIter.hasNext()) {
			X c = inIter.next();
			if (c.getStrand()==Strand.PLUS)
				if (nextPLUS==null)
					nextPLUS = c;
				else 
					cachedPlus.add(c);
			else if (c.getStrand()==Strand.MINUS)
				if (nextMINUS==null)
					nextMINUS = c;
				else 
					cachedMinus.add(c);
		}
	}
	
	public Iterator<X> getForwardIterator() {
		return new Iterator<X>() {

			public boolean hasNext() {
				return nextPLUS!=null;
			}

			public X next() {
				X ret = nextPLUS;
				populate();
				return ret;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}

	public Iterator<X> getBackwardIterator() {
		return new Iterator<X>() {

			public boolean hasNext() {
				return nextMINUS!=null;
			}

			public X next() {
				X ret = nextMINUS;
				populate();
				return ret;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}
}
