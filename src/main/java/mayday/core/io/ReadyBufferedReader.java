package mayday.core.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/** This class is the same as a regular buffered writer, only that ready() works as I expect it.
 * ready() tells if the stream has more data to be read, not if it _currently_ has more data,
 * i.e. ready() is only false if the stream is at its end. 
 * @author battke
 */
public class ReadyBufferedReader extends BufferedReader {

	public ReadyBufferedReader(Reader in) {
		super(in);
	}

	public boolean ready() throws IOException {
		mark(1);
		boolean ready = read() != -1;
		reset();
		return ready;
	}
	
	
}
