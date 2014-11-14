package mayday.mpf.filters;

import mayday.mpf.options.OptBoolean;
import mayday.mpf.options.OptDouble;
import mayday.core.Probe;
import mayday.mpf.FilterBase;

/** @author Florian Battke */
public class ExpValCorridor extends FilterBase {

	OptDouble minVal = new OptDouble(
			"Lower bound",
			"The lowest value still in the corridor",
			0.0);
	
	OptDouble maxVal = new OptDouble(
			"Upper bound",
			"The highest value still in the corridor",
			100.0);
	
	OptBoolean invert = new OptBoolean(
			"Inverted mode", 
			"Select this option to discard probes that fall inside the corridor "
			+ "instead of keeping them", 			
			false);
	
	OptBoolean nullInside = new OptBoolean(
			"Consider missing values as inside",
			"Select this option to treat missing values as falling into the "
			+ "corridor as opposed to lying outside of it.",
			false);

	public ExpValCorridor() {
		super(1,1);
		
		pli.setName("Expression Value Corridor");
		pli.setIdentifier("PAS.mpf.expvalcorridor");
		pli.setAuthor("Florian Battke");
		pli.setEmail("battke@informatik.uni-tuebingen.de");
		pli.setAbout("Filters probes based on whether their expression values fall"
				+ " within a certain corridor.");
		pli.replaceCategory("Filtering");
		
		Options.add(minVal);
		Options.add(maxVal);
		Options.add(nullInside);
		Options.add(invert);
	}

	private boolean checkCriteria(Probe pb) {
		boolean isInside = true;
		for (int i=0; i!=pb.getNumberOfExperiments() && isInside; ++i) {
			Double d = pb.getValue(i);			
			if (d==null) 
				isInside &= nullInside.Value;
			else 
				isInside &= (d<=maxVal.Value) && (d>=minVal.Value);
		}
		return isInside;
	}
	
	public void execute() {
		OutputData[0]=InputData[0];
		for (Probe pb : OutputData[0]) {
			boolean keep = checkCriteria(pb);
			if (invert.Value) keep=!keep;
			if (!keep) OutputData[0].remove(pb);
		}
	}

}
