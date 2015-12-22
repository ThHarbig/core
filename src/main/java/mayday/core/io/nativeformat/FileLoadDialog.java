package mayday.core.io.nativeformat;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import mayday.core.DataSet;
import mayday.core.MaydayDefaults;
import mayday.core.Preferences;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.io.gudi.prototypes.DatasetFileImportPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;

public class FileLoadDialog {
	
    public static final String LAST_OPEN_DIR_KEY = "lastopendir";
    public static final String LAST_USED_FILTER_KEY = "lastusedfilter";
    protected final Preferences prefs = MaydayDefaults.Prefs.getPluginPrefs().node( this.getClass().getName() ); 

	protected JFileChooser fc;
    
	public FileLoadDialog() {
		
		PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.zippedsnapshot.read");
		final DatasetFileImportPlugin dsi = (DatasetFileImportPlugin)pli.getInstance();
		final LinkedList<String> filenames = new LinkedList<String>();
		
		ImportFileFilter iff = new ImportFileFilter(pli);
		
		// build dialog;
		fc = new JFileChooser(
	            prefs.get(LAST_OPEN_DIR_KEY, System.getProperty("user.home"))
		);

		
		fc.setAcceptAllFileFilterUsed(false);
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		fc.setFileFilter(iff);
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		// show dialog
		fc.setDialogTitle("Open Mayday Project");
		int res = fc.showOpenDialog(null);
        if (res==JFileChooser.APPROVE_OPTION) {
            prefs.put(LAST_OPEN_DIR_KEY, fc.getCurrentDirectory().getAbsolutePath());            
         
            filenames.add(fc.getSelectedFile().getAbsolutePath());
            
			//run in another thread to free up SWING/AWT thread
            new Thread() {
            	public void run() {
            		Collection<DataSet> cds = dsi.importFrom(filenames);
            		for(DataSet ds : cds) {
            			DataSetManagerView.getInstance().addDataSet(ds);
            		}
            		DataSetManagerView.getInstance().setSelectedDataSets(cds);
                    FileRepository.setName(fc.getSelectedFile().getAbsolutePath());                    
            	}
            }.start();          
        }            
        
        

	}
	
	


	
	
	
	protected class ImportFileFilter extends FileFilter {

		protected final String fileExtensions;
		protected final HashSet<String> extensions = new HashSet<String>();
		protected final String description;
		
	    public ImportFileFilter( PluginInfo pli) {
//			String importerDesc = (String)(pli.getProperties().get(GUDIConstants.IMPORTER_DESCRIPTION));
			String typeDesc = (String)(pli.getProperties().get(GUDIConstants.TYPE_DESCRIPTION));
			fileExtensions = (String)(pli.getProperties().get(GUDIConstants.FILE_EXTENSIONS));
			description = typeDesc + " ["+fileExtensions+"]"; // (" + importerDesc + ")";
			String[] exts = fileExtensions.split("[|]");
			for (String s : exts)
				extensions.add(s.toLowerCase());
 	    }
	
		public boolean accept(File arg0) {
			String fext  = (arg0.getName().contains(".") ? arg0.getName().substring(arg0.getName().lastIndexOf(".")+1) : "");
			return (fileExtensions=="*" || extensions.contains(fext.toLowerCase()) || arg0.isDirectory());
		}

		public String getDescription() {
			return description;
		} 

	}
	

}
