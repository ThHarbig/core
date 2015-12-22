package mayday.core.io.gudi;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;

import mayday.core.DataSet;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.io.gudi.prototypes.DatasetFileImportPlugin;
import mayday.core.io.gudi.prototypes.DatasetImportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class GUDIDataSet extends GUDIBase {

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.GUDI.DataSet",
				new String[0],
				Constants.MC_CORE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Manages all DataSet import plugins",
				"DataSet Import"
				);		
		return pli;
	}
	
	public Vector<JMenuItem> createMenu() {
		Vector<JMenuItem> theMenu = super.createMenu();
		// Quickload
//		PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.Quickload");
//		if (pli!=null)
//			theMenu.add(0, new JMenuItem(createAction(pli)));
		return theMenu;
	}
	
	@SuppressWarnings("serial")
	protected class RunPluginAction extends GUDIBase.RunPluginAction {

		public RunPluginAction(PluginInfo pli) {
			super(pli);
		}
		public void actionPerformed(final ActionEvent arg0) {
			new Thread("DataSet import") {
				public void run() {
					List<DataSet> results = null;
					AbstractPlugin apl = getPlugin().getInstance();
					if (apl instanceof DatasetFileImportPlugin) {
						LinkedList<String> files = new LinkedList<String>();
						JFileChooser fc = (JFileChooser)(arg0.getSource());
						if (fc.isMultiSelectionEnabled())
							for (File selfile : fc.getSelectedFiles())
								files.add(selfile.getAbsolutePath());
						else 
							files.add(fc.getSelectedFile().getAbsolutePath());
						DatasetFileImportPlugin dsip = (DatasetFileImportPlugin)apl;
						results = dsip.importFrom(files);			
					} else if (apl instanceof DatasetImportPlugin){
						DatasetImportPlugin dsip = (DatasetImportPlugin)apl;
						results = dsip.run();
					}
					
					if (results!=null) {
						for (DataSet ds : results) {
							if (ds!=null) {
								DataSetManagerView.getInstance().addDataSet(ds);
					    		if (ds.getProbeListManager().getNumberOfObjects()==0)
					    			ds.getProbeListManager().addObjectAtTop(ds.getMasterTable().createGlobalProbeList(true));	
							}
						}
						DataSetManagerView.getInstance().setSelectedDataSets(results);
					}								
				}
			}.start();
		}
		
	}

	protected RunPluginAction createAction(PluginInfo pli) {
		return new RunPluginAction(pli);
	}

	@Override
	protected String objectType() {
		return "DataSet";
	}

	@Override
	protected String providedMasterComponent() {
		return Constants.MC_DATASET_IMPORT;
	}

	@Override
	public void init() {
	}
	
	
}
