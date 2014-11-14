package mayday.core.pluma.filemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import mayday.core.pluma.PluginManager;

public class FMFile {
	
	public static final int SOURCE_FILE = 0;
	public static final int SOURCE_JAR = 1;
	
	public String Name;
	public String Path;
	public int Source;
	public ClassLoader ContextClassLoader;
	private FileManager parent;
	private String originalPath;
	private FMFile alternative;
	
	public FMFile(String name, String originalPath) {
		if (name.contains(File.separator)) {
			Path = name.substring(0, name.lastIndexOf(File.separatorChar));
			Name = name.substring(Path.length()+1);
		} else {
			Path = "";
			Name=name;
		}
		// make all paths linux-like
		Path=Path.replace(File.separator, "/");  
		this.originalPath=originalPath;
		Source = SOURCE_FILE;
	}
	
	public FMFile(String name, Class<?> classLoaderContextAnchor, String JarPath) {
		// coming from a jar, we know that the path Separator is ALWAYS "/"
		if (name.contains("/")) {
			Path = name.substring(0, name.lastIndexOf("/"));
			Name = name.substring(Path.length()+1);
		} else {
			Path = "";
			Name=name;
		}
		originalPath = JarPath;
		Source = SOURCE_JAR;
		ContextClassLoader = classLoaderContextAnchor.getClassLoader();
	}
	
	/** 
	 * @return the full path to the resource in the filesystem, or null if the resource is in a jar.
	 */
	public String getFullPath() {
		if (Source==SOURCE_FILE) {
			return originalPath;
		} else
			return null;
	}
	
	public InputStream getStream() {
		InputStream is=null;
		switch(Source) {
		case SOURCE_FILE:
			try {
				is = new FileInputStream(getFullPath());
			} catch (Exception e) {
				System.err.println("PluginManager: Can't open filesystem resource "+getFullPath()+"\n"+e.getMessage());
			}
			break;
		case SOURCE_JAR: // coming from webstart or regular jar. Use context to get the resource
			//System.out.println("getStream from JAR: "+Path+" / "+Name);
			is = ContextClassLoader.getResourceAsStream(Path+"/"+Name);
		}
		return is;
	}

			/*
	private void replaceBy(FMFile other) {
		Name=other.Name;
		Path=other.Path;
		Source=other.Source;
		ContextClassLoader=other.ContextClassLoader;
		parent=other.parent;
		originalPath=other.originalPath;
		alternative=other.alternative;
	}*/
	
	/**
	 * Extracts a resource to the filesystem, if necessary
	 * @return true if the resource is now present in the filesystem, false otherwise
	 */
	public boolean extract() {
		if (Source==SOURCE_JAR) {
			InputStream is = getStream();
			if (is==null) { 
				System.err.println("PluginManager: Could not extract resource "+Path+"/"+Name+"" +
						"\nException: Input Stream is null");
				return false;
			}
			String tgtpath = PluginManager.getInstance().getPluginRoot()+File.separator
				+this.Path.replace("/", File.separator)+File.separator+this.Name;
			try {
				// make directory structure if needed
				FileManager.copy(is, tgtpath);
			} catch (Exception e) {
				System.err.println("PluginManager: Could not extract resource "+Path+"/"+Name+"\n" +
						"Target file "+tgtpath+"\nException: "+e.getMessage());
				return false;
			}
			originalPath = tgtpath;
			Source = SOURCE_FILE;
			return true;
		}
		return true; 
	}
	
	public boolean force_extract() {
		if (Source==SOURCE_JAR) 
			return extract();
		else
			if (alternative!=null) {
				boolean res = alternative.force_extract();
				// replace previous entry
				//this.replaceBy(alternative);
				return res;
			}
				
			else
				return false; 
	}


	public FileManager getParent() {
		return parent;
	}

	public void setParent(FileManager parent) {
		this.parent = parent;
	}
	
	public void setAlternative(FMFile alt) {
		this.alternative=alt;
	}
	
	public String toString() {
		return "["+(Source==SOURCE_FILE ? "F" : "J")+"] "+Path+"/"+Name+
			   " ["+originalPath+"]";
	}
}