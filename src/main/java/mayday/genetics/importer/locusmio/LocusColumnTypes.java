package mayday.genetics.importer.locusmio;

import mayday.core.gui.columnparse.ColumnTypes;

public class LocusColumnTypes implements ColumnTypes<LocusColumnTypes.CTYPE> {

	public static enum CTYPE {
		Identifier,
		LocusMIO,
	}
	
	
	public int indexOf(CTYPE type) {
		return CTYPE.valueOf(type.name()).ordinal();
	}

	public CTYPE typeOf(String value) {
		return CTYPE.valueOf(value);
	}

	public CTYPE[] values() {
		return CTYPE.values();
	}
}
