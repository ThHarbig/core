package mayday.core.structures.linalg.matrix;

import mayday.core.structures.linalg.vector.AbstractVector;

public class PermutableSubMatrix extends PermutableMatrix {

	protected AbstractMatrix parent;
	protected int[] subCols;
	protected int[] subRows;
	
	public PermutableSubMatrix(AbstractMatrix am, int[] rows, int[] columns) {
		parent = am;
		subRows = rows;
		subCols = columns;
	}
	
	protected int subIndex(int dimension, int index) {
		int[] map = dimension==0?subRows:subCols;
		if (map!=null)
			index = map[index];
		return index;
	}
	
	@Override
	protected double get0(int row, int col) {
		return parent.get(subIndex(0,row), subIndex(1,col));
	}

	@Override
	protected int dim0(int dimension) {
		int[] map = dimension==0?subRows:subCols;
		if (map!=null)
			return map.length;
		return parent.dim(dimension);
	}

	protected void set0(int row, int col, double value) {
		parent.set(subIndex(0,row),subIndex(1,col),value);
	}
	
	// access to names
	public String getName() {
		if (parent instanceof NamedMatrix)
			return ((NamedMatrix)parent).getName();
		return null;
	}	
	
	public void setName(String name) {
		if (parent instanceof NamedMatrix)
			((NamedMatrix)parent).setName(name);
	}

	protected String getDimName0(int dim, int index) { // this is called AFTER our own transformations are applied
		index = subIndex(dim, index);
		if (parent instanceof NamedMatrix)
			return ((NamedMatrix)parent).getDimName(dim, index); // this applies the parent's transformations
		return null;
	}
	
	protected void setDimName0(int dim, int index, String name) { // this is called AFTER our own transformations are applied in setDimName
		index = subIndex(dim, index);
		if (parent instanceof NamedMatrix)			
			((NamedMatrix)parent).setDimName(dim, index, name); // this applies the parent's transformations
	}

	@Override
	protected AbstractVector getDimVec0(int dimension, int index) { // this is called AFTER our own transformations in getDimVec
		if (dimension==0)
			return new MatrixVector.Row(this, index, colPermute);
		else
			return new MatrixVector.Column(this, index, rowPermute);
	}
	
	

}
