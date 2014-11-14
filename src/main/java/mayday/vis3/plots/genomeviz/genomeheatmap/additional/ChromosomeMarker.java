package mayday.vis3.plots.genomeviz.genomeheatmap.additional;

import java.util.TreeSet;

public class ChromosomeMarker {

	private static final double DBL_EPSILON = Math.pow(2, -52);
    private static final double rounding_eps = 1e-7;
    private static final String[] UUNITS = new String[] {"","k","M","G","T","P"};
    private static final String[] LUNITS = new String[] {"","m","µ","n","a","f"};
    
	public ChromosomeMarker(){
		
	}
	
	
	public static double[] tickmarks(double min, double max, int n)
    {
        return tickmarks(min, max, n, n/3);
    }
    
	public static TreeSet<Double> tickmarks(double min, double max, int n, boolean val){
		double[] tickmarks = tickmarks(min, max, n, n/3);
		TreeSet<Double> set = new TreeSet<Double>();
		for(double d: tickmarks){
			set.add(d);
		}
		return set;
	}
    /**
     * 
     * @see #tickmarks(double, double, int)
     */
    public static double[] tickmarks(double min, double max, int n, int min_n)
    {
        /*
         * defaults
         */
        //int eps_correction = 0;
        //int ndiv=n;
        //double shrink_sml = 0.75;
        //double rounding_eps = 1e-7;
        //double h = 1.5;
        //double h5 = 0.5+1.5*h;
        
        double[] ticks = tickmarks_pretty0(
            min, max,
            n, //ndiv 
            min_n, //min_n
            0.75, //shrink_sml
            new double[] {1.5, 2.75}, //{h,h5=0.5+1.5*h}
            0, //eps_correction
            1 //return_bounds
        );
        
        /*
         * create the sequence of tickmarks 
         */
        double[] seq = new double[(int)ticks[2]];
        double di = Math.abs(ticks[1]-ticks[0])/seq.length;
        for(int i=0; i!=seq.length; ++i)
        {
            seq[i] = ticks[0]+ i*di;
        }
        return seq;
        
    }
    
    static double[] tickmarks_pretty0(
            double min, double max,
            int ndiv, int min_n,
            double shrink_sml,
            double[] high_u_fact,
            int eps_correction,
            int return_bounds )
      {
          
          double h = high_u_fact[0];
          double h5 = high_u_fact[1];
          
          double dx, cell, unit, base, U;
          double ns,nu;
          int k;
          boolean i_small;
          
          double lo = Math.min(max,min);
          double up = Math.max(max,min); 
          dx = up - lo;
          if(dx==0 && lo==0) //i.e. max==min==0
          {
              cell = 1;
              i_small = true;
          } else
          {
              cell = Math.max(Math.abs(lo),Math.abs(up));
              U = 1 + ((h5 >= 1.5*h+.5) ? 1/(1+h) : 1.5/(1+h5));
              i_small = dx < cell * U * Math.max(1,ndiv) * DBL_EPSILON * 3;
          }
          
          if(i_small)
          {
              if(cell > 10) cell = 9 + cell/10;
              cell *= shrink_sml;
              if(min_n > 1) cell /= min_n;
          }else
          {
              cell = dx;
              if(ndiv > 1) cell/=ndiv;
          }
          
          if(cell < 20*Double.MIN_VALUE)
          {
              //warning: very small range.. corrected
              cell = 20 * Double.MIN_VALUE;
          }else if(cell * 10 > Double.MAX_VALUE)
          {
              //warning: very large range.. corrected
              cell = 0.1 * Double.MAX_VALUE;
          }
          base = Math.pow(10d, Math.floor(Math.log10(cell)));
          unit = base;
          if((U=2*base)-cell < h*(cell-unit)) 
          {
              unit = U;
              if((U=5*base)-cell <h5*(cell-unit))
              {
                  unit = U;
                  if((U=10*base)-cell<h*(cell-unit)) unit = U;
              }
          }
          
          ns = Math.floor(lo/unit+rounding_eps);
          nu = Math.ceil(up/unit-rounding_eps);
          
          if(eps_correction!=0 && (eps_correction > 1 || !i_small))
          {
              if(lo!=0) lo *= (1 - DBL_EPSILON); else lo = -Double.MIN_VALUE;
              if(up!=0) up *= (1 + DBL_EPSILON); else up = +Double.MIN_VALUE;
          }
          
          while(ns*unit > lo + rounding_eps*unit) ns--;
          
          while(nu*unit < up - rounding_eps*unit) nu++;
          
          k = (int)(0.5 + nu -ns);
          if(k < min_n)
          {
              k = min_n -k;
              if(ns >= 0d)
              {
                  nu += k>>1;
                  ns -= k>>1 + (k&1);
              }else
              {
                  ns -= k>>1;
                  nu += k>>1 + (k&1);
              }
              ndiv = min_n;
          }else
          {
              ndiv = k;
          }
          
          if(return_bounds!=0)
          {
              if(ns * unit < lo) lo = ns * unit;
              if(nu * unit > up) up = nu * unit;
          }else
          {
              lo = ns;
              up = nu;
          }
          
          return new double[] {lo,up,ndiv};
      }
    
    public static String[] units(double[] values, double[] powers, boolean lower, boolean upper)
    {
        String[] result = new String[values.length];
        for(int k=0; k!=values.length; ++k)
        {
            double x=values[k];
            x = Math.abs(x);
            if(x<Double.MIN_VALUE) result[k] = ""; //log(0)=-Inf 
            
            if(x>=1) //UUNITS
            {
                if(upper)
                {
                    int n = ((int)Math.floor(Math.log10(x))); //number of positions - 1;
                    int i = n / 3; //number of group, i.e. index of unit in UUNITS;
                    if(i>5) i=5;
                    powers[k]=Math.pow(10,i*-3);                
                    result[k] = UUNITS[i];
                }else
                {
                    powers[k] = 1.0;
                    result[k] = UUNITS[0];
                }
                
            }else //LUNITS
            {
                if(lower)
                {
                    int n = ((int)Math.floor(Math.log10(1/x))); //number of positions - 1;
                    int i = n / 3; //number of group, i.e. index of unit in UUNITS;
                    if(i>5) i=5;
                    powers[k]=Math.pow(10,i*3);                
                    result[k] = LUNITS[i];
                }else
                {
                    powers[k] = 1.0;
                    result[k] = LUNITS[0];
                }
            }
        }
        return result;
    }
    
    public static String[] units(double[] values, double[] powers)
    {
        return units(values,powers,true,true);
    }
}
