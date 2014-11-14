package mayday.core.structures.natives;

import java.util.Iterator;
import java.util.LinkedList;

import mayday.core.structures.CompactableStructure;
import mayday.core.structures.natives.mmap.MMLongArray;


public class MultiArray implements CompactableStructure { //, DumpableStructure {
	
	private MMLongArray content;	//stores the blocks of the "linkedlists" as [ <link_to_previous_block | -1>, entry0, entry1, ... entryn ] 
	private MMLongArray header;  	//stores one header for each "linkedlist" as [ <last_added_block_start | -1>, <next_insert_position_in_content>, <list_size> ]
	
	private long maxContent=-1, contentSize=0;
	
	private int blocksize;
	
	private final static long NO_FURTHER_BLOCK = -1;
	// all block identifiers are shifted using +1 so that no_further_block is zero (to avoid negative entries for compaction)
	
	public MultiArray(int blocksize) {
		 this.blocksize=blocksize+1;
		 // every block has one entry as header (next block)
		 content = new MMLongArray(1000*this.blocksize);
		 header = new MMLongArray(1000*3);
	}
	
	public void add(long listID, long value) {
		long firstblock = header.get(listID)-1; // block index shift, see NO_FURTHER_BLOCK
		long insertpos = header.get(listID+1);
		long size = header.get(listID+2);
		if (size % (blocksize-1) == 0) {
			// needs new block
			newBlock(firstblock, listID);
		} else {
			header.set(listID+1, insertpos+1);
		}
		header.set(listID+2, size+1);

		insertpos = header.get(listID+1);
		
		maxContent=maxContent>value?maxContent:value;
		
		content.set(insertpos, value);
	}
	
	protected void newBlock(long oldBlock, long headerBlock) {
		long newblockstart = content.size();
		content.increaseSizeBy(blocksize);
		contentSize = content.size();
		content.set(newblockstart, oldBlock+1); //chain back-to-front, block index shift, see NO_FURTHER_BLOCK
		header.set(headerBlock, newblockstart+1); // block index shift, see NO_FURTHER_BLOCK
		header.set(headerBlock+1, newblockstart+1);		
	}
	
	public long createList() {
		header.add(NO_FURTHER_BLOCK+1); // no first block yet  // block index shift, see NO_FURTHER_BLOCK
		header.add(0); // no insert position;
		header.add(0); // no size yet
		return header.size()-3;
	}
	
	public ListIterator getList(long listID) {
		return new ListIterator(listID);
	}
	
	public long getHighestListIdentifier() {
		return header.size()-3;
	}
	
	public class ListIterator implements Iterator<Long>, Iterable<Long> {

		protected long blockStart;
		protected int blockOffset;
		
		public ListIterator(long listID) {
			blockStart = header.get(listID)-1; // block index shift, see NO_FURTHER_BLOCK 
			blockOffset = (int)(header.get(listID+1) - blockStart); // start at last inserted value 
		}
		
		public boolean hasNext() {
			return blockStart > -1;
		}

		public Long next() {
			long ret = content.get(blockStart+blockOffset);
			--blockOffset;
			if (blockOffset==0) { // reached start of block, go to next block
				blockStart = content.get(blockStart)-1; // block index shift, see NO_FURTHER_BLOCK
				blockOffset = blocksize - 1;
			}
			return ret;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public Iterator<Long> iterator() {
			return this;
		}
		
	}

	@Override
	public void compact() {
		content = content.changeStorageRange(Math.max(contentSize,maxContent),true);
		header = header.changeStorageRange(contentSize,true); // no block pointer can be larger than this
	}

	@Override
	public String getCompactionInitializer() {
		return maxContent+"\t"+contentSize;
	}

	@Override
	public void setCompaction(LinkedList<String> compactionInitializer) {
		maxContent = Long.parseLong(compactionInitializer.removeFirst());
		contentSize = Long.parseLong(compactionInitializer.removeFirst());
		compact();		
	}

//	@Override
//	public void readDump(DataInputStream dis) throws IOException {
//		blocksize = dis.readInt();
//		content.readDump(dis);
//		header.readDump(dis);
//	}
//
//	@Override
//	public void writeDump(DataOutputStream dos) throws IOException {
//		dos.writeInt(blocksize);
//		content.writeDump(dos);
//		header.writeDump(dos);
//	}
	
//	public static void main(String args[]) {
//		MultiArray bla = new MultiArray(2);
//		long list1 = bla.createList();
//		long list2 = bla.createList();
//		bla.add(list1, 0);
//		bla.add(list2, 1);
//		bla.add(list1, 2);
//		bla.add(list2, 3);
//		bla.add(list1, 4);
//		bla.add(list2, 5);
//		bla.add(list1, 6);
//		bla.add(list2, 7);
//		for (Long l : bla.getList(list1))
//			System.out.println(l);
//		for (Long l : bla.getList(list2))
//			System.out.println(l);
//
//	}
	
}
