package mayday.core.pluma.filemanager;

import java.util.TreeMap;

public class FMDirectory {
	public String Name;
	TreeMap< String, FMDirectory > subDirs = new TreeMap< String, FMDirectory >();
	TreeMap< String, FMFile> files = new TreeMap<String, FMFile>();
	//private long changeCount=0;
	
	public FMDirectory(String name) {
		Name = name;
	}
	
	public FMDirectory getDirectory(String subPath, boolean createPath) {
		if (subPath.startsWith("/"))
			subPath = subPath.substring(1);
		if (subPath.equals("")) return this;
		String nextSubPath;
		String nextPath;
		if (subPath.contains("/")) {
			nextPath = subPath.substring(subPath.indexOf("/")+1);
			nextSubPath = subPath.substring(0,subPath.indexOf("/"));
		} else {
			nextSubPath = subPath;
			nextPath = "";
		}
		FMDirectory nextRoot = this.subDirs.get(nextSubPath);
		if (nextRoot==null && createPath) {
			nextRoot = new FMDirectory(nextSubPath);
			putDirectory(nextRoot);
		}
		if (nextRoot!=null) {
			return nextRoot.getDirectory(nextPath, createPath);
		}
		return null;			
	}
	
	public FMFile getFile(String filename) {
		return files.get(filename);
	}	 
	
	public void putFile(FMFile file) {
		//changeCount++;
		files.put(file.Name, file);
	}
	
	public void putDirectory(FMDirectory dir) {
		//changeCount++;
		subDirs.put(dir.Name, dir);
	}
	
	
	public String toString() {
		StringBuilder res = new StringBuilder("Directory listing for  "+this.Name+"\n");
		for ( FMFile fmf : getFiles(true) ) {
			res.append(fmf+"\n");
		}
		return res.toString();
	}
	
	
	public FMFileIterator getFiles(boolean recursive) {
		return new FMFileIterator(recursive, this);
	}
	
	public FMFileRegexIterator getFiles(String regExp, boolean recursive) {
		return new FMFileRegexIterator(regExp, recursive, this);
	}
	
	
}