package mayday.core.math;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public final class Binomial {

	protected final static int MAX_CACHE_SIZE=1000;
	
	// return integer nearest to x
	public static long nint(double x) {
		if (x < 0.0) return (long) Math.ceil(x - 0.5);
		return (long) Math.floor(x + 0.5);
	}

	protected static LinkedHashMap<Integer, Double> cache = new LinkedHashMap<Integer, Double>() {
		@SuppressWarnings("unchecked")
		public boolean removeEldestEntry(Map.Entry eldest) {
			return size()>MAX_CACHE_SIZE;
		}
	};

	protected static double logFactorial0(int n) {
		double ans = 0.0;
		for (int i = 1; i <= n; i++)
			ans += Math.log(i);
		return ans;
	}

	
	// return log n!	
	public static double logFactorial(int n) {
		
		if (n<25)			
			return logFactorial0(n);
		
		// use cache for larger n
		Double ans = cache.get(n);		
		if (ans==null) {
			ans = logFactorial0(n);
			cache.put(n, ans);
		} else {
			cache.remove(n); // make it newest entry again
			cache.put(n,ans);
		}
		return ans;
	}

	// return the binomial coefficient n choose k.
	public static long binomial(int n, int k) {
		return nint(Math.exp(logBinomial(n,k)));
	}


	public static double logBinomial(int n, int k) {
		return (logFactorial(n) - logFactorial(k) - logFactorial(n-k));
	}

}