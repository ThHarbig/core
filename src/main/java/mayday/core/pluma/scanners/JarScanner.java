package mayday.core.pluma.scanners;	

import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;

public class JarScanner {

	private static final JarScanner theInstance = new JarScanner();

	private JarScanner() {};

	public static JarScanner getInstance() {
		return theInstance;
	}
	
	public void scan(PluginManager parent, String root) 
	{
		parent.extendClassPath(new java.io.File(root));
		
		try {
			JarFile jar = new JarFile( root ); 
	        Manifest manifest = jar.getManifest();
	        
	        String anchor = (String)(ManifestScanner.getInstance().scan(parent, manifest, root));
	        Class<?> clcontext = (anchor==null) ? null : parent.loadClass(anchor, root);
	        
	        if (clcontext == null) {
	        	System.err.println("PluginManager: Resources have no anchor class in "+root);
	        } else {
		        // list all resources
		        Enumeration<JarEntry> enm = jar.entries(); 
		        while (enm.hasMoreElements()) {
		            JarEntry jent = enm.nextElement();
		            if (!jent.getName().endsWith(".class") && !jent.isDirectory() && !jent.getName().startsWith("META-INF") ) {
		            	FMFile entry = new FMFile(jent.getName(), clcontext, root);
		            	parent.getFilemanager().addFile(entry);
		            } 
		        }	        	
	        }
	        
            jar.close();
		} catch(Exception e) {
	       	System.err.println("PluginManager: Couldn't load jar "+root);
	       	e.printStackTrace();
	    }
	}

}
