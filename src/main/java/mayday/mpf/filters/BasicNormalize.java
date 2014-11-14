package mayday.mpf.filters;

import java.util.Vector;

import mayday.mpf.options.OptBoolean;
import mayday.core.Probe;
import mayday.mpf.FilterBase;

public class BasicNormalize extends FilterBase {

	OptBoolean doCenter = new OptBoolean("Centering","Select this option to center the data. \n" +
			"This means that the values for each probe are adjusted \n" +
			"so that the probe mean is zero for every probe.",true);
	
	OptBoolean doScale = new OptBoolean("Scaling","Select this option to scale the data. \n" +
			"This means that the values for each probe are adjusted \n" +
			"so that their standard deviation sd=1 for every probe.",true);
	
	public BasicNormalize() {
		super(1,1);
		
		pli.setName("Z-Score Normalization");
		pli.setIdentifier("PAS.mpf.normalize");
		pli.setAuthor("Florian Battke");
		pli.setEmail("battke@informatik.uni-tuebingen.de");
		pli.setAbout("Normalizes expression values so that mu=0 (centered) and sd=1 (scaled).");
		pli.replaceCategory("Data transformation");
		
		Version=1;		
		Options.add(doCenter);
		Options.add(doScale);
	}
	
	public void execute() throws Exception {
				
		if (!(doCenter.Value || doScale.Value)) 
			throw new RuntimeException("Normalization called without useful options. \n" +
					"Select at least one action to perform (centering or scaling or both).");
		
		ProgressMeter.initializeStepper(InputData[0].size());
			
		OutputData[0]=InputData[0];
		int noe = OutputData[0].getNumberOfExperiments();
		Vector<Double> newvals = new Vector<Double>(noe);
		
		// for each probe calculate mu, sd and then calculate x' for every experiment value x
		
		for (Probe pb : OutputData[0]) {
			if (isCancelled()) return;
			double mu;
			double sd;
			try {
				mu = pb.getMean();
				sd = pb.getStandardDeviation();
			} catch (Exception e) {
				throw new RuntimeException("Could not calculate probe mean/sd because of missing values.");
			}
			
			if (sd == 0) sd=1; // prevent generation of NaNs   
			
			newvals.clear();
			for(int i=0; i!=noe; ++i) {
				Double v = pb.getValue(i);
				if (v==null)
					newvals.add(null);
				else if (doCenter.Value && doScale.Value)
					newvals.add((v-mu)/sd);
				else if (doCenter.Value && !doScale.Value)
					newvals.add(v-mu);
				else if (!doCenter.Value && doScale.Value)
					newvals.add(v/sd);
			}
			OutputData[0].replaceValues(pb, newvals);
			ProgressMeter.stepStepper(1);
		}

		
		
		/*//Old code did normalization using the mu,sd over all probes
		// first calculate current mu, sd
		// we ignore NULL values (as does getMean())
		double mu=0;
		double sd=0;
		double ct=0;
		Probe mp = InputData[0].getProbeList().getMean();
		for (int i=0; i!=mp.getNumberOfExperiments(); ++i) {
			Double v = mp.getValue(i);
			if (v!=null) {
				mu += v;
				++ct;
			}
		}
		mu/=ct;
		ct=0;
		for (Probe pb : InputData[0]) {
			for (int i=0; i!=pb.getNumberOfExperiments(); ++i) {
				Double v = pb.getValue(i);
				if (v!=null) {
					sd += Math.pow(v-mu,2);
					++ct;
				}
			}
		}
		sd /= ct;
		sd = Math.sqrt(sd);
				
		if (mu==0.0 && sd==1.0) { // nothing to do
			OutputData[0]=InputData[0];
			return; 
		}
		
		// set new mu, sd: M_ij' = (M_ij-mu)/sd
		OutputData[0]=InputData[0];
		int noe = OutputData[0].getNumberOfExperiments();
		
		for (Probe pb : OutputData[0]) {
			Vector<Double> newvals = new Vector<Double>(noe);
			
			for(int i=0; i!=noe; ++i) {
				Double v = pb.getValue(i);
				newvals.add( (v==null ? null : (v-mu)/sd ) );
			}
			OutputData[0].replaceValues(pb, newvals);
		}
		*/
	}

}
