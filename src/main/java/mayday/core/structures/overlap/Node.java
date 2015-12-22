package mayday.core.structures.overlap;

import java.util.Iterator;
import java.util.Set;


public interface Node {
	
	public final static int levelSize = 100;
	
	public void put(long from, long to, long ID);
	
	public Set<Long> get(long from, long to, Set<Long> result);
	
	public boolean isCovered(long position);
	
	public Iterator<Long> coverageIterator();
	
	public Iterator<Long> coverageIterator(long startposition);

	
	public int getActualLevelSize();

	public long getEnd();

}