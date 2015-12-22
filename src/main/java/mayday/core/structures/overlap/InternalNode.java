package mayday.core.structures.overlap;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


public class InternalNode extends AbstractNode {
	
	protected long multiplier;
	protected Node[] children;

	public InternalNode(ListManager lm, long mult, long start, Node firstChild) {
		this(lm,mult,start);
		children[0] = firstChild;
	}
	
	public InternalNode(ListManager lm, long mult, long start) {
		super(lm, start);
		children = new Node[levelSize];
		multiplier = mult;
	}

	public void put(long from, long to, long objectKey) {
		// if completely covered, add my own id
		if (putSelf(from, to, objectKey))
			return;
		
		long start_base = from - start;
		start_base/=multiplier;
		long  end_base = to-start;
		end_base/=multiplier;
		
		start_base = Math.max(0, start_base);
		end_base = Math.min(levelSize-1, end_base);
		
		
		int count = (int)(end_base-start_base+1);

		for (int i=0; i!=count; ++i) {
			int offset = (int)start_base+i;
			Node sub = children[offset];
			if (sub==null) {
				long nextmult = multiplier/levelSize;
				long nextnextmult = nextmult/levelSize;
				if (nextnextmult==0)
					sub = new LeafNode(lm, start+(offset*multiplier));
				else
					sub = new InternalNode(lm, nextmult, start+(offset*multiplier));
				children[offset]=sub;
			}
			sub.put(from, to, objectKey);
		}  	
		
  	}
	  	
	public Set<Long> get(long from, long to, Set<Long> result) {		
		if (result==null)
			result = new TreeSet<Long>(); 
		
		getSelf(result);
		
		long start_base = from - start;
		start_base/=multiplier;
		long  end_base = to-start;
		end_base/=multiplier;
		
		start_base = Math.max(0, start_base);
		end_base = Math.min(levelSize-1, end_base);
		
		int count = (int)(end_base-start_base+1);
		
  		for (int i=0; i!=count; ++i) {
  			int offset = (int)start_base+i;
			Node sub = children[offset];
			if (sub!=null)
				sub.get(from, to, result);
  		}  	
  		
  		return result;
  	}
	
	@Override
	public boolean isCovered(long position) {
		
		if (position>getEnd())
			return false;
		
		if (myID!=-1)
			return true;
		
		position-=start;
		position = Math.max(position, 0);
		position = Math.min(levelSize-1, position);
		
		int offset = (int)(position/multiplier);
		Node sub = children[offset];		
		if (sub!=null)
			return sub.isCovered(position);
		
		return false;		
	}
	
	@Override
	public int getActualLevelSize() {
		return levelSize;
	}
	
	public long getEnd() {
		return start+levelSize*multiplier -1;
	}
	
	@Override
	public Iterator<Long> coverageIterator() {
		if (myID==-1) // no global coverage 
			return new LeafIterator();
		return new TotalIterator(); // global coverage
	}

	public Iterator<Long> coverageIterator(long startposition) {
		if (myID==-1) // no global coverage 
			return new LeafIterator(startposition);
		return new TotalIterator(startposition); // global coverage
	}
	
	
	protected class LeafIterator implements Iterator<Long> {

		protected int pos=-1;
		protected Iterator<Long> subIter;
		
		public LeafIterator() {
			this(-1);
		}
		
		public LeafIterator(long startposition) {
			if (startposition < start)
				startposition = start;
			pos = (int)((startposition-start)/multiplier);
			--pos;
			nextSubIter(startposition);
		}

		
		@Override
		public boolean hasNext() {
			return pos<levelSize && subIter!=null && subIter.hasNext();
		}

		public void nextSubIter(long startposition) {
			while (subIter == null && pos<levelSize) {
				++pos;
				if (pos<levelSize) {
					Node sub = children[pos];
					if (sub!=null) {
						subIter = sub.coverageIterator(startposition);
						if (!subIter.hasNext())
							subIter = null;
					}					
				}
			}
		}
		
		@Override
		public Long next() {
			Long result = subIter!=null ? subIter.next() : null;
			
			if (subIter!=null && !subIter.hasNext())
				subIter = null;
			
			if (subIter == null) 
				nextSubIter(-1);
			
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}

}
