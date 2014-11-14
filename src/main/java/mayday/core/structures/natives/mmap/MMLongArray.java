package mayday.core.structures.natives.mmap;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;

import mayday.core.structures.natives.QuickSorter;

public class MMLongArray extends AbstractMemoryMappedArray<long[]>{

	protected int bytesPerLong;
	
	/** construct a memory mapped long array with a predefined number of bytes per long
	 * by using <8 bytes per long, the buffer files will become smaller while the value range
	 * of the longs will be truncated 
	 * Besides using less disk space, this also improves speed since the OS does not need
	 * to move so many pages when accessing the data.
	 * CAUTION: if using <8 bytes/long, negative values can not be stored! 
	 * @param minsize
	 * @param bytesPerLong
	 */
	public MMLongArray(int minsize, int bytesPerLong) {
		super(minsize, bytesPerLong);
		if (bytesPerLong>8 || bytesPerLong<1)
			throw new IllegalArgumentException("Long value byte count has to be between 1 and 8, including.");
		this.bytesPerLong = bytesPerLong;
		double factor = (double)bytesPerLong/8.0;
		setBufferSize((int)(DEFAULT_BUFFER_SIZE*factor)); // scale down
	}
	
	public MMLongArray(int minsize) {
		this(minsize, 8);
	}

	public MMLongArray(MMLongArray other) {
		super(other);
	}	

	@Override
	protected void copyBuffers(AbstractMemoryMappedArray<long[]> other) {
		MMLongArray lla = (MMLongArray)other;
		for (long i = minimalsize; i!=length; ++i)
			set(i, lla.get(i));
	}

	@Override
	protected long[] makeBlock(int blocksize) {
		return new long[blocksize];
	}

	
	public long get(long i) {
		if (i>size() || i<0)
			throw new ArrayIndexOutOfBoundsException(""+i+" (size "+size()+")");
		if (i<minimalsize)
			return first_block[(int)i];
		i-=minimalsize;
		int base = (int)(i / effectiveBufferSize);
		int offset = (int)(i % effectiveBufferSize);
		ByteBuffer bb = linked_buffers.get(base);
		
		offset*=bytesPerLong; // the long starts here		
		byte b1=0,b2=0,b3=0,b4=0,b5=0,b6=0,b7=0,b8=0;		
		switch(bytesPerLong) { // fall-though is required on each case!
			case 8: b8 = bb.get(offset++);
			case 7: b7 = bb.get(offset++);
			case 6: b6 = bb.get(offset++);
			case 5: b5 = bb.get(offset++);
			case 4: b4 = bb.get(offset++);
			case 3: b3 = bb.get(offset++);
			case 2: b2 = bb.get(offset++);
			case 1: b1 = bb.get(offset++);
		}
		return makeLong(b8,b7,b6,b5,b4,b3,b2,b1);
	}

