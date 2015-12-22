package mayday.core.structures.natives.mmap;

import java.util.LinkedList;

import mayday.core.structures.ByteTools;
import mayday.core.structures.CompactableStructure;

public class MMBooleanArray implements CompactableStructure {

	protected MMByteArray underling;

	protected long maxPos=-1;	
	protected boolean allTrue = true;
	protected boolean allFalse = true;	

	public MMBooleanArray(int minsize) {
		underling = new MMByteArray(minsize);
	}

	public void set(long position, boolean value) {		
		if (underling==null) {
			if (value!=allTrue)
				throw new RuntimeException("New value is not conformant to stored value");
			return;
		}
		long _byte = position / 8;
		int _bit = (int)(position%8);
		underling.ensureSize(_byte+1);
		byte val = underling.get(_byte);
		byte mask;
		if (value) {
			mask = ByteTools.fromTo(_bit, _bit);
		} else {
			byte maskleft = (_bit>0) ? ByteTools.fromTo(0, _bit-1) : 0;
			byte maskright = (_bit<7) ? ByteTools.fromTo(_bit+1, 7) : 0;
			mask = (byte) (maskleft | maskright);
		}
		val = (byte) (
				value?
				val | mask:
				val & mask
				);					
		underling.set(_byte, val);
		maxPos = Math.max(maxPos, position);
		allTrue &= value;
		allFalse &= !value;
	}

	public boolean get(long position) {
		if (underling==null) { // has been compacted
			return allTrue;
		}
		long _byte = position / 8;
		int _bit = (int)(position%8);
		underling.ensureSize(_byte+1);
		byte val = underling.get(_byte);
		byte mask = ByteTools.fromTo(_bit, _bit);
		return (val & mask)!=0;
	}

	public long size() {
		return maxPos+1;
	}

	public void compact() {
		if (underling!=null) {
			if (allTrue || allFalse) {
				underling.finalize();
				underling = null;
			}		
		}
	}

	@Override
	public String getCompactionInitializer() {
		return (underling==null?"0":"1")+"\t"+Boolean.toString(allTrue)+"\t"+Boolean.toString(allFalse);
	}

	@Override
	public void setCompaction(LinkedList<String> compactionInitializer) {
		int i = Integer.parseInt(compactionInitializer.removeFirst());
		if (i==0) {
			underling = null;
		}
		allTrue = Boolean.parseBoolean(compactionInitializer.removeFirst());
		allFalse = Boolean.parseBoolean(compactionInitializer.removeFirst());
	}
	
//	@Override
//	public void readDump(DataInputStream dis) throws IOException {
//		maxPos = dis.readLong();
//		underling.readDump(dis);		
//	}
//
//	@Override
//	public void writeDump(DataOutputStream dos) throws IOException {
//		dos.writeLong(maxPos);
//		underling.writeDump(dos);
//	}

}
