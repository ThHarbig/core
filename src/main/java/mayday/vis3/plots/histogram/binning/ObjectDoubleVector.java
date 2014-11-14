package mayday.vis3.plots.histogram.binning;

import java.util.Map;
import java.util.Map.Entry;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;

public class ObjectDoubleVector<T> extends AbstractVector {

	protected double[] array;
	protected T[] names;
	
	@SuppressWarnings("unchecked")
	public ObjectDoubleVector(Map<T, Number> map) {
		array = new double[map.size()];
		names = (T[])new Object[map.size()];
		int i=0;
		for (Entry<T, Number> e : map.entrySet()) {
			Number o = e.getValue();
			array[i] = o.doubleValue();
			names[i] = e.getKey();
			++i;
		}		
	}
	
	@Override
	public double get0(int i) {
		return array[i];
	}

	@Override
	public void set0(int i, double v) {
		array[i] = v;
	}

	@Override
	public int size() {
		return array.length;
	}
	
	public double[] toArrayUnpermuted() {
		return array;
	}
	
	public DoubleVector clone() {
		DoubleVector nv = clone( array );
		nv.setPermutation(indices);
		if (names!=null)
			nv.setNames(this);
		return nv;
	}
	
	public static DoubleVector clone( double[] array ) {
		double[] second = new double[array.length];
		System.arraycopy(array, 0, second, 0, array.length);
		return new DoubleVector( second );
	}
	
	public static DoubleVector rep( double value, int count) {
		double[] array = new double[count];
		for (int i=0; i!=array.length; ++i)
			array[i] = value;
		return new DoubleVector(array);
	}

	protected String getName0(int i) {
		return null;
	}

	protected void setName0(int i, String name) {
	}
	
	public final T getObject(int i) {
		return names[mapIndex(i)];
	}		


}
