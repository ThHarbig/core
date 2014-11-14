package mayday.core.pluma.filemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class FileManager {

	public FMDirectory Root = new FMDirectory("");
	
	public FileManager() {		
	}
	
	public FMDirectory getDirectory(String path) {
		return Root.getDirectory(path, false);
	}
	
	public FMFile getFile(String name) {
		if (name.startsWith("/")) name = name.substring(1);
		String path;
		if (name.contains("/")) {
			path = name.substring(0, name.lastIndexOf("/"));
			name = name.substring(path.length()+1);
		} else {
			path="";
		}
		FMDirectory parent = Root.getDirectory(path, false);
		if (parent!=null) 
			return parent.getFile(name);
		return null;
	}
	
	public FMFileRegexIterator getFiles(String regExp, boolean recursive) {
		return Root.getFiles(regExp, recursive);
	}
	
	public FMFileIterator getFiles(boolean recursive) {
		return Root.getFiles(recursive);
	}
	
	public void addFile(FMFile file) {
		file.setParent(this);
		FMDirectory dir = Root.getDirectory(file.Path, true);
		FMFile existingFile = dir.getFile(file.Name);
		// files in the local file system have precendence over files in jars
		if (existingFile==null || existingFile.Source==FMFile.SOURCE_JAR) {
			file.setAlternative(existingFile);
			dir.putFile(file);
		}
		if (existingFile!=null) {
			System.out.println("FileManager: File DUPLICATE replaced:\n"
					+"--> Old: "+existingFile+"\n--> New: "+file);
			
		}
		//System.out.println("FileManager: Adding "+file);
	}
	
	
	public int getNumberOfFiles() {
		int ret=0;
		for (@SuppressWarnings("unused") FMFile f : getFiles(true))
			++ret;
		return ret;
	}
	
	/** Copy data from a reader to a writer. Neither is closed after the work is done. */
	public static void copy( Reader fis, Writer fos ) throws IOException {
		try {
			char  buffer[] = new char[0xffff];
			int   nbytes;
			  
			while ( (nbytes = fis.read(buffer)) != -1 )
				  fos.write( buffer, 0, nbytes );
	    } finally {
	    	fos.flush();
	    }
	}
	
	/** Copy a file from one stream to another, closing the streams after the work is done */
	public static void copy( InputStream fis, OutputStream fos ) throws IOException {
		copy(fis,fos,true);
	}
	
	/** Copy a file from one stream to another, closing the streams after the work is done if doClose true */
	public static void copy( InputStream fis, OutputStream fos, boolean doClose ) throws IOException {
		try {
			byte  buffer[] = new byte[0xffff];
			int   nbytes;
			  
			while ( (nbytes = fis.read(buffer)) != -1 )
				  fos.write( buffer, 0, nbytes );
	    } finally {
	    	if (doClose) {
	    		if ( fis != null ) fis.close();
	    		if ( fos != null ) fos.close();
	    	}
	    }
	}
	
	public static void copy( InputStream is, String targetFile ) throws IOException {
		File target = new File(targetFile);
		target.getParentFile().mkdirs();
		FileOutputStream os = new FileOutputStream( target );
		copy(is, os);
	}
	
	public static void copy( File ifile, String targetFile ) throws IOException {
		copy(ifile.getCanonicalPath(), targetFile);
	}
	
	public static void copy( String sourceFile, String targetFile ) throws IOException {
		if (new File(sourceFile).getCanonicalPath().equals(new File(targetFile).getCanonicalPath())) {
			System.out.println("Ignoring request to copy file "+sourceFile+" over itself.");
		} else {
			File source = new File(sourceFile);
			FileInputStream is = new FileInputStream( source );
			copy(is, targetFile);
		}
	}
	
	public static void copy( String sourceFile, OutputStream os ) throws IOException {
		File source = new File(sourceFile);
		FileInputStream is = new FileInputStream( source );
		copy(is, os);
	}
	
}
