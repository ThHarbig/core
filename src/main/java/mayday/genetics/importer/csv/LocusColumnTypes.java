package mayday.genetics.importer.csv;

import mayday.core.gui.columnparse.ColumnTypes;

public class LocusColumnTypes implements ColumnTypes<LocusColumnTypes.CTYPE> {

	// ordinal() here is a perfect mapping of CTYPE to VariableGeneticCoordinateElement
	
	public static enum CTYPE {
		Identifier,
		Species,
		Chromosome,
		Strand,
		From,
		To,
		Length
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
