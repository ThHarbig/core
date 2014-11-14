/*
 * Created on 29.11.2005
 */
package mayday.core.meta.io.tabular;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.TableModel;

import mayday.core.DataSet;
import mayday.core.Preferences;
import mayday.core.gui.abstractdialogs.SimpleStandardDialog;
import mayday.core.io.csv.CSVImportSettingComponent;
import mayday.core.io.csv.ParsingTableModel;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.plugins.MetaInfoPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.prototypes.ApplicableFunction;
import mayday.core.structures.maps.MultiHashMap;
import mayday.core.tasks.AbstractTask;

/**
 * Text file import plugin.
 * 
 * @author Matthias Zschunke
 * @version 0.1
 * Created on 29.11.2005
 *
 */
public abstract class AbstractCSVImportPlugin
extends AbstractPlugin implements MetaInfoPlugin, ApplicableFunction
{

	public static final String LAST_OPEN_DIR_KEY = "lastopendir";


	/* (non-Javadoc)
	 * @see mayday.core.AbstractPlugin#run(java.util.List, mayday.core.MasterTable)
	 */
	public List<DataSet> run(List<DataSet> datasets)
	{
		final DataSet dataSet = datasets.get(0);

		Preferences prefs = PluginInfo.getPreferences("PAS.mio.import.csv");

		JFileChooser fc = new JFileChooser(
				prefs.get(LAST_OPEN_DIR_KEY, System.getProperty("user.home"))
				);
		fc.setDialogTitle("Import Meta Information from ...");
		fc.setMultiSelectionEnabled(false);
		int res = fc.showOpenDialog(null);
		if(res!=JFileChooser.APPROVE_OPTION)
		{
			return  null;
		}

		prefs.put(LAST_OPEN_DIR_KEY, fc.getCurrentDirectory().getAbsolutePath());

		runWithFile(dataSet, fc.getSelectedFile());
		return null;
	}


	public MIGroup[] runWithFile(final DataSet dataSet, File f) {

		// User can select how to import the table

		try {

			CSVImportSettingComponent comp = new CSVImportSettingComponent(new ParsingTableModel(f));
			SimpleStandardDialog dlg = new SimpleStandardDialog("Import Meta Information",comp,false);

			dlg.setVisible(true);

			if(!dlg.okActionsCalled())
				return null;

			return runWithModel(dataSet, comp.getTableModel());

		}catch(Exception ex) {
			ex.printStackTrace();

			JOptionPane.showMessageDialog(null, 
					"An exception occured. The import will be canceled.\n" +
							"\nMessage:\n" +
							ex.getLocalizedMessage(),
							"Import Meta Information ...",
							JOptionPane.ERROR_MESSAGE
					);   
		}
		return null;
	}

	public MIGroup[] runWithModel(final DataSet dataSet, final TableModel model) {

		// User can select which columns to import in what way            

		TreeMap<String, PluginInfo> MITYPES = new TreeMap<String, PluginInfo>();

		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(Constants.MC_METAINFO);
		for (PluginInfo pli : plis)
			if (pli.getInstance() instanceof MIType)
				MITYPES.put(pli.getName(), pli);	

				ColumnTypeDialog ctd = new ColumnTypeDialog(model, MITYPES);

				int directKeyColumn = -1;   // names
				int indirectKeyColumn = -1; // display names

				do {
					ctd.setVisible(true);

					if (!ctd.canceled()) {

						int kcnt = 0;
						for(int i=0; i!=model.getColumnCount(); ++i ) {
							if (ctd.getColumnType(i)==ColumnType.Name) {
								directKeyColumn = i;
								++kcnt;
							}
							else if (ctd.getColumnType(i)==ColumnType.DisplayName) {
								indirectKeyColumn = i;
								++kcnt;
							}
						}

						if(kcnt>0) {
							break;
						} else {
							JOptionPane.showMessageDialog(
									null, 
									"There is need to have at least one column for Names or Display Names.\n" +
											"If both Names and Display Names are specified, Names take precedence.", 
											"Import MIO ...", 
											JOptionPane.ERROR_MESSAGE    
									);
						}        				
					}

				} while(!ctd.canceled());

				if (directKeyColumn==-1 && indirectKeyColumn==-1)
					return null;


				// Create MIGroups for selected columns 

				final int directKey = directKeyColumn;
				final int indirectKey = indirectKeyColumn;

				final MIGroup[] groups = new MIGroup[model.getColumnCount()];

				for(int i=0; i!=model.getColumnCount(); ++i) {
					if (ctd.getColumnType(i)==ColumnType.MetaInfo) {
						String plumaID = ctd.getMIOType(i);
						groups[i] = dataSet.getMIManager().newGroup(plumaID, model.getColumnName(i));
					}
				}


				AbstractTask readMIOs = new AbstractTask("Parsing Meta Information") {

					@Override
					protected void doWork() throws Exception {

						setProgress(-1);
						MultiHashMap<String, Object> byDisplayName = null;

						// build hashmap of displaynames
						if (indirectKey>-1) {
							byDisplayName = new MultiHashMap<String, Object>();
							fillDisplayNames(dataSet, byDisplayName);
						}

						// assign metainfo
						for(int row=0; row!=model.getRowCount(); ++row) {

							List<Object> targets = Collections.emptyList();

							if (directKey>-1) {
								String pn = (String)model.getValueAt(row, directKey);
								targets = new ArrayList<Object>();
								if (pn!=null) {
									Object oo = getObject(dataSet, pn.trim());
									if (oo!=null)
										targets.add(oo);
								}
							}

							if (targets.size()==0 && byDisplayName!=null) {
								String pn = (String)model.getValueAt(row, indirectKey);            	
								if (pn!=null)
									targets = byDisplayName.get(pn.trim()); // multiple targets!!
							}

							for (int i=0; i!=model.getColumnCount(); ++i) {
								if (groups[i]!=null) {
									String val = (String)model.getValueAt(row, i);
									if (val==null || val.length()==0) {
										continue;
									}
									MIType mt = null;
									for (Object oo : targets) {
										if (mt==null) {
											mt = groups[i].add(oo);
											if (!mt.deSerialize(MIType.SERIAL_TEXT, val)) {
												groups[i].remove(oo);
												mt=null;
											}
										} else {
											groups[i].add(oo, mt);
										}
									}

								}
							}

						}

					}


					@Override
					protected void initialize() {

					}

				};
				readMIOs.start();
				return groups;
	}


	public void init() {
	}


	@SuppressWarnings("unchecked")
	public void run(MIGroupSelection input, MIManager miManager) {
		LinkedList<DataSet> ds = new LinkedList<DataSet>();
		ds.add(miManager.getDataSet());
		run(ds);		
	}

	@Override
	public boolean isApplicable(Object... o) {
		return true; // always possible
	}


	protected abstract Object getObject(DataSet ds, String name);
	protected abstract void fillDisplayNames(DataSet ds, MultiHashMap<String, Object> map);

}
