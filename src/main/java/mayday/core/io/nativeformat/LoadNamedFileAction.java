/**
 * 
 */
package mayday.core.io.nativeformat;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import mayday.core.DataSet;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.io.gudi.prototypes.DatasetFileImportPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;

@SuppressWarnings("serial")
public class LoadNamedFileAction extends FileLoadAction {
	
	protected String name;
	
	public LoadNamedFileAction(String n) {		
		super(new File(n).getName()+" ["+new File(n).getParent()+"]");
		name = n;
	}
	
	protected void load() {
		new Thread("Loading file "+name) {
			public void run() {
				PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.zippedsnapshot.read");
				DatasetFileImportPlugin dsi = (DatasetFileImportPlugin)pli.getInstance();
				LinkedList<String> filenames = new LinkedList<String>();
				filenames.add(name);
				Collection<DataSet> cds = dsi.importFrom(filenames);
				for(DataSet ds : cds) {
					DataSetManagerView.getInstance().addDataSet(ds);
				}
				
				DataSetManagerView.getInstance().setSelectedDataSets(cds);
				
				FileRepository.setName(name);
			}
			
		}.start();
	}
	
	
	public String getFileName() {
		return name;
	}
	
}