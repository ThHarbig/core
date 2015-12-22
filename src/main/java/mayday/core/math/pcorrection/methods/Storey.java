package mayday.core.math.pcorrection.methods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import mayday.core.math.Statistics;
import mayday.core.math.pcorrection.PCorrectionPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

/** Implements Storey's q Value correction method as in bioconductor package "qvalue", 
 * only the "bootstrap" pi0 estimation method is implemented for now.
 * @author keller
 *
 */
public class Storey extends PCorrectionPlugin {

	
	public List<Double> correct(Collection<Double> pvalues) {
		int size = pvalues.size();
		double pi0=1;
		ArrayList<Double> ret = new ArrayList<Double>();
		for(double pValue:pvalues) {
			ret.add(pValue);
		}
		
//		String method="bootstrap";
	
		double[] lambda = new double[] {0,0.05,0.1,0.15,0.2,0.25,0.3,0.35,0.4,0.45,0.5,0.55,0.6,0.65,0.7,0.75,0.8,0.85,0.9};
		
		double [] pi0arr = new double[lambda.length];
		for(int pos=0;pos!=lambda.length;pos++) {
			int greater=0;
			for(int pos2=0;pos2!=size;pos2++) {
				if(ret.get(pos2)>=lambda[pos]) {
					greater++;
				}
			}
				
			pi0arr[pos] = (((double)greater)/size)/(1-lambda[pos]);
	    }

//		if(method.equals("bootstrap")) {
			double minpi0 = 1;
			for(int pos=0;pos!=pi0arr.length;pos++) {
				if(pi0arr[pos]<minpi0) {
					minpi0=pi0arr[pos];
				}
			}
			double[] mse = new double[lambda.length];
			double[] pi0boot = new double[lambda.length];
			for(int pos=0;pos!=pi0boot.length;pos++) {
				pi0boot[pos]=0;
				mse[pos]=0;
			}
			double[] sampleValues = new double[size];
			Random rand = new Random(System.currentTimeMillis());
			for(int sample=0;sample!=100;sample++) {
				for(int pos=0;pos!=size;pos++) {
					sampleValues[pos]= ret.get(rand.nextInt(size));
				}
				for(int pos=0;pos!=lambda.length;pos++) {
					int greater=0;
					for(int pos2=0;pos2!=size;pos2++) {
						if(sampleValues[pos2]>lambda[pos]) {
							greater++;
						}
					}
					pi0boot[pos]=(((double)greater)/size)/(1-lambda[pos]);
				}
				for(int pos=0;pos!=lambda.length;pos++) {
					mse[pos]=mse[pos]+(pi0boot[pos]-minpi0)*(pi0boot[pos]-minpi0);
				}
			}
			double minMSE=mse[0]; 
			double minPi0=pi0arr[0];
			for(int pos=0;pos!=pi0arr.length;pos++) {
				if(mse[pos]<minMSE) {
					minMSE=mse[pos];
					minPi0=pi0arr[pos];
				}
				else if(pi0arr[pos]<minPi0) {
					minPi0=pi0arr[pos];
				}
	        }
			if(minPi0<1) {
				pi0=minPi0;
			}
//		}
//		System.out.println("Pi0: " + pi0);
		
		if (pi0<=0)
			throw new RuntimeException("Storey's q value: pi0 (proportion of p values expected to be 0) is <=0.");
		
		//pi0=0.6726722;
		
		List<Integer> order = Statistics.order(ret);
		int[] cumsums = new int[size];
		int sum=0;
		int i=0;
		while(i<size) {
			int j=i;
			while(j<size&&ret.get(order.get(i)).doubleValue()==ret.get(order.get(j)).doubleValue()) {
				j++;
			}
			sum+=j-i;
			for(int pos=i;pos!=j;pos++) {
				cumsums[order.get(pos)]=sum;
			}
		    i=j;
		}
		
		for(int pos=0;pos!=size;pos++) {
			//ret.set(pos,pi0*size*ret.get(pos)/(cumsums[pos]*(1-Math.pow((1-ret.get(pos)),size))));
			ret.set(pos,pi0*size*ret.get(pos)/cumsums[pos]);
		}
		
	    ret.set(order.get(size-1),Math.min(ret.get(order.get(size-1)),1));
		for(int pos=size-2;pos>=0;pos--) {
			ret.set(order.get(pos),Math.min(ret.get(order.get(pos)),ret.get(order.get(pos+1))));
		}
		return ret;
	}
	
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.pcorrection.Storey",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Roland Keller",
				"kellerr@informatik.uni-tuebingen.de",
				"Storey p-value correction",
				"Storey FDR"
				);
		
//		System.out.println(correct(new double[]{.006529891,.144371819,.217803027,.009793726,.384139919}));
		
		return pli;
	}

}

