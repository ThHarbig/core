package mayday.core.structures.linalg.impl;

import java.util.ArrayList;
import java.util.Collection;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.structures.linalg.matrix.MatrixVector;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;

/** 
 * Matrix to be used within console for handling probelists easier 
 *
 * @version 1.0
 */
public class ProbeMatrix extends PermutableMatrix {
	
	protected ArrayList<ProbeVector> aPb;
	protected MasterTable mt;
	
	public ProbeMatrix(Collection<Probe> c){
		aPb = new ArrayList<ProbeVector>();
		for(Probe p : c)
			aPb.add(new ProbeVector(p));
		mt = aPb.get(0).getProbe().getMasterTable();
	}

	@Override
	protected String getDimName0(int dim, int index) {
		return dim == 0 ? aPb.get(index).getProbe().getName() : mt.getExperimentName(index);
	}

	@Override
	protected void setDimName0(int dim, int index, String name) {
		if (dim == 0)
			aPb.get(index).getProbe().setName(name); 
		else 
			mt.setExperimentName(index, name);
	}

	@Override
	protected int dim0(int dimension) {
		return dimension==0?aPb.size():mt.getNumberOfExperiments();
	}

	@Override
	protected double get0(int row, int col) {
		return aPb.get(row).get(col);
	}

	@Override
	protected AbstractVector getDimVec0(int dimension, int index) {
		if (dimension==0)
			return aPb.get(index);
		else
			return new MatrixVector.Column(this, index, rowPermute);
	}

	@Override
	protected void set0(int row, int col, double value) {
		aPb.get(row).set(col, value);
	}

}
