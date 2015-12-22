package mayday.core.math;

/** A re-implementation of the pnorm functions found in R (r-base-core) */

public class PNorm {

	/**
	 * Calculate all values.length probabilities by integrating from upper =
	 * true -> x to infinity or upper = false -> -infinity to x
	 * 
	 * @param values
	 * @param upper
	 * @return integrated probabilities
	 */
	public static double[] getDistribution(double[] values, boolean upper) {
		double[] result = new double[values.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = getDistribution(values[i], upper);
		}
		return result;
	}
	
	/**
	 * Calculates the integrated probability for the value z for upper = true =
	 * x to infinity or upper = false = -infinity to x
	 * 
	 * @param z
	 * @param upper
	 * @return integrated probability
	 */
	public static double getDistribution(double z, boolean upper) {
		return pnorm(z, 0,1, !upper, false);
	}
	
	public static double pnorm(double x, double mu, double sigma, boolean lower_tail, boolean log_p) {
		
	    double p;

	    if (Double.isNaN(x) || Double.isNaN(mu) || Double.isNaN(sigma))
	    	return Double.NaN;

	    if (Double.isInfinite(x) && mu==x)
	    	return Double.NaN;


	    if (sigma <= 0) {
	    	if(sigma < 0)
	    		return Double.NaN;
	    	/* sigma = 0 : */
	    	return (x < mu) ? 0 : 1;
	    }
	    
	    p = (x - mu) / sigma;
	    
	    if(Double.isInfinite(p))
	    	return (x < mu) ? 0 : 1;
	    
	    x = p;

	    double[] p_cp = pnorm_both(x, (lower_tail ? 0 : 1), log_p);

	    return(lower_tail ? p_cp[0] : p_cp[1]);
	}
	
    protected final static double a[] = new double[]{
		2.2352520354606839287,
		161.02823106855587881,
		1067.6894854603709582,
		18154.981253343561249,
		0.065682337918207449113
	    };
    
    protected final static double b[] = new double[]{
		47.20258190468824187,
		976.09855173777669322,
		10260.932208618978205,
		45507.789335026729956
	    };
    
    protected final static double c[] = new double[]{
		0.39894151208813466764,
		8.8831497943883759412,
		93.506656132177855979,
		597.27027639480026226,
		2494.5375852903726711,
		6848.1904505362823326,
		11602.651437647350124,
		9842.7148383839780218,
		1.0765576773720192317e-8
	    };
    
    protected final static double d[] = new double[]{
		22.266688044328115691,
		235.38790178262499861,
		1519.377599407554805,
		6485.558298266760755,
		18615.571640885098091,
		34900.952721145977266,
		38912.003286093271411,
		19685.429676859990727
	    };
    
    protected final static double p[] = new double[]{
		0.21589853405795699,
		0.1274011611602473639,
		0.022235277870649807,
		0.001421619193227893466,
		2.9112874951168792e-5,
		0.02307344176494017303
	    };
    
    protected final static double q[] = new double[]{
		1.28426009614491121,
		0.468238212480865118,
		0.0659881378689285515,
		0.00378239633202758244,
		7.29751555083966205e-5
	    };
	
    protected final static double SQRT32 = Math.sqrt(32d);
    protected static final double M_1_SQRT_2PI = 1.0/Math.sqrt(2*Math.PI);
    
