package mayday.core.math;

public class DNorm {

	protected final static double LN_SQRT_2PI = 0.918938533204672741780329736406d;
	protected final static double ONE_SQRT_2PI = 0.398942280401432677939946059934d;
	
	public static double dnorm(double x, double mean, double sd, boolean logged) {
		
		if (sd <= 0)
			throw new IllegalArgumentException("Standard Deviation <= 0");
		
		if (Double.isNaN(x) || Double.isNaN(mean) || Double.isNaN(sd))
			return Double.NaN;
		
		if (Double.isInfinite(sd))
			return 0d;
		
		if (Double.isInfinite(x) && mean==x)
			return Double.NaN;
		
		if (sd==0)
			return (x==mean)? Double.POSITIVE_INFINITY : 0d;
				
		double y = (x - mean) / sd;
		
		if (Double.isInfinite(y))
			return 0d;
		
		return logged?
				-(LN_SQRT_2PI + .5 * y * y + Math.log(sd)) :
				ONE_SQRT_2PI * Math.exp(-.5 * y * y) / sd;
	}


	
}
