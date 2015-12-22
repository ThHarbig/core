package mayday.genetics.importer.csv;

import javax.swing.table.TableModel;

import mayday.core.gui.columnparse.ColumnTypeEstimator;
import mayday.genetics.basic.Strand;
import mayday.genetics.importer.csv.LocusColumnTypes.CTYPE;

public class LocusColumnTypeEstimator extends ColumnTypeEstimator<CTYPE> {

	public LocusColumnTypeEstimator(TableModel tableModel) {
		super(tableModel);
	}
	
	protected boolean hasFrom = false;
	protected boolean hasTo = false;
	
	@SuppressWarnings("unchecked")
	@Override
	protected CTYPE estimateColumn(int i) {
		// try to find strand info
		if (isValidColumn(i, new StrandChecker(), null))
			return CTYPE.Strand;
		if (isValidColumn(i, new IntegerChecker(), null)) {
			if (!hasFrom) {
				hasFrom = true;
				return CTYPE.From;
			}
			if (!hasTo) {
				hasTo = true;
				return CTYPE.To;
			}
			return null; // ignore this column
		}
		// not numeric and not a strand 
		if (isValidColumn(i, new NoMissingChecker(), null)) {
			if (i==0) {
				return CTYPE.Identifier;
			} else {
				return CTYPE.Chromosome; // more likely than species
			}		
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected class StrandChecker implements ValueChecker {

		public boolean isValid(String value, Object memory) {
			value = value.trim();
			if (value.length()==0)
				return false;
			return Strand.validChar(value.charAt(0));
		}
		
	}


}
