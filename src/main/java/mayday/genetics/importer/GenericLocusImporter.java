/*
 * Created on 29.11.2005
 */
package mayday.genetics.importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.swing.table.TableModel;

import mayday.core.gui.abstractdialogs.SimpleStandardDialog;
import mayday.core.gui.columnparse.ColumnTypeDialog;
import mayday.core.io.csv.CSVImportSettingComponent;
import mayday.core.io.csv.ParsingTableModel;
import mayday.genetics.locusmap.LocusMap;

public abstract class GenericLocusImporter<DialogType extends ColumnTypeDialog<?>> extends AbstractLocusImportPlugin implements LocusFileImportPlugin {


	public LocusMap importFrom(List<String> files) {
		TableModel tm = getTableModel(new File(files.get(0)));
		return getLocusMap(tm, files.get(0));
	}
	
	protected TableModel getTableModel(File f) {
		if (f==null)
			return null;
		CSVImportSettingComponent comp;
		try {
			comp = new CSVImportSettingComponent(new ParsingTableModel(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}            		
		SimpleStandardDialog dlg = new SimpleStandardDialog("Import Locus Information",comp,false);
		dlg.setVisible(true);
		if(!dlg.okActionsCalled())
			return null;
		return comp.getTableModel();
	}
		
	protected LocusMap getLocusMap(final TableModel tm, final String name) {
		if (tm==null)
			return null;		
		// create the dialog now
		DialogType ctd = makeDialog(tm);
		ctd.setVisible(true);
		if (!ctd.canceled())
			return makeLocusMap(name, tm, ctd);
		return null;
	}
	
	protected abstract DialogType makeDialog(TableModel tm);
	
	protected abstract LocusMap makeLocusMap(String name, TableModel tm, DialogType ctd);
		
}
