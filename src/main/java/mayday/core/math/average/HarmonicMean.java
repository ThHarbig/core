package mayday.core.math.average;

import java.util.List;

public class HarmonicMean extends AbstractAverage
{

	public double getAverage(double[] x, boolean ignoreNA) 
	{
		double avg=0.0;
		int ignored=0;
		for(double d:x)
		{
			if (ignoreNA && Double.isNaN(d)) 
				++ignored;
			else
				avg+=(1.0/d);
		}
		return (x.length-ignored)*(1.0/avg);
	}

	public double getAverage(List<Double> x, boolean ignoreNA) {
		double avg=0.0;
		int ignored=0;
		for(double d:x)
		{
			if (ignoreNA && Double.isNaN(d)) 
				++ignored;
			else
				avg+=(1.0/d);
		}
		return (x.size()-ignored)*(1.0/avg);
	}
	
	public String toString() {
		return "Harmonic Mean";
	}

}
