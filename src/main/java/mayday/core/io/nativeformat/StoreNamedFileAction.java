/**
 * 
 */
package mayday.core.io.nativeformat;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import mayday.core.DataSet;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.io.gude.prototypes.DatasetFileExportPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;

@SuppressWarnings("serial")
public class StoreNamedFileAction extends AbstractAction {
		
	public static final StoreNamedFileAction instance = new StoreNamedFileAction();
	
	StoreNamedFileAction() {		
		super("Save");
		setEnabled(FileRepository.hasFile());
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if (!FileRepository.hasFile())
			return;
		
		new Thread() {
			public void run() {
				PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.zippedsnapshot.write");
				DatasetFileExportPlugin dsi = (DatasetFileExportPlugin)pli.getInstance();
				List<DataSet> datasets = DataSetManager.singleInstance.getDataSets();
				dsi.exportTo(datasets, FileRepository.getName());
			}
		}.start();

		
	}
	
	
}