package mayday.core.structures.natives.mmap;

import java.util.Iterator;
import java.util.LinkedList;

import mayday.core.structures.CompactableStructure;

/** Stores strings of arbitrary length in an mmap buffer. Once added, strings can not be changed or removed.
 */
public class MMFinalStringArray implements CompactableStructure {

	protected MMLongArray indexMap; //maps index to start position of string;
	protected MMCharArray underling; //stores the actual string data
	protected long totalChars;
	
	public MMFinalStringArray(int minsize, boolean wideChars) {
		indexMap = new MMLongArray(minsize);
		underling = new MMCharArray(minsize, wideChars);
	}

	public String get(long i) {
		long offset = indexMap.get(i);
		return getString(offset);
	}

	public void set(long i, String value) {
		throw new UnsupportedOperationException();
	}
	
	public long add(String value) {	
		long nextPos = underling.size();
		long ret = indexMap.add(nextPos);
		putString(value);		
		return ret;
	}
	
	protected void putString(String value) {
		int len = value.length();
		for (int i=0; i!=len; ++i)
			underling.add(value.charAt(i));			
		underling.add('\0');		
		totalChars = underling.size();
	}
	
	protected String getString(long offset) {
		StringBuffer sb = new StringBuffer();
		int i=0;
		
		while (true) {
			char c = underling.get(offset+i);
			if (c=='\0')
				break;
			sb.append(c);
			++i;
		}
			
		return sb.toString();
	}
	
	public long size() {
		return indexMap.size();
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

	@Override
	public void compact() {
		indexMap = indexMap.changeStorageRange(totalChars,true);	
	}

	@Override
	public String getCompactionInitializer() {
		return Long.toString(underling.size());
	}

	@Override
	public void setCompaction(LinkedList<String> compactionInitializer) {
		totalChars = Long.parseLong(compactionInitializer.removeFirst());
		compact();
	}
	
	
//	public void writeDump(DataOutputStream dos) throws IOException {
//		indexMap.writeDump(dos);
//		underling.writeDump(dos);
//	}
//	
//	public void readDump(DataInputStream dis) throws IOException {
//		indexMap.readDump(dis);
//		underling.readDump(dis);
//	}

}
