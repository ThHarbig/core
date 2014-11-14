package mayday.core.structures.overlap;

import java.util.Iterator;
import java.util.Set;


public abstract class AbstractNode implements Node {
	
	protected ListManager lm;	
	protected long start;
	protected long myID = -1;

	public AbstractNode(ListManager lm, long start) {
		this.lm = lm;
		this.start = start;
//		lm.addNode(this);
	}
	
	protected boolean putSelf(long from, long to, long objectKey) {
		// if completely covered, add my to my own list
		if (from<=start && to>=getEnd()) {
			if (myID==-1) {
				myID = lm.newLists();
			}
			lm.add(myID, objectKey, 0l);
			return true;
		}
		return false;
	}
	
	protected void getSelf(Set<Long> result) {
		if (myID!=-1) {
			Iterator<Long> iIDs = lm.getIDs(myID);
			while (iIDs.hasNext()) {
				result.add(iIDs.next());
			}				
		}
	}
	

	protected class TotalIterator implements Iterator<Long> {
		protected long pos;

		public TotalIterator() {
			this(-1);
		}
		
		public TotalIterator(long startposition) {
			if (startposition<0)
				startposition = start;
			pos = startposition;
		}
		
		@Override
		public boolean hasNext() {
			return pos <= getEnd();
		}

		@Override
		public Long next() {
			return pos++;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}


}
