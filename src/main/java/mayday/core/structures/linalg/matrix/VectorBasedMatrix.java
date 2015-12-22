package mayday.core.structures.linalg.matrix;

import java.util.Collection;

import mayday.core.structures.linalg.vector.AbstractVector;

/**
 * A matrix build from column or row vectors
 * @author battke
 *
 */
public class VectorBasedMatrix extends StringArrayNamedPermutableMatrix {

	protected AbstractVector[] vectors;
	protected String[] rowNames, colNames;
	protected boolean rowMajor = true;

	/** Constructs a new matrix from an array of vectors. IF the vectors are matrix rows, rowMajor must be true, else false.
	 * @param matrix the matrix data
	 * @param rowMajor the binding order of the vectors.
	 */
	public VectorBasedMatrix(AbstractVector[] vectors, boolean rowMajor) {
		this.vectors=vectors;
		this.rowMajor = rowMajor;
		int firstLen=-1;
		for (AbstractVector v : vectors)
			if (firstLen==-1)
				firstLen = v.size();
			else
				if (firstLen!=v.size())
					throw new RuntimeException("Matrix vectors must be of equal length!");
	}
	
	public VectorBasedMatrix(boolean rowMajor, AbstractVector... vectors) {
		this(vectors, rowMajor);
	}	
	
	public VectorBasedMatrix(Collection<AbstractVector> vectors, boolean rowMajor) {
		this(vectors.toArray(new AbstractVector[0]), rowMajor);
	}
	
	
	
	// INTERNAL access methods
	
	
	@Override
	protected double get0(int row, int col) {		
		return rowMajor?vectors[row].get(col):vectors[col].get(row);
	}

	protected int dim0(int dimension) {
		if (!rowMajor)
			dimension = 1-dimension;
		if (dimension==0)
			return vectors.length;
		if (vectors.length>0)
			return vectors[0].size();
		return 0;
	}
	
	@Override
	protected void set0(int row, int col, double value) {
		if (rowMajor)
			vectors[row].set(col,value);
		else
			vectors[col].set(row,value);
	}
	
	@Override
	protected AbstractVector getDimVec0(int dimension, int index) {
		// get access to the row/column at the given index, no transposition, no permutation applies here.
		switch (dimension) {
		case 0: 
			if (rowMajor && colPermute==null) // vector orientation fits AND the vector does not need to be permuted 
				return vectors[index];
			return new MatrixVector.Row(this, index, colPermute);
		case 1:
			if (!rowMajor && rowPermute==null) // vector orientation fits AND the vector does not need to be permuted
				return vectors[index];
			return new MatrixVector.Column(this, index, rowPermute);
		}
		return null;
	}



	public DoubleMatrix deepClone() {
		return new DoubleMatrix(this);
	}

}