	public static double[] pnorm_both(double x, int i_tail, boolean log_p)
	{
		double cum = Double.NaN, ccum=Double.NaN;
		
	/* i_tail in {0,1,2} means: "lower", "upper", or "both" :
	   if(lower) return  *cum := P[X <= x]
	   if(upper) return *ccum := P[X >  x] = 1 - P[X <= x]
	*/

	    double xden, xnum, temp, del, eps, xsq, y;
	    int i;
	    boolean lower, upper;

	    if (Double.isNaN(x))
	    	cum=ccum=x;
	    else {

		    /* Consider changing these : */
		    eps = Double.MIN_NORMAL; // * 0.5;

		    /* i_tail in {0,1,2} =^= {lower, upper, both} */
		    lower = i_tail != 1;
		    upper = i_tail != 0;

		    y = Math.abs(x);
		    if (y <= 0.67448975) { 
		    	if (y > eps) {
		    		xsq = x * x;
		    		xnum = a[4] * xsq;
		    		xden = xsq;
		    		for (i = 0; i < 3; ++i) {
		    			xnum = (xnum + a[i]) * xsq;
		    			xden = (xden + b[i]) * xsq;
		    		}
		    	} else xnum = xden = 0.0;

		    	temp = x * (xnum + a[3]) / (xden + b[3]);
		    	if(lower) 
		    		cum = 0.5 + temp;
		    	if(upper) 
		    		ccum = 0.5 - temp;
		    	if(log_p) {
		    		if(lower) 
		    			cum = Math.log(cum);
		    		if(upper) 
		    			ccum = Math.log(ccum);
		    	}
		    }
		    else if (y <= SQRT32) {

		    	/* Evaluate pnorm for 0.674.. = qnorm(3/4) < |x| <= sqrt(32) ~= 5.657 */

		    	xnum = c[8] * y;
		    	xden = y;
		    	for (i = 0; i < 7; ++i) {
		    		xnum = (xnum + c[i]) * y;
		    		xden = (xden + d[i]) * y;
		    	}
		    	temp = (xnum + c[7]) / (xden + d[7]);

//		#define do_del(X)			
		    	double X = y;
		    	xsq = Math.floor(X * 16) / 16;				
		    	del = (X - xsq) * (X + xsq);					
		    	if(log_p) {							
		    		cum = (-xsq * xsq * 0.5) + (-del * 0.5) + Math.log(temp);	
		    		if((lower && x > 0.) || (upper && x <= 0.))			
		    			ccum = Math.log1p(-Math.exp(-xsq * xsq * 0.5) *		
		    					Math.exp(-del * 0.5) * temp);		
		    	}								
		    	else {								
		    		cum = Math.exp(-xsq * xsq * 0.5) * Math.exp(-del * 0.5) * temp;	
		    		ccum = 1.0 - cum;						
		    	}

//		#define swap_tail						
		    	if (x > 0.) {/* swap  ccum <--> cum */			
		    		temp = cum; 
		    		if(lower) 
		    			cum = ccum; 
		    		ccum = temp;	
		    	}

		    }
		    else if(log_p
			    || (lower && -37.5193 < x  &&  x < 8.2924)
			    || (upper && -8.2924  < x  &&  x < 37.5193)
			) {

		    	/* Evaluate pnorm for x in (-37.5, -5.657) union (5.657, 37.5) */
		    	xsq = 1.0 / (x * x);
		    	xnum = p[5] * xsq;
		    	xden = xsq;
		    	for (i = 0; i < 4; ++i) {
		    		xnum = (xnum + p[i]) * xsq;
		    		xden = (xden + q[i]) * xsq;
		    	}
		    	temp = xsq * (xnum + p[4]) / (xden + q[4]);
		    	temp = (M_1_SQRT_2PI - temp) / y;

		    	double X = x;
//		    	xsq = trunc(X * 16) / 16;				
		    	xsq = (double)((int)(X*16)) / 16;
		    	del = (X - xsq) * (X + xsq);					
		    	if(log_p) {							
		    		cum = (-xsq * xsq * 0.5) + (-del * 0.5) + Math.log(temp);	
		    		if((lower && x > 0.) || (upper && x <= 0.))			
		    			ccum = Math.log1p(-Math.exp(-xsq * xsq * 0.5) *		
		    					Math.exp(-del * 0.5) * temp);		
		    	}								
		    	else {								
		    		cum = Math.exp(-xsq * xsq * 0.5) * Math.exp(-del * 0.5) * temp;	
		    		ccum = 1.0 - cum;						
		    	}

		    	if (x > 0.) {/* swap  ccum <--> cum */			
		    		temp = cum; 
		    		if(lower) 
		    			cum = ccum; 
		    		ccum = temp;	
		    	}
		    	
		    }
		    else { /* no log_p , large x such that probs are 0 or 1 */
		    	if(x > 0) {	cum = 1.; ccum = 0.;}
		    	else { cum = 0.; ccum = 1.;	}
		    }

	    }
		return new double[]{cum, ccum};
	}
	
	
	public static void main(String... args) {
		// should be around -220
		System.out.println(PNorm.pnorm(0, -5619, 270, false, true));
	}
	/*
	private int trunc(double x){
		String s = Double.toString(x);
		int index = s.indexOf(".");
		if(index == -1) return Integer.parseInt(s);
		s = s.substring(0, index);
		return Integer.parseInt(s);
	}
	*/
}
