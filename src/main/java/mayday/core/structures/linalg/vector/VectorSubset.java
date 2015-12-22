package mayday.core.structures.linalg.vector;

import java.util.Collection;

import mayday.core.structures.linalg.Algebra;

public class VectorSubset extends AbstractVector {

	protected int[] subset;
	protected AbstractVector parent;
	
	protected void init(AbstractVector parent, int[] subset) {
		this.parent = parent;
		this.subset = subset;
	}
	
	public VectorSubset(AbstractVector parent, int... subset) {
		init(parent, subset);
	}
	
	public VectorSubset(AbstractVector parent, Integer... subset) {
		init(parent, Algebra.<int[]>createNativeArray(subset));
	}
	
	public VectorSubset(AbstractVector parent, Collection<Integer> subset) {
		this(parent, subset.toArray(new Integer[0]));
	}
	
	public VectorSubset(AbstractVector parent, boolean[] subset, boolean inverse) {
		this(parent, convert(parent, subset, inverse));
	}
	
	protected static int[] convert(AbstractVector parent, boolean[] subset, boolean inverse) {
		if (subset.length<parent.size())
			throw new RuntimeException("Subset bool vector must be of same length as parent vector.");
		int l=0;
		for (int i=0; i!=subset.length; ++i)
			if (subset[i]^inverse)
				++l;
		int[] ret = new int[l];
		l=0;
		for (int i=0; i!=subset.length; ++i)
			if (subset[i]^inverse)
				ret[l++] = i;
		return ret;
	}
	
	@Override
	protected double get0(int i) {
		return parent.get(subset[i]);
	}

	@Override
	protected String getName0(int i) {
		return parent.getName(subset[i]);
	}

	@Override
	protected void set0(int i, double v) {
		parent.set(subset[i],v);
	}

	@Override
	protected void setName0(int i, String name) {
		parent.setName(subset[i], name);
	}

	@Override
	public int size() {
		return subset.length;
	}
	

}
