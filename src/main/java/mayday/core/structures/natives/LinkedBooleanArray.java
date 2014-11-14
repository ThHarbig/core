package mayday.core.structures.natives;

import java.util.Iterator;

import mayday.core.structures.LongTools;
import mayday.core.structures.SparseArray;

public class LinkedBooleanArray {
	
	protected SparseArray<Long> underling;
	
	protected long maxPos=-1;
	
	public LinkedBooleanArray() {
		clear();
	}
	
	public final void set(long position, boolean value) {
		put(position,position,value);
	}
  	
  	public void put(long startposition, long endposition, boolean value) {
  		if (endposition<startposition) {
  			long tmp = endposition;
  			endposition=startposition;
  			startposition = tmp;
  		}
  		
  		long start_base = startposition/64;
  		int start_offset = (int)(startposition%64);
  		long end_base = endposition/64;
  		int end_offset = (int)(endposition%64);
  		
  		int count = (int)(end_base-start_base+1);

  		for (int i=0; i!=count; ++i) {
  			int start = i==0 ? start_offset : 0;
  			int end = i==(count-1) ? end_offset : 63;
  			putPart(start_base+i, start, end, value);
  		}  	  
  		
  		maxPos = Math.max(maxPos, endposition);
  	}
  	
  	protected void putPart(long base, int start, int end, boolean value) {
  		Long val = underling.get(base);
  		long mask;
  		if (value) {
  			mask = LongTools.fromTo(start, end);
  		} else {
  			long maskleft = (start>0) ? LongTools.fromTo(0, start-1) : 0;
  			long maskright = (end<63) ? LongTools.fromTo(end+1, 63) : 0;
  			mask = maskleft | maskright;
  		}
  		if (val==null)
  			val = 0l;
  		val = value? 
  			val | mask:
  			val & mask;
  		if (val==0)
  			val = null;
  		underling.putReplace(base, val);
  	}
	
  	
	public boolean get(long position) {
		long base = position/64;
		int from = (int) (position % 64);
		Long val = underling.get(base);
		if (val==null)
			return false;
		Long mask = LongTools.fromTo(from, from);
		return (val & mask)!=0;
	}
	
	public void clear() {
		underling = new SparseArray<Long>(false);
	}
	
	public long size() {
		return maxPos+1;
	}
	
	public GenomeArrayKeyIterator coveredPositionsIterator(long startposition) {
		return new GenomeArrayKeyIterator(startposition);
	}
	
	public class GenomeArrayKeyIterator implements Iterator<Long>, Iterable<Long> {

		protected long base;
		protected int offset;
		protected Iterator<Long> saki;
		
		public GenomeArrayKeyIterator(long start) {
			--start;
			base = start/64;
			offset = (int)start % 64;
			saki = underling.keyIterator(base);
			if (!saki.hasNext()) {
				base=-1;
			} else { 
				long realbase = saki.next();
				if (realbase!=base)
					offset=0;
				base = realbase;
				findNext();
			}			
		}
		
		public boolean hasNext() {
			return (base>=0);
		}

		protected void findNext() {
			while (base>=0) {
				++offset;
				if (offset==64) {
					if (!saki.hasNext()) {
						base = -1;
						return;
					}
					base=saki.next();
					offset=0;
				}
				boolean c = get(base*64+offset);
				if (c)
					return;
			}
		}
		
		public Long next() {
			long ret = base*64+offset;
			findNext();
			return ret;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public Iterator<Long> iterator() {
			return this;
		}
		
	}	
	
}
