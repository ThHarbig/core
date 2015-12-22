package mayday.core.pluma.scanners;

import java.io.File;

import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;


public class FilesystemScanner {

    private static final String MAYDAY_PACKAGE = "mayday";
	private static JarScanner jarScanner = JarScanner.getInstance();
	
	private static final FilesystemScanner theInstance = new FilesystemScanner();
	
	private FilesystemScanner() {};
	
	public static FilesystemScanner getInstance() {
		return theInstance;
	}
	
	public void scan(PluginManager parent, File root) 
	{
		if (root.isDirectory()) {
			handleDirectory(parent, root, null, null);
		} else {			
			handleFile(parent, root, null, checkForRoot(root.getParentFile()));
		}
	}
	
    private synchronized void handleDirectory(PluginManager Parent, File root, String pckg, File topDir)
    {
        if(root==null || !root.exists() || !root.isDirectory()) return;      
        
       	// Root is a directory.
       	// A directory named MAYDAY_PACKAGE starts a java classpath hierarchy
        	
       	// (I) - Set the correct package prefix
        	
       	if (pckg!=null) { 
       		
       		// (A) We are already in the package structure
       		// Now there are two possibilities: 
       		// (1) Either we can extend the package prefix...
       		if (!root.getName().equals(MAYDAY_PACKAGE)) {
       			pckg += "." + root.getName();	
       		} 
       		// (2) or we have hit a case as described in issue 11: .../mayday/.../mayday/
       		// In this case we assume that the second "mayday" is the package root for all its children
       		else {
       			pckg = MAYDAY_PACKAGE;
       			Parent.extendClassPath(root.getParentFile());
       			topDir = root.getParentFile();
       		}
       		
       	} else {		
       		
       		// (B) We are not yet in the package structure
       		// Now there are two possibilities:
       		// (1) The package structure starts right here ...
          	if (root.getName().equals(MAYDAY_PACKAGE)) { 
           		pckg = MAYDAY_PACKAGE;
           		Parent.extendClassPath(root.getParentFile());
       			topDir = root.getParentFile();
           	} 
          	// (2) ... or it doesn't. In that case, we don't need to do anything special
       	}
        	
       	// (II) - Handle all children of this directory
        	
       	File[] files = root.listFiles();
       	if (files!=null) {
       		for (File file : files) {       			     			
       			if (file.isDirectory()) {       					// Recurse into directories
       				if (!file.getName().equals("CVS"))
       					handleDirectory(Parent, file, pckg, topDir);
       			} else {
           			handleFile(Parent,file,pckg, topDir);
       			}      				
        	} // for all files
        } // files!=null
    
    }

    private void handleFile(PluginManager Parent, File file, String pckg, File topDir) {
    	
    	if (file.getName().endsWith(".class")) {		// Add class files
        	if (pckg!=null) {
        		String classname = file.getName();
        		classname = classname.replace(".class", "");
        		Parent.addClass(pckg+"."+classname, file.toString());
        	}    		
    	} else {
            if ( file.getName().endsWith(".jar") ) {		// Scan jar files
            	jarScanner.scan(Parent, file.toString());
            }     		
        	// class files are NOT added
            String path = file.toString();
        	if (topDir!=null) 
        		path = path.replace(topDir.toString(), "");
        	else
        		path = path.replace(Parent.getPluginRoot(), "");
        	if (path.length()>0 && path.startsWith(File.separator))
        		path = path.substring(1);
        	FMFile entry = new FMFile( path , file.toString());
        	Parent.getFilemanager().addFile(entry);
    	}
    }
    
    private File checkForRoot(File parentDir) {
    	File candidate = new File(parentDir+File.separator+MAYDAY_PACKAGE);
    	// the root directory is the one containing the start of mayday's package structure
    	if (candidate.exists() && candidate.isDirectory())
    		return parentDir;
    	else 
    		return null;
    }
    
}
