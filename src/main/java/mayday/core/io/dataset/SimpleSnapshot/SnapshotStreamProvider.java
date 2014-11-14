package mayday.core.io.dataset.SimpleSnapshot;

import java.io.InputStream;
import java.io.OutputStream;

public interface SnapshotStreamProvider {

	/** will be called AFTER the dataset is serialized, for each hugeMIO object to be written */
	public OutputStream getOutputStream(String identifier);
	
	/** will be called AFTER the dataset is completely loaded, for each hugemio object to be read */
	public InputStream getInputStream(String identifier);
	
}
