/**
 * 
 */
package mayday.core.io.nativeformat;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import mayday.core.DataSet;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.io.dataset.SimpleSnapshot.Snapshot;
import mayday.core.io.gude.prototypes.DatasetFileExportPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;

@SuppressWarnings("serial")
public class QuickStoreAction extends AbstractAction {
	
	public final static QuickStoreAction instance = new QuickStoreAction();
	
	QuickStoreAction() {
		super("Quick Save");
	}
	
	public void actionPerformed(ActionEvent e) {
		
		new Thread() {
			public void run() {
				PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.zippedsnapshot.write");
				DatasetFileExportPlugin dsi = (DatasetFileExportPlugin)pli.getInstance();
				List<DataSet> datasets = DataSetManager.singleInstance.getDataSets();
				dsi.exportTo(datasets, Snapshot.QUICKSAVE_FILENAME);
			}
		}.start();

		
	}
}