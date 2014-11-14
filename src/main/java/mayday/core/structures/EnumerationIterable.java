package mayday.core.structures;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

public class EnumerationIterable<T> implements Iterable<T> {

	public static <T> Iterator<T> iterator(final Enumeration<T> e) {
		return new Iterator<T>() {
			public boolean hasNext() {
				return e.hasMoreElements();
			}

			public T next() {
				return e.nextElement();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	protected Enumeration<T> e;
	
	public EnumerationIterable(Enumeration<T> e) {
		this.e=e;
	}

	@Override
	public Iterator<T> iterator() {
		return iterator(e);
	}
	
	public void addToCollection(Collection<T> c) {
		for (T bla : this) {
			c.add(bla);
		}
	}
	
}
