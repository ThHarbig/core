package mayday.core.io.dataset.ZippedSnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import mayday.core.io.dataset.SimpleSnapshot.SnapshotStreamProvider;

class ZipStreamProvider implements SnapshotStreamProvider {
	
	protected ZipFile parent;
	protected ZipOutputStream zout;
	
	public ZipStreamProvider(ZipFile parent_in, ZipOutputStream parent_out) {
		this.parent = parent_in;
		zout = parent_out;
	}
	@Override
	public InputStream getInputStream(String identifier) {
		try {
			return parent.getInputStream(parent.getEntry(identifier));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Can not create stream for "+identifier);
		}
	}
	@Override
	public OutputStream getOutputStream(String identifier) {
		try {
			zout.putNextEntry(new ZipEntry(identifier));
			return zout;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Can not create stream for "+identifier);
		}
	}
	
}