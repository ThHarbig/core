package mayday.vis3.model.subset;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.vis3.model.wrapped.WrappedExperiment;
import mayday.vis3.model.wrapped.WrappedMasterTable;

/** An implementation of the WrappedMasterTable where only a subset of the experiments are visible */
public class SubsetMasterTable extends WrappedMasterTable {

	protected int[] experimentSubset;
	
	public SubsetMasterTable(MasterTable parent, int[] experimentSubset) {
		super(parent);		
		this.experimentSubset = experimentSubset;
		createExperiments();
	}

	public double[] getDerivedProbeValues(Probe wrapped) {
		double[] vals = new double[experimentSubset.length];
		for (int i=0; i!=experimentSubset.length; ++i)
			vals[i] = wrapped.getValues()[experimentSubset[i]];
		return vals;
	}
	
	public int getNumberOfExperiments() {
		return experimentSubset.length;
	}

	protected WrappedExperiment createExperiment(int experiment) {
		return new WrappedExperiment(wrapped.getExperiment(experimentSubset[experiment]),this);
	}
	
}
