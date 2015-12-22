package mayday.vis3.model.summarize;

import java.util.List;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.math.average.IAverage;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.core.structures.maps.MultiHashMap;
import mayday.vis3.model.wrapped.WrappedExperiment;
import mayday.vis3.model.wrapped.WrappedMasterTable;

/** An implementation of the WrappedMasterTable where replicates are summarized */
public class SummaryMasterTable extends WrappedMasterTable {

	MultiHashMap<Integer, Integer> exMappings;
	List<String> exnames;
	IAverage avg;
	
	public SummaryMasterTable(MasterTable parent, List<String> classNames, MultiHashMap<Integer, Integer> experimentMappings, IAverage averager) {
		super(parent);
		exMappings = experimentMappings;
		avg = averager;
		exnames = classNames;
		createExperiments();
	}

	public double[] getDerivedProbeValues(Probe wrapped) {
		double[] primary = wrapped.getValues();
		double[] vals = new double[exnames.size()];
		for (int i=0; i!=vals.length; ++i) {
			// get experiments to average
			List<Integer> map = exMappings.get(i);
			DoubleVector vec = new DoubleVector(map.size());
			for (int j=0; j!=map.size(); ++j)
				vec.set(j, primary[map.get(j)]);
			vals[i] = avg.getAverage(vec);
		}
		return vals;
	}
	
	public int getNumberOfExperiments() {
		return exnames.size();
	}

	protected WrappedExperiment createExperiment(int experiment) {
		String name = exnames.get(experiment);
		String annot="Summary of\n";
		for (int i : exMappings.get(experiment)) {
			annot+="- "+wrapped.getExperimentDisplayName(i)+"\n";
		}
		WrappedExperiment ret = new SummaryExperiment(name, this, annot);
		return ret;
	}



	
}
