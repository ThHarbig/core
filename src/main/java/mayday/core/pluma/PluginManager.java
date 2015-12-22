package mayday.core.pluma;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import mayday.core.EventFirer;
import mayday.core.Mayday;
import mayday.core.MaydayDefaults;
import mayday.core.pluma.filemanager.FileManager;
import mayday.core.pluma.gui.ProgressSplash;
import mayday.core.pluma.prototypes.CorePlugin;
import mayday.core.pluma.scanners.FilesystemScanner;
import mayday.core.pluma.scanners.WebstartScanner;


public class PluginManager {
	
	/** useful to hide classes from pluma, even if they would be perfect plugins */
	@Retention(RetentionPolicy.RUNTIME)
	public @interface IGNORE_PLUGIN {}

	protected String pluginRoot;
	
	// All known working plugins. Key=MasterComponent
	// this is filled by initializePlugins() using data from allplugins
	protected TreeMap<String, TreeSet<PluginInfo> > plugins = new TreeMap<String,TreeSet<PluginInfo>>();

	// Reverse Map of all working plugins: java class --> PluginInfo
	protected HashMap<Class<? extends AbstractPlugin>, PluginInfo> reverseMap 
	= new HashMap<Class<? extends AbstractPlugin>, PluginInfo> ();

	// Reverse Map of all working plugins: pluma id --> PluginInfo
	protected HashMap<String, PluginInfo> idMap = new HashMap<String, PluginInfo> ();

	// All known plugins, key=plugin id
	// this is filled by registerPlugins() using data from plugin_candidates
	protected TreeMap<String, PluginInfo> allplugins = new TreeMap<String, PluginInfo>();

	// All plugin candidates - this is filled by the scanners
	protected HashSet<Class<?>> plugin_candidates = new HashSet<Class<?>>();
	protected HashMap<Class<?>, String> plugin_class_sources = new HashMap<Class<?>, String>();
	
	// this list is filled with classes which are missing dependencies during instantiation. they are tried again later 
	protected HashMap<String,String> classes_missing_dependencies = new HashMap<String,String>();

	protected FileManager availableFiles;

	protected AppendableURLClassLoader classLoader = new AppendableURLClassLoader();

	protected static PluginManager INSTANCE = null;
	protected static final Object instantiationLock = new Object();

	protected boolean already_shut_down=false;

	protected ProgressSplash progress = new ProgressSplash();

	// Event code
	protected EventFirer<PluginInfo, PluginManagerListener> firer = new EventFirer<PluginInfo, PluginManagerListener>() {
		protected void dispatchEvent(PluginInfo event, PluginManagerListener listener) {
			listener.pluginAdded(event);			
		}		
	};		
	
	protected PluginManager() {
		//needed to permit loading of local packages
		System.setSecurityManager(null);
	}

	public static PluginManager getInstance() {
		synchronized (instantiationLock) { // make sure we're not called again during init()
			if (INSTANCE==null) {
				INSTANCE = new PluginManager();
				INSTANCE.init();
			}
			return INSTANCE;			
		}
	}

