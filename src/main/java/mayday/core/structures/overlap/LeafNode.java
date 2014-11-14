package mayday.core.structures.overlap;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.structures.LongTools;


public class LeafNode extends AbstractNode {
	
	protected long[] content;

	public LeafNode(ListManager lm, long start) {
		super(lm, start);
		content = new long[levelSize];
	}

	public void put(long from, long to, long objectKey) {
		if (putSelf(from, to, objectKey))
			return;
	
		// restrict coordinates
		from-=start;
		to-=start;
		from = Math.max(from, 0);
		to = Math.min(levelSize*64-1, to);	
		
		int start_base = (int)from/64;
		int start_offset = (int)(from%64);
		int  end_base = (int)to/64;
		int end_offset = (int)(to%64);

		int count = (int)(end_base-start_base+1);

		for (int i=0; i!=count; ++i) {
			int start = i==0 ? start_offset : 0;
			int end = i==(count-1) ? end_offset : 63;
			putPart(start_base+i, start, end, objectKey);
		}  	
  	}
	  	
	protected void putPart(int offset, int start, int end, long objectKey) {
		long listID = content[offset]-1;
  		if (listID==-1) {
  			listID = lm.newLists();
  			content[offset] = listID+1;
		}
		long mask = LongTools.fromTo(start, end);
		lm.add(listID, objectKey, mask);
	}
	
	public Set<Long> get(long from, long to, Set<Long> result) {		
		if (result==null)
			result = new TreeSet<Long>(); 
		
		getSelf(result);		
		
		// restrict coordinates
		from-=start;
		to-=start;
		from = Math.max(from, 0);
		to = Math.min(levelSize*64-1, to);		
		
		int start_base = (int)from/64;
  		int start_offset = (int)(from%64);
  		int end_base = (int)to/64;
  		int end_offset = (int)(to%64);
  		
  		int count = (int)(end_base-start_base+1);

  		for (int i=0; i!=count; ++i) {
  			int start = i==0 ? start_offset : 0;
  			int end = i==(count-1) ? end_offset : 63;
  			getPart(start_base+i, start, end, result);
  		}  	
  		
  		return result;
  	}
	
	protected void getPart(int offset, int start, int end, Set<Long> ret) {
		
  		long listID = content[offset]-1;
  		if (listID==-1)
  			return;
  		
  		long mask = LongTools.fromTo(start, end);
  		if (listID != -1) {
  			Iterator<Long> iOverlaps = lm.getOverlaps(listID);
			Iterator<Long> iIDs = lm.getIDs(listID);
			while (iOverlaps.hasNext()) {
				long overlap = iOverlaps.next();
				long id = iIDs.next();
				if ((overlap & mask)!=0)
					ret.add(id);
			}				
  		}
  	}

	@Override
	public boolean isCovered(long position) {
		if (myID!=-1)
			return true;
		
		position-=start;
		position = Math.max(position, 0);
		position = Math.min(levelSize*64-1, position);		
		
		int base = (int)position/64;
  		int offset = (int)(position%64);

  		long listID = content[base]-1;
  		if (listID==-1)
  			return false;
  		
  		Iterator<Long> iOverlaps = lm.getOverlaps(listID);
		while (iOverlaps.hasNext()) {
			long overlap = iOverlaps.next();
			if (LongTools.covered(overlap, offset))
				return true;
		}				

		return false;
	}


	
	@Override
	public int getActualLevelSize() {		
		return levelSize*64;
	}
	
	public long getEnd() {
		return start+levelSize*64 -1;
	}

	@Override
	public Iterator<Long> coverageIterator() {
		if (myID==-1) // no global coverage 
			return new LeafIterator();
		return new TotalIterator(); // global coverage
	}
	
	@Override
	public Iterator<Long> coverageIterator(long startposition) {
		if (myID==-1) // no global coverage 
			return new LeafIterator(startposition);
		return new TotalIterator(startposition); // global coverage
	}
	
	protected class LeafIterator implements Iterator<Long> {

		protected int pos;
		protected int offset;
		
		public LeafIterator() {
			this(-1);
		}
		
		public LeafIterator(long startposition) {
			if (startposition< start) // do not allow positions before this node
				startposition = start;
			pos = (int)(startposition-start)/64;
			offset = (int)(startposition-start)%64;
			--offset;
			if (offset<0) {
				offset=63;
				--pos;
			}
			next();
		}
		
		@Override
		public boolean hasNext() {
			return pos<levelSize;
		}

		@Override
		public Long next() {
			long last = start+pos*64+offset;			
			boolean found=false;
			while(pos<levelSize && !found) {
				++offset;
				if (offset>63) {
					offset=0;
					pos++;
				}
				if (pos<levelSize) {
					long listID = content[pos]-1;
					if (listID>-1) {
			  			Iterator<Long> iOverlaps = lm.getOverlaps(listID);
						while (iOverlaps.hasNext() && !found) {
							long overlap = iOverlaps.next();
							found = LongTools.covered(overlap, offset);
				  		}
					} else {
						offset=63; // jump to next pos
					}
				}
			}
			return last;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}

}
