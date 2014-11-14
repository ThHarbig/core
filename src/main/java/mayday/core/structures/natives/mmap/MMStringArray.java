package mayday.core.structures.natives.mmap;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;

import mayday.core.structures.natives.QuickSorter;

/** Stores strings of a fixed maximum length in a mmap buffer. Internally, strings are \0-terminated
 */
public class MMStringArray extends AbstractMemoryMappedArray<String[]>{

	protected int maxStringLength;
	protected boolean cropStrings, cropFront, wideChars;
	protected int SHIFT;
	protected int observedMaxStringLength=0;
	
	/** Creates a new memory mapped string array.
	 * 
	 * @param minsize the minimal number of elements to reserve space for (= the blocksize)
	 * @param maxStringLength the maximal length of each string
	 * @param cropStrings true if overly long strings should be cropped, false to throw exceptions
	 * @param cropFront true if cropping should remove the beginning of the string, false to crop the end.
	 * @param wideChars true to store unicode chars (java default), false to store only 8bit-ASCII
	 */
	public MMStringArray(int minsize, int maxStringLength, boolean cropStrings, boolean cropFront, boolean wideChars) {
		super(minsize, maxStringLength * (wideChars?2:1) );
		this.maxStringLength=maxStringLength;
		this.cropStrings=cropStrings;
		this.cropFront=cropFront;
		this.wideChars=wideChars;
		SHIFT = (wideChars?1:0);
	}

	public MMStringArray(MMStringArray other) {
		super(other);
		maxStringLength = other.maxStringLength;
		this.cropStrings = other.cropStrings;
		this.cropFront = other.cropFront;
		this.wideChars=other.wideChars;
		this.SHIFT=other.SHIFT;
	}	

	@Override
	protected void copyBuffers(AbstractMemoryMappedArray<String[]> other) {
		MMStringArray lla = (MMStringArray)other;
		for (long i = minimalsize; i!=length; ++i)
			set(i, lla.get(i));
	}

	@Override
	protected String[] makeBlock(int blocksize) {
		return new String[blocksize];
	}

	
	public String get(long i) {
		if (i>size())
			throw new ArrayIndexOutOfBoundsException(""+i+" > "+size());
		if (i<minimalsize)
			return first_block[(int)i];
		i-=minimalsize;
		int base = (int)(i / effectiveBufferSize);
		int offset = (int)(i % effectiveBufferSize);
		return getString(linked_buffers.get(base),offset*maxStringLength<<SHIFT);
	}

	public void set(long i, String value) {
		
		if (i>size())
			throw new  ArrayIndexOutOfBoundsException(""+i+" > "+size());
		
		if (value.length()>maxStringLength) {
			if (cropStrings) {
				if (cropFront)
					value = value.substring(value.length()-maxStringLength);
				else
					value = value.substring(0,maxStringLength);
			} else {
				throw new RuntimeException("String too long for container: \n" +
						"\tContainer maximum: "+maxStringLength+"\n" +
						"\tString length: "+value.length()+"\n" +
						"\tString:"+value);
			}
		}
		
		if (value.contains("\0"))
			throw new RuntimeException("String max not contain the null character (\\0)");
		
		observedMaxStringLength = Math.max(observedMaxStringLength, value.length());
		
		if (i<minimalsize)
			first_block[(int)i]=value;
		else {
			i-=minimalsize;
			int base = (int)(i / effectiveBufferSize);
			int offset = (int)(i % effectiveBufferSize);
			putString(linked_buffers.get(base), offset*maxStringLength<<SHIFT, value);
		}
		
	}
	
	protected void putString(ByteBuffer bb, int offset, String value) {
		int len = Math.min(maxStringLength, value.length());
//		int remain = maxStringLength-len;
		if (wideChars) {
			for (int i=0; i!=len; ++i)
				bb.putChar(offset+(i<<SHIFT), value.charAt(i));			
			if (len<maxStringLength)
				bb.putChar(offset+(len<<SHIFT),'\0');
		} else {
			for (int i=0; i!=len; ++i)
				bb.put(offset+i, (byte)value.charAt(i));
			if (len<maxStringLength)
				bb.put(offset+(len<<SHIFT),(byte)0);
		}		
	}
	