	public void init() 
	{
		if (availableFiles!=null) {
			shutdown();
			allplugins.clear();
			plugin_candidates.clear();
			plugins.clear();
		}

		availableFiles = new FileManager();

		// get core root path
		String coreRoot = "NOT FOUND";
		try {
			coreRoot = getCoreRootPath();
		} catch (IOException e) {
			e.printStackTrace();
			error("PluginManager: Can't resolve core root path");
		}
		
		// get plugin root path
		pluginRoot = MaydayDefaults.Prefs.getPluginDirectory();

		message0("-----------------------------------------------------");
		message("\n"+
				"-- Core root:   "+coreRoot+"\n"+
				"-- Plugin root: "+pluginRoot);

		// 0 - Scan Webstart files OR scan core root 
		if (MaydayDefaults.isWebstartApplication()) {
			WebstartScanner.getInstance().scan(this);
		} else { 
			if (!coreRoot.equals("NOT FOUND") && !coreRoot.startsWith(pluginRoot)) { //only scan if not enclosed by core root
				FilesystemScanner.getInstance().scan(this, new File(coreRoot));
			}
		}

		// now we can show the progress splash
		if (!Mayday.parameters.noSplash())
			progress.setVisible(true); // core is loaded=10%
		
		// 1 - Scan local plugin dir for all present files, extend classpath, scan jars
		if (!MaydayDefaults.isWebstartApplication() &&
		//GJ (20.11.2014): due to the new security updates in Java 8 adding local source code during start up is no longer permitted!
		//TODO automatically scan afterwards?!
				!pluginRoot.startsWith(coreRoot))				
			FilesystemScanner.getInstance().scan(this, new File(pluginRoot));

		// 2 - make sure that at least ONE location is actually scanned if both are equal.
		if (coreRoot.equals(pluginRoot)) {
			FilesystemScanner.getInstance().scan(this, new File(coreRoot));
		}

		// 2.5 - try again to load classes which had missing dependencies before, they could be in the classpath now
		if (classes_missing_dependencies!=null && classes_missing_dependencies.size()>0) {
			message("Re-trying "+classes_missing_dependencies.size()+" classes with previously missing dependencies.");
			Iterator<Entry<String,String>> classMissingDependency = classes_missing_dependencies.entrySet().iterator();
			classes_missing_dependencies = null;
			while (classMissingDependency.hasNext()) {
				Entry<String, String> e = classMissingDependency.next();
				addClass(e.getKey(), e.getValue());
			}
		}
		
		// 3 - For all found classes, call register
		registerPlugins();	// 0-50%

		// 4 - Solve dependencies and call init() on all classes
		initializePlugins(); // 50-100%

		message("Finished ("+
				this.getNumberOfPlugins()+" plugins, "+
				this.getFilemanager().getNumberOfFiles()+" resources"+
		")");
		message0("-----------------------------------------------------");
		
		// hide the splash again
		if (!Mayday.parameters.noSplash())
			progress.setVisible(false);

	}

	public void startCore() {
		// Start core plugins
		System.out.print("Starting core plugins: ");
		for (PluginInfo pli : getPluginsFor(Constants.MC_CORE)) {
			try {
				System.out.print("["+pli.getName()+"] ");
				((CorePlugin)(pli.getInstance())).run();
			} catch (Throwable t) {
				error("Can't run core plugin "+pli.getIdentifier());
				System.err.println(t.getMessage());
				t.printStackTrace();
			}
		}
		System.out.println();
	}

