package mayday.core.meta;

import java.io.InputStream;
import java.io.OutputStream;

/** Implementing classes will not be asked to serialize to/from Strings during Snapshot writing.
 * Instead, they will be given a ZipOutputStream/InputStream to write to/read from.
 * @author battke
 *
 * @param <T>
 */
public interface HugeMIO {
	
	public void serializeHuge( OutputStream os );
	
	public void deserializeHuge( InputStream is );
	
}
