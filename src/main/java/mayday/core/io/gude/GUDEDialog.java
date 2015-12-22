package mayday.core.io.gude;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import mayday.core.MaydayDefaults;
import mayday.core.Preferences;
import mayday.core.pluma.PluginInfo;

public class GUDEDialog {
	
    public static final String LAST_OPEN_DIR_KEY = "lastopendir";
    public static final String LAST_USED_FILTER_KEY = "lastusedfilter";
    protected final Preferences prefs = MaydayDefaults.Prefs.getPluginPrefs().node( this.getClass().getName() ); 

	protected JFileChooser fc;
    
	public GUDEDialog(List<GUDEBase.RunPluginAction> plugins, String exportObjectName) {
		
		// build list of import filters
		TreeMap<String, FileFilter> FilterList = new TreeMap<String, FileFilter>();
		
		for (GUDEBase.RunPluginAction rpi : plugins) {
			ExportFileFilter eff = new ExportFileFilter(rpi);
			FilterList.put(eff.getDescription(), eff);
		}
		
		// build dialog;

		fc = new JFileChooser(
	            prefs.get(LAST_OPEN_DIR_KEY, System.getProperty("user.home"))
		);

		fc.setDialogTitle("Export "+exportObjectName);
        fc.setMultiSelectionEnabled(false);
        fc.setSelectedFile(new File("Exported "+exportObjectName));
		fc.setAcceptAllFileFilterUsed(false);
		for (FileFilter ff : FilterList.values()) 
			fc.addChoosableFileFilter(ff);
		try {
			fc.setFileFilter(FilterList.get(prefs.get(LAST_USED_FILTER_KEY, "")));
			if (fc.getFileFilter()==null)
				fc.setFileFilter(FilterList.values().iterator().next());
		} catch (Throwable t) {}

		// show dialog
		boolean done=false;
		while (!done) {
			int res = fc.showSaveDialog(null);
			done=true;
			if (res==JFileChooser.APPROVE_OPTION) {
				String fileName = fc.getSelectedFile().getAbsolutePath();
				String baseExtension = ((ExportFileFilter)fc.getFileFilter()).getExtension();
				if (!fileName.endsWith(baseExtension))
					fileName = fileName + "." + baseExtension;
				final String finalFileName = fileName;
				prefs.put(LAST_OPEN_DIR_KEY, fc.getCurrentDirectory().getAbsolutePath());
	            prefs.put(LAST_USED_FILTER_KEY, fc.getFileFilter().getDescription());
				if (new File(fileName).exists()) {
					if (JOptionPane.showConfirmDialog(fc, 
							"Do you really want to overwrite the existing file \""+fileName+"\"?",
							"Confirm file overwrite", 
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
							!=JOptionPane.YES_OPTION) 
    				done=false;
				}
				if (done) {
		            // call the chosen import plugin
					//run in another thread to free up SWING/AWT thread
		            new Thread() {
		            	public void run() {
		            		((ExportFileFilter)fc.getFileFilter()).getAction().actionPerformed(new GUDEEvent(fc, finalFileName));
		            	}
		            }.start();					
				}
            }
        }        
		
	}
	
	

	protected class ExportFileFilter extends FileFilter {

		protected final GUDEBase.RunPluginAction pluginAction;
		protected final String fileExtension;
		protected final String description;
		
	    public ExportFileFilter( GUDEBase.RunPluginAction rpa) {
			pluginAction = rpa;
			PluginInfo pli = rpa.getPlugin();
//			String importerDesc = (String)(pli.getProperties().get(GUDEConstants.EXPORTER_DESCRIPTION));
			String typeDesc = (String)(pli.getProperties().get(GUDEConstants.TYPE_DESCRIPTION));
			fileExtension = ((String)(pli.getProperties().get(GUDEConstants.FILE_EXTENSION))).toLowerCase();
			description = typeDesc + " ["+fileExtension+"]"; // (" + importerDesc + ")";
 	    }
	
		public boolean accept(File arg0) {
			String fext  = (arg0.getName().contains(".") ? arg0.getName().substring(arg0.getName().lastIndexOf(".")+1) : "");
			return (fileExtension=="*" || fileExtension.equals(fext.toLowerCase()) || arg0.isDirectory());
		}

		public String getDescription() {
			return description;
		} 
		
		public String getExtension() {
			return fileExtension;
		}
		
		public GUDEBase.RunPluginAction getAction() {
			return pluginAction;
		}
		

	}
	

}
