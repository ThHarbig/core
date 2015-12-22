package mayday.core.math;

<<<<<<< HEAD
import org.apache.commons.math.distribution.HypergeometricDistribution;
import org.apache.commons.math.distribution.HypergeometricDistributionImpl;
=======
import org.apache.commons.math.distribution.DistributionFactory;
>>>>>>> bd8805447b59c9475dfbcdf6f975397ad3c2209e

public class Hypergeometric {
	
	public static double phyper(int q, int m, int n, int k ) throws Exception
	{
<<<<<<< HEAD
		return new HypergeometricDistributionImpl(m+n, m, k).cumulativeProbability(q);
=======
		return DistributionFactory.newInstance().createHypergeometricDistribution(m+n, m, k).cumulativeProbability(q);
>>>>>>> bd8805447b59c9475dfbcdf6f975397ad3c2209e
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
