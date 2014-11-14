package mayday.core.io.nativeformat;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import mayday.core.DataSet;
import mayday.core.MaydayDefaults;
import mayday.core.Preferences;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.io.gude.prototypes.DatasetFileExportPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;

public class FileStoreDialog {
	
    public static final String LAST_SAVE_DIR_KEY = "laststoredir";
    protected final Preferences prefs = MaydayDefaults.Prefs.getPluginPrefs().node( this.getClass().getName() ); 

	protected JFileChooser fc;
    
	public FileStoreDialog() {
		
		PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.zippedsnapshot.write");
		final DatasetFileExportPlugin dsi = (DatasetFileExportPlugin)pli.getInstance();
		final List<DataSet> datasets = DataSetManager.singleInstance.getDataSets();
		
		// build dialog;
		fc = new JFileChooser(
	            prefs.get(LAST_SAVE_DIR_KEY, System.getProperty("user.home"))
		);

		
		fc.setDialogType(JFileChooser.SAVE_DIALOG);

		// show dialog
		fc.setDialogTitle("Save Mayday Project");
		
		int res = fc.showSaveDialog(null);
        if (res==JFileChooser.APPROVE_OPTION) {
            prefs.put(LAST_SAVE_DIR_KEY, fc.getCurrentDirectory().getAbsolutePath());            
         
            String _filename = fc.getSelectedFile().getAbsolutePath();
            if (!_filename.toLowerCase().endsWith(".maydayz"))
            	_filename+=".maydayz";
            
            final String filename = _filename;
            
            if (new File(filename).exists()) {
				if (JOptionPane.showConfirmDialog(fc, 
						"Do you really want to overwrite the existing file \""+filename+"\"?",
						"Confirm file overwrite", 
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
						!=JOptionPane.YES_OPTION) 
				return;
			}
            
			//run in another thread to free up SWING/AWT thread
            new Thread() {
            	public void run() {
            		dsi.exportTo(datasets, filename);
            		FileRepository.setName(filename);
            	}
            }.start();          
        }            

	}

}
