package mayday.core.pluma.scanners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;

public class WebstartScanner {

	public static final String WEBSTART_ENTRY = "META-INF/webstart.files";
	
	private static final WebstartScanner theInstance = new WebstartScanner();
	
	private WebstartScanner() {};
	
	public static WebstartScanner getInstance() {
		return theInstance;
	}
	
	
	public void scan(PluginManager parent) {
   		// System.out.println("Webstart-PluginScanner: Starting");
		Class<?> clcontext = this.getClass();
		try {
			// scan for plugin classes
			Enumeration<URL> e = this.getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
			while(e.hasMoreElements()) {
   				URL u = e.nextElement();
   				System.out.println("PluginManager: Reading "+u.toString());
       			try {
       				InputStream is = u.openStream();
       				Manifest mf = new Manifest(is);
       				ManifestScanner.getInstance().scan(parent, mf, "*WEBSTART*");
       			} catch (Exception ex) {
        			System.err.println("PluginManager: Error while loading Manifest");
        			ex.printStackTrace();
        		}
        	}
			// scan for all resources
			e = this.getClass().getClassLoader().getResources(WEBSTART_ENTRY);
			while(e.hasMoreElements()) {			
   				URL u = e.nextElement();
   				System.out.println("PluginManager: Adding "+u.toString());
	        	try {
       				InputStream is = u.openStream();
		        	processlist(is,clcontext,parent,u.toString());
		        } catch (Exception exc) {
		        	System.err.println("PluginManager: Can't process resources list in "+u);
		        	exc.printStackTrace();
		        }

		        }       				

			
       	} catch (Exception e) {
       		System.err.println("PluginManager: Error in WebstartScanner");
       		e.printStackTrace();
        }
	}


	private void processlist(InputStream is, Class<?> clcontext, PluginManager parent, String enclosingJar) throws IOException  {
		BufferedReader rdr = new BufferedReader(new InputStreamReader(is));
		while(rdr.ready()) {
			String nextfile;
			nextfile = rdr.readLine();
			if (nextfile.length()>2 && nextfile.startsWith("./"))
				nextfile = nextfile.substring(2);
			FMFile entry = new FMFile(nextfile, clcontext, enclosingJar);
			parent.getFilemanager().addFile(entry);	   				
		}
	}
	
}
