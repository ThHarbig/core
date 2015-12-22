package mayday.core.structures.natives.mmap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import mayday.core.structures.natives.QuickSorter;

public class MMByteArray extends AbstractMemoryMappedArray<byte[]>{

	public MMByteArray(int minsize) {
		super(minsize, 1);
	}

	public MMByteArray(MMByteArray other) {
		super(other);
	}
	
	@Override
	protected void copyBuffers(AbstractMemoryMappedArray<byte[]> other) {
		MMByteArray lla = (MMByteArray)other;
		for (long i = minimalsize; i!=length; ++i)
			set(i, lla.get(i));
	}
	
	public byte[] makeBlock(int size) {
		return new byte[size];
	}

	public byte get(long i) {
		if (i>size())
			throw new ArrayIndexOutOfBoundsException(""+i+" > "+size());
		if (i<minimalsize)
			return first_block[(int)i];
		i-=minimalsize;
		int base = (int)(i / effectiveBufferSize);
		int offset = (int)(i % effectiveBufferSize);
		return linked_buffers.get(base).get(offset);
	}

	public void set(long i, byte value) {
		if (i>size())
			throw new  ArrayIndexOutOfBoundsException(""+i+" > "+size());
		if (i<minimalsize)
			first_block[(int)i]=value;
		else {
			i-=minimalsize;
			int base = (int)(i / effectiveBufferSize);
			int offset = (int)(i % effectiveBufferSize);
			linked_buffers.get(base).put(offset, value);
		}
	}

	public long add(byte value) { 
		increaseSizeBy(1);
		set(length-1, value);
		return length-1;
	}

	@Override
	protected String toString(byte[] part) {
		return Arrays.toString(part);
	}
	
	
	public void sort() {
		new Sorter().sort();
	}

	public Iterator<Byte> iterator() {
		return new Iterator<Byte>() {
			protected long next=0;

			public boolean hasNext() {
				return next<size();
			}

			public Byte next() {
				return get(next++);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}			
		};
	}
	
	public Iterator<Byte> reverseIterator() {
		return new Iterator<Byte>() {
			protected long next=size()-1;

			public boolean hasNext() {
				return next>=0;
			}

			public Byte next() {
				return get(next--);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}			
		};
	}
	
	protected void writeDump(DataOutputStream dos, byte[] firstblock, int length) throws IOException {
		dos.write(firstblock, 0, length);
	}
	
	
	protected void readDump(DataInputStream dis, byte[] firstblock, int length) throws IOException {
		dis.read(firstblock, 0, length);
	}

//	@Override
//	protected void readInternals(DataInputStream dis) throws IOException {
//		//nothing to do
//	}
//
//	@Override
//	protected void writeInternals(DataOutputStream dos) throws IOException {
//		// nothing to do
//	}
	
	public class Sorter extends QuickSorter<MMByteArray, Byte> {

		public Sorter() {
			super(MMByteArray.this);
		}

		@Override
		protected Byte getElement(MMByteArray container, long index) {
			return container.get(index);
		}

		@Override
		protected long size(MMByteArray container) {
			return container.size();
		}

		@Override
		protected void swapElements(MMByteArray container, long i1, long i2) {
			byte k = container.get(i1);
			container.set(i1, container.get(i2));
			container.set(i2, k);
		}

		@Override
		protected int compareElements(Byte e1, Byte e2) {
			return e1.compareTo(e2);
		}

	}

	public long binarySearch(Byte key) {
		long low = 0;
		long high = size()-1;

		while (low <= high) {
			long mid = (low + high) >>> 1;
			Byte midVal = get(mid);
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
