package mayday.genetics.advanced;

import java.util.Iterator;

import mayday.core.structures.LongTools;
import mayday.core.structures.natives.LinkedLongArray;
import mayday.genetics.basic.Strand;

/** Long has 64 bits, of which pairs are used to represent strands (2 bit for each strand)
 * 00 - minus
 * 01 - plus
 * 10 - both
 * 11 - unspecified
 * @author battke
 *
 * @param <V>
 */

public class LinkedStrandArray implements Iterable<Strand> {
	
	protected final static Strand[] values = new Strand[]{
		Strand.MINUS, Strand.PLUS, Strand.BOTH, Strand.UNSPECIFIED
	};
	
	protected LinkedLongArray content;
	protected long len = 0;
	
	public LinkedStrandArray(int blocksize) {
		content = new LinkedLongArray((int)Math.ceil((double)blocksize/32d));
	}
	
	public LinkedStrandArray(LinkedStrandArray other) {
		content = new LinkedLongArray(other.content);
	}
	
	public Strand get(long i) {
		if (i>=len)
			throw new  ArrayIndexOutOfBoundsException(""+i+" >= "+len);
		long base = (i / 32);
		int offset = (int)(i % 32);
		offset *= 2;
		long l = content.get(base);
		int val = (int)LongTools.extract(l, offset, offset+1);
		return values[val];		
	}
	
	public long size() {
		return len;
	}
	
	public void set(long i, Strand value) {
		if (i>=len)
			throw new  ArrayIndexOutOfBoundsException(""+i+" >= "+len);
		long base = (i / 32);
		int offset = (int)(i % 32);
		offset *= 2;
		long l = content.get(base);
		int val = 3; //unspec
		switch (value) {
		case MINUS : val = 0; break;
		case PLUS : val = 1; break;
		case BOTH : val = 2; break;
		}
		l=LongTools.set(l, offset, offset+1, val);
		content.set(base, l);
	}
	
	public long add(Strand value) {
		++len;
		long base = (int)((len-1) / 32);
		if (base>=content.size())
			content.add(0l);
		set(len-1, value);
		return len-1;
	}
	
	public Iterator<Strand> iterator() {
		return new Iterator<Strand>() {
			protected long next=0;

			public boolean hasNext() {
				return next<len;
			}

			public Strand next() {
				return get(next++);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}			
		};
	}
	
}
