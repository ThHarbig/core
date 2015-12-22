package mayday.core.structures.natives.inmemory;

import java.util.Arrays;
import java.util.Iterator;

import mayday.core.structures.natives.QuickSorter;

public class LinkedDoubleArray extends AbstractLinkedArray<double[]>{
 
	public LinkedDoubleArray(int blocksize) {
		super(blocksize);
	}

	public LinkedDoubleArray(LinkedDoubleArray other) {
		super(0);
		cloneFrom(other);
	}	
	
	public double[] makeBlock() {
		return new double[blocksize];
	}

	public double get(long i) {
		if (i>size())
			throw new ArrayIndexOutOfBoundsException(""+i+" > "+size());
		if (i<blocksize)
			return first_block[(int)i];
		int base = (int)(i / blocksize);
		int offset = (int)(i % blocksize);
		return linked_arrays.get(base-1)[offset];
	}

	public void set(long i, double value) {
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

	public long add(double value) {
		increaseSizeBy(1);
		set(length-1, value);
		return length-1;
	}

	@Override
	protected String toString(double[] part) {
		return Arrays.toString(part);
	}
	
	
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

	public class Sorter extends QuickSorter<LinkedDoubleArray, Double> {

		public Sorter() {
			super(LinkedDoubleArray.this);
		}

		@Override
		protected Double getElement(LinkedDoubleArray container, long index) {
			return container.get(index);
		}

		@Override
		protected long size(LinkedDoubleArray container) {
			return container.size();
		}

		@Override
		protected void swapElements(LinkedDoubleArray container, long i1, long i2) {
			double k = container.get(i1);
			container.set(i1, container.get(i2));
			container.set(i2, k);
		}

		@Override
		protected int compareElements(Double e1, Double e2) {
			return e1.compareTo(e2);
		}

	}	

}
