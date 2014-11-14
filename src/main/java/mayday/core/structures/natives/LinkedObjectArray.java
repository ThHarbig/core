package mayday.core.structures.natives;

import java.util.Iterator;

import mayday.core.structures.natives.inmemory.AbstractLinkedArray;

public class LinkedObjectArray<V> extends AbstractLinkedArray<V[]> implements Iterable<V>{
	 
	public LinkedObjectArray(int blocksize) {
		super(blocksize);
	}
	
	public LinkedObjectArray(LinkedObjectArray<V> other) {
		super(0);
		cloneFrom(other);
	}
	
	public V get(long i) {
		if (i>size())
			throw new ArrayIndexOutOfBoundsException(""+i+" > "+size());
		if (i<blocksize)
			return first_block[(int)i];
		int base = (int)(i / blocksize);
		int offset = (int)(i % blocksize);
		return linked_arrays.get(base-1)[offset];	
	}
	
	public void set(long i, V value) {
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
	
	public long add(V value) {
		increaseSizeBy(1);
		set(length-1, value);
		return length-1;
	}
	
	public Iterator<V> iterator() {
		return new Iterator<V>() {
			protected long next=0;

			public boolean hasNext() {
				return next<size();
			}

			public V next() {
				return get(next++);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}			
		};
	}	
}
