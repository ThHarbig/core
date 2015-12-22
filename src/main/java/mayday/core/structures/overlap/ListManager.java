package mayday.core.structures.overlap;

import java.util.Iterator;
import java.util.LinkedList;

import mayday.core.structures.CompactableStructure;
import mayday.core.structures.natives.MultiArray;

public class ListManager implements CompactableStructure {//, DumpableStructure {

	protected MultiArray overlaps;
	protected MultiArray ids;
	
//	protected ArrayList<Node> allNodes = new ArrayList<Node>();
	
	public ListManager(int expectedCoverage) {
		overlaps = new MultiArray(expectedCoverage);
		ids = new MultiArray(expectedCoverage);
	}
	
	public Iterator<Long> getIDs(long x) {
		return ids.getList(x);
	}
	
	public Iterator<Long> getOverlaps(long x) {
		return overlaps.getList(x);
	}
	
	public long newLists() {
		overlaps.createList();
		return ids.createList();
	}
	
	public void add(long x, long id, long overlap) {
		overlaps.add(x, overlap);
		ids.add(x, id);
	}

//	public void addNode(Node n) {
//		allNodes.add(n);
//	}
	
//	public ArrayList<Node> getNodes() {
//		return allNodes;
//	}
	
	@Override
	public void compact() {
		ids.compact();
		overlaps.compact();
	}

//	@Override
//	public void readDump(DataInputStream dis) throws IOException {
//		ids.readDump(dis);
//		overlaps.readDump(dis);
//	}
//
//	@Override
//	public void writeDump(DataOutputStream dos) throws IOException {
//		ids.writeDump(dos);
//		overlaps.writeDump(dos);
//	}

	@Override
	public String getCompactionInitializer() {
		return ids.getCompactionInitializer()+"\t"+overlaps.getCompactionInitializer();
	}

	@Override
	public void setCompaction(LinkedList<String> compactionInitializer) {
		ids.setCompaction(compactionInitializer);
		overlaps.setCompaction(compactionInitializer);		
	}
		
}
