package mayday.core.meta.miotree;

import java.util.Iterator;
import java.util.Stack;

import mayday.core.meta.MIGroup;

public class FileIterator implements Iterator<MIGroup>, Iterable<MIGroup> {

	//private long myChangeCount = FMDirectory.this.changeCount;
	protected boolean Recursive;
	protected Stack<Iterator<Directory>> dirIterators = new Stack<Iterator<Directory>>();
	protected Iterator<Directory> dirIterator;
	protected MIGroup fileIterator;
			
	public FileIterator(boolean recursive, Directory rootDir) {
		Recursive = recursive;
		dirIterator = rootDir.getSubDirs().iterator();
		fileIterator = rootDir.getGroup();
		getNext();
	}
	
	public Iterator<MIGroup> iterator() { // this allows nice java 5 for loops on this object
		return this;
	}
	
	private boolean downOneDir() {
		if (dirIterator.hasNext()) {
			dirIterators.push(dirIterator);
			Directory nextDir = dirIterator.next();
			fileIterator = nextDir.getGroup();
			dirIterator = nextDir.getSubDirs().iterator();
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
			while (fileIterator==null && downOneDir());				
		} while (fileIterator==null && upOneDir());
		return fileIterator!=null;
	}
	
	private void getNext() {
		if (fileIterator==null) {
			if (Recursive) {
				nextDir();
			}
		}
	}
	
	public boolean hasNext() {
		return fileIterator!=null;
	}

	public MIGroup next() {
		MIGroup ret = fileIterator;
		fileIterator=null;
		getNext();
		return ret;
	}

	public void remove() {
		throw new RuntimeException("Removing is not supported on iterators");
	}
	
}