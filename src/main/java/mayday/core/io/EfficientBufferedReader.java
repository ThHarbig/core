package mayday.core.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import mayday.core.structures.natives.LinkedCharArray;

/** This class is the same as a regular buffered writer, only that ready() works as I expect it.
 * ready() tells if the stream has more data to be read, not if it _currently_ has more data,
 * i.e. ready() is only false if the stream is at its end. 
 * Secondly, an additional function readLineOptimized() can read a line without allocating a new string
 * It's almost a complete clone of BufferedReader, because all relevant variables in BR are private.
 * 	// redeclare ALL members of bufferedreader to make them accessible.
	// all members that were previously PRIVATE are now PROTECTED and start with an underscore "_"
 
 * @author battke
 */
public class EfficientBufferedReader extends BufferedReader {

	protected final GrowingCharSequence optimizedLine = new GrowingCharSequence();


	/** Read one line from the input as a char sequence without allocating a new string */

	public CharSequence readLineOptimized(boolean ignoreLF) throws IOException {

		int startChar;
		optimizedLine.clear();

		synchronized (lock) {
			ensureOpen();
			boolean omitLF = ignoreLF || _skipLF;

			for (;;) {

				if (_nextChar >= _nChars)
					_fill();
				if (_nextChar >= _nChars) { /* EOF */
					if (optimizedLine.length>0)
						return optimizedLine;
					else
						return null;
				}
				boolean eol = false;
				char c = 0;
				int i;

				/* Skip a leftover '\n', if necessary */
				if (omitLF && (_cb[_nextChar] == '\n')) 
					_nextChar++;
				_skipLF = false;
				omitLF = false;

				charLoop:
					for (i = _nextChar; i < _nChars; i++) {
						c = _cb[i];
						if ((c == '\n') || (c == '\r')) {
							eol = true;
							break charLoop;
						}
					}

				startChar = _nextChar;
				_nextChar = i;

				if (eol) {
					optimizedLine.append(_cb, startChar, i - startChar);
					_nextChar++;
					if (c == '\r') {
						_skipLF = true;
					}
					return optimizedLine;
				}

				optimizedLine.append(_cb, startChar, i - startChar);
			}
		}
	}


	public CharSequence readLineOptimized() throws IOException {
		return readLineOptimized(false);
	}


	protected final class GrowingCharSequence implements CharSequence {

		protected LinkedCharArray line = new LinkedCharArray(_defaultExpectedLineLength); 
		protected int length=0;

		@Override
		public char charAt(int index) {
			if (index<0 || index>=length)
				throw new IndexOutOfBoundsException("Index "+index+" not within range [0,"+(length-1)+"]");
			return line.get(index);
		}

		@Override
		public int length() {
			return length;
		}

		@Override
		public CharSequence subSequence(int start, int end) {			
			throw new UnsupportedOperationException("Use toString().substring() instead.");
		}

		public void clear() {
			length=0;
		}

		public void append(char c) {
			line.ensureSize(length+1);
			line.set(length++,c);
		}

		public void append(char str[], int offset, int len) {
			line.ensureSize(length+len);
			for (int i=len; i!=0; --i) {
				line.set(length++, str[offset+len-i]);
			}
		}
		
		public String toString() {
			return new StringBuffer(this).toString();
		}

	}


	// This is the BufferedReader
	
	protected Reader _in;

	protected char _cb[];
	protected int _nChars, _nextChar;

	protected static final int _INVALIDATED = -2;
	protected static final int _UNMARKED = -1;
	protected int _markedChar = _UNMARKED;
	protected int _readAheadLimit = 0; /* Valid only when markedChar > 0 */

	/** If the next character is a line feed, skip it */
	protected boolean _skipLF = false;

	/** The skipLF flag when the mark was set */
	protected boolean _markedSkipLF = false;

	protected static int _defaultCharBufferSize = 8192;
	protected final static int _defaultExpectedLineLength = 1000;

	/**
	 * Creates a buffering character-input stream that uses an input buffer of
	 * the specified size.
	 *
	 * @param  in   A Reader
	 * @param  sz   Input-buffer size
	 *
	 * @exception  IllegalArgumentException  If sz is <= 0
	 */
	public EfficientBufferedReader(Reader in, int sz) {
		super(in);
		if (sz <= 0)
			throw new IllegalArgumentException("Buffer size <= 0");
		this._in = in;
		_cb = new char[sz];
		_nextChar = _nChars = 0;
	}

	/**
	 * Creates a buffering character-input stream that uses a default-sized
	 * input buffer.
	 *
	 * @param  in   A Reader
	 */
	public EfficientBufferedReader(Reader in) {
		this(in, _defaultCharBufferSize);
	}

	/** Checks to make sure that the stream has not been closed */
	private void ensureOpen() throws IOException {
		if (_in == null)
			throw new IOException("Stream closed");
	}

	/**
	 * Fills the input buffer, taking the mark into account if it is valid.
	 */
	protected void _fill() throws IOException {
		int dst;
		if (_markedChar <= _UNMARKED) {
			/* No mark */
			dst = 0;
		} else {
			/* Marked */
			int delta = _nextChar - _markedChar;
			if (delta >= _readAheadLimit) {
				/* Gone past read-ahead limit: Invalidate mark */
				_markedChar = _INVALIDATED;
				_readAheadLimit = 0;
				dst = 0;
			} else {
				if (_readAheadLimit <= _cb.length) {
					/* Shuffle in the current buffer */
					System.arraycopy(_cb, _markedChar, _cb, 0, delta);
					_markedChar = 0;
					dst = delta;
				} else {
					/* Reallocate buffer to accommodate read-ahead limit */
					char ncb[] = new char[_readAheadLimit];
					System.arraycopy(_cb, _markedChar, ncb, 0, delta);
					_cb = ncb;
					_markedChar = 0;
					dst = delta;
				}
				_nextChar = _nChars = delta;
			}
		}

		int n;
		do {
			n = _in.read(_cb, dst, _cb.length - dst);
		} while (n == 0);
		if (n > 0) {
			_nChars = dst + n;
			_nextChar = dst;
		}
	}

	/**
	 * Reads a single character.
	 *
	 * @return The character read, as an integer in the range
	 *         0 to 65535 (<tt>0x00-0xffff</tt>), or -1 if the
	 *         end of the stream has been reached
	 * @exception  IOException  If an I/O error occurs
	 */
	public int read() throws IOException {
		synchronized (lock) {
			ensureOpen();
			for (;;) {
				if (_nextChar >= _nChars) {
					_fill();
					if (_nextChar >= _nChars)
						return -1;
				}
				if (_skipLF) {
					_skipLF = false;
					if (_cb[_nextChar] == '\n') {
						_nextChar++;
						continue;
					}
				}
				return _cb[_nextChar++];
			}
		}
	}

	/**
	 * Reads characters into a portion of an array, reading from the underlying
	 * stream if necessary.
	 */
	protected int _read1(char[] cbuf, int off, int len) throws IOException {
		if (_nextChar >= _nChars) {
			/* If the requested length is at least as large as the buffer, and
		       if there is no mark/reset activity, and if line feeds are not
		       being skipped, do not bother to copy the characters into the
		       local buffer.  In this way buffered streams will cascade
		       harmlessly. */
			if (len >= _cb.length && _markedChar <= _UNMARKED && !_skipLF) {
				return _in.read(cbuf, off, len);
			}
			_fill();
		}
		if (_nextChar >= _nChars) return -1;
		if (_skipLF) {
			_skipLF = false;
			if (_cb[_nextChar] == '\n') {
				_nextChar++;
				if (_nextChar >= _nChars)
					_fill();
				if (_nextChar >= _nChars)
					return -1;
			}
		}
		int n = Math.min(len, _nChars - _nextChar);
		System.arraycopy(_cb, _nextChar, cbuf, off, n);
		_nextChar += n;
		return n;
	}

