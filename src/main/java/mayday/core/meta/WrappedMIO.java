/**
 * 
 */
package mayday.core.meta;


// Connect a MIO, the Object it belongs to  and its MIGroup

public class WrappedMIO {
	protected MIType mio;
	protected MIGroup group;
	protected Object mioExtendable;
	
	public WrappedMIO(MIType mt, MIGroup mg, Object mioExtendable) {
		mio = mt;
		group = mg;
		this.mioExtendable = mioExtendable;
	}

	public MIGroup getGroup() {
		return group;
	}

	public void setGroup(MIGroup group) {
		this.group = group;
	}

	public MIType getMio() {
		return mio;
	}

	public void setMio(MIType mio) {
		this.mio = mio;
	}

	public Object getMioExtendable() {
		return mioExtendable;
	}

	public void setMioExtendable(Object mioExtendable) {
		this.mioExtendable = mioExtendable;
	}
	
}