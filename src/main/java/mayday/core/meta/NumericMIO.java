package mayday.core.meta;



public abstract class NumericMIO<T extends Number> extends GenericMIO<T> implements MIType, ComparableMIO {
	
	public String serialize(int serializationType) {
		// no difference whether xml or text
		return Value!=null?Value.toString():null; 
	}
	
	
}
