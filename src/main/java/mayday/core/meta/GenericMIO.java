package mayday.core.meta;

import mayday.core.pluma.AbstractPlugin;


public abstract class GenericMIO<T> extends AbstractPlugin implements MIType {
	
	protected T Value;
	
	public abstract MIType clone();
	
	public T getValue() {
		return Value;
	}
	
	public void setValue(T value) {
		Value = value;
	}
	
	public String toString() {
		return Value==null? "(null)" : Value.toString();
	}
	
	@SuppressWarnings("unchecked")
	public Class getPayloadClass() {
		return Value.getClass();
	}
	
}
