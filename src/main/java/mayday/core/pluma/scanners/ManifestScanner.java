package mayday.core.pluma.scanners;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

import mayday.core.pluma.PluginManager;

public class ManifestScanner {

	public static final String MANIFEST_ENTRY = "MaydayPlugins";
	public static final String BUILDINFO_ENTRY = "BuildInfo";
	
	private static final ManifestScanner theInstance = new ManifestScanner();

	private ManifestScanner() {};

	public static ManifestScanner getInstance() {
		return theInstance;
	}
	
	// returns ONE class from inside the jar file. This class serves as an anchor to get proper classloaders in JarScanner
	public String scan(PluginManager parent, Manifest root, String source) {
		
		String anchorclass = null;
        
        if (root!=null) { 
            //read the MaydayPlugin attribute from Manifest
           Attributes atts = root.getMainAttributes();
            
           if (atts==null) {
        	   System.err.println("PluginManager: Improper manifest in jar file ");        	   
           } else {
               String value=atts.getValue(MANIFEST_ENTRY);	            
               if (value != null && !value.trim().equals("") ) {
            	   	String[] split=value.split("[\\s]*[,][\\s]*"); //split by comma            	   	
            	   	for(String s:split) {
            	   		s = s.trim();
            	   		if (parent.addClass(s, source))
            	   			anchorclass=s;
            	   	}
               } // value
               value=atts.getValue(BUILDINFO_ENTRY);	     
               if (value != null && !value.trim().equals("") ) {
            	   System.out.println("Build Info: "+value);
               }
           } // atts             	           
        } // manifest
        return anchorclass;
	} //scan
	
}
