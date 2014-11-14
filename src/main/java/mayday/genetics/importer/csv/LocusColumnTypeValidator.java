package mayday.genetics.importer.csv;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.gui.columnparse.ColumnTypeValidator;
import mayday.genetics.importer.csv.LocusColumnTypes.CTYPE;

public class LocusColumnTypeValidator implements ColumnTypeValidator<CTYPE> {

	public String getValidityHint() {
		return "You need at one positional column (From or To) and no type may be used more than once.";
	}

	public boolean isValid(List<CTYPE> columnTypes) {
		// species, chrome, strand, length  can come from default settings
		
		// we need at least a FROM coordinate or a TO coordinate
		boolean hasPosition = false;
		
		// one of each type maximum
		Set<Object> foundTypes = new HashSet<Object>();
		
		for (CTYPE o : columnTypes) {
			if (o==CTYPE.From || o==CTYPE.To)
				hasPosition = true;
			if (!foundTypes.add(o) && o!=null)
				return false;
		}

		return hasPosition;
	}

}
