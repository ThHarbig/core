package mayday.mpf.filters;

import java.util.Vector;

import mayday.mpf.options.OptDouble;
import mayday.mpf.options.OptDropDown;
import mayday.mpf.options.OptPagedDropDown;
import mayday.core.Probe;
import mayday.mpf.FilterBase;

public class LogTransform extends FilterBase {

	private OptPagedDropDown method = new OptPagedDropDown("Transformation",
			"Select what kind of log transformation to apply",
			new String[]{"log 2","log 10","ln","log x"},0);
	private OptDouble logbase = new OptDouble("Log base x=","Set the base x to calculate log x",2.0);
	private OptDropDown treatnegative = new OptDropDown("Negative values","Select how to treat negative values including zero",
			new String[]{"Remove => Create missing values", "Set log(k)=0 for k<=0", "Stop processing that dataset"},2);
	
	public LogTransform() {
		super(1,1);
		
		pli.setName("Log transformation");
		pli.setIdentifier("PAS.mpf.logtransform");
		pli.setAuthor("Florian Battke");
		pli.setEmail("battke@informatik.uni-tuebingen.de");
		pli.setAbout("Apply logarithmic transformation to all values. You can select " +
				"whether to calculate log2, log10, ln or logX for any base x");
		pli.replaceCategory("Data transformation");
		
		method.addOption(3,logbase);
		Options.add(method);
		Options.add(logbase);
		Options.add(treatnegative);
	}

	
	private Double transform(Double v) {
		switch(method.Value) {
		case 0: return Math.log(v)/Math.log(2);
		case 1: return Math.log(v)/Math.log(10);
		case 2: return Math.log(v);
		case 3: return Math.log(v)/Math.log(logbase.Value);
		}
		return null;
	}
	
	
	public void execute() throws Exception {

		OutputData[0] = InputData[0];
		int noe = OutputData[0].getNumberOfExperiments();
		
		ProgressMeter.initializeStepper(InputData[0].size());

		for (Probe pb : OutputData[0]) {
			
			Vector<Double> newvals = new Vector<Double>(noe);
			
			for(int i=0; i!=noe; ++i) {
				Double v = pb.getValue(i);
				if (v!=null && v<=0) {
					switch(treatnegative.Value) {
					case 0: v=null; break;
					case 1: v=1.0; break;
					case 2: throw new RuntimeException("Could not perform log transformation on non-positive values.\n Value encountered: "+v);
					}
				} else if (v!=null) v=transform(v);
				newvals.add(v);
			}
			
			OutputData[0].replaceValues(pb, newvals);
			ProgressMeter.stepStepper(1);
		}
		
	}

}
