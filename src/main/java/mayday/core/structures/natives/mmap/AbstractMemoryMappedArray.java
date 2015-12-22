package mayday.core.structures.natives.mmap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * the first <minimalsize> elements will always be held in memory.
 * If more elements are needed, memory mapped files will be created,
 * each with a maximum size of BUFFERSIZE;
 * 
 * 110215 - code now groups buffers into fewer files while limiting
 * individual file sizes to <2GB to work on systems like fat32
 * 
 * @author battke
 *
 * @param <ArrayType> 
 */
public abstract class AbstractMemoryMappedArray<ArrayType> { //implements DumpableStructure {
	 
	/* The buffer size should be a multiple of megabytes, especially it should be a multiple
	 * of eight, so that native types like LONGs with a width of 8 bytes can fit into the 
	 * buffer without causing problems at the borders between buffers.
	 * The maximum of this number is 2GB (Integer.MAX_VALUE) but this would results in 
	 * allocating HUGE files even for relatively small arrays. 
	 * I set this to 10MB so that large arrays don't require too many buffers while still
	 * growing with a reasonable increment size.  
	 */
	protected final static int DEFAULT_BUFFER_SIZE = 10*1024*1024;//in blocks of 10M on disk
	protected final static long MAX_SIZE_PER_FILE = Integer.MAX_VALUE; //no larger than 2GB to work on Fat32 and such

	protected static int global_id=0;
	protected static boolean use_mmap = true;
	protected static String pathParent;
	
	
	protected ArrayType first_block; // for small lists, the first elements reside in memory
	protected List<ByteBuffer> linked_buffers;
	protected int effectiveBufferSize; // number of elements per buffer for the given BUFFERSIZE
	protected int minimalsize;
	protected long length;
	protected long lastAddedBufferStart;

	private int id = ++global_id;
	protected List<File> bufferFiles; // usually fewer files than blocks
	private int BUFFERSIZE = DEFAULT_BUFFER_SIZE; 

