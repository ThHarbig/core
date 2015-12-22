package mayday.core.structures.natives.mmap;

import java.util.Arrays;
import java.util.Iterator;

import mayday.core.structures.natives.QuickSorter;

public class MMIntArray extends AbstractMemoryMappedArray<int[]>{

	public MMIntArray(int minsize) {
		super(minsize, 4);
	}

	public MMIntArray(MMIntArray other) {
		super(other);
	}
	
	@Override
	protected void copyBuffers(AbstractMemoryMappedArray<int[]> other) {
		MMIntArray lla = (MMIntArray)other;
		for (long i = minimalsize; i!=length; ++i)
			set(i, lla.get(i));
	}
	
	public int[] makeBlock(int size) {
		return new int[size];
	}

	public int get(long i) {
		if (i>size())
			throw new ArrayIndexOutOfBoundsException(""+i+" > "+size());
		if (i<minimalsize)
			return first_block[(int)i];
		i-=minimalsize;
		int base = (int)(i / effectiveBufferSize);
		int offset = (int)(i % effectiveBufferSize);
		return linked_buffers.get(base).getInt(offset*4);
	}

	public void set(long i, int value) {
		if (i>size())
			throw new  ArrayIndexOutOfBoundsException(""+i+" > "+size());
		if (i<minimalsize)
			first_block[(int)i]=value;
		else {
			i-=minimalsize;
			int base = (int)(i / effectiveBufferSize);
			int offset = (int)(i % effectiveBufferSize);
			linked_buffers.get(base).putInt(offset*4, value);
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
	
	public class Sorter extends QuickSorter<MMIntArray, Integer> {

		public Sorter() {
			super(MMIntArray.this);
		}

		@Override
		protected Integer getElement(MMIntArray container, long index) {
			return container.get(index);
		}

		@Override
		protected long size(MMIntArray container) {
			return container.size();
		}

		@Override
		protected void swapElements(MMIntArray container, long i1, long i2) {
			int k = container.get(i1);
			container.set(i1, container.get(i2));
			container.set(i2, k);
		}

		@Override
		protected int compareElements(Integer e1, Integer e2) {
			return e1.compareTo(e2);
		}

	}

//	@Override
//	protected void writeDump(DataOutputStream dos, int[] firstblock, int length) throws IOException {
//		for (int i=0; i!=length; ++i)
//			dos.writeInt(firstblock[i]);
//	}
//	
//	@Override
//	protected void readDump(DataInputStream dis, int[] firstblock, int length) throws IOException {
//		for (int i=0; i!=length; ++i)
//			firstblock[i] = dis.readInt();
//	}
//
//	@Override
//	protected void readInternals(DataInputStream dis) throws IOException {
//		//nothing to do
//	}
//
//	@Override
//	protected void writeInternals(DataOutputStream dos) throws IOException {
//		// nothing to do
//	}

	
	public long binarySearch(int key) {
		long low = 0;
		long high = size()-1;

		while (low <= high) {
			long mid = (low + high) >>> 1;
			Integer midVal = get(mid);
			int cmp = midVal.compareTo(key);
			
			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // key found
			}
		return -(low + 1);  // key not found
	}
	
}