	protected Constructor<? extends AbstractPlugin> getConstructor(Class<? extends AbstractPlugin> c) {
		if (c.isAnnotationPresent( IGNORE_PLUGIN.class )) {
			message("Class wishes to be ignored: "+c.getName());
			return null;
		}
		
		int mod = c.getModifiers();
		if (Modifier.isAbstract(c.getModifiers()))
			message("Ignoring abstract class: "+c.getName());
		else if (Modifier.isPrivate(c.getModifiers()))
			message("Ignoring private class: "+c.getName());
		else if (Modifier.isProtected(c.getModifiers()))
			message("Ignoring protected class: "+c.getName());
		else if (!Modifier.isPublic(c.getModifiers()))
			message("Ignoring not-public class: "+c.getName());
		else if (c.isMemberClass() && !Modifier.isStatic(mod))
			error("Not a top-level or static class: "+c.getName());
		else {			
			try {
				Constructor<? extends AbstractPlugin> constructor = ((Class<? extends AbstractPlugin>)c).getConstructor();
				return constructor;
			} catch (NoSuchMethodException nsme) {
				error("No public zero-parameter constructor found for "+c.getName());		
			} catch (NoClassDefFoundError ncdfe) {
				error("Dependencies missing for class "+c.getName()+": "+ncdfe.getMessage());
			} catch(Throwable t) {
				error("Trying to instantiate "+c.getName()+" "+
						"-> FAILED: "+t.getClass().getCanonicalName()+": "+t.getMessage());
				if (!(t instanceof InstantiationException))
					t.printStackTrace();
			}
		}
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void registerPlugins() {
		message0("-----------------------------------------------------");
		message("Checking "+plugin_candidates.size() + " classes...");
		int percentagecount=0;
		for (Class c : plugin_candidates) {
			try {
				Constructor<? extends AbstractPlugin> constructor = getConstructor(c);
				if (constructor!=null) {
					AbstractPlugin apl = constructor.newInstance();
					PluginInfo pli = apl.register();
					if (registerPlugin(c, pli))
						pli.getProperties().put(Constants.CLASS_INSTANCE, apl); // store the instance for speedup
				}
			} catch (PluginManagerException plme) {
				if (MaydayDefaults.isDebugMode())
					error(c.getCanonicalName()+": "+plme.getMessage());
			} catch (Throwable goTellTheFish) {
				// do not care
			}
			++percentagecount;
			float percentage = (float)percentagecount / (float)plugin_candidates.size();
			progress.setProgress((int)(100*percentage/2));
		}
		plugin_candidates.clear();
		plugin_class_sources.clear();
	}
	
	private boolean registerPlugin(Class<?> c, PluginInfo pli) {
		if (pli==null) {
			if (SurrogatePlugin.class.isAssignableFrom(c)) {
				message("Found surrogate plugin class "+c.getName());
			} else {
				error("register() returned \"null\" for "+c.getName()+" ");
			}
			return false;
		}        
		
		// if we come here, we are almost done
		if (allplugins.containsKey(pli.getIdentifier())) {
			PluginInfo p1 = allplugins.get(pli.getIdentifier());
			if (p1!=pli) {
				Class<?> c1 = pli.getPluginClass();
				Class<?> c2 = p1.getPluginClass();
				if (c1==c2)
					message("Updating plugin information for "+pli.getIdentifier());
				else {
					error(""+pli.getIdentifier()+" has been registered twice, replacing:\n"+
							"-- "+c1+" via "+plugin_class_sources.get(c1)+"\n"+
							"-- "+c2+" via "+plugin_class_sources.get(c2)
					);
				}
			}
		}
		allplugins.put(pli.getIdentifier(),pli);
		return true;
	}

	private void initializePlugins() {
		// iterate over all plugins, resolve dependencies
		int percentagecount=0;
		int totalcount = allplugins.values().size();
		for (PluginInfo pli : allplugins.values() ) {
			initializePlugin(pli, "PluginManager: ");
			++percentagecount;
			float percentage = (float)percentagecount / (float)totalcount;
			progress.setProgress(50+(int)(100*percentage/2));
		}
	}

	@SuppressWarnings("unchecked")
	private void initializePlugin(PluginInfo pli, String debug) 
	{
		if (pli.isInitialized()) 
			return;

		//		System.out.println(debug+"Initializing "+pli.getIdentifier());

		// satisfy dependencies
		for (String depends : pli.getDependencies()) {
			PluginInfo dependspli = allplugins.get(depends);

			if (dependspli != null) {
				initializePlugin(dependspli, debug+"  ");
				if (dependspli.hasUnmetDependencies()) {
					dependspli = null;
				}
			}

			if (dependspli == null) {
				error(debug+"Initializing "+pli.getIdentifier());
				error(debug+" -> Unmet dependency: "+depends);
				pli.setUnmetDependencies(true);
				Vector<String> missingDeps = 
					(Vector<String>)(pli.getProperties().get(Constants.MISSING_DEPENDENCIES));
				if (missingDeps==null) {
					missingDeps = new Vector<String>();
					pli.getProperties().put(Constants.MISSING_DEPENDENCIES, missingDeps);
				}
				missingDeps.add(depends);				
			}

		}

		if (!pli.hasUnmetDependencies()) {
			try {
				AbstractPlugin instance = ((AbstractPlugin)pli.getProperties().get(Constants.CLASS_INSTANCE));
				if (instance==null)
					instance = pli.newInstance();
				instance.init();
				addPlugin(pli);				
			} catch (Throwable t) {
				error("An exception occurred while initializing a plugin that was registering ok: "+pli.getIdentifier());
				t.printStackTrace();
				pli.getProperties().put(Constants.MISSING_DEPENDENCIES, new Vector<String>());
			}
		}

		// remove class instance from memory
		pli.getProperties().remove(Constants.CLASS_INSTANCE);

		pli.setInitialized(true);
	}

	private void addPlugin(PluginInfo pli) {
		TreeSet<PluginInfo> plvec;
		plvec = plugins.get(pli.getMasterComponent());
		if (plvec==null) {
			plvec = new TreeSet<PluginInfo>();
			plugins.put(pli.getMasterComponent(),plvec);
		}
		plvec.add(pli);
		// add to reverse list
		reverseMap.put(pli.getPluginClass(), pli);
		idMap.put(pli.getIdentifier(), pli);
	}

	/** adds a plugin AFTER PluginManager has done the scanning and validation of found plugins */
	public void addLatePlugin(PluginInfo pli) {
		if (getConstructor(pli.getPluginClass())==null) {
			error("[Late Plugin] rejected "+pli.getPluginClass().getCanonicalName());
		} else {
			registerPlugin(pli.getPluginClass(), pli);
			initializePlugin(pli, "PluginManager: [Late Plugin] ");
			StackTraceElement ste = Thread.currentThread().getStackTrace()[1];			
			plugin_class_sources.put(pli.getPluginClass(), "[Late Plugin]: "+ste);
			firer.fireEvent(pli);
		}
	}

	public Set<PluginInfo> getPluginsFor(String MasterComponent) {
		Set<PluginInfo> plvec;		
		if (MasterComponent==null) {
			throw new RuntimeException("Can not retrieve plugins for the NULL mastercomponent");
		}
		plvec = plugins.get(MasterComponent);
		if (plvec==null) {
			plvec = Collections.emptySet();
		} else {
			plvec = Collections.unmodifiableSet(plvec);
		}
		return plvec;		
	}
	
	public Set<PluginInfo> getPluginsFor(String[] MasterComponents) {
		if (MasterComponents.length==1)
			return getPluginsFor(MasterComponents[0]);
		
		Set<PluginInfo> plvec = new TreeSet<PluginInfo>();
		for (String MC:MasterComponents)
			plvec.addAll(getPluginsFor(MC));
		return plvec;		
	}

	public PluginInfo getPluginFromID(String identifier) {
		return idMap.get(identifier);
	}

	public PluginInfo getPluginFromClass(Class<? extends AbstractPlugin> clazz) {
		return reverseMap.get(clazz);
	}

	public Set<String> getMasterComponents() {
		return plugins.keySet();
	}

	public Vector<PluginInfo> getBrokenPlugins() {
		Vector<PluginInfo> ret = new Vector<PluginInfo>();
		for (PluginInfo pli : allplugins.values()) {
			if (pli.getProperties().containsKey(Constants.MISSING_DEPENDENCIES))
				ret.add(pli);
		}
		return ret;
	}

	public AbstractPlugin getInstance(String pluginID) {
		PluginInfo pli = allplugins.get(pluginID);
		AbstractPlugin result = pli.getInstance();		
		return result;
	}

	public FileManager getFilemanager() {
		return availableFiles;
	}

	public Class<?> loadClass(String classname, String theSource) {
		try {
			return classLoader.loadClass(classname);
		} catch (NoClassDefFoundError ncdf) {
			if (classes_missing_dependencies!=null) {
				classes_missing_dependencies.put(classname, theSource);
				message(""+classname+" has missing dependency: "+ncdf.getMessage()+" -- will try again later");				
			} else {
				error(""+classname+" has missing dependency: "+ncdf.getMessage(), true);				
			}			
		} catch (Throwable e) { // NoClassDefFound is an ERROR, not an EXCEPTION
			error("Can't load class \'"+classname+"\'\n"+
					"Throwable type: "+e.getClass().getCanonicalName()+"\n"+
					"Throwable message: "+e.getMessage(), true);
		}
		return null;
	}

	public boolean addClass(String classname, String theSource) {
		// get the class object, classpath has to be properly extended for this class
		Class<?> theClass = loadClass(classname, theSource);
		if (theClass!=null 
				&& !Modifier.isInterface(theClass.getModifiers())
				&& !Modifier.isAbstract(theClass.getModifiers())
				&& AbstractPlugin.class.isAssignableFrom(theClass)) 
		{
			String prevLoc = plugin_class_sources.get(theClass);
			if (prevLoc!=null) {
				error("Class "+classname+" was found again!"+"\n"+
						"Previous location: "+prevLoc+"\n"+
						"New location:      "+theSource);
			} else {
				plugin_candidates.add(theClass);
				plugin_class_sources.put(theClass, theSource);
			}
			return true; // class can be loaded, so it can be used as anchor for JARred resources
		}
		return false;
	}

	protected Method invokableAddURL;

	synchronized public void extendClassPath(URL parent) {  
		message("Extending Classpath by "+parent);

		classLoader.addURL(parent);

		// try to make the system classloader accept our extended classpath
		if (invokableAddURL==null) {
			Method method;
			try {
				method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
				method.setAccessible(true);
				invokableAddURL = method;
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}

		//    	System.out.println(invokableAddURL);

		if (invokableAddURL==null)
			return; // no can do

		//    	System.out.println("updating parent classloader");

		try {
			invokableAddURL.invoke(Thread.currentThread().getContextClassLoader(), new Object[] {parent});
			String Classpath = System.getProperty("java.class.path");
			Classpath = Classpath + System.getProperty("path.separator") + parent.toString();
			System.setProperty("java.class.path", Classpath);
			//        	System.out.println("update ok!");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}    	
	}    

	public void extendClassPath(java.io.File file) {
		try {
			extendClassPath(file.toURI().toURL());
		} catch (Exception e) {
			error("Can't extend classpath for "+file+"\n"+e.getMessage());
		}
	}

	public URL[] getClassPath() {
		return classLoader.getURLs();
	}

	public int getNumberOfPlugins() {
		int ret=0;
		for (String mc : this.getMasterComponents()) 
			ret+=this.getPluginsFor(mc).size();
		return ret;
	}

	private ClassLoader getCoreClassLoader()
	{
		ClassLoader c = this.getClass().getClassLoader();
		if (c==null) c = ClassLoader.getSystemClassLoader();

		return c;
	}

	private File getCoreRootFile()
	{
		String pmRelative = PluginManager.class.getName().replace('.', '/').concat(".class"); 
		URL url = getCoreClassLoader().getResource(pmRelative);

		String name = url.getPath();
		if(name.contains("!")) { 		//root is an archive
			name = name.split("!",2)[0];
		} else { 						//root is a directory in the file system
			name = name.substring(0, name.length()-pmRelative.length());
		}

		if (name.startsWith("file:")) { 
			name = name.substring("file:".length());
		}

		try {
			name = URLDecoder.decode(name, "UTF-8");
		} catch(UnsupportedEncodingException ex) {
			error("Can't decode CoreRootFile name\n"+ex.getMessage());
			ex.printStackTrace();
		}        
		return new File(name);
	}

	private String getCoreRootPath() throws IOException {
		if (MaydayDefaults.isWebstartApplication()) 
			return getCoreRootFile().toString();
		else
			return getCoreRootFile().getCanonicalPath();

	
	}
	
	public boolean addNativeLibraryRelativePath(String relativePath) {
		return addNativeLibraryPath(pluginRoot + File.separatorChar + relativePath);
	}
	
	public boolean addNativeLibraryPath(String fullPath) {
		
		try {
			// Reset the "sys_paths" field of the ClassLoader to null.
			Class<ClassLoader> clazz = ClassLoader.class;
			Field field = clazz.getDeclaredField("sys_paths");
			boolean accessible = field.isAccessible();
			if (!accessible)
				field.setAccessible(true);
			
			String libPath = System.getProperty("java.library.path");
			String newLibPath = libPath + File.pathSeparatorChar + fullPath;
			
			// Reset ClassLoader's field to null so that whenever "System.loadLibrary" is called, 
			// it will be reconstructed with the changed value of java.library.path
			field.set(clazz, null);
			System.setProperty("java.library.path", newLibPath);
			message("New Java Library Path: " + System.getProperty("java.library.path"));
			
			field.setAccessible(accessible);
			
			message("Added native library path "+fullPath);
			
			return true;

		} catch (Exception e) {
			error("Could not add native library path: "+fullPath+"\n"+
				e.getClass().getSimpleName()+":"+e.getMessage());
		}
		
		return false;
		
	}

	public void shutdown() {
		if (already_shut_down)
			return;
		already_shut_down=true;
		message("Shutting down...");
		for (TreeSet<PluginInfo> plugin_subset : plugins.values())
			for (PluginInfo pli : plugin_subset) {				
				try {
					if (overridesAbstractPluginMethod(pli, "unload")) {
						message("Unloading "+pli.getIdentifier());
						pli.getInstance().unload();
					}
				} catch (Throwable t) {
					error("Error when unloading plugin "+pli.getIdentifier()+"\n"+
							t.getMessage());
					t.printStackTrace();
				}
			}
		message("Shutdown complete.");	
	}


	protected boolean overridesAbstractPluginMethod(PluginInfo pli, String methodName, Class<?>... paramTypes) {
		try {
			Method theMethod = pli.getPluginClass().getMethod(methodName, paramTypes);
			Class<?> decClass = theMethod.getDeclaringClass();
			return (!decClass.equals(AbstractPlugin.class));
		} catch (Throwable t) {			
			error(t.getMessage());
			t.printStackTrace();
			return false;
		}
	}
	
	/** add a listener that is notified whenever a late plugin is added via addLatePlugin() */
	public void addPluginManagerListener(PluginManagerListener pml) {
		firer.addListener(pml);
	}
	
	public void removePluginManagerListener(PluginManagerListener pml) {
		firer.removeListener(pml);
	}

	/** create a clone of a plugin with the same settings */
	@SuppressWarnings("unchecked")
	public <T extends AbstractPlugin>  T getInitializedClone(T apl) {
		T clone = (T)getPluginFromClass(apl.getClass()).newInstance();
		if (clone.getSetting()!=null) {
			clone.getSetting().fromPrefNode(apl.getSetting().toPrefNode());
		}
		return clone;
	}
	
	public AppendableURLClassLoader getClassLoader() {
		return classLoader;
	}
	
	
	protected static String padRight(String s, int n) {
	     return String.format("%1$-" + n + "s", s);  
	}
	
	public void dumpToFile(File file) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));

			for (String MC : PluginManager.getInstance().getMasterComponents()) {
				// add the component
				bw.write(MC+"\n");
				for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(MC)) {
					bw.write("  "+padRight(pli.getName(),50)+" "+padRight(pli.getIdentifier(),50)+" "+pli.getPluginClass()+"\n");
				}
			}
			bw.flush();
			bw.close();
			message("Saved plugin list file to \""+file.getAbsolutePath()+"\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getPluginRoot() {
		return pluginRoot;
	}
	
	protected final static void message(String m) {
		message0("PluginManager: "+m);
	}
	
	protected final static void message0(String m) {
		System.out.println(m);
	}

	protected final static void error(String m) {
		error(m, false);
	}
	
	protected final static void error(String m, boolean ignore) {
		System.err.println("PluginManager: " +m);
//		if (MaydayDefaults.isDebugMode() && !ignore) {
//			System.err.println("In DEBUG mode, Mayday terminates when PluginManager detects an error.");
//			System.exit(1);
//		}
	}
	
}
