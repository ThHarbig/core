package mayday.genetics.sequences;

import java.io.File;

import mayday.core.io.BufferedRandomAccessFile;

public class FastaChromosomeSequenceFactory {

	public static void addChromosomeSequences(SequenceContainer sc, File fastaFile, String species, boolean parseHeader ) throws Exception {
		
		BufferedRandomAccessFile raf = new BufferedRandomAccessFile(fastaFile, "r");
		
		int b = raf.read();
		char c;
		
		while ( (b!=-1) ) {
			c = (char)b;
			if (c=='>') {
				// read until end of line
				StringBuffer hdr = new StringBuffer();
				while ( (b=raf.read())!=-1 && (c=(char)b)!='\n')
					hdr.append(c);
				if (hdr.length()==0)
					break;
				String chro = hdr.toString().trim();				
				
				if (parseHeader) {
					String hparts[] = chro.split("[\\s]+");
				
					// after space is only comment, use only first part of string.
					chro = hparts[0];
					// extract chrome name from ncbi header
					hparts = chro.split("\\|");
					if (hparts.length>=4)
						chro = hparts[3];
				}
					
				// skip whitespace until sequence
				while ( (b=raf.read())!=-1 && Character.isWhitespace((c=(char)b)) );

				// create new sequence
				FastaChromosomeSequence fcr = new FastaChromosomeSequence(fastaFile, raf, chro, species);				
				sc.addSequence(fcr);				
				b = (int)'>';
					
			} else {
				b = raf.read();
			}
		}		
		
		raf.close();
	}
	
	public static void addChromosomeSequences(File fastaFile, String species, boolean parse ) throws Exception {
		addChromosomeSequences(SequenceContainer.getDefault(), fastaFile, species, parse);
	}
	
}
