package mayday.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class BufferedRandomAccessFile extends RandomAccessFile {
	
	private final static int BUFSIZE = 1024*1024;
	protected byte[] BUF = new byte[BUFSIZE];
	protected int delta=0, lastRead=0;
	
	public BufferedRandomAccessFile(File file, String mode) throws FileNotFoundException {
		super(file, mode);
	}
	
	public BufferedRandomAccessFile(String file, String mode) throws FileNotFoundException {
		super(file, mode);
	}

	@Override
	public void seek(long position) throws IOException {
		super.seek(position);
		delta=0;			
	}
	
	@Override 
	public long getFilePointer() throws IOException {
		return super.getFilePointer()-lastRead+delta;
	}
	
	@Override
	public int read() throws IOException {
		int ret;
		if (delta==0)
			lastRead = super.read(BUF);
		if (delta>=lastRead)
			return -1;
		ret = BUF[delta++];
		if (delta>=lastRead) {
			delta=0;
			lastRead = 0;
		}
		return ret;
	}
	
}
