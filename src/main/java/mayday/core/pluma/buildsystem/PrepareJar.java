package mayday.core.pluma.buildsystem;

//import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
//import java.io.InputStream;
//import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FileManager;
import mayday.core.pluma.scanners.JarScanner;
import mayday.core.pluma.scanners.ManifestScanner;
import mayday.core.pluma.scanners.WebstartScanner;

/* use an existing jar file and 
 * 1) add AbstractPlugins to the Manifest
 * 2) add a list of all resources for Mayday Webstart
 */
<<<<<<< HEAD
// TODO imrove parameter parsing (perhaps a help function?)
=======
>>>>>>> bd8805447b59c9475dfbcdf6f975397ad3c2209e
public class PrepareJar {
	
	private final static PrepareJar helper = new PrepareJar();
	
	public static void main(String[] args) {
<<<<<<< HEAD

		// name of the output files
		// memo: Adjust in build.bash
		String MANIFEST = "plugins.mf";
		String RESOURCES = WebstartScanner.WEBSTART_ENTRY;

=======
	
>>>>>>> bd8805447b59c9475dfbcdf6f975397ad3c2209e
		if (args.length<2) {
			System.err.println("Arguments: JarFile OutputFolder");
			System.exit( 1);
		}
		
		File jar = new File(args[0]);
		if (!jar.exists()) {
			System.err.println("Jarfile "+jar+" not found.");
			System.exit( 1);
		}
		try {
<<<<<<< HEAD
			new File(args[1] + File.separator + MANIFEST);
			new File(args[1] + File.separator + RESOURCES);
=======
			new File(args[1]+"/META-INF").mkdirs();
>>>>>>> bd8805447b59c9475dfbcdf6f975397ad3c2209e
		} catch(Exception e) {
			System.err.println("Can't write to output path");
			System.exit( 1);
		}		
		
		try {
			JarFile jarfile = new JarFile(jar);

			StringBuilder PluginList = new StringBuilder();
			StringBuilder FileList = new StringBuilder();

			tinyPluginManager tpl = helper.new tinyPluginManager();
			tpl.extendClassPath(jar);
			
			Vector<String> classNames = new Vector<String>();
			
			Enumeration<JarEntry> jarentries = jarfile.entries();
			while (jarentries.hasMoreElements()) {
				JarEntry entry = jarentries.nextElement();
				if (!entry.isDirectory()) {
					if (!entry.getName().endsWith(".class")) {
						FileList.append(entry.getName()+"\n");
					} else {
						String classname = entry.getName().replace("/", ".");
						classname = classname.substring(0, classname.lastIndexOf(".class"));
						//System.out.println("Classname: "+classname);
						if (tpl.addClass(classname)) {
							//System.out.println("Accept");
							classNames.add(classname);
						}
					}
				}
			}
							
			//InputStream is = jarfile.getInputStream(jarfile.getEntry("META-INF/MANIFEST.MF"));
			//BufferedReader br = new BufferedReader(new InputStreamReader(is));
			//while (br.ready()) {
			//				PluginList.append(br.readLine()+"\n");
			//}
			PluginList.append(ManifestScanner.MANIFEST_ENTRY+": ");
			
			jarfile.close();

			for (int i=0; i!=classNames.size(); ++i) { 
				String cname = classNames.get(i);
				//System.out.println("Canonical Name: "+ cname);
				PluginList.append(cname);
				if (i<classNames.size()-1) 
					PluginList.append(", ");
				else
					PluginList.append("\n");
			}
			make72Safe(PluginList);
			System.out.println(PluginList.toString());
			
			
<<<<<<< HEAD
			BufferedWriter bowr = new BufferedWriter(new FileWriter(args[1]+File.separator+MANIFEST));
=======
			BufferedWriter bowr = new BufferedWriter(new FileWriter(args[1]+File.separator+"META-INF/MANIFEST.MF"));
>>>>>>> bd8805447b59c9475dfbcdf6f975397ad3c2209e
			bowr.append(PluginList.toString());
			bowr.flush();
			bowr.close();
			
<<<<<<< HEAD
			bowr = new BufferedWriter(new FileWriter(args[1]+File.separator+RESOURCES));
=======
			bowr = new BufferedWriter(new FileWriter(args[1]+File.separator+WebstartScanner.WEBSTART_ENTRY));
>>>>>>> bd8805447b59c9475dfbcdf6f975397ad3c2209e
			bowr.append(FileList.toString());
			bowr.flush();
			bowr.close();
			
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit( 1);
		}
		
		
		System.exit( 0);
	}
	
    static void make72Safe(StringBuilder line) {
        int length = line.length();
        if (length > 72) {
            int index = 70;
            while (index < length - 2) {
                line.insert(index, "\r\n ");
                index += 72;
                length += 3;
            }
        }
    }
	
    private class tinyPluginManager extends PluginManager {
    	
    	public void init() {
    		availableFiles = new FileManager(); 
    		// do nothing else
    	}
    	
    	@SuppressWarnings("unused")
		public void new_init(String file) {
    		JarScanner.getInstance().scan(this, file);
    	}
    	
        public boolean addClass(String cname)
        {
        	try {
        		Class<?> c = loadClass(cname, null);
        		
        		if (c==null) {
        			return false;
        		}
        		
        		int modifiers = c.getModifiers();        		
        	            
        		if (Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers)) {
        			return false;
        		}

        		Class<?> iter=c;
        		String canName;
        		String AbstractPluginClassName = AbstractPlugin.class.getCanonicalName();
        		
        		// iterate until the end of the object hierarchy is found,
        		// tolerate unnamed classes
        		while ( (canName=iter.getCanonicalName())==null 
        				|| !canName.equals("java.lang.Object")) {
        			if (canName!=null)
        				if (canName.equals(AbstractPluginClassName))
        					return true;
        			iter=iter.getSuperclass();
        			if (iter==null)
        				break;
        		}
        	} catch(Throwable t) {
        		System.err.println("Exception when examining class hierarchy for: "+cname);
        		System.err.println("-- Exception type: "+t.getClass().getCanonicalName());        		
        		System.err.println("-- Exception message: "+t.getMessage());
        		System.err.println("-- Exception Stack Trace:");
        		t.printStackTrace();
        		System.err.println("-- End of Stack Trace.");
        	}
        	return false;
        }
        
        @SuppressWarnings("unused")
		public Enumeration<Class<?>> getAllClasses() {
        	return new Vector<Class<?>>(this.plugin_candidates).elements();
        }
    }
    
}
