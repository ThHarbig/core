package mayday.genetics.importer.locusmio;

import javax.swing.table.TableModel;

import mayday.core.gui.columnparse.ColumnTypeEstimator;
import mayday.genetics.LocusMIO;
import mayday.genetics.importer.locusmio.LocusColumnTypes.CTYPE;

public class LocusColumnTypeEstimator extends ColumnTypeEstimator<CTYPE> {

	public LocusColumnTypeEstimator(TableModel tableModel) {
		super(tableModel);
	}
	
	protected boolean hasID = false;
	protected boolean hasMIO = false;
	
	@SuppressWarnings("unchecked")
	@Override
	protected CTYPE estimateColumn(int i) {
		// try to find strand info
		if (isValidColumn(i, new MIOChecker(), new LocusMIO())) {
			if (!hasMIO) {
				hasMIO = true;
				return CTYPE.LocusMIO;
			}
			return null;
		}		
		//  
		if (isValidColumn(i, new NoMissingChecker(), null)) {
			if (!hasID) {
				hasID = true;
				return CTYPE.Identifier;
			}			
		}
		return null;
	}
	

}
