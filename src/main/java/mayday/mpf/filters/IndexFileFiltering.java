package mayday.mpf.filters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;

import mayday.core.Probe;
import mayday.mpf.FilterBase;
import mayday.mpf.options.OptDropDown;
import mayday.mpf.options.OptFile;

/**
 * This filter removes probes based on whether they are not present in a file of probe names
 * @author Florian Battke
 */
public class IndexFileFiltering extends FilterBase {

	private OptDropDown matchmode = new OptDropDown("Keep probes that","Select whether to keep matching or non-matching probes.",
			new String[]{"are in the list","are not in the list"},0);
	private OptFile indexFile = new OptFile("Index file","A file containing probe names for filtering", "");

	
	public IndexFileFiltering() {
		super(1,1);
		
		pli.setName("Index File Filter");
		pli.setIdentifier("PAS.mpf.indexfilefilter");
		pli.replaceCategory("Filtering");
		pli.setAuthor("Florian Battke");
		pli.setEmail("battke@informatik.uni-tuebingen.de");
		pli.setAbout("Removes probes contained in an index file");
		
		Version = 1; // Version 1 didn't have "AND" matching for multiple MIOs, instead defaulted to "OR". This version defaults to "AND".

		Options.add(indexFile);
		Options.add(matchmode);
	}

	public void execute() throws Exception {
		
		OutputData[0]=InputData[0];
		
		int rpCounter=0;
		
		// load index file into hashmap
		HashSet<String> index = new HashSet<String>();
        BufferedReader br = new BufferedReader(new FileReader(indexFile.Value));

        String line;
        while ((line = br.readLine()) != null) {
            if (!line.trim().startsWith("#")) {
            	index.add(line.trim());
            }
        }
        br.close();
		
		// go over all probes and check all selected StringMIOs against the regex
		
		for (Probe pb : OutputData[0]) {
			boolean matching = index.contains(pb.getName());
			if (matchmode.Value==1) matching=!matching;
				
			if (!matching) {
				OutputData[0].remove(pb);
				++rpCounter;
			}
		}		
			ProgressMeter.writeLogLine(this.getName()+": " +rpCounter + " probes removed.");

	}
	


}