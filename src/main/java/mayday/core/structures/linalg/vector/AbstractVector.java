package mayday.core.structures.linalg.vector;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.RandomAccess;

import mayday.core.math.Statistics;

public abstract class AbstractVector implements Iterable<Double> {

	protected abstract double get0(int i);
	protected abstract void set0(int i, double v);
	protected abstract String getName0(int i);
	protected abstract void setName0(int i, String name);
	public abstract int size();
	
	protected int[] indices = null;
	protected Map<String, Integer> nameCache;
	
	protected final int mapIndex(int index) {
		return indices==null?index:indices[index];			
	}
	
	public final double get(int i) {
		return get0(mapIndex(i));
	}
	
	public final void set(int i, double v) {
		set0(mapIndex(i),v);
	}
	
	public void setAllToValue(double v) {
		for (int i=0; i!=size(); ++i)
			set0(i, v);
	}
	
	public void set(AbstractVector other) {
		for (int i=0; i!=other.size(); ++i)
			set(i, other.get(i));
	}
	
	public void set(double[] field) {
		for (int i=0; i!=field.length; ++i)
			set(i, field[i]);		
	}

	
	public final String getName(int i) {
		return getName0(mapIndex(i));
	}		
	
	public void add( double scalar ) {
		add( scalar, 0 , size()-1);
	}
	
	public void add( double scalar, int firstIndex, int lastIndex) {
		for (int i=firstIndex; i<=lastIndex; ++i)
			set(i, get(i)+scalar);
	}
	
	public void add( AbstractVector d) {
		for (int i=0; i!=size(); ++i)
			set(i, get(i)+d.get(i));
	}
	
	public final void set(double v, int firstIndex, int lastIndex) {
		for (int i=firstIndex; i<=lastIndex; ++i)
			set(i, v);
	}

	
	public void subtract( AbstractVector d ) {
		for (int i=0; i!=size(); ++i)
			set(i, get(i)-d.get(i));
	}
	
	public void multiply( double scalar ) {
		for (int i=0; i!=size(); ++i)
			set(i, get(i)*scalar); 
	}
	
	public void divide( double scalar ) {
		multiply (1.0 / scalar);
	}
	
	public void divide( AbstractVector d ) {
		for (int i=0; i!=size(); ++i)
			set(i, get(i)/d.get(i));
	}
	
	public void multiply( AbstractVector d ) {
		for (int i=0; i!=size(); ++i)
			set(i, get(i)*d.get(i));
	}

	public void raise( double exponent ) {
		for (int i=0; i!=size(); ++i)
			set(i, Math.pow(get(i),exponent));
	}
	
	public void log(double base) {
		double lbase = 1.0/Math.log(base);
		for (int i=0; i!=size(); ++i)
			set(i, Math.log(get(i))*lbase);
	}
	
	public void exp(double base) {
		for (int i=0; i!=size(); ++i)
			set(i, Math.pow(base, get(i)));
	}
	
	public void abs() {
		for (int i=0; i!=size(); ++i)
			set(i, Math.abs(get(i)));
	}
	
	public void replace(double value, double replacement) {
		// ordering doesn't matter
		for (int i=0; i!=size(); ++i) {
			if (get0(i)==value)
				set0(i, replacement);
		}
	}

	public void replaceNA(double replacement) {
		// ordering doesn't matter
		for (int i=0; i!=size(); ++i) {
			if (Double.isNaN(get0(i)))
				set0(i, replacement);
		}
	}
	
	public void replaceInfinity(double replacementPositive, double replacementNegative) {
		// ordering doesn't matter
		for (int i=0; i!=size(); ++i) {
			double d = get0(i);
			if (Double.isInfinite(d)) {
				if (d>0)
					set0(i, replacementPositive);
				else
					set0(i, replacementNegative);
			}
		}
	}
	
