package mayday.core.structures.natives.inmemory;

import java.util.Arrays;

import mayday.core.structures.natives.QuickSorter;


public class LinkedCharArray extends AbstractLinkedArray<char[]>{
	
	public LinkedCharArray(int blocksize) {
		super(5);
	}
	

	public LinkedCharArray(LinkedCharArray other) {
		super(0);
		cloneFrom(other);
	}	
	
	public char get(long i) {
		if (i>size())
			throw new ArrayIndexOutOfBoundsException(""+i+" > "+size());
		if (i<blocksize)
			return first_block[(int)i];
		int base = (int)(i / blocksize);
		int offset = (int)(i % blocksize);
		return linked_arrays.get(base-1)[offset];
	}
	
	public void set(long i, char value) {
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
	
	public long add(char value) {
		increaseSizeBy(1);
		set(length-1, value);
		return length-1;
	}
	
	
	public String toString(char[] part) {
		return Arrays.toString(part);
	}

	
	public char[] makeBlock() {
		return new char[blocksize];
	}
	
	public void sort() {
		new Sorter().sort();
	}
	
	public class Sorter extends QuickSorter<LinkedCharArray, Character> {

		public Sorter() {
			super(LinkedCharArray.this);
		}

		@Override
		protected Character getElement(LinkedCharArray container, long index) {
			return container.get(index);
		}

		@Override
		protected long size(LinkedCharArray container) {
			return container.size();
		}

		@Override
		protected void swapElements(LinkedCharArray container, long i1, long i2) {
			Character k = container.get(i1);
			container.set(i1, container.get(i2));
			container.set(i2, k);
		}

		@Override
		protected int compareElements(Character e1, Character e2) {
			return e1.compareTo(e2);
		}

	}	
	
}