	/**
	 * set the value at a given position. 
	 * @param i the position to change
	 * @param value the new value to put at position i
	 * @throws IllegalArgumentException if value<0 and using less than 8 bytes per long (@see MMLongArray(int,int))
	 */
	public void set(long i, long value) {
		if (i>size())
			throw new  ArrayIndexOutOfBoundsException(""+i+" > "+size());
		if (i<minimalsize)
			first_block[(int)i]=value;
		else {
			i-=minimalsize;
			int base = (int)(i / effectiveBufferSize);
			int offset = (int)(i % effectiveBufferSize);
			ByteBuffer bb = linked_buffers.get(base);
			
			if (value<0 && bytesPerLong<8)
				throw new IllegalArgumentException("MMLongArray can not story negative values if using less than 8 bytes/long.");
			
			offset*=bytesPerLong; // the long starts here			
			switch(bytesPerLong) { // fall-though is required on each case!
				case 8: bb.put(offset++, long7(value));
				case 7: bb.put(offset++, long6(value));
				case 6: bb.put(offset++, long5(value));
				case 5: bb.put(offset++, long4(value));
				case 4: bb.put(offset++, long3(value));
				case 3: bb.put(offset++, long2(value));
				case 2: bb.put(offset++, long1(value));
				case 1: bb.put(offset++, long0(value));
			}
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


	public class Sorter extends QuickSorter<MMLongArray, Long> {

		public Sorter() {
			super(MMLongArray.this);
		}

		@Override
		protected Long getElement(MMLongArray container, long index) {
			return container.get(index);
		}

		@Override
		protected long size(MMLongArray container) {
			return container.size();
		}

		@Override
		protected void swapElements(MMLongArray container, long i1, long i2) {
			long k = container.get(i1);
			container.set(i1, container.get(i2));
			container.set(i2, k);
		}

		@Override
		protected int compareElements(Long e1, Long e2) {
			return e1.compareTo(e2);
		}

	}
	
//	@Override
//	protected void writeDump(DataOutputStream dos, long[] firstblock, int length) throws IOException {
//		for (int i=0; i!=length; ++i)
//			dos.writeLong(firstblock[i]);
//	}
//	
//	@Override
//	protected void readDump(DataInputStream dis, long[] firstblock, int length) throws IOException {
//		for (int i=0; i!=length; ++i)
//			firstblock[i] = dis.readLong();
//	}	
//
//	@Override
//	protected void readInternals(DataInputStream dis) throws IOException {
//		bytesPerLong = dis.readInt();
//		
//	}
//
//	@Override
//	protected void writeInternals(DataOutputStream dos) throws IOException {
//		 dos.writeInt(bytesPerLong);		
//	}
	
	/** reduce or increase the number of bytes used for each element in this array
	 * CAUTION: if using <8 bytes/long, negative values can not be stored! 
	 * @param newBytesPerLong number of bytes to use for each long in the new array
	 * @param finalizeOld: if true, the original instance of this array is finalized and removed from disk
	 * @returns a copy of this array with the defined value range, or this array itself if 
	 * no change was needed 
	 * @throws IllegalArgumentException if this array contains negative values and 
	 * newBytesPerLong<8.
	 */
	public MMLongArray changeStorageBytes(int newBytesPerLong, boolean finalizeOld) {
		if (newBytesPerLong==bytesPerLong)
			return this;
		MMLongArray other = new MMLongArray(this.minimalsize, newBytesPerLong);
		// use a smaller buffer size if the current array does not have more than 1 buffer (scale down)
		if (newBytesPerLong<bytesPerLong && this.bufferFiles.size()<2) {
			double factor = (double)newBytesPerLong/(double)bytesPerLong;
			other.setBufferSize((int)(DEFAULT_BUFFER_SIZE*factor)); // scale down
		}
		long sz = size();
		for (long l=0; l!=sz; ++l)
			other.add(this.get(l));
		if (finalizeOld)
			this.finalize();
//		System.out.println("COMPACTING: "+id+" to "+other.id);
		return other;
	}
	
	
	/** reduce or increase the number of bytes used for each element in this array, based on the 
	 * maximal value specified.
	 * CAUTION: if using <8 bytes/long, negative values can not be stored! 
	 * @param maxmimalValue the maximal Value this array should be able to store
	 * If maximalValue==0, a fake container is returned that "contains" only 0's.
	 * @param finalizeOld: if true, the original instance of this array is finalized and removed from disk
	 * @returns a copy of this array with the defined value range, or this array itself if 
	 * no change was needed 
	 * @throws IllegalArgumentException if this array contains negative values and 
	 * newBytesPerLong<8.
	 */
	public MMLongArray changeStorageRange(long maximalValue, boolean finalizeOld) {
		if (maximalValue==0) {
			final long sz = this.size();
			if (finalizeOld)
				this.finalize();
			return new MMLongArray(0) {
				public long get(long i) {return 0;}
				public void set(long i, long value) { 
					throw new IllegalArgumentException("This is a make-believe container that cannot contain values"); 
				}
				public long add(long value) { 
					throw new IllegalArgumentException("This is a make-believe container that cannot contain values");
				}
				public long size() { return sz; }
			};			
		} 
		int new_bytes = Math.max(1,(int)Math.ceil((Math.log(maximalValue)/Math.log(2))/8.0));
		return changeStorageBytes(new_bytes, finalizeOld);
	}
	
	static private long makeLong(byte b7, byte b6, byte b5, byte b4,
			byte b3, byte b2, byte b1, byte b0)
	{
		return ((((long)b7 & 0xff) << 56) |
				(((long)b6 & 0xff) << 48) |
				(((long)b5 & 0xff) << 40) |
				(((long)b4 & 0xff) << 32) |
				(((long)b3 & 0xff) << 24) |
				(((long)b2 & 0xff) << 16) |
				(((long)b1 & 0xff) <<  8) |
				(((long)b0 & 0xff) <<  0));
	}

    private static byte long7(long x) { return (byte)(x >> 56); }
    private static byte long6(long x) { return (byte)(x >> 48); }
    private static byte long5(long x) { return (byte)(x >> 40); }
    private static byte long4(long x) { return (byte)(x >> 32); }
    private static byte long3(long x) { return (byte)(x >> 24); }
    private static byte long2(long x) { return (byte)(x >> 16); }
    private static byte long1(long x) { return (byte)(x >>  8); }
    private static byte long0(long x) { return (byte)(x >>  0); }

    
    public long binarySearch(long key) {
		long low = 0;
		long high = size()-1;

		while (low <= high) {
			long mid = (low + high) >>> 1;
			Long midVal = get(mid);
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