	/**
	 * Find the indices of all elements equal to a given value  
	 * @param value the value to test for
	 * @return the indices where the value was found
	 */
	public List<Integer> whichIs( double value ) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (int i=0; i!=size(); ++i) {
			if (get(i)==value)
				ret.add(i);			
		}
		return ret;
	}
	
	/**
	 * Find the indices of all elements which are Double.NaN  
	 * @return the indices where NaN was found
	 */
	public List<Integer> whichIsNA() {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (int i=0; i!=size(); ++i) {
			if (Double.isNaN(get(i)))
				ret.add(i);			
		}
		return ret;
	}

	/**
	 * Find the indices of all elements which are infinite  
	 * @return the indices where infinity was found
	 */
	public List<Integer> whichIsInfinite() {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (int i=0; i!=size(); ++i) {
			if (Double.isInfinite(get(i)))
				ret.add(i);			
		}
		return ret;
	}
	
	/**
	 * Find the indices of all elements which are neither Double.NaN nor infinite  
	 * @return the indices where finite values were found
	 */

	public List<Integer> whichIsFinite() {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (int i=0; i!=size(); i++){
			double v = get(i);
			if (!Double.isInfinite(v) && !Double.isNaN(v))
				ret.add(i);
		}
		return ret;
	}

	
	/**
	 * Create a boolean vector indicating which positions are Double.NaN  
	 */
	public boolean[] isNA() {
		return indices_to_boolean(whichIsNA());
	}
	
	/**
	 * Create a boolean vector indicating which positions are neither Double.NaN nor infinite  
	 */
	public boolean[] isFinite() {
		return indices_to_boolean(whichIsFinite());
	}
	
	/**
	 * Create a boolean vector indicating which positions are infinite
	 */
	public boolean[] isInfinite() {
		return indices_to_boolean(whichIsInfinite());
	}
	
	/**
	 * Create a boolean vector indicating which positions are equal to a given value
	 */
	public boolean[] isEqualTo(double value) {
		return indices_to_boolean(whichIs(value));
	}
	
	protected boolean[] indices_to_boolean(Collection<Integer> indices) {
		boolean[] ret = new boolean[size()];
		Arrays.fill(ret, false);
		for (Integer i : indices)
			ret[i] = true;
		return ret;
	}
	
	
	/** compares the contents of two vectors */
	public boolean allValuesEqual( AbstractVector other ) {
		if (size()!=other.size())
			return false;
		for (int i=0; i!=size(); ++i)
			if (get(i)!=other.get(i))
				return false;
		return true;
	}
	
		/** compares the contents of two vectors including names*/
	public boolean allEqual( AbstractVector other ) {
		if (size()!=other.size())
			return false;
		for (int i=0; i!=size(); ++i)
			if (get(i)!=other.get(i) && !getName(i).equals(other.getName(i)))
				return false;
		return true;
	}
	
	
	public void reverse() {
		int[] permutation = indices;
		AbstractVector permVec;
		
		if (indices==null) {
			permVec = new ConstantIndexVector(size(),0).clone();
			permutation = new int[size()];
		} else {
			permVec = new DoubleVector(indices);
		}
		
		Collections.reverse(permVec.asList());
		
		for (int i=0; i!=permVec.size(); ++i)
			permutation[i] = (int)permVec.get(i);
		
		setPermutation(permutation);
		
	}
	
	public void permute(Random r) {
		int[] temp_indices = new int[size()];
		for (int i=0; i!=size(); ++i)
			temp_indices[i] = i;
		
        for (int i=size(); i>0; i--) {
        	int rndi = r.nextInt(size());
        	int tmp = temp_indices[i-1];
        	temp_indices[i-1] = temp_indices[rndi];
        	temp_indices[rndi] = tmp;
        }
        setPermutation(temp_indices);
	}
	
	public void setPermutation(int[] permutation) {
		indices = permutation;
	}
	
	public void unpermute() {
		indices = null;
	}
	
	/** returns an array containing the contents of this vector. This is ALWAYS a copy. */
	public final double[] toArray() {
		double[] ret = new double[size()];
		for (int i=0; i!=size(); ++i)
			ret[i] = get(i);
		return ret;
	}
	
	/** return an array containing the unpermuted vector. This MAY be a pointer to the underlying array OR a copy!
	 * override in subclasses for more efficient methods
	 * @return
	 */
	public double[] toArrayUnpermuted() {
		double[] ret = new double[size()];
		for (int i=0; i!=size(); ++i)
			ret[i] = get0(i);
		return ret;
	}
	
	
	public List<Double> asList() {
		return new VectorAsList();
	}
	
	// this should be random access in most storage implementations
	private class VectorAsList extends AbstractList<Double> implements RandomAccess {
		public Double get(int index) {
			return AbstractVector.this.get(index);
		}
		public int size() {
			return AbstractVector.this.size();
		}
		public Double set(int index, Double value) {
			Double old = get(index);
			AbstractVector.this.set(index, value);
			return old;
		}
	}
	
	/**
	 * Return a vector of unique values of the input vector. 
	 * The output is sorted in ascending order
	 * @return the unique elements
	 */
	public DoubleVector unique() {
		AbstractVector cl = this.shallow_clone();
		cl.sort();
		Double Last=null;
		ArrayList<Double> uniqs = new ArrayList<Double>();
		for (int i=0; i!=cl.size(); ++i) {
			double cur = cl.get(i);
			if (Last==null || !Last.equals(cur)) {
				Last = cur;
				uniqs.add(cur);
			}
		}
		return new DoubleVector(uniqs);
	}
	
	/**
	 * Calculates the ordering of the vector:
	 * x={34, 27, 45, 55, 22, 34} leads to:
	 * [4, 1, 0, 5, 2, 3]
	 * NAs are always sorted to the end.
	 * This function's result is the same as order(x)-1 in R
	 * @return the array of indices that define the ordering on the vector
	 */
	public int[] order() {
		return Statistics.order(toArrayUnpermuted());
	}
	
	/**
	 * Sorts the vector by changing the internal permutation.
	 * NAs are always sorted to the end.
	 * The resulting ordering is consistent with the ordering obtained by calling order()
	 * @see setPermutation() 
	 */
	public void sort() {
		setPermutation(order());
	}
	
	public DoubleVector rank() {		
		DoubleVector nv = new DoubleVector(Statistics.rank(toArrayUnpermuted()));
		nv.setPermutation(indices);
		return nv;
	}
	
	public double range(boolean ignoreNA, boolean ignoreInfinity) {
		return max(ignoreNA, ignoreInfinity)-min(ignoreNA, ignoreInfinity);
	}
	
	public double min(boolean ignoreNA, boolean ignoreInfinity) {
		double min = Double.NaN;
		for (int i=0; i!=size(); ++i) {
			double v = get0(i);
			boolean isNA = Double.isNaN(v);
			boolean isNegInf = Double.isInfinite(v) && v<0;
			if (isNegInf && !ignoreInfinity)
				return v;
			if (isNA && !ignoreNA)
				return v;
			if (!isNA && !isNegInf)
				min = min<v?min:v; // test evaluates to false when min==NA, so it gets replaced
		}
		return min;
	}
	
	public double max(boolean ignoreNA, boolean ignoreInfinity) {
		double max = Double.NaN;
		for (int i=0; i!=size(); ++i) {
			double v = get0(i);
			boolean isNA = Double.isNaN(v);
			boolean isPosInf = Double.isInfinite(v) && v>0;
			if (isPosInf && !ignoreInfinity)
				return v;
			if (isNA && !ignoreNA)
				return v;
			if (!isNA && !isPosInf)
				max = max>v?max:v; // test evaluates to false when min==NA, so it gets replaced
		}
		return max;
	}
	

	public double range() {
		return range(false, false);
	}
	
	public double min() {
		return min(false, false);
	}

	public double max() {
		return max(false, false);
	}
		
	public double median() {
		return median(false);
	}
	
	public double mean() {
		return mean(false);
	}
	
	public double sd() {
		return sd(false);
	}

	public double mad() {
		return mad(false);
	}
	
	public double sum() {
		return sum(false);
	}
	
	public double sum(boolean ignoreNA) {
		double sum = 0;
		for (int i=0; i!=size(); ++i) {
			double v = get0(i);
			boolean isNA = Double.isNaN(v);
			if (isNA && !ignoreNA)
				return v;
			sum+=v;
		}
		return sum;
	}
	
	public double prod() {
		return prod(false);
	}
	
	public double prod(boolean ignoreNA) {
		double prod = 1;
		for (int i=0; i!=size(); ++i) {
			double v = get0(i);
			boolean isNA = Double.isNaN(v);
			if (isNA && !ignoreNA)
				return v;
			prod*=v;
		}
		return prod;
	}
	
	public double median(boolean ignoreNA) {
		return Statistics.median(this, ignoreNA);
	}
	
	public double mean(boolean ignoreNA) {
		return Statistics.mean(this, ignoreNA);
	}
	
	public double sd(boolean ignoreNA) {
		return Statistics.sd(this, ignoreNA);
	}
	
	public double mad(boolean ignoreNA) {
		return Statistics.mad(this, ignoreNA);
	}
	
	public void normalize(boolean ignoreNA) {
		Statistics.normalize(this, ignoreNA);			
	}
	
	public double norm() {
		double norm = 0.0;
		for (int i=0; i!=size(); ++i)
			norm+=get0(i)*get0(i);
		norm = Math.sqrt(norm);
		return norm;
	}

	/**
	 * Calculates the k-th q-quantile of a SORTED vector. If the vector is not sorted and isSorted is TRUE, the result is undefined.
	 * @param q The quantile (i.e. 100 for percentiles, 4 for quartiles)
	 * @param k The number of the quantile to compute
	 * @param isSorted If set to true, the function assumes that the vector is already sorted, if false, sorting is done here
	 */
	public double quantile(double q, double k, boolean isSorted)
	{
		if(k > q) throw new IllegalArgumentException("Can not calculate the "+k+"th quantile of "+q);
		return quantile(k/q, isSorted); 
	}
	
	/**
	 * Calculates the k-th percentile of a SORTED vector. If the vector is not sorted and isSorted is TRUE, the result is undefined.
	 * @param p The percentile (i.e. 0.75 for the 75%-percentile)
	 * @param isSorted If set to true, the function assumes that the vector is already sorted, if false, sorting is done here
	 */
	public double quantile(double p, boolean isSorted)
	{
		double pos=size()*p;
		int idx=(int)Math.ceil(pos);
		AbstractVector in = this;
		if (!isSorted) {
			in = in.shallow_clone();
			in.sort();
		}
		if (idx>=size())
			idx = size()-1;
		return in.get(idx); 
	}
	
	public Iterator<Double> iterator() {
		return asList().iterator();
	}


	public AbstractVector subset(int... subset) {
		return new VectorSubset(this, subset);
	}
	
	public AbstractVector subset(Integer... subset) {
		return new VectorSubset(this, subset);
	}
	
	protected AbstractVector subset_int_collection(Collection<Integer> raw_collection) {
		return new VectorSubset(this, raw_collection);
	}
	
	protected AbstractVector subset_str_collection(Collection<String> raw_collection) {
		ArrayList<Integer> subs = new ArrayList<Integer>();
		if (nameCache==null || nameCache.size()<size())
			buildNameCache();
		for (String n : raw_collection) {
			Integer i = nameCache.get(n);
			if (i!=null)
				subs.add(i);
		}
		return subset_int_collection(subs);
	}

	
	@SuppressWarnings("unchecked")
	public AbstractVector subset(Collection<?> subset) {
		if (subset.size()==0)
			return new VectorSubset(this, new int[0]);
		Object e = subset.iterator().next();
		if (e instanceof String)
			return subset_str_collection(((Collection<String>)subset));
		else
			return subset_int_collection(((Collection<Integer>)subset));
	}
	
	public AbstractVector subset(int from, int to) {
		int[] subset = new int[to-from+1];
		for (int i=0; i!=subset.length; ++i)
			subset[i]=from+i;
		return subset(subset);
	}
	
	public AbstractVector subset(boolean[] subset) {
		return subset(subset, false);
	}
	
	public AbstractVector subset(boolean[] subset, boolean inverse) {
		return new VectorSubset(this, subset, inverse);
	}
	
	public AbstractVector subset(String[] names) {
		ArrayList<Integer> subs = new ArrayList<Integer>();
		if (nameCache==null || nameCache.size()<size())
			buildNameCache();
		for (String n : names) {
			Integer i = nameCache.get(n);
			if (i!=null)
				subs.add(i);
		}
		return subset(subs);
	}

	
	
	/** Creates a deep clone of this vector including its current permutation and its names */ 
	public DoubleVector clone() {
		double[] array = new double[size()];
		for (int i=0; i!=size(); ++i) {
			array[i] = get0(i);  // do not apply permutations here!
		}
		DoubleVector nv = new DoubleVector(array);
		nv.names = new String[size()];
		for (int i=0; i!=size(); ++i) {
			nv.names[i] = getName0(i);  // do not apply permutations here!
		}
		nv.setPermutation(indices);		
		return nv;
	}
	
	/** Creates a shallow clone of this vector. The returned vector accesses the data of
	 * the original vector, but uses its own permutation ON TOP OF the permutation of the input
	 * vector. If the input vector's permutation is changed, this will be reflected in the
	 * shallow clone. Changing any values in the shallow clone (except the permutation)
	 * will change the original object.  
	 * */ 
	public AbstractVector shallow_clone() {
		return new ShallowVectorClone(this);
	}
	
	public String toString() {
		StringBuffer res = new StringBuffer();
		res.append("{");
		boolean first=true;
		for (int i=0; i!=size(); ++i) {
			if (!first) 
				res.append(",");
			else
				first = false;
			String s = getName(i);
			if (s!=null) {
				res.append(s);
				res.append("=");
			}
			res.append(get(i));
		}
		res.append("}");
		return res.toString();
	}
	
	/** Create a string representation that can be pasted into an R session */
	public String toRString() {
		StringBuffer res = new StringBuffer();
		res.append("<-c(");
		boolean first=true;
		for (int i=0; i!=size(); ++i) {
			if (!first) { 
				res.append(",");
				if (i%100==0)
					res.append("\n");
			} else
				first = false;			
			String s = getName(i);
			if (s!=null) {
				res.append("\"");
				res.append(s);
				res.append("\"");
				res.append("=");
			}
			res.append(get(i));
		}
		res.append(");");
		return res.toString();
	}
	
	
	
	
	/** Access a value by its name. Works only if there are names and is not reliable if names are not unique. 
	 * For speedup, this function will build a cache of names (might be memory expensive)
	 * If some names are not unique, the cache will be built EVERY TIME the function is called. (Which is slow).
	 * @return null if the name is not found
	 * */	
	public Double get(String name) {
		Integer i;
		
		synchronized(this) { //cannot synchronize on nameCache as it might be null
			if (nameCache==null || nameCache.size()<size())
				buildNameCache();
			i = nameCache.get(name);
		}
		
		if (i==null)
			return Double.NaN;
		return get0(i);
	}
	
	protected void buildNameCache() {
		synchronized(this) { //cannot synchronize on nameCache as it might be null
			if (nameCache==null)
				nameCache = new HashMap<String, Integer>();
			else
				nameCache.clear();
			// cache circumvents permutations!
			for (int i=0; i!=size(); ++i) {
				nameCache.put(getName0(i), i);
			}
		}
	}
	
	public Iterable<String> getNames() {
		return new Iterable<String>() {
			public Iterator<String> iterator() {
				return new Iterator<String>() {
					public int idx = 0;
					public boolean hasNext() {
						return idx<size();
					}
					public String next() {
						return getName(idx++);
					}
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
	
	public List<String> getNamesList() {
		List<String> l = new ArrayList<String>(); // can be optimized in overriding classes
		for (String s : getNames())
			l.add(s);
		return l;
	}
	
	
	public void setName(int index, String name) {
		setName0(mapIndex(index), name);
		synchronized(this) { //cannot synchronize on nameCache as it might be null
			if (nameCache!=null)
				nameCache.put(name, index);
		}
	}
	
	public void setNames(Collection<String> Names) {
		if (Names.size()!=size())
			throw new RuntimeException("Number of names must match number of vector elements: "+size());
		int i=0;
		for (String s : Names)
			setName(i++, s);
	}
	
	public void setNames(String[] Names) {
		if (Names.length!=size())
			throw new RuntimeException("Number of names must match number of vector elements: "+size());
		int i=0;
		for (String s : Names)
			setName(i++, s);
	}
	
	public void setNames(AbstractVector other) {
		setNames(other.getNamesList());
	}
	
	/** optimized implementation -- allows to synchronize the name cache of several vectors WITH IDENTICAL NAMING! */ 
	public void setNameCacheDirectly(Map<String, Integer> nameCache) {
		this.nameCache=nameCache;
	}
	
	/** optimized implementation -- allows to synchronize the name cache of several vectors WITH IDENTICAL NAMING! */ 
	public void setNameCacheDirectly(AbstractVector other) {
		if (other.size()!=this.size())
			throw new RuntimeException("Size of naming vector must be identical to size of data vector");
		if (other.nameCache==null)
			other.buildNameCache();
		this.nameCache=other.nameCache;
	}

}
