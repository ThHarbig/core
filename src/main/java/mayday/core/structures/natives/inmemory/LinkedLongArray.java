package mayday.core.structures.natives.inmemory;

import java.util.Arrays;
import java.util.Iterator;

import mayday.core.structures.natives.QuickSorter;

public class LinkedLongArray extends AbstractLinkedArray<long[]>{

	public LinkedLongArray(int blocksize) {
		super(blocksize);
	}

	public LinkedLongArray(LinkedLongArray other) {
		super(0);
		cloneFrom(other);
	}	
	
	public long[] makeBlock() {
		return new long[blocksize];
	}

	public long get(long i) {
		if (i>size())
			throw new ArrayIndexOutOfBoundsException(""+i+" > "+size());
		if (i<blocksize)
			return first_block[(int)i];
		int base = (int)(i / blocksize);
		int offset = (int)(i % blocksize);
		return linked_arrays.get(base-1)[offset];
	}

	public void set(long i, long value) {
		if (i>size())
			throw new  ArrayIndexOutOfBoundsException(""+i+" > "+size());
		if (i<blocksize)
			first_block[(int)i] = value;
		else {
			int base = (int)(i / blocksize);
			int offset = (int)(i % blocksize);
			linked_arrays.get(base-1)[offset] = value;
		}
	}

	public long add(long value) { 
		increaseSizeBy(1);
		set(length-1, value);
		return length-1;
	}

	@Override
	protected String toString(long[] part) {
		return Arrays.toString(part);
	}
	
	
	public void sort() {
		new Sorter().sort();
	}

	public Iterator<Long> iterator() {
		return new Iterator<Long>() {
			protected long next=0;

			public boolean hasNext() {
				return next<size();
			}

			public Long next() {
				return get(next++);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}			
		};
	}
	
	public Iterator<Long> reverseIterator() {
		return new Iterator<Long>() {
			protected long next=size()-1;

			public boolean hasNext() {
				return next>=0;
			}

			public Long next() {
				return get(next--);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}			
		};
	}
	
	public class Sorter extends QuickSorter<LinkedLongArray, Long> {

		public Sorter() {
			super(LinkedLongArray.this);
		}

		@Override
		protected Long getElement(LinkedLongArray container, long index) {
			return container.get(index);
		}

		@Override
		protected long size(LinkedLongArray container) {
			return container.size();
		}

		@Override
		protected void swapElements(LinkedLongArray container, long i1, long i2) {
			long k = container.get(i1);
			container.set(i1, container.get(i2));
			container.set(i2, k);
		}

		@Override
		protected int compareElements(Long e1, Long e2) {
			return e1.compareTo(e2);
		}

	}

}
