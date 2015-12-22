/**
 * 
 */
package mayday.core.io.nativeformat;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.AbstractAction;

import mayday.core.DataSet;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.io.dataset.SimpleSnapshot.Snapshot;
import mayday.core.io.gudi.prototypes.DatasetFileImportPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;

@SuppressWarnings("serial")
public class QuickLoadAction extends AbstractAction {
	
	public QuickLoadAction() {		
		super("Quick Load");
	}
	
	public void actionPerformed(ActionEvent e) {
		
		// does not require open files to be closed
		
		new Thread() {
			public void run() {
				PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.zippedsnapshot.read");
				DatasetFileImportPlugin dsi = (DatasetFileImportPlugin)pli.getInstance();
				LinkedList<String> filenames = new LinkedList<String>();
				filenames.add(Snapshot.QUICKSAVE_FILENAME);
				Collection<DataSet> cds = dsi.importFrom(filenames);
				for(DataSet ds : cds) {
					DataSetManagerView.getInstance().addDataSet(ds);
				}
				DataSetManagerView.getInstance().setSelectedDataSets(cds);
				FileRepository.setName(null); // not shown in most recent list
			}
		}.start();
		
		
	}
	
}