	/**
	 * Reads characters into a portion of an array.
	 *
	 * <p> This method implements the general contract of the corresponding
	 * <code>{@link Reader#read(char[], int, int) read}</code> method of the
	 * <code>{@link Reader}</code> class.  As an additional convenience, it
	 * attempts to read as many characters as possible by repeatedly invoking
	 * the <code>read</code> method of the underlying stream.  This iterated
	 * <code>read</code> continues until one of the following conditions becomes
	 * true: <ul>
	 *
	 *   <li> The specified number of characters have been read,
	 *
	 *   <li> The <code>read</code> method of the underlying stream returns
	 *   <code>-1</code>, indicating end-of-file, or
	 *
	 *   <li> The <code>ready</code> method of the underlying stream
	 *   returns <code>false</code>, indicating that further input requests
	 *   would block.
	 *
	 * </ul> If the first <code>read</code> on the underlying stream returns
	 * <code>-1</code> to indicate end-of-file then this method returns
	 * <code>-1</code>.  Otherwise this method returns the number of characters
	 * actually read.
	 *
	 * <p> Subclasses of this class are encouraged, but not required, to
	 * attempt to read as many characters as possible in the same fashion.
	 *
	 * <p> Ordinarily this method takes characters from this stream's character
	 * buffer, filling it from the underlying stream as necessary.  If,
	 * however, the buffer is empty, the mark is not valid, and the requested
	 * length is at least as large as the buffer, then this method will read
	 * characters directly from the underlying stream into the given array.
	 * Thus redundant <code>BufferedReader</code>s will not copy data
	 * unnecessarily.
	 *
	 * @param      cbuf  Destination buffer
	 * @param      off   Offset at which to start storing characters
	 * @param      len   Maximum number of characters to read
	 *
	 * @return     The number of characters read, or -1 if the end of the
	 *             stream has been reached
	 *
	 * @exception  IOException  If an I/O error occurs
	 */
	public int read(char cbuf[], int off, int len) throws IOException {
		synchronized (lock) {
			ensureOpen();
			if ((off < 0) || (off > cbuf.length) || (len < 0) ||
					((off + len) > cbuf.length) || ((off + len) < 0)) {
				throw new IndexOutOfBoundsException();
			} else if (len == 0) {
				return 0;
			}

			int n = _read1(cbuf, off, len);
			if (n <= 0) return n;
			while ((n < len) && _in.ready()) {
				int n1 = _read1(cbuf, off + n, len - n);
				if (n1 <= 0) break;
				n += n1;
			}
			return n;
		}
	}

	/**
	 * Reads a line of text.  A line is considered to be terminated by any one
	 * of a line feed ('\n'), a carriage return ('\r'), or a carriage return
	 * followed immediately by a linefeed.
	 *
	 * @param      ignoreLF  If true, the next '\n' will be skipped
	 *
	 * @return     A String containing the contents of the line, not including
	 *             any line-termination characters, or null if the end of the
	 *             stream has been reached
	 * 
	 * @see        java.io.LineNumberReader#readLine()
	 *
	 * @exception  IOException  If an I/O error occurs
	 */
	String readLine_replace(boolean ignoreLF) throws IOException {
		StringBuffer s = null;
		int startChar;

		synchronized (lock) {
			ensureOpen();
			boolean omitLF = ignoreLF || _skipLF;

			for (;;) {

				if (_nextChar >= _nChars)
					_fill();
				if (_nextChar >= _nChars) { /* EOF */
					if (s != null && s.length() > 0)
						return s.toString();
					else
						return null;
				}
				boolean eol = false;
				char c = 0;
				int i;

				/* Skip a leftover '\n', if necessary */
				if (omitLF && (_cb[_nextChar] == '\n')) 
					_nextChar++;
				_skipLF = false;
				omitLF = false;

				charLoop:
					for (i = _nextChar; i < _nChars; i++) {
						c = _cb[i];
						if ((c == '\n') || (c == '\r')) {
							eol = true;
							break charLoop;
						}
					}

				startChar = _nextChar;
				_nextChar = i;

				if (eol) {
					String str;
					if (s == null) {
						str = new String(_cb, startChar, i - startChar);
					} else {
						s.append(_cb, startChar, i - startChar);
						str = s.toString();
					}
					_nextChar++;
					if (c == '\r') {
						_skipLF = true;
					}
					return str;
				}

				if (s == null) 
					s = new StringBuffer(_defaultExpectedLineLength);
				s.append(_cb, startChar, i - startChar);
			}
		}
	}

