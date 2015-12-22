package mayday.core.pluma.filemanager;

import java.util.Iterator;
import java.util.Stack;

public class FMFileIterator implements Iterator<FMFile>, Iterable<FMFile> {

	//private long myChangeCount = FMDirectory.this.changeCount;
	protected boolean Recursive;
	protected Stack<Iterator<FMDirectory>> dirIterators = new Stack<Iterator<FMDirectory>>();
	protected Iterator<FMDirectory> dirIterator;
	protected Iterator<FMFile> fileIterator;
			
	public FMFileIterator(boolean recursive, FMDirectory rootDir) {
		Recursive = recursive;
		dirIterator = rootDir.subDirs.values().iterator();
		fileIterator = rootDir.files.values().iterator();
		getNext();
	}
	
	public Iterator<FMFile> iterator() { // this allows nice java 5 for loops on this object
		return this;
	}
	
	private boolean downOneDir() {
		if (dirIterator.hasNext()) {
			dirIterators.push(dirIterator);
			FMDirectory nextDir = dirIterator.next();
			fileIterator = nextDir.files.values().iterator();
			dirIterator = nextDir.subDirs.values().iterator();
			return true;
		} else return false;				
	}
	
	private boolean upOneDir() {
		if (dirIterators.size()>0) {
			dirIterator = dirIterators.pop();
			return true;
		} else return false;
		// this dir has no more files (files are checked before subdirs)
	}
	
	private boolean nextDir() { // go down & up the hierarchy until files are found or its all over
		do {
			while (!fileIterator.hasNext() && downOneDir());				
		} while (!fileIterator.hasNext() && upOneDir());
		return fileIterator.hasNext();
	}
	
	private void getNext() {
		if (!fileIterator.hasNext()) {
			if (Recursive) {
				nextDir();
			}
		}
	}
	
	public boolean hasNext() {
		return fileIterator.hasNext();
	}

	public FMFile next() {
		FMFile ret = fileIterator.next();
		getNext();
		return ret;
	}

	public void remove() {
		throw new RuntimeException("PluginManager/FileManager dows not allow removal of elements");
	}
	
}