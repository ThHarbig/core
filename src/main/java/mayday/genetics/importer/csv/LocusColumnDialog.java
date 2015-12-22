package mayday.genetics.importer.csv;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.table.TableModel;

import mayday.core.gui.columnparse.ColumnTypeDialog;
import mayday.core.settings.SettingComponent;
import mayday.core.structures.maps.MultiHashMap;
import mayday.genetics.importer.DefaultLocusSetting;
import mayday.genetics.importer.csv.LocusColumnTypes.CTYPE;

@SuppressWarnings("serial")
public class LocusColumnDialog extends ColumnTypeDialog<CTYPE> {

	public LocusColumnDialog(TableModel tableModel) {
		super(tableModel, 
				new LocusColumnTypes(),
				new LocusColumnTypeEstimator(tableModel),
				new LocusColumnTypeValidator()
				);
	}

	protected DefaultLocusSetting defaults = new DefaultLocusSetting();
	protected SettingComponent defcomp = defaults.getGUIElement();
	protected HashMap<CTYPE, Integer> asCol = new HashMap<CTYPE, Integer>();

	protected void init() {
		super.init();
		add(defcomp.getEditorComponent(), BorderLayout.NORTH);
	}
	
	public HashMap<CTYPE, Object> getDefaults() {
		defcomp.updateSettingFromEditor(false);
		HashMap<CTYPE, Object> ret = new MultiHashMap<CTYPE, Object>();
		ret.put(CTYPE.Length, defaults.getLength());
		ret.put(CTYPE.Chromosome, defaults.getChromosome());
		ret.put(CTYPE.Species, defaults.getSpecies());
		ret.put(CTYPE.Strand, defaults.getStrand());
		return ret;
	}
	
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
