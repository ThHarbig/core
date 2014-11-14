package mayday.core.math.pcorrection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;


public abstract class PCorrectionPlugin extends AbstractPlugin {
	
	public final static String MC = "Math/P-value correction methods";
	
	/** Perform correction of pvalues on a vector of p-values **/
	public final List<Double> correct(double[] pvalues) {
		List<Double> in = new ArrayBackedDoubleList(pvalues);
		return correct(in);
	}
	
	/** Perform correction of pvalues on a matrix, each column is corrected separately. **/
	public final List<double[]> correct(List<double[]> pvalues) {
		List<double[]> out = new ArrayList<double[]>();
		if (pvalues.size()==0)
			return out;		
		int expc = pvalues.get(0).length;
		for (int i=0; i!=pvalues.size(); ++i)
			out.add(new double[expc]);
		
		for (int i=0; i!=expc; ++i) {
			Collection<Double> in = new ArrayBackedDoubleListOverArrays(pvalues,i);
			List<Double> ld = correct(in);
			for (int j=0; j!=ld.size(); ++j) 
				out.get(j)[i] = ld.get(j);
		}
		
		return out;
	}
	
	/** Perform correction of pvalues on a matrix, each column is corrected separately. **/
	public final List<double[]> correct(double[][] pvalues) {
		List<double[]> in = new ArrayBackedDoubleArrayList(pvalues);
		return correct(in);
	}
	
	/** Perform correction of pvalues given as a MIGroup */
	public final MIGroup correct(MIGroup pvalues) {
		List<Double> in = new ArrayList<Double>();
		for (Entry<Object, MIType> e : pvalues.getMIOs()) {
			in.add(((DoubleMIO)e.getValue()).getValue());
		}
		List<Double> out = correct(in);
		PluginInfo dMio = PluginManager.getInstance().getPluginFromID("PAS.MIO.Double");
		MIGroup mg = new MIGroup(dMio, toString(), null);
		int i=0;
		for (Entry<Object, MIType> e : pvalues.getMIOs()) {
			((DoubleMIO)mg.add(e.getKey())).setValue(out.get(i));
			++i;
		}
		return mg;
	}
	
	public abstract List<Double> correct(Collection<Double> pvalues);

    public void init() {}
    
    public String toString() {
    	return PluginManager.getInstance().getPluginFromClass(getClass()).getName();
    }
    
    public boolean equals(Object o) {
    	return toString().equals(o.toString()); 
    }

}