	protected String getString(ByteBuffer bb, int offset) {
		StringBuffer sb = new StringBuffer();
		if (wideChars) {
			for (int i=0; i!=maxStringLength; ++i) {
				char c = bb.getChar(offset+(i<<SHIFT));
				if (c=='\0')
					break;
				sb.append(c);
			}
		} else {
			for (int i=0; i!=maxStringLength; ++i) {
				char c = (char)bb.get(offset+i);
				if (c=='\0')
					break;
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public long add(String value) { 
		increaseSizeBy(1);
		set(length-1, value);
		return length-1;
	}

	@Override
	protected String toString(String[] part) {
		return Arrays.toString(part);
	}
	
	
	public void sort() {
		new Sorter().sort();
	}
	
//	protected void writeString(DataOutputStream dos, String s) throws IOException {
//		dos.writeInt(s.length());
//		dos.writeChars(s);
//	}
//	
//	protected String readString(DataInputStream dis) throws IOException {
//		int len = dis.readInt();
//		StringBuffer sb = new StringBuffer();
//		while (sb.length()<len)
//			sb.append(dis.readChar());
//		return sb.toString();
//	}
//	
//	@Override
//	protected void writeDump(DataOutputStream dos, String[] firstblock, int length) throws IOException {
//		for (int i=0; i!=length; ++i)
//			writeString(dos, firstblock[i]);
//			
//	}	
//	
//	@Override
//	protected void readDump(DataInputStream dis, String[] firstblock, int length) throws IOException {
//		for (int i=0; i!=length; ++i)
//			firstblock[i] = readString(dis);
//	}
//
//	@Override
//	protected void readInternals(DataInputStream dis) throws IOException {
//		maxStringLength = dis.readInt();
//		cropStrings = dis.readBoolean();
//		cropFront = dis.readBoolean();
//		wideChars = dis.readBoolean();
//		observedMaxStringLength = dis.readInt();
//		SHIFT = (wideChars?1:0);
//	}
//
//	@Override
//	protected void writeInternals(DataOutputStream dos) throws IOException {
//		dos.writeInt(maxStringLength);
//		dos.writeBoolean(cropStrings);
//		dos.writeBoolean(cropFront);
//		dos.writeBoolean(wideChars);
//		dos.writeInt(observedMaxStringLength);
//	}
	
	/** uses the maximal length of all encountered elements and aligns content
	 * accordingly. Longer elements added after this step WILL BE TRUNCATED.
	 */
	public void compact() {
		// this was a bad idea, because of strings that may span buffer junctions after compacting.
		// it could be redone, but it's probably not worth the time spent debugging it.
//		int newSL = observedMaxStringLength;
//		int oldSL = maxStringLength;
//		
//		if (newSL>=oldSL)
//			return;
//		
//		for (long i=first_block.length; i!=size(); ++i) { // skip memory buffer right away
//			// get string with old length
//			maxStringLength = oldSL;
//			String s = get(i);
//			// put string with new length
//			maxStringLength = newSL;
//			set(i,s);
//		}
//		// maxStringLength is now equal to newSL
//		
//		//drop extra blocks
//		long i=size()-minimalsize;
//		int base = (int)(i / effectiveBufferSize);
//		while (linked_buffers.size()>base+1) {
//			linked_buffers.remove(linked_buffers.size()-1);
//			bufferFiles.remove(bufferFiles.size()-1);  // This is also wrong: what if memory buffers are used?
//		}
		
	}

	public Iterator<String> iterator() {
		return new Iterator<String>() {
			protected long next=0;

			public boolean hasNext() {
				return next<size();
			}

			public String next() {
				return get(next++);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}			
		};
	}
	
	public Iterator<String> reverseIterator() {
		return new Iterator<String>() {
			protected long next=size()-1;

			public boolean hasNext() {
				return next>=0;
			}

			public String next() {
				return get(next--);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}			
		};
	}


	public class Sorter extends QuickSorter<MMStringArray, String> {

		public Sorter() {
			super(MMStringArray.this);
		}

		@Override
		protected String getElement(MMStringArray container, long index) {
			return container.get(index);
		}

		@Override
		protected long size(MMStringArray container) {
			return container.size();
		}

		@Override
		protected void swapElements(MMStringArray container, long i1, long i2) {
			String k = container.get(i1);
			container.set(i1, container.get(i2));
			container.set(i2, k);
		}

		@Override
		protected int compareElements(String e1, String e2) {
			return e1.compareTo(e2);
		}

	}
	
	public long binarySearch(String key) {
		long low = 0;
		long high = size()-1;

		while (low <= high) {
			long mid = (low + high) >>> 1;
			String midVal = get(mid);
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
