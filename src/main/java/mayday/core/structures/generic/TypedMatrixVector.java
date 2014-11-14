package mayday.core.structures.generic;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

import mayday.core.structures.generic.TypedMatrix.MatrixAccessor;


public class TypedMatrixVector<E> extends AbstractList<E> implements List<E> {

	private MatrixAccessor<E> mac;

	public TypedMatrixVector(MatrixAccessor<E> accessor) {
		mac=accessor;
	}

	public boolean add(E o) {
		throw new RuntimeException("List size is constant, no elements may be added or removed");
	}

	public void add(int index, E element) {
		throw new RuntimeException("List size is constant, no elements may be added or removed");		
	}

	public void clear() {
		throw new RuntimeException("List size is constant, no elements may be added or removed");		
	}

	public int indexOf(Object elem) {
		int size = mac.size();
		if (elem == null) {  //taken from java 5 arraylist code
			for (int i = 0; i < size; i++)
				if (get(i)==null)
					return i;
		} else {
			for (int i = 0; i < size; i++)
				if (elem.equals(get(i)))
					return i;
		}
		return -1;
	}

	public E get(int index) {
		return mac.Get(index);
	}

	public boolean contains(Object o) {
		return indexOf(o)>=0;
	}

	public boolean isEmpty() {
		return mac.size()==0;
	}

	public int lastIndexOf(Object elem) {
		int size=mac.size();
		if (elem == null) {
			for (int i = size-1; i >= 0; i--)
				if (get(i)==null)
					return i;
		} else {
			for (int i = size-1; i >= 0; i--)
				if (elem.equals(get(i)))
					return i;
		}
		return -1;
	}

	public boolean remove(Object o) {
		throw new RuntimeException("List size is constant, no elements may be added or removed");		
	}

	public E remove(int index) {
		throw new RuntimeException("List size is constant, no elements may be added or removed");		
	}

	public boolean removeAll(Collection<?> c) {
		throw new RuntimeException("List size is constant, no elements may be added or removed");		
	}

	public boolean retainAll(Collection<?> c) {
		throw new RuntimeException("List size is constant, no elements may be added or removed");		
	}

	public E set(int index, E element) {
		mac.Set(index, element);
		return element;
	}
	
	public void setAll(Collection<E> elements) {
		if (elements.size()!=this.size())
			throw new RuntimeException("Vector size mismatch");
		int i=0;
		for(E v : elements)
			set(i++, v);
	}

	public int size() {
		return mac.size();
	}

	public Object[] toArray() {
		return toArray(new Object[0]);
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		int size = size();
		if (a.length < size)
			a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size());
		for (int i=0; i!=size; ++i)
			a[i]=(T)get(i);
		if (a.length > size)
			a[size] = null;
		return a;
	}


}
