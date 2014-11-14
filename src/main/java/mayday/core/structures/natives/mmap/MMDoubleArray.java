package mayday.core.structures.natives.mmap;

import java.util.Arrays;
import java.util.Iterator;

import mayday.core.structures.natives.QuickSorter;

public class MMDoubleArray extends AbstractMemoryMappedArray<double[]>{
 
	public MMDoubleArray(int minsize) {
		super(minsize, 8);
	}

	public MMDoubleArray(MMDoubleArray other) {
		super(other);
	}	
	
	@Override
	protected void copyBuffers(AbstractMemoryMappedArray<double[]> other) {
		MMDoubleArray lla = (MMDoubleArray)other;
		for (long i = minimalsize; i!=length; ++i)
			set(i, lla.get(i));
	}
	
	public double[] makeBlock(int size) {
		return new double[size];
	}

	public double get(long i) {
		if (i>size())
			throw new ArrayIndexOutOfBoundsException(""+i+" > "+size());
		if (i<minimalsize)
			return first_block[(int)i];
		i-=minimalsize;
		int base = (int)(i / effectiveBufferSize);
		int offset = (int)(i % effectiveBufferSize);
		return linked_buffers.get(base).getDouble(offset*8);
	}

	public void set(long i, double value) {
		if (i>size())
			throw new  ArrayIndexOutOfBoundsException(""+i+" > "+size());
		if (i<minimalsize)
			first_block[(int)i]=value;
		else {
			i-=minimalsize;
			int base = (int)(i / effectiveBufferSize);
			int offset = (int)(i % effectiveBufferSize);
			linked_buffers.get(base).putDouble(offset*8, value);
		}
	}

	public long add(double value) {
		increaseSizeBy(1);
		set(length-1, value);
		return length-1;
	}

	@Override
	protected String toString(double[] part) {
		return Arrays.toString(part);
	}
	
//	@Override
//	protected void writeDump(DataOutputStream dos, double[] firstblock, int length) throws IOException {
//		for (int i=0; i!=length; ++i)
//			dos.writeDouble(firstblock[i]);
//	}
//	
//	@Override
//	protected void readDump(DataInputStream dis, double[] firstblock, int length) throws IOException {
//		for (int i=0; i!=length; ++i)
//			firstblock[i] = dis.readDouble();
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
	
	public void sort() {
		new Sorter().sort();
	}

	public Iterator<Double> iterator() {
		return new Iterator<Double>() {
			protected long next=0;

			public boolean hasNext() {
				return next<size();
			}

			public Double next() {
				return get(next++);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}			
		};
	}

	public class Sorter extends QuickSorter<MMDoubleArray, Double> {

		public Sorter() {
			super(MMDoubleArray.this);
		}

		@Override
		protected Double getElement(MMDoubleArray container, long index) {
			return container.get(index);
		}

		@Override
		protected long size(MMDoubleArray container) {
			return container.size();
		}

		@Override
		protected void swapElements(MMDoubleArray container, long i1, long i2) {
			double k = container.get(i1);
			container.set(i1, container.get(i2));
			container.set(i2, k);
		}

		@Override
		protected int compareElements(Double e1, Double e2) {
			return e1.compareTo(e2);
		}

	}
	
//	public class Sorter extends HeapSorter<LinkedDoubleArray, Double> {
//
//		public Sorter() {
//			super(LinkedDoubleArray.this);
//		}
//
//		@Override
//		protected int compareElements(long i1, long i2) {
//			Double l1 = get(i1);
//			Double l2 = get(i2);
//			return l1.compareTo(l2);
//		}
//
//		@Override
//		protected Double getElement(long index) {
//			return get(index);
//		}
//
//		@Override
//		protected void setElement(long index, Double content) {
//			set(index, content);
//		}
//
//		@Override
//		protected long size() {
//			return size();
//		}
//
//	}

	public long binarySearch(double key) {
		long low = 0;
		long high = size()-1;

		while (low <= high) {
			long mid = (low + high) >>> 1;
			Double midVal = get(mid);
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
