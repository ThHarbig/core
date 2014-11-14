package mayday.genetics.advanced;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import mayday.core.structures.CompactableStructure;
import mayday.core.structures.natives.mmap.MMLongArray;
import mayday.core.structures.overlap.OverlapArrayLong;

public class ChromosomeArrayLong implements CompactableStructure { //, DumpableStructure {
	
	public final static int BLOCKSIZE = 10000; // the number of elements for each block in the starts, ends array
	
//	protected int expectedCoverage;
	
	// 4 
	protected MMLongArray starts, ends;
	
	protected long maxStart, maxEnd; // for compacting
	protected long minStart, minEnd; // for compacting
	
	// 1 and 2
	protected OverlapArrayLong genome;
	
	
	/** Creates a new GenomeArray object with expected nonempty coverage of 3 */
	public ChromosomeArrayLong() {		
		this(3);
	}
	
	/** Creates a new GenomeArray object. 
	 * @param expectedCoverage - the expected number of objects overlapping genomic positions calculated only for those positions
	 * that are actually overlapped by something, excluding empty positions. Will always be an integer >=1. 
	 */
	public ChromosomeArrayLong(int expectedCoverage) {
//		this.expectedCoverage=expectedCoverage;
		genome = new OverlapArrayLong(expectedCoverage);
		starts = new MMLongArray(BLOCKSIZE);
		ends = new MMLongArray(BLOCKSIZE);
//		System.out.println("CAL starts: "+starts.id);
//		System.out.println("CAL ends  : "+ends.id);
//		System.out.println("CAL g ov c: "+genome.lm.overlaps.content.id);
//		System.out.println("CAL g ov h: "+genome.lm.overlaps.header.id);
//		System.out.println("CAL g id c: "+genome.lm.ids.content.id);
//		System.out.println("CAL g id h: "+genome.lm.ids.header.id);
	}

	
	public final long put(long position) {
		return put(position,position);
	}
  	
  	public long put(long startposition, long endposition) {
  		if (endposition<startposition) {
  			long tmp = endposition;
  			endposition=startposition;
  			startposition = tmp;
  		}
  		
  		long objectKey = starts.add(startposition);
  		ends.add(endposition);
  		
  		maxStart = maxStart>startposition?maxStart:startposition;
  		maxEnd = maxEnd>endposition?maxEnd:endposition;
  		minStart = minStart<startposition?minStart:startposition;
  		minEnd = minEnd<endposition?minEnd:endposition;
  		
  		genome.put(startposition, endposition, objectKey);
  		
  		return objectKey;
  	}
  
	public Set<Long> getIDs(long position) {
		return genome.get(position, position, null);
	}
	
	public Set<Long> getIDs(long from, long to) {
		return genome.get(from, to, null);
	}
	
	public Set<Long> getIDs(long startposition, long endposition, Set<Long> ret) {
		return genome.get(startposition, endposition, ret);
  	}
	
	public long getStart(long id) {
		return starts.get(id);
	}
	
	public long getEnd(long id) {
		return ends.get(id);
	}

	public MMLongArray getStarts() {
		return starts;
	}
	
	public MMLongArray getEnds() {
		return ends;
	}

	public boolean isCovered(long position) {
		return genome.isCovered(position);
	}

//	public void clear() {
//		genome = new OverlapArrayLong(expectedCoverage);
//		starts = new MMLongArray(BLOCKSIZE);
//		ends = new MMLongArray(BLOCKSIZE);
//	}
	
	public boolean containsKey(long key) {
		return getIDs(key).size()>0;
	}
	
	public long size() {
		return starts.size();
	}
	
	public boolean isEmpty() {		
		return size()==0;
	}
	
	public Iterator<Long> coveredPositionsIterator(long startposition) {
		return genome.coverageIterator(startposition);
	}

	@Override
	public void compact() {
		genome.compact();
		if (minStart>=0)  // can be <0 in circular genomes 131112fb
			starts = starts.changeStorageRange(maxStart, true);
		if (minEnd>=0)  // can be <0 in circular genomes 131112fb
			ends = ends.changeStorageRange(maxEnd,true);				
	}

//	@Override
//	public void readDump(DataInputStream dis) throws IOException {
//		maxStart = dis.readLong();
//		maxEnd = dis.readLong();
//		starts.readDump(dis);
//		ends.readDump(dis);		
//		genome.readDump(dis);
//	}
//
//	@Override
//	public void writeDump(DataOutputStream dos) throws IOException {
//		dos.writeLong(maxStart);
//		dos.writeLong(maxEnd);
//		starts.writeDump(dos);
//		ends.writeDump(dos);
//		genome.writeDump(dos);
//	}

	
	
	@Override
	public String getCompactionInitializer() {
		return maxStart+"\t"+maxEnd+"\t"+genome.getCompactionInitializer();
	}

	@Override
	public void setCompaction(LinkedList<String> compactionInitializer) {
		maxStart = Long.parseLong(compactionInitializer.removeFirst());
		maxEnd = Long.parseLong(compactionInitializer.removeFirst());
		genome.setCompaction(compactionInitializer);
		compact();		
	}
	
}

