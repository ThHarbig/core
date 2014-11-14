package mayday.core.structures.linalg.matrix;

import java.util.Arrays;

import mayday.core.structures.linalg.vector.AbstractVector;


public class DoubleMatrix extends StringArrayNamedPermutableMatrix {

	protected double[][] matrix; // the matrix (rowMajor[row][col], !rowMajor[col][row])
	protected boolean rowMajor = true;

	/** Constructs a new matrix from a double double array. If the array is in row-major layout, i.e. if the access path is matrix[row][column],
	 * set rowMajor to true, else to false.
	 * @param matrix the matrix data
	 * @param rowMajor the memory layout of the data.
	 */
	public DoubleMatrix(double[][] matrix, boolean rowMajor) {
		this.matrix=matrix;
		this.rowMajor = rowMajor;
	}
	
	public DoubleMatrix(int nrow, int ncol, boolean rowMajor) {
		this.rowMajor = rowMajor;
		int d1,d2;
		if (rowMajor) {
			d1 = nrow;
			d2 = ncol;
		} else {
			d1 = ncol;
			d2 = nrow;
		}
		matrix = new double[d1][];
		for (int i=0; i!=d1; ++i)
			matrix[i] = new double[d2];
	}
	
	public DoubleMatrix(int nrow, int ncol) {
		this(nrow, ncol, true);
	}
	
	/** Create a (n times 1) matrix from a vector */
	public DoubleMatrix(AbstractVector vec) {
		this(new double[1][], false);
		matrix[0] = new double[vec.size()];
		setColumn(0, vec);
	}
	
	public DoubleMatrix(AbstractMatrix m) {
		this(m.nrow(), m.ncol());
		int nc = ncol();
		int nr = nrow();
		for (int i=0; i!=nc; ++i)
			for (int j=0; j!=nr; ++j)
				setValue(j, i, m.get(j, i));
		setColumnNames(m);
		setRowNames(m);
	}


	
	// INTERNAL access methods
	
	@Override
	protected double get0(int row, int col) {		
		return rowMajor?matrix[row][col]:matrix[col][row];
	}

	protected int dim0(int dimension) {
		if (!rowMajor)
			dimension = 1-dimension;
		if (dimension==0)
			return matrix.length;
		if (matrix.length>0)
			return matrix[0].length;
		return 0;
	}

	@Override
	protected void set0(int row, int col, double value) {
		if (rowMajor)
			matrix[row][col]=value;
		else
			matrix[col][row]=value;
	}

	@Override
	protected AbstractVector getDimVec0(int dimension, int index) {
		// get access to the row/column at the given index, no transposition, no permutation applies here.
		switch (dimension) {
		case 0: 
			if (rowMajor) 
				return new MatrixVectorImmediate.Row(this, index, colPermute, matrix[index]);
			return new MatrixVector.Row(this, index, colPermute);
		case 1:
			if (!rowMajor)
				return new MatrixVectorImmediate.Column(this, index, rowPermute, matrix[index]);
			return new MatrixVector.Column(this, index, rowPermute);
		}
		return null;
	}

	
	public DoubleMatrix deepClone() {
		// apply all transformations now
		double[][] m2 = new double[ncol()][];
		DoubleMatrix ret = new DoubleMatrix(m2, false);
		int nc = ncol();
		int nr = nrow();
		for (int i=0; i!=nc; ++i) {
			ret.setColumnName(i, getColumnName(i));
			m2[i]= new double[nr];
			for (int j=0; j!=nr; ++j) 
				m2[i][j] = get(j, i);
		}
		for (int j=0; j!=nr; ++j) 
			ret.setRowName(j, getRowName(j));
		return ret;
	}

	
	public double[][] getInternalData() {
		return matrix;
	}
	
	
	
	public static void main(String[] bla) {
		DoubleMatrix dm = new DoubleMatrix(10,10,true);
		for (int i=0; i!=10; ++i)
			for (int j=0; j!=10; ++j)
				dm.set(j,i,10*j+i);
		dm.setRowName(4, "TEST");
		dm.setColumnName(7,"A");
		dm.setColumnName(3,"B");
		
		PermutableMatrix m = dm.submatrix(new int[]{2,3,4,5,6}, new int[]{5,6,7,8});
		m.transpose();
		System.out.println("Expecting 46: "+m.get(1,2));
		System.out.println("Expecting 37: "+m.get(2,1));
		AbstractVector row = m.getRow("A");
		System.out.println("Expecting 5: "+row.size());
		System.out.println("Expecting 47: "+row.get(2));
		AbstractVector column = m.getColumn("TEST");
		System.out.println("Expecting 47: "+column.get(2));
		
		dm.transpose();
		PermutableMatrix pm = dm.deepClone();
		pm.transpose();
		pm.setRowPermutation(new int[]{9,8,7,6,5,4,3,2,1,0});
		pm.setColumnPermutation(new int[]{9,8,7,6,5,4,3,2,1,0});
		m = pm.submatrix(new int[]{2,3,4,5,6}, new int[]{5,6,7,8});
		m.transpose();

		System.out.println("Expecting 53: "+m.get(1,2));
		System.out.println("Expecting 62: "+m.get(2,1));
		row = m.getRow("B");		
		System.out.println("Expecting 5: "+row.size());
		System.out.println("Content: "+row);
		System.out.println("Ranking: "+row.rank());
		row.sort();
		System.out.println("Sorted: "+row);
		row.unpermute();
		System.out.println("Content: "+row);
		System.out.println("Expecting 43: "+row.get(3));
		column = m.getColumn("TEST");
		System.out.println("Expecting 43: "+column.get(1));
		
	}

	public void clear() {
		for(int i = 0; i < matrix.length; i++)
			Arrays.fill(matrix[i], 0);
	}
}

