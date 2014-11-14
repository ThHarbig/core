package mayday.core.structures.linalg.vector;


public class ConstantIndexVector extends AbstractVector {

	protected int size;
	protected double offset;
	
	public ConstantIndexVector(int size, double offset) {
		this.size = size;
		this.offset = offset;
	}
	
	public double get0(int i) {
		return i+offset;
	}

	public void set0(int i, double v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return size;
	}

	protected String getName0(int i) {
		return null;
	}

	protected void setName0(int i, String name) {
		// ignore this
	}

}
