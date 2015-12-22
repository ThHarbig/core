package mayday.core.math.pcorrection;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

public class ArrayBackedDoubleArrayList extends AbstractList<double[]>
implements List<double[]>, RandomAccess, Cloneable, java.io.Serializable
{
	private static final long serialVersionUID = 8683452581122892189L;

	private transient double[][] elementData;

	public ArrayBackedDoubleArrayList(double[][] o) {
		elementData = o;
	}

	public int size() {
		return elementData.length;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public boolean contains(Object o) {
		return indexOf(o) >= 0;
	}

	public int indexOf(Object o) {
		if (o!=null) {
			for (int i = 0; i < size(); i++)
				if (o.equals(get(i)))
					return i;
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of the specified element
	 * in this list, or -1 if this list does not contain the element.
	 * More formally, returns the highest index <tt>i</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
	 * or -1 if there is no such index.
	 */
	public int lastIndexOf(Object o) {
		if (o != null) {
			for (int i = size()-1; i >= 0; i--)
				if (o.equals(get(i)))
					return i;
		}
		return -1;
	}

	public Object clone() {
		throw new RuntimeException("sorry");
	}

	public Object[] toArray() {
		throw new RuntimeException("sorry");
	}
	
	public double[][] toArray(double[][] some) {
		return elementData;
	}
	
    public <T> T[] toArray(T[] a) {
    	throw new RuntimeException("sorry");
    }
    
	// Positional Access Operations

	public double[] get(int index) {
		return elementData[index];
	}


	public Double set(int index, Double element) {
		throw new UnsupportedOperationException("Read only object");
	}

	public boolean add(Double e) {
		throw new UnsupportedOperationException("Read only object");
	}


	public void add(int index, Double element) {
		throw new UnsupportedOperationException("Read only object");
	}
	
	public double[] remove(int index) {
		throw new UnsupportedOperationException("Read only object");	
	}


	public boolean remove(Object o) {
		throw new UnsupportedOperationException("Read only object");
	}
	
	public void clear() {
		throw new UnsupportedOperationException("Read only object");
	}

	public boolean addAll(Collection<? extends double[]> c) {
		throw new UnsupportedOperationException("Read only object");
	}

	public boolean addAll(int index, Collection<? extends double[]> c) {
		throw new UnsupportedOperationException("Read only object");
	}

}
