package mayday.core.math;

import org.apache.commons.math3.distribution.HypergeometricDistribution;

public class Hypergeometric {
	
	public static double phyper(int q, int m, int n, int k ) throws Exception
	{
		return new HypergeometricDistribution(m+n, m, k).cumulativeProbability(q);
	}
	
	public static double overlapProbability(int n, int n1, int n2, int m) throws Exception
	{
		if(m==0)
			return 1; 
		return phyper(Math.min(n1,n2), n1, n-n1, n2) - phyper(m-1, n1, n-n1, n2);
	}
	
	public static void main(String[] args) throws Exception{
		
		System.out.println(overlapProbability(100,20,25,10));
	}
	

}
