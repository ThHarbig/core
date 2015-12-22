package mayday.core.io.gude;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JMenuItem;

import mayday.core.DataSet;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.io.gude.prototypes.DatasetExportPlugin;
import mayday.core.io.gude.prototypes.DatasetFileExportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class GUDEDataSet extends GUDEBase {

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.GUDE.DataSet",
				new String[0],
				Constants.MC_CORE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Manages all DataSet export plugins",
				"DataSet Export"
				);		
		return pli;
	}

	public Vector<JMenuItem> createMenu() {
		Vector<JMenuItem> theMenu = super.createMenu();
		// Quickload
//		PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.Quicksave");
//		if (pli!=null)
//			theMenu.add(0, new JMenuItem(new RunPluginAction(pli)));
		return theMenu;
	}
	
	@SuppressWarnings("serial")
	protected class RunPluginAction extends GUDEBase.RunPluginAction {

		public RunPluginAction(PluginInfo pli) {
			super(pli);
		}
		public void actionPerformed(final GUDEEvent arg0) {

			new Thread() {
				public void run() {
										
					List<DataSet> input = DataSetManagerView.getInstance().getSelectedDataSets();
					
					AbstractPlugin apl = getPlugin().getInstance();
					
					if (apl instanceof DatasetFileExportPlugin) {
						//JFileChooser fc = (JFileChooser)(arg0.getSource());
						DatasetFileExportPlugin dsep = (DatasetFileExportPlugin)apl;
						dsep.exportTo(input, arg0.getFileName());			
					} else if (apl instanceof DatasetExportPlugin){
						DatasetExportPlugin dsep = (DatasetExportPlugin)apl;
						dsep.run(input);
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
		return Constants.MC_DATASET_EXPORT;
	}

	@Override
	public void init() {	
	}

	
}
