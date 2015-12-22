package mayday.core.structures.linalg.matrix;

import mayday.core.structures.linalg.vector.AbstractVector;

public class MatrixDiagonal extends AbstractVector {
	
	protected AbstractMatrix parent;
	
	public MatrixDiagonal(AbstractMatrix parent) {
		this.parent = parent;
	}
	
	public double get0(int i) {
		return parent.getValue(i, i);			
	}

	public void set0(int i, double v) {
		parent.setValue(i, i, v);
	}

	public int size() {
		return Math.min(parent.ncol(), parent.nrow());
	}

	protected String getName0(int i) {
		return null;
	}
		
	protected void setName0(int i, String name) {
		throw new RuntimeException("Diagonals have no names");
	}
	
}
