package mayday.core.structures.linalg.impl;

import mayday.core.Probe;
import mayday.core.structures.linalg.vector.AbstractVector;

/** 
 * Part of probematrix 
 *
 * @version 1.0
 */
public class ProbeVector extends AbstractVector {
	
	protected Probe pb;
	
	public ProbeVector(Probe p){
		this.pb = p;
	}
	
	public Probe getProbe() {
		return pb;
	}
	
	@Override
	protected double get0(int i) {
		return pb.getValues()[i];
	}

	@Override
	protected String getName0(int i) {	
		return pb.getMasterTable().getExperimentName(i);
	}

	@Override
	protected void set0(int i, double v) {
		pb.getValues()[i] = v;
	}

	@Override
	protected void setName0(int i, String name) {
		pb.setName(name);
	}

	@Override
	public int size() {
		return pb.getMasterTable().getNumberOfExperiments();
	}

}
