package mayday.core.structures.linalg.matrix;

import java.util.Arrays;

import mayday.core.structures.linalg.vector.AbstractVector;


public class PermutableMatrixClone extends PermutableMatrix {

	protected PermutableMatrix parent;
	
	public PermutableMatrixClone(PermutableMatrix parent) {
		this.parent = parent;
		this.transposed = parent.transposed;
		this.rowPermute = parent.rowPermute!=null?Arrays.copyOf(parent.rowPermute, parent.rowPermute.length):null;
		this.colPermute = parent.colPermute!=null?Arrays.copyOf(parent.colPermute, parent.colPermute.length):null;
	}
	
	@Override
	protected double get0(int row, int col) {
		return parent.get0(row, col); //bypass parent's transformations
	}
	
	@Override
	protected int dim0(int dimension) {
		return parent.dim0(dimension);
	}

	@Override
	protected void set0(int row, int col, double value) {
		parent.set0(row, col, value);
	}
	
	protected String getDimName0(int dim, int index) {
		return parent.getDimName0(dim, index);
	}

	protected void setDimName0(int dim, int index, String name) {
		parent.setDimName0(dim, index, name);
	}

	protected AbstractVector getDimVec0(int dimension, int index) {
		return parent.getDimVec0(dimension, index);
	}
	
	// access to names

	public String getName() {
		return parent.getName();
	}	
	public void setName(String name) {
		parent.setName(name);
	}


	
	
	
	

	
}
