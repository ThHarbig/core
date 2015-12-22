package mayday.genetics.importer.locusmio;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.gui.columnparse.ColumnTypeValidator;
import mayday.genetics.importer.locusmio.LocusColumnTypes.CTYPE;

public class LocusColumnTypeValidator implements ColumnTypeValidator<CTYPE> {

	public String getValidityHint() {
		return "You need a LocusMIO column";
	}

	public boolean isValid(List<CTYPE> columnTypes) {
		// one of each type maximum
		Set<Object> foundTypes = new HashSet<Object>();
		
		for (CTYPE o : columnTypes) {
			if (!foundTypes.add(o) && o!=null)
				return false;
		}

		return foundTypes.contains(CTYPE.LocusMIO);
	}

}
