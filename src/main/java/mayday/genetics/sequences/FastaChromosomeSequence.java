package mayday.genetics.sequences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;

import mayday.core.structures.natives.LinkedIntArray;
import mayday.core.structures.natives.LinkedLongArray;

public class FastaChromosomeSequence implements ChromosomeSequence {
	
	protected File file;
	protected long startOffset;
	protected String chrome;
	protected String species;
	protected int length = -1;
	protected StringBuffer tmp = new StringBuffer();
	protected WeakReference<RandomAccessFile> myFile;
	
	protected LinkedIntArray sequenceOffsets = new LinkedIntArray(5000);
	protected LinkedLongArray fileOffsets = new LinkedLongArray(5000);
	
	public FastaChromosomeSequence( File fasta, long start, String chrome, String species ) {
		this.chrome = chrome;
		this.species = species;
		this.file = fasta;
		this.startOffset = start;		
	}
	
	public FastaChromosomeSequence( File fasta, RandomAccessFile raf, String chrome, String species ) throws IOException {
		this.chrome = chrome;
		this.species = species;
		this.file = fasta;
		this.startOffset = raf.getFilePointer()-1;		
		buildIndex(raf); // will move the file pointer in raf along until the end or the next ">"
	}
	
	
	@Override
	public String getChromosomeName() {
		return chrome;
	}

	@Override
	public String getSpeciesName() {
		return species;
	}
	
	protected RandomAccessFile getFile() throws IOException {
		RandomAccessFile raf = myFile!=null?myFile.get():null;
		if (raf==null) {
			raf = new SelfClosingRandomAccessFile(file, "r");
			myFile = new WeakReference<RandomAccessFile>(raf);
		}
		return raf;
	}
	
	protected void buildIndex() {
		try {
			RandomAccessFile raf = getFile();
			buildIndex(raf);
		} catch (Exception e) {
			System.out.println("Can not read fasta file "+file);
		}
	}
	
	protected void buildIndex(RandomAccessFile raf) {
		try {
			
			raf.seek(startOffset);
			char c = '0';
			int seqlen=0;
			boolean skipWhite = false;
			sequenceOffsets.add(0);
			fileOffsets.add(startOffset);
			int b;
			
			while ( (b=raf.read())!=-1 && (c=(char)b)!='>') { // next header
				if (Character.isWhitespace(c)) {
					skipWhite = true;
				} else {
					if (skipWhite) {
						sequenceOffsets.add(seqlen);
						fileOffsets.add(raf.getFilePointer()-1);
						skipWhite = false;
					}
					++seqlen;					
				}
			}

			length = seqlen;
		} catch (Exception e) {
			System.out.println("Can not read fasta file "+file);
		}
		
		
	}

	@Override
	public char charAt(int index) {
		if (length<0)
			buildIndex();
		if (length<0)
			return '?';
		
		long fileoffset;
		// map index to file offset
		
		long pos = sequenceOffsets.binarySearch(index);
		if (pos>=0)
			fileoffset = fileOffsets.get(pos);
		else {
			// pos is now -(insertion point)-1
			// i want the element before the insertion, i.e. the one at (insertion point)-1
			// -pos = (insertion point + 1)
			// -pos-1 = (insertion point)
			// -pos-2 = (insertion point)-1
			pos = -pos-2;
			int indexBase = sequenceOffsets.get(pos);
			long fileBase = fileOffsets.get(pos);
			fileoffset = fileBase + (index-indexBase);
		}

		try {
			RandomAccessFile raf = getFile();
			synchronized (raf) {
				raf.seek(fileoffset);
				return (char)raf.read();
			}
		} catch (Exception e) {
			System.out.println("Can not read fasta file "+file+ " at position "+fileoffset);
		}
		
		return '?';
	}

	@Override
	public int length() {
		if (length<0)
			buildIndex();
		return length;
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		synchronized(tmp) {
			tmp.ensureCapacity(end-start);
			tmp.setLength(0);
			for (int l = start; l<end; ++l) {
				tmp.append(charAt(l));
			}
			return tmp.toString();
		}
	}
	
	protected static class SelfClosingRandomAccessFile extends RandomAccessFile {
		
		public SelfClosingRandomAccessFile(File file, String mode)
				throws FileNotFoundException {
			super(file, mode);
		}

		public void finalize() throws Throwable {
			super.finalize();
			this.close();
		}
		
	}
	
}

