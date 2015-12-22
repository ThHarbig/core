package mayday.mpf.filters;

import mayday.mpf.FilterBase;

/** @author Florian Battke */
public class Duplicate extends FilterBase {

	public Duplicate() {
		super(1,2);
		
		pli.setName("Duplicate Input");
		pli.setIdentifier("PAS.mpf.duplicate");
		pli.replaceCategory("Data handling");
		pli.setAuthor("Florian Battke");
		pli.setEmail("battke@informatik.uni-tuebingen.de");
		pli.setAbout("Duplicates an input probe list. One input yields two identical outputs. " +
				"This is only useful in pipelines, e.q. to copy data into a visualization plugin that " +
				"doesn't return any output.");
		
	}

	public void execute() {
		OutputData[0]=InputData[0];
		OutputData[1]=InputData[0].duplicate();
	}

}
