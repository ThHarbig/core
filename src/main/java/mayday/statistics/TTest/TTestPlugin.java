package mayday.statistics.TTest;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import mayday.core.ClassSelectionModel;
import mayday.core.math.stattest.StatTestPlugin;
import mayday.core.math.stattest.StatTestResult;
import mayday.core.math.stattest.UncorrectedStatTestResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.structures.linalg.Algebra;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;
import org.apache.commons.math3.stat.inference.TTest;

public class TTestPlugin extends StatTestPlugin {

	protected TTestSetting setting;

	public Map<Object, double[]> thevals;

	public TTestPlugin(){
		setting = new TTestSetting("Student's t test");		
	}
	
	@Override
	public Setting getSetting() {
		return setting;
	}



	@Override
	public StatTestResult runTest(Map<Object, double[]> values, ClassSelectionModel classes) {

		UncorrectedStatTestResult res = new UncorrectedStatTestResult();
		
		TTest tTest= new TTest();
		
		// transform the data into a form that is much easier to work with for RP
		Map<Object, Integer> indexMap = new TreeMap<Object, Integer>();		
		PermutableMatrix allInputData = Algebra.matrixFromMap(values, indexMap);			
		Integer[][] index = classIndices(classes);
		PermutableMatrix data1 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[0]));
		PermutableMatrix data2 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[1]));
		int num_gene = values.size();
		
		double[] tvals = new double[num_gene];
		double[] pvals = new double[num_gene];
		
		boolean paired = setting.isPaired();
		boolean equalvar = setting.isEqualVairance();
		
		for (int i=0; i!=num_gene; ++i) {
			AbstractVector v1 = data1.getRow(i);
			AbstractVector v2 = data2.getRow(i);
			try {
				tvals[i] = computeT(tTest, v1.toArray(), v2.toArray(), paired, equalvar);
				pvals[i] = computeP(tTest, v1.toArray(), v2.toArray(), paired, equalvar);
			} catch (Exception e) {
				tvals[i] = Double.NaN;
				pvals[i] = Double.NaN;
			}
		}
		
		res.initRawScore();
		MIGroup pgroup = res.getPValues();
		MIGroup tgroup = res.getRawScore();
		
		for (Object o : values.keySet()) {
			int i = indexMap.get(o);
			pgroup.add(o, new DoubleMIO(pvals[i]));
			tgroup.add(o, new DoubleMIO(tvals[i]));
		}
		
		return res;
	}
	

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.statistics.ttest",
				new String[0],
				StatTestPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Calculates a t test using the apache library",
				"Student's t test"
		);
		return pli;
	}

	
	private double computeT(TTest tTest, double [] sample1, double [] sample2, boolean paired, boolean equalvar)
	throws Exception{
		double tStat = 0.0d;
		if (paired) { // paired t-test
			tStat = tTest.pairedT(sample1,sample2);
		} else if (equalvar) { // equal variances
			tStat = tTest.homoscedasticT(sample1, sample2);
		} else { // unequal variances
			tStat = tTest.t(sample1,sample2);
		}
		return tStat;
	}

	// compute p-value
	private double computeP(TTest tTest, double [] sample1, double [] sample2, boolean paired, boolean equalvar)
	throws Exception {
		double pVal = 2.0d;
		if (paired) { // paired t-test
			pVal = tTest.pairedTTest(sample1,sample2);
		} else if (equalvar) { // equal variances
			pVal = tTest.homoscedasticTTest(sample1, sample2);
		} else { // unequal variances
			pVal = tTest.tTest(sample1,sample2);
		}

		return pVal;
	}
	

}
