package mayday.core.structures.linalg.matrix;

import mayday.core.structures.linalg.vector.AbstractVector;

/**
 * A Matrix Column based on the data of the matrix. the permutations in the parent are reflected here, access is directly
 * to the underlying data, circumventing any further permutations/transpositions of the parent.
 * This is an optimized implementation for the cases where we can directly access an array of double.
 * @author fb
 *
 */
public abstract class MatrixVectorImmediate extends AbstractVector {
	
	protected AbstractMatrix parent;
	protected int index;
	protected int[] parentPermutation;
	protected int dimension;
	protected double[] data;
	
	public MatrixVectorImmediate(AbstractMatrix parent, int index, int[] parentPermutation, double[] data, int dimension) {
		this.parent = parent;
		this.index = index;
		this.parentPermutation = parentPermutation;
		this.dimension = dimension;
		this.data = data;
	}

	protected int mapParent(int i) {
		if (parentPermutation!=null)
			i = parentPermutation[i];
		return i;
	}
	
	public int size() {
		return data.length;
	}
	
	public double get0(int i) {
		return data[mapParent(i)];			
	}

	public void set0(int i, double v) {
		data[mapParent(i)] = v;
	}


	protected String getName0(int i) {		
		if (parent instanceof NamedMatrix)
			return ((NamedMatrix)parent).getDimName0(dimension,mapParent(i));
		return null;
	}

	protected void setName0(int i, String name) {		
		if (parent instanceof NamedMatrix)
			((NamedMatrix)parent).setDimName0(dimension, mapParent(i), name);
	}

	
	
	public static class Column extends MatrixVectorImmediate {
		
		public Column(AbstractMatrix parent, int colIndex, int[] rowPermutation, double[] data) {
			super(parent, colIndex, rowPermutation, data, 0); // 0 because the row-dimension is our dimension since we are a column
		}

	}
	
	
	public static class Row extends MatrixVectorImmediate {
		
		public Row(AbstractMatrix parent, int rowIndex, int[] colPermutation, double[] data) {
			super(parent, rowIndex, colPermutation, data, 1); // 1 because the column-dimension is our dimension since we are a row
		}

	}
	
	
}
