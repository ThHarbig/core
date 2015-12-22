package mayday.core.structures;

import java.util.Iterator;
import java.util.Stack;



@SuppressWarnings("unchecked")

public class SparseArray<V>  {

	private final static int levelSize = 1000;
	
	private Object[] levelOne;
	private long multiplierOne; 
	
	protected int contentCount=0;
	
	protected static class ResultPosition {
		public Object[] level;
		public int offset;
		public ResultPosition(Object[] Level, int Offset) {
			replace(Level, Offset);
		}
		public void replace(Object[] Level, int Offset) {
			level=Level; offset=Offset;
		}
		public Object toElement() {			
			return offset==-1?null:level[offset];
		}
		public void setElement(Object o) {
			if (offset==-1)
				throw new RuntimeException("Can't set element at invalid position");
			level[offset]=o;
		}
		public String toString() {
			return offset+"%%"+level.hashCode();
		}
	}

	protected ResultPosition reuseablePosition;
	
	public SparseArray(boolean threadsafe) {
		levelOne = new Object[levelSize];
		multiplierOne = 1;
		if (!threadsafe)
			reuseablePosition = new ResultPosition(null,0);  // for speedup
	}
	
	/** Constructs a new SparseArray object. This object is NOT thread-safe. Use SparseArray(true) to create a thread-safe object */
	public SparseArray() {
		this(false);
	}
	
	protected Object getElement(long position) {
		if (position >= multiplierOne*levelSize)
			return null;
		return getElementPosition(position, levelOne, multiplierOne, 0, false, reuseablePosition, null).toElement();
	}
	
	protected ResultPosition getElementPosition(long position, Object[] level, double multiplier,
												double levelStart, boolean create, ResultPosition resPos, Stack<ResultPosition> trace) {
		int offset = (int)(position - levelStart);
		offset/=multiplier;
		Object next = level[offset];
		
		if (trace!=null)
			trace.push(new ResultPosition(level,offset));
		
		if (multiplier>1) {
			if (next==null) {
				if (create) {
					// add new level to the tree at the bottom
					Object[] newlevel = new Object[levelSize];
					level[offset] = newlevel;
					next = newlevel;
				} else {
					level=null;
					offset=-1;
				}
			}
			if (next!=null && next instanceof Object[]) {
				return getElementPosition(position, (Object[])next, multiplier/levelSize, 
										  levelStart+offset*multiplier, create, resPos, trace);
			}
		}
		if (resPos == null)
			resPos = new ResultPosition(level, offset);
		else
			resPos.replace(level, offset);
		return resPos;
	}
	
	public synchronized void put(long position, V obj) {
		checkLevels(position);
		ResultPosition rp = getElementPosition(position, levelOne, multiplierOne, 0, true, reuseablePosition, null);
		Object o = rp.toElement();
		if (o==null) {
			++contentCount;
			rp.setElement(obj);
		} else {
			throw new RuntimeException("Position already occupied: "+position);
		}				
	}
	
	public synchronized void putReplace(long position, V obj) {
		checkLevels(position);
		ResultPosition rp = getElementPosition(position, levelOne, multiplierOne, 0, true, reuseablePosition, null);
		Object o = rp.toElement();
		if (o==null) 
			++contentCount;
		rp.setElement(obj);
	}
	
	public void put(long position, long end, V obj) {
		for (long s = position; s<=end; ++s)
			put(s,obj);
	}
	
	protected void checkLevels(long position) {
		while (position >= multiplierOne*levelSize) {
			// add level to the tree at the top
			Object[] oldLevelOne = levelOne;
			levelOne = new Object[levelSize];
			multiplierOne *= levelSize;
			levelOne[0] = oldLevelOne;
//			System.out.println("SparseArray: new level with mult "+multiplierOne);
		}
	}
	
	public synchronized V get(long position) {		
		Object o = getElement(position);
		return (V)o;
	}
	
	
	public int size() {
		return contentCount;
	}

	public void clear() {
		multiplierOne = 1;
		levelOne = new Object[levelSize];
		contentCount = 0;
	}

	public boolean isEmpty() {		
		return contentCount == 0;
	}
		
	public SparseArrayKeyIterator keyIterator(long startposition) {
		return new SparseArrayKeyIterator(startposition);
	}
	
	public class SparseArrayKeyIterator implements Iterator<Long>, Iterable<Long> {

		protected Stack<Object[]> levels = new Stack<Object[]>();
		protected Stack<Integer> levelPositions = new Stack<Integer>();
		protected long levelMultiplier = multiplierOne;
		protected long curpos;
		
		public SparseArrayKeyIterator(long start) {
			levels.push(levelOne);
			levelPositions.push(0);					
			if (start>0) {
				Stack<ResultPosition> trace = new Stack<ResultPosition>();
				getElementPosition(start, levelOne, multiplierOne, 0, false, null, trace);
				trace.removeElementAt(0);
				for (ResultPosition rp : trace) {
					levels.push(rp.level);
					levelPositions.push(rp.offset);
					levelMultiplier/=levelSize;
				}
			}
			int lpl = levelPositions.size()-1;
			levelPositions.set(lpl, levelPositions.get(lpl)-1);
			
			findNext();
//			while (hasNext() && curpos<start) 
//				findNext();
		}
		
		public boolean hasNext() {
			return (curpos>=0);
		}

		protected void findNext() {
			boolean found = false;
			while (!found) {
				Object[] l = levels.peek();
				int i = levelPositions.peek();			
				// go further to the next filled position
				i++;
				levelPositions.set(levelPositions.size()-1, i);
				if (i>=l.length) { 					
					if (levels.size()==1) {
						// end of array reached
						curpos=-1;
						return;
					} else {
						// go up if end of array reached
						levels.pop();
						levelPositions.pop();		
						levelMultiplier*=levelSize;
					}
				} else {
					if (l[i]!=null) {
						if (levelMultiplier>1) {
							// go down if multiplier>1
							levels.push((Object[])l[i]);
							levelPositions.push(-1);
							levelMultiplier/=levelSize;
						} else {
							// finished!
							found=true;
						}
					} 
				}
			}
			curpos=0;
			double mult = multiplierOne;
			for (int lpos : levelPositions)  {
				curpos += lpos*mult;
				mult/=levelSize;
			}
		}
		
		public Long next() {
			long ret = curpos;
			findNext();
			return ret;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public Iterator<Long> iterator() {
			return this;
		}
				
	}
	
	public static void main(String[] args) {
		Object a = new Integer(5);
		SparseArray<Object> sa = new SparseArray<Object>(false);
		sa.put(10,a);
		sa.put(1000232,1000236, a);
		sa.get(1000232);
		sa.get(10023);
		sa.get(9000000);
		for (Long l : sa.keyIterator(11))
			System.out.println(l);
	}

}
