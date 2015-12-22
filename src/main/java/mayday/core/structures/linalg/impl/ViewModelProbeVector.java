package mayday.core.structures.linalg.impl;

import mayday.core.Probe;
import mayday.vis3.model.ViewModel;

/** 
 * Part of probematrix 
 *
 * @version 1.0
 */
public class ViewModelProbeVector extends ProbeVector {

	protected ViewModel vm;
	
	public ViewModelProbeVector(Probe p, ViewModel vm){
		super(p);
		this.vm = vm;
	}
	
	@Override
	protected double get0(int i) {
		return vm.getProbeValues(pb)[i];
	}

}
