package mayday.core.structures.linalg.vector;


public class ShallowVectorClone extends AbstractVector {

	protected AbstractVector parent;
	
	protected ShallowVectorClone(AbstractVector parent) {
		this.parent = parent;
	}
	
	@Override
	protected double get0(int i) {
		return parent.get(i);
	}

	@Override
	protected String getName0(int i) {
		return parent.getName(i);
	}

	@Override
	protected void set0(int i, double v) {
		parent.set(i,v);
	}

	@Override
	protected void setName0(int i, String name) {
		parent.setName(i, name);
	}

	@Override
	public int size() {
		return parent.size();
	}
	
}
