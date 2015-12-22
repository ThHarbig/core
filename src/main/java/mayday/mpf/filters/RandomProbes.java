package mayday.mpf.filters;

import java.util.Random;

import mayday.core.Probe;
import mayday.mpf.FilterBase;
import mayday.mpf.options.OptInteger;

/** @author Florian Battke */
public class RandomProbes extends FilterBase  {


	private OptInteger OptExperiments = new OptInteger("Experiments","How many columns?", 10);
	private OptInteger OptProbes = new OptInteger("Probes","How many rows?",1000);
	
	public RandomProbes() {
		super(1,1);
		Options.add(OptExperiments);
		Options.add(OptProbes);
		
		pli.setName("Random Probes");
		pli.setIdentifier("PAS.mpf.randomprobes");
		pli.setAuthor("Florian Battke");
		pli.setEmail("battke@informatik.uni-tuebingen.de");
		pli.setAbout("Creates new probes with random values.");
		pli.replaceCategory("Import Modules");
		
	}

	public void execute() {
		OutputData[0]=InputData[0];

		this.ProgressMeter.initializeStepper(OptProbes.Value);
		
		Random rnd = new Random();
		
		for(int i=0; i!=this.OptProbes.Value; ++i) {
			Probe pb = OutputData[0].newProbe(""+i);
			for (int j=0; j!=this.OptExperiments.Value;++j)
				pb.addExperiment(rnd.nextGaussian());
			this.ProgressMeter.stepStepper(1);
		}
		
	}

}
