package mayday.core.structures.linalg.matrix;

import mayday.core.structures.linalg.vector.AbstractVector;

/**
 * A Matrix Column based on the data of the matrix. the permutations in the parent are reflected here, access is directly
 * to the underlying data, circumventing any further permutations/transpositions of the parent.
 * @author fb
 *
 */
public abstract class MatrixVector extends AbstractVector {
	
	protected AbstractMatrix parent;
	protected int index;
	protected int[] parentPermutation;
	protected int dimension;
	
	public MatrixVector(AbstractMatrix parent, int index, int[] parentPermutation, int dimension) {
		this.parent = parent;
		this.index = index;
		this.parentPermutation = parentPermutation;
		this.dimension = dimension;
	}

	protected int mapParent(int i) {
		if (parentPermutation!=null)
			i = parentPermutation[i];
		return i;
	}
	
	public int size() {
		return parent.dim0(dimension);
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

	
	
	public static class Column extends MatrixVector {
		
		public Column(AbstractMatrix parent, int colIndex, int[] rowPermutation) {
			super(parent, colIndex, rowPermutation, 0); // 0 because the row-dimension is our dimension since we are a column
		}

		public double get0(int i) {
			return parent.get0(mapParent(i), index);			
		}

		public void set0(int i, double v) {
			parent.set0(mapParent(i), index, v);
		}

	}
	
	
	public static class Row extends MatrixVector {
		
		public Row(AbstractMatrix parent, int rowIndex, int[] colPermutation) {
			super(parent, rowIndex, colPermutation, 1); // 1 because the column-dimension is our dimension since we are a row
		}

		public double get0(int i) {
			return parent.get0(index, mapParent(i));			
		}

		public void set0(int i, double v) {
			parent.set0(index, mapParent(i), v);
		}

	}
	
	
}
