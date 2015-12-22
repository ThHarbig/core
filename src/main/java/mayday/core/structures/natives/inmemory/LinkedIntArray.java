package mayday.core.structures.natives.inmemory;

import java.util.Arrays;
import java.util.Iterator;

import mayday.core.structures.natives.QuickSorter;

public class LinkedIntArray extends AbstractLinkedArray<int[]>{

	public LinkedIntArray(int blocksize) {
		super(blocksize);
	}

	public LinkedIntArray(LinkedIntArray other) {
		super(0);
		cloneFrom(other);
	}	
	
	public int[] makeBlock() {
		return new int[blocksize];
	}

	public int get(long i) {
		if (i>size())
			throw new ArrayIndexOutOfBoundsException(""+i+" > "+size());
		if (i<blocksize)
			return first_block[(int)i];
		int base = (int)(i / blocksize);
		int offset = (int)(i % blocksize);
		return linked_arrays.get(base-1)[offset];
	}

	public void set(long i, int value) {
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

	public long add(int value) { 
		increaseSizeBy(1);
		set(length-1, value);
		return length-1;
	}

	@Override
	protected String toString(int[] part) {
		return Arrays.toString(part);
	}
	
	
	public void sort() {
		new Sorter().sort();
	}

	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			protected long next=0;

			public boolean hasNext() {
				return next<size();
			}

			public Integer next() {
				return get(next++);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}			
		};
	}
	
	public Iterator<Integer> reverseIterator() {
		return new Iterator<Integer>() {
			protected long next=size()-1;

			public boolean hasNext() {
				return next>=0;
			}

			public Integer next() {
				return get(next--);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}			
		};
	}

	public class Sorter extends QuickSorter<LinkedIntArray, Integer> {

		public Sorter() {
			super(LinkedIntArray.this);
		}

		@Override
		protected Integer getElement(LinkedIntArray container, long index) {
			return container.get(index);
		}

		@Override
		protected long size(LinkedIntArray container) {
			return container.size();
		}

		@Override
		protected void swapElements(LinkedIntArray container, long i1, long i2) {
			int k = container.get(i1);
			container.set(i1, container.get(i2));
			container.set(i2, k);
		}

		@Override
		protected int compareElements(Integer e1, Integer e2) {
			return e1.compareTo(e2);
		}

	}

}
