package mayday.core.io.dataset.SimpleSnapshot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

class FileStreamProvider implements SnapshotStreamProvider {
	
	protected String parent;
	
	public FileStreamProvider(String pname) {
		parent=pname;
	}
	@Override
	public InputStream getInputStream(String identifier) {
		try {
			return new FileInputStream(parent+"/"+identifier);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Can not create stream for "+identifier);
		}
	}
	@Override
	public OutputStream getOutputStream(String identifier) {
		try {
			return new FileOutputStream(parent+"/"+identifier);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Can not create stream for "+identifier);
		}
	}
	
}