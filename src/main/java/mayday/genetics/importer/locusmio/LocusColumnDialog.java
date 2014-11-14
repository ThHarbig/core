package mayday.genetics.importer.locusmio;

import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.table.TableModel;

import mayday.core.gui.columnparse.ColumnTypeDialog;
import mayday.genetics.importer.locusmio.LocusColumnTypes.CTYPE;

@SuppressWarnings("serial")
public class LocusColumnDialog extends ColumnTypeDialog<CTYPE> {

	public LocusColumnDialog(TableModel tableModel) {
		super(tableModel, 
				new LocusColumnTypes(),
				new LocusColumnTypeEstimator(tableModel),
				new LocusColumnTypeValidator()
				);
	}

	protected HashMap<CTYPE, Integer> asCol = new HashMap<CTYPE, Integer>();

	public HashMap<CTYPE, Integer> getColumns() {
		return asCol;
	}
	
	protected void makeMap() {
		asCol.clear();
		for (int i=0; i!=table.getColumnCount(); ++i) {
			asCol.put(getColumnType(i), i);
		}
	}
	
	protected AbstractAction getOKAction() {
		return new OKAction();
	}
	
	public class OKAction extends ColumnTypeDialog<CTYPE>.OKAction {
		public void actionPerformed(ActionEvent e) {
			makeMap();
			super.actionPerformed(e);			
		}
	}
	
}
