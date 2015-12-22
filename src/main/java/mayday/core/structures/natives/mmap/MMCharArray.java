package mayday.core.structures.natives.mmap;

import java.util.Arrays;

import mayday.core.structures.natives.QuickSorter;


public class MMCharArray extends AbstractMemoryMappedArray<char[]>{
	
	protected boolean wideChars;
	protected int shift;
	
	public MMCharArray(int minsize, boolean wideChars) {
		super(minsize, wideChars?2:1);
		this.wideChars = wideChars;
		this.shift = wideChars?1:0;
	}
	
	public MMCharArray(int minsize) {
		this(minsize, true);
	}
	
	public MMCharArray(MMCharArray other) {
		super(other);
		wideChars = other.wideChars;
		this.shift = other.shift;
	}	
	
	@Override
	protected void copyBuffers(AbstractMemoryMappedArray<char[]> other) {
		MMCharArray lla = (MMCharArray)other;
		for (long i = minimalsize; i!=length; ++i)
			set(i, lla.get(i));
	}
	
	public char get(long i) {
		if (i>size())
			throw new ArrayIndexOutOfBoundsException(""+i+" > "+size());
		if (i<minimalsize)
			return first_block[(int)i];
		i-=minimalsize;
		int base = (int)(i / effectiveBufferSize);
		int offset = (int)(i % effectiveBufferSize);
		if (wideChars)
			return linked_buffers.get(base).getChar(offset<<shift);
		else
			return (char)linked_buffers.get(base).get(offset);
	}
	
	public void set(long i, char value) {
		if (i>size())
			throw new  ArrayIndexOutOfBoundsException(""+i+" > "+size());
		if (i<minimalsize)
			first_block[(int)i]=value;
		else {
			i-=minimalsize; 
			int base = (int)(i / effectiveBufferSize);
			int offset = (int)(i % effectiveBufferSize);
			if (wideChars)
				linked_buffers.get(base).putChar(offset<<shift, value);
			else
				linked_buffers.get(base).put(offset, (byte)value);
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

	
	public char[] makeBlock(int size) {
		return new char[size];
	}
	
	public void sort() {
		new Sorter().sort();
	}
	
	public class Sorter extends QuickSorter<MMCharArray, Character> {

		public Sorter() {
			super(MMCharArray.this);
		}

		@Override
		protected Character getElement(MMCharArray container, long index) {
			return container.get(index);
		}

		@Override
		protected long size(MMCharArray container) {
			return container.size();
		}

		@Override
		protected void swapElements(MMCharArray container, long i1, long i2) {
			Character k = container.get(i1);
			container.set(i1, container.get(i2));
			container.set(i2, k);
		}

		@Override
		protected int compareElements(Character e1, Character e2) {
			return e1.compareTo(e2);
		}

	}	
	
	
//	@Override
//	protected void writeDump(DataOutputStream dos, char[] firstblock, int length) throws IOException {
//		for (int i=0; i!=length; ++i)
//			dos.writeChar(firstblock[i]);
//	}
//	
//	@Override
//	protected void readDump(DataInputStream dis, char[] firstblock, int length) throws IOException {
//		for (int i=0; i!=length; ++i)
//			firstblock[i] = dis.readChar();
//	}
//
//	@Override
//	protected void readInternals(DataInputStream dis) throws IOException {
//		wideChars = dis.readBoolean(); 
//		shift = wideChars?1:0;
//	}
//
//	@Override
//	protected void writeInternals(DataOutputStream dos) throws IOException {
//		dos.writeBoolean(wideChars);
//	}
	
	public long binarySearch(char key) {
		long low = 0;
		long high = size()-1;

		while (low <= high) {
			long mid = (low + high) >>> 1;
			Character midVal = get(mid);
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
