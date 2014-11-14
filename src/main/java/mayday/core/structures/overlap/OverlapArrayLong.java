package mayday.core.structures.overlap;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.structures.CompactableStructure;


public class OverlapArrayLong implements Node, CompactableStructure { //, DumpableStructure {

	protected Node levelOne;
	protected long mult=1;
	protected int contentCount=0;
	protected ListManager lm;

	public OverlapArrayLong(int expectedCoverage) {
		lm = new ListManager(expectedCoverage);
		clear();
	}
	
	public void put(long from, long to, long ID) {			
		checkLevels(from);
		checkLevels(to);
		levelOne.put(from, to, ID);
	}
	
	public void put(long position, long ID) {
		put(position, position, ID);
	}	

	@Override
	public Set<Long> get(long from, long to, Set<Long> result) {
		if (result==null)
			result = new TreeSet<Long>();
		if (from<=mult*levelSize)
			levelOne.get(from, to, result);
		return result;
	}
	
	public Set<Long> get(long position, Set<Long> result) {
		return get(position, position, result);
	}
	
	protected void checkLevels(long position) {
		while (position >= mult*levelSize) {
			// add level to the tree at the top
			mult*=levelOne.getActualLevelSize();
			levelOne = new InternalNode(lm, mult, 0, levelOne);
		}
	}
	
	public int size() {
		return contentCount;
	}

	public void clear() {
		levelOne = new LeafNode(lm, 0);
		contentCount = 0;
	}

	public boolean isEmpty() {		
		return contentCount == 0;
	}

	@Override
	public Iterator<Long> coverageIterator() {
		return levelOne.coverageIterator();
	}
	
	public Iterator<Long> coverageIterator(long startposition) {
		return levelOne.coverageIterator(startposition);
	}
	
	@Override
	public int getActualLevelSize() {
		return levelSize;
	}

	@Override
	public long getEnd() {
		return levelOne.getEnd();
	}

	@Override
	public boolean isCovered(long position) {
		return levelOne.isCovered(position);
	}

	@Override
	public void compact() {
		// compact the list manager only
		lm.compact();
	}

//	@Override
//	public void readDump(DataInputStream dis) throws IOException {
//		lm.readDump(dis);
//		HashMap<Integer, Node> nodeid = new HashMap<Integer, Node>();
//		HashMap<Node, int[]> children = new HashMap<Node,int[]>();
//		int nc = dis.readInt();
//		for (int i=0; i!=nc; ++i) {
//			int id = dis.readInt();
//			boolean internal = dis.readBoolean();
//			Node n;
//			if (internal) {
//				long myid = dis.readLong();
//				long multiplier = dis.readLong();
//				long start = dis.readLong();
//				InternalNode in = new InternalNode(lm,multiplier,start);
//				in.myID=myid;
//				int[] lchildren = new int[levelSize];
//				for (int xi=0; xi!=levelSize; ++xi)
//					lchildren[xi] = dis.readInt();
//				children.put(in, lchildren);
//				n = in;
//			} else {
//				long myid = dis.readLong();
//				long start = dis.readLong();
//				LeafNode ln = new LeafNode(lm,start);
//				ln.myID=myid;
//				for (int xi=0; xi!=levelSize; ++xi)
//					ln.content[xi] = dis.readLong();
//				n=ln;
//			}
//			nodeid.put(id, n);
//		}		
//		// link nodes
//		for (Node n : children.keySet()) {
//			InternalNode parent = (InternalNode)n;
//			int[] lchildren = children.get(parent);
//			for (int i=0; i!=levelSize; ++i)
//				if (lchildren[i]!=-1)
//					parent.children[i] = nodeid.get(lchildren[i]);
//		}
//		
//		contentCount = dis.readInt();
//		mult = dis.readLong();
//		levelOne = nodeid.get(dis.readInt());
//	}
//
//	@Override
//	public void writeDump(DataOutputStream dos) throws IOException {
//		lm.writeDump(dos);
//		HashMap<Node, Integer> nodeid = new HashMap<Node, Integer>();
//		int nc = 0;
//		for (Node n : lm.getNodes()) {
//			nodeid.put(n, nc++);			
//		}
//		dos.writeInt(nc);
//		for (Node n : nodeid.keySet()) {
//			int id = nodeid.get(n);
//			dos.writeInt(id);			
//			// now write all the children
//			if (n instanceof InternalNode) {
//				dos.writeBoolean(true);
//				InternalNode in = (InternalNode)n;
//				dos.writeLong(in.myID);
//				dos.writeLong(in.multiplier);
//				dos.writeLong(in.start);
//				for (Node child : in.children)
//					if (child!=null)
//						dos.writeInt(nodeid.get(child));
//					else
//						dos.writeInt(-1);
//			} else {
//				dos.writeBoolean(false);
//				LeafNode ln = (LeafNode)n;
//				dos.writeLong(ln.myID);
//				dos.writeLong(ln.start);
//				for (long child : ln.content)
//					dos.writeLong(child);		
//			}
//		}
//
//		dos.writeInt(contentCount);
//		dos.writeLong(mult);
//		dos.writeInt(nodeid.get(levelOne));
//	}

	@Override
	public String getCompactionInitializer() {
		return lm.getCompactionInitializer();
	}

	@Override
	public void setCompaction(LinkedList<String> compactionInitializer) {
		lm.setCompaction(compactionInitializer);		
	}


}