	/**
	 * Reads a line of text.  A line is considered to be terminated by any one
	 * of a line feed ('\n'), a carriage return ('\r'), or a carriage return
	 * followed immediately by a linefeed.
	 *
	 * @return     A String containing the contents of the line, not including
	 *             any line-termination characters, or null if the end of the
	 *             stream has been reached
	 *
	 * @exception  IOException  If an I/O error occurs
	 */
	public String readLine() throws IOException {
		return readLine_replace(false);
	}

	/**
	 * Skips characters.
	 *
	 * @param  n  The number of characters to skip
	 *
	 * @return    The number of characters actually skipped
	 *
	 * @exception  IllegalArgumentException  If <code>n</code> is negative.
	 * @exception  IOException  If an I/O error occurs
	 */
	public long skip(long n) throws IOException {
		if (n < 0L) {
			throw new IllegalArgumentException("skip value is negative");
		}
		synchronized (lock) {
			ensureOpen();
			long r = n;
			while (r > 0) {
				if (_nextChar >= _nChars)
					_fill();
				if (_nextChar >= _nChars)	/* EOF */
					break;
				if (_skipLF) {
					_skipLF = false;
					if (_cb[_nextChar] == '\n') {
						_nextChar++;
					}
				}
				long d = _nChars - _nextChar;
				if (r <= d) {
					_nextChar += r;
					r = 0;
					break;
				}
				else {
					r -= d;
					_nextChar = _nChars;
				}
			}
			return n - r;
		}
	}

	/**
	 * Tells whether this stream is ready to be read.  A buffered character
	 * stream is ready if the buffer is not empty, or if the underlying
	 * character stream is ready.
	 *
	 * @exception  IOException  If an I/O error occurs
	 */
	public boolean ready() throws IOException {
		synchronized (lock) {
			ensureOpen();

			/* 
			 * If newline needs to be skipped and the next char to be read
			 * is a newline character, then just skip it right away.
			 */
			if (_skipLF) {
				/* Note that in.ready() will return true if and only if the next 
				 * read on the stream will not block.
				 */
				if (_nextChar >= _nChars && _in.ready()) {
					_fill();
				}
				if (_nextChar < _nChars) {
					if (_cb[_nextChar] == '\n') 
						_nextChar++;
					_skipLF = false;
				} 
			}
			return (_nextChar < _nChars) || _in.ready();
		}
	}

	/**
	 * Tells whether this stream supports the mark() operation, which it does.
	 */
	public boolean markSupported() {
		return true;
	}

	/**
	 * Marks the present position in the stream.  Subsequent calls to reset()
	 * will attempt to reposition the stream to this point.
	 *
	 * @param readAheadLimit   Limit on the number of characters that may be
	 *                         read while still preserving the mark. An attempt
	 *                         to reset the stream after reading characters
	 *                         up to this limit or beyond may fail.
	 *                         A limit value larger than the size of the input
	 *                         buffer will cause a new buffer to be allocated
	 *                         whose size is no smaller than limit.
	 *                         Therefore large values should be used with care.
	 *
	 * @exception  IllegalArgumentException  If readAheadLimit is < 0
	 * @exception  IOException  If an I/O error occurs
	 */
	public void mark(int readAheadLimit) throws IOException {
		if (readAheadLimit < 0) {
			throw new IllegalArgumentException("Read-ahead limit < 0");
		}
		synchronized (lock) {
			ensureOpen();
			this._readAheadLimit = readAheadLimit;
			_markedChar = _nextChar;
			_markedSkipLF = _skipLF;
		}
	}

	/**
	 * Resets the stream to the most recent mark.
	 *
	 * @exception  IOException  If the stream has never been marked,
	 *                          or if the mark has been invalidated
	 */
	public void reset() throws IOException {
		synchronized (lock) {
			ensureOpen();
			if (_markedChar < 0)
				throw new IOException((_markedChar == _INVALIDATED)
						? "Mark invalid"
								: "Stream not marked");
			_nextChar = _markedChar;
			_skipLF = _markedSkipLF;
		}
	}

	public void close() throws IOException {
		synchronized (lock) {
			if (_in == null)
				return;
			_in.close();
			_in = null;
			_cb = null;
		}
	}


}