	static {
		use_mmap = ScratchDiskPreferences.useMMap();
		if (use_mmap) {
			try {			
				File ppathParent = ScratchDiskPreferences.getScratchDir("MAYDAY_mmap_");
				pathParent = ppathParent.getCanonicalPath();
			} catch (IOException e) {
				System.err.println("Problem preparing mmap() directory: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public AbstractMemoryMappedArray(int minimalsize, int bytesPerElement) {
		this.minimalsize = minimalsize;
		first_block = makeBlock(minimalsize);
		updateBufferSize(bytesPerElement);
		linked_buffers = new ArrayList<ByteBuffer>();
		bufferFiles = new ArrayList<File>();
	}
	
	public AbstractMemoryMappedArray(AbstractMemoryMappedArray<ArrayType> other) {
		cloneFrom(other);
	}
	
	protected abstract ArrayType makeBlock(int blocksize);
	protected abstract String toString(ArrayType part);
	protected abstract void copyBuffers(AbstractMemoryMappedArray<ArrayType> other);
	
	public void cloneFrom(AbstractMemoryMappedArray<ArrayType> other) {
		minimalsize = other.minimalsize;
		
		first_block = makeBlock(minimalsize);
		System.arraycopy(other.first_block, 0, first_block, 0, minimalsize);
		
		ensureSize(other.size());

		copyBuffers(other);
	}
	
	public long size() {
		return length;
	}
	
	public void removeLast() {
		removeLast(1);		
	}

	public void removeLast(int n) {
		trimToSize(length-n);		
	}
	

	public String toString() {
		String s = "{";
		s+=toString(first_block);
		if (linked_buffers.size()>0)
			s+="...["+linked_buffers.size()+" buffers]";
		s+="}";
		return s;
	}
	
	protected long effectiveSize() {
		return minimalsize+linked_buffers.size()*effectiveBufferSize;
	}

	public void increaseSizeBy(long len) {		
		length+=len;		
		while(effectiveSize() < length) {
			linked_buffers.add(makeBuffer());
		}
	}
	
	public void trimToSize(long size) {
		length=size;
		// We could do more, but we won't
	}
	
	public void ensureSize(long size) {
		long missing = size-length;
		if (missing>0)
			increaseSizeBy(missing);
	}
	
	@Override
	public void finalize() {
		for (File f : bufferFiles)
			f.delete();
	}
	
	protected ByteBuffer makeBufferMMAP() {
		try {

			File bufferFile;
			lastAddedBufferStart+=BUFFERSIZE;

			// first find out if we can add another buffer in the file last allocated
			if (lastAddedBufferStart+BUFFERSIZE < MAX_SIZE_PER_FILE && bufferFiles.size()>0) {
				// we can append this buffer to the current file
				bufferFile = bufferFiles.get(bufferFiles.size()-1);
			} else { 
				// we need a new buffer file
				lastAddedBufferStart = 0;
				bufferFile = new File(pathParent, first_block.getClass().getSimpleName()+"_"+id+"_["+bufferFiles.size()+"]");
				bufferFile.deleteOnExit();
				bufferFiles.add(bufferFile);
			} 
			
			RandomAccessFile raf = new RandomAccessFile(bufferFile, "rw");
			FileChannel out = raf.getChannel();
			MappedByteBuffer mbb = out.map(FileChannel.MapMode.READ_WRITE, lastAddedBufferStart, BUFFERSIZE);
			out.close();
			return mbb;
		} catch (Throwable t) {
			return null;
		}
	}
	
	protected ByteBuffer makeBufferMemory() {
		 return ByteBuffer.allocate(BUFFERSIZE);
	}
	
	protected void updateBufferSize(int bytesPerElement) {
		if (!use_mmap && (linked_buffers==null || linked_buffers.size()==0)) { //don't change if already some buffers present
			BUFFERSIZE = minimalsize*bytesPerElement;
			effectiveBufferSize = minimalsize;
		} else { //mmap or late switching:
			effectiveBufferSize = BUFFERSIZE/bytesPerElement;
			BUFFERSIZE = effectiveBufferSize*bytesPerElement; // remove rounding problems;
		}
	}
	
	protected ByteBuffer makeBuffer() {
		ByteBuffer bb = null; 
		
		if (use_mmap) {
			bb = makeBufferMMAP();
		}
		
		if (bb==null) {
			// could not or should not mmap()
			if (use_mmap) {
				System.err.println("Could not use mmap() -- switching to memory buffers.");
				use_mmap = false;
			}
			updateBufferSize(BUFFERSIZE/effectiveBufferSize); 
			bb = makeBufferMemory(); //OOM here is beyond help
		}

		return bb;
	}
	
	protected void setBufferSize(int bufferSize) {
		if (effectiveBufferSize==0)
			return;
		int bytesPerElement = BUFFERSIZE/effectiveBufferSize;
		BUFFERSIZE = bufferSize;
		updateBufferSize(bytesPerElement);
	}
	
//	protected abstract void writeDump(DataOutputStream dos, ArrayType firstblock, int length) throws IOException;	
//	protected abstract void readDump(DataInputStream dis, ArrayType firstblock, int length) throws IOException;
//	protected abstract void writeInternals(DataOutputStream dos) throws IOException;
//	protected abstract void readInternals(DataInputStream dis) throws IOException;
//	
//	protected void dumpBuffer(DataOutputStream dos, ByteBuffer bb, int amount) throws IOException {
//		byte[] block = new byte[1024*1024];
//		long remaining = amount;
//		bb.position(0);
//		while (remaining > 0) {
//			int toWrite = (int)Math.min(remaining, block.length);
//			bb.get(block,0,toWrite);
//			dos.write(block, 0, toWrite);
//			remaining -= toWrite;
//		}
//	}	
//	
//	protected void readBuffer(DataInputStream dis, ByteBuffer bb, int amount) throws IOException {
//		byte[] block = new byte[1024*1024];
//		long remaining = amount;
//		bb.position(0);
//		while (remaining > 0) {
//			int toRead = (int)Math.min(remaining, block.length);
//			dis.read(block, 0, toRead);
//			bb.put(block,0,toRead);
//			remaining -= toRead;
//		}
//	}
//	
//	public void writeDump(DataOutputStream dos) throws IOException {
//		writeInternals(dos);
//		dos.writeLong(length);
//		if (length==0)
//			return;
//		dos.writeInt(minimalsize);
//		writeDump(dos, first_block, (int)Math.min(minimalsize, length));
//		// write linked blocks
//		long remaining = size()-minimalsize;
//		double factor = BUFFERSIZE/effectiveBufferSize;
//		remaining *= factor; 
//		dos.writeInt(BUFFERSIZE);
//		dos.writeInt(effectiveBufferSize);
//		dos.writeInt(linked_buffers.size());		
//		for (ByteBuffer bb : linked_buffers) {
//			int realBufSize = (int) Math.min(BUFFERSIZE, remaining);
//			dos.writeInt(realBufSize);
//			dumpBuffer(dos, bb, realBufSize);
//			remaining -= realBufSize;
//		}
//	}
//	
//	public void readDump(DataInputStream dis) throws IOException {
//		if (size()>0)
//			throw new RuntimeException("Cannot deserialize a dump into a nonempty array");
//		readInternals(dis);
//		length = dis.readLong();
//		if (length==0) {
//			first_block = null;
//			return;
//		}
//		minimalsize = dis.readInt();
//		first_block = makeBlock(minimalsize);
//		readDump(dis, first_block, (int)Math.min(minimalsize, length));
//		BUFFERSIZE = dis.readInt();
//		effectiveBufferSize = dis.readInt();
//		int bufcount = dis.readInt();		
//		for (int i=0; i!=bufcount; ++i) {
//			ByteBuffer bb = makeBuffer();
//			int realBufSize = dis.readInt();
//			readBuffer(dis, bb, realBufSize);
//			linked_buffers.add(bb);
//		}
//	}

}
