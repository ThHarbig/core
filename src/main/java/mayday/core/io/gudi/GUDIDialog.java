package mayday.core.io.gudi;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import mayday.core.MaydayDefaults;
import mayday.core.Preferences;
import mayday.core.pluma.PluginInfo;

public class GUDIDialog implements PropertyChangeListener{
	
    public static final String LAST_OPEN_DIR_KEY = "lastopendir";
    public static final String LAST_USED_FILTER_KEY = "lastusedfilter";
    protected final Preferences prefs = MaydayDefaults.Prefs.getPluginPrefs().node( this.getClass().getName() ); 

	protected JFileChooser fc;
    
	public GUDIDialog(List<GUDIBase.RunPluginAction> plugins, String importObjectName) {
		
		// build list of import filters
		TreeMap<String, FileFilter> FilterList = new TreeMap<String, FileFilter>();
		
		for (GUDIBase.RunPluginAction rpi : plugins) {
			ImportFileFilter iff = new ImportFileFilter(rpi);
			FilterList.put(iff.getDescription(), iff);
		}
		
		// build dialog;
		fc = new JFileChooser(
	            prefs.get(LAST_OPEN_DIR_KEY, System.getProperty("user.home"))
		);

		fc.addPropertyChangeListener(this);
		fc.setAcceptAllFileFilterUsed(false);
		for (FileFilter ff : FilterList.values()) 
			fc.addChoosableFileFilter(ff);
		try {
			fc.setFileFilter(FilterList.get(prefs.get(LAST_USED_FILTER_KEY, "")));
			if (fc.getFileFilter()==null)
				fc.setFileFilter(FilterList.values().iterator().next());
		} catch (Throwable t) {}

		// show dialog
		fc.setDialogTitle("Import "+importObjectName);
		int res = fc.showOpenDialog(null);
        if (res==JFileChooser.APPROVE_OPTION) {
            prefs.put(LAST_OPEN_DIR_KEY, fc.getCurrentDirectory().getAbsolutePath());            
            prefs.put(LAST_USED_FILTER_KEY, fc.getFileFilter().getDescription());
            // call the chosen import plugin
			//run in another thread to free up SWING/AWT thread
            new Thread() {
            	public void run() {
            		((ImportFileFilter)fc.getFileFilter()).getAction().actionPerformed(new GUDIEvent(fc));
            	}
            }.start();
            /*
            try {
            	((ImportFileFilter)fc.getFileFilter()).getAction().actionPerformed(
            			new GUDIEvent(fc));
            } catch ( Exception exception ) {
	            exception.printStackTrace();	            
	            JOptionPane.showMessageDialog( null,
                    "File import failed:\n"+exception.getMessage(),
	                MaydayDefaults.Messages.ERROR_TITLE,
	                JOptionPane.ERROR_MESSAGE );
	        }
	        */
        }            

	}
	
	

	public void propertyChange(PropertyChangeEvent arg0) {
		if (arg0.getPropertyName().equals(JFileChooser.FILE_FILTER_CHANGED_PROPERTY)) {
			JFileChooser fc = (JFileChooser)(arg0.getSource());
			ImportFileFilter iff = (ImportFileFilter)(fc.getFileFilter());
			if (iff==null) return;
			PluginInfo pli = iff.getAction().getPlugin();
			Integer type = (Integer)(pli.getProperties().get(GUDIConstants.FILESYSTEM_IMPORTER_TYPE));
			if (type==null) {
				System.err.println("GUDI: "+pli.getIdentifier()+" has no valid FILESYSTEM_IMPORTER_TYPE");
			} else {
				switch (type) {
				case GUDIConstants.ONEFILE:
					fc.setDialogTitle("Import File");
			        fc.setMultiSelectionEnabled(false);
			        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			        break;
				case GUDIConstants.MANYFILES:
					fc.setDialogTitle("Import Files");
			        fc.setMultiSelectionEnabled(true);
			        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			        break;
				case GUDIConstants.DIRECTORY:
					fc.setDialogTitle("Import Directory");
			        fc.setMultiSelectionEnabled(false);
			        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			        break;			        
			     } 
			}
		}
	}
	
	
	
	protected class ImportFileFilter extends FileFilter {

		protected final GUDIBase.RunPluginAction pluginAction;
		protected final String fileExtensions;
		protected final HashSet<String> extensions = new HashSet<String>();
		protected final String description;
		
	    public ImportFileFilter( GUDIBase.RunPluginAction rpa) {
			pluginAction = rpa;
			PluginInfo pli = rpa.getPlugin();
//			String importerDesc = (String)(pli.getProperties().get(GUDIConstants.IMPORTER_DESCRIPTION));
			String typeDesc = (String)(pli.getProperties().get(GUDIConstants.TYPE_DESCRIPTION));
			fileExtensions = (String)(pli.getProperties().get(GUDIConstants.FILE_EXTENSIONS));
			description = typeDesc + " ["+fileExtensions+"]";// (" + importerDesc + ")";
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
		
		public GUDIBase.RunPluginAction getAction() {
			return pluginAction;
		}
		

	}
	

}
