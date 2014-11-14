package mayday.core.math.average;

import java.util.List;

import mayday.core.structures.linalg.vector.AbstractVector;

public abstract class AbstractAverage implements IAverage {

	public double getAverage(double[] x) {
		return getAverage(x, false);
	}

	public double getAverage(AbstractVector x) {
		return getAverage(x, false);
	}

	public double getAverage(List<Double> x) {
		return getAverage(x, false);
	}
	
	public double getAverage(AbstractVector x, boolean ignoreNA) {
		return getAverage(x.toArrayUnpermuted(),ignoreNA);
	}
	

}
