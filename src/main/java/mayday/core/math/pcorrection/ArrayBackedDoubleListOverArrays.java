package mayday.core.math.pcorrection;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

public class ArrayBackedDoubleListOverArrays extends AbstractList<Double>
implements List<Double>, RandomAccess, Cloneable, java.io.Serializable
{
	private static final long serialVersionUID = 8683452581122892189L;

	private transient List<double[]> elementData;
	private int column;

	public ArrayBackedDoubleListOverArrays(List<double[]> o, int column) {
		elementData = o;
		this.column = column;
	}

	public int size() {
		return elementData.size();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public boolean contains(Object o) {
		return indexOf(o) >= 0;
	}

	public int indexOf(Object o) {
		throw new RuntimeException("sorry");
//		if (o!=null) {
//			for (int i = 0; i < size(); i++)
//				if (o.equals(get(i)))
//					return i;
//		}
//		return -1;
	}

	/**
	 * Returns the index of the last occurrence of the specified element
	 * in this list, or -1 if this list does not contain the element.
	 * More formally, returns the highest index <tt>i</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
	 * or -1 if there is no such index.
	 */
	public int lastIndexOf(Object o) {
		throw new RuntimeException("sorry");
//		if (o != null) {
//			for (int i = size()-1; i >= 0; i--)
//				if (o.equals(get(i)))
//					return i;
//		}
//		return -1;
	}

	public Object clone() {
		throw new RuntimeException("sorry");
	}

	public Object[] toArray() {
		Double[] ret = new Double[elementData.size()];
		int i=0;
		for (double d : this)
			ret[i++] = d;
		return ret;
	}
	
	public double[] toArray(double[] some) {
		throw new RuntimeException("sorry");
	}
	
    public <T> T[] toArray(T[] a) {
    	throw new RuntimeException("sorry");
    }
    
	// Positional Access Operations

	public Double get(int index) {
		return elementData.get(index)[column];
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
	
	public Double remove(int index) {
		throw new UnsupportedOperationException("Read only object");	
	}


	public boolean remove(Object o) {
		throw new UnsupportedOperationException("Read only object");
	}
	
	public void clear() {
		throw new UnsupportedOperationException("Read only object");
	}

	public boolean addAll(Collection<? extends Double> c) {
		throw new UnsupportedOperationException("Read only object");
	}

	public boolean addAll(int index, Collection<? extends Double> c) {
		throw new UnsupportedOperationException("Read only object");
	}

}
