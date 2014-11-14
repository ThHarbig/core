package mayday.statistics;

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
import mayday.core.structures.linalg.vector.DoubleVector;

public class WADPlugin extends StatTestPlugin 
{
	private PermutationTestSetting setting=new PermutationTestSetting();
	private double min;
	private double max;
	
	/* (non-Javadoc)
	 * @see mayday.core.pluma.AbstractPlugin#register()
	 */
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.Statistics.WadPlugin",
				new String[0],
				StatTestPlugin.MC,
				new HashMap<String, Object>(),
				"Sina Beier, Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Weighted Average Difference",
				"Weighted Average Difference (WAD)"
		);
		
		return pli;				
	}

	/* (non-Javadoc)
	 * @see mayday.core.math.stattest.StatTestPlugin#init()
	 */
	public void init() {}
	
	public Setting getSetting()
	{
		return setting;
	}
	
	public WADPlugin() {
	}
	
	@Override
	public StatTestResult runTest(Map<Object, double[]> values,	ClassSelectionModel classes)
	{
		Map<Object, Integer> indexMap = new TreeMap<Object, Integer>();		
		PermutableMatrix allInputData = Algebra.matrixFromMap(values, indexMap);			
		Integer[][] index = classIndices(classes);
		PermutableMatrix data1 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[0]));
		PermutableMatrix data2 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[1]));
		
		
		
		double _totalruns_ = setting.getNumberOfPermutations() + 1;
		
		AbstractVector wadStatistic=WAD(data1,data2);		
		UncorrectedStatTestResult res = new UncorrectedStatTestResult();
		// add raw scores to result
		res.initRawScore();
		MIGroup rawScores= res.getRawScore();		
		for(Object o: indexMap.keySet())
		{
			DoubleMIO mio=new DoubleMIO(wadStatistic.get(indexMap.get(o)));
			rawScores.add(o, mio);
		}
		setProgress(10000.0 / _totalruns_);
		
		// do permutations
		DoubleVector extremerValues = new DoubleVector(data1.nrow());	
		
		ClassSelectionModel mod=classes.permute();		
		for(int i=0; i!= setting.getNumberOfPermutations(); ++i )
		{
			Integer[][] per_index = classIndices(mod);
			PermutableMatrix per_data1 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(per_index[0]));
			PermutableMatrix per_data2 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(per_index[1]));
			
			AbstractVector currentWAD=WAD(per_data1, per_data2);	
			currentWAD.abs();

			for (int p=0; p!=currentWAD.size(); ++p) {
				if (currentWAD.get(p) > wadStatistic.get(p))
					extremerValues.add(1, p, p);
			}
			mod=mod.permute();
			setProgress(10000.0*(double)(i+1) / _totalruns_);
		}
		
		extremerValues.divide(((double)setting.getNumberOfPermutations()));
		// create MIGroup containing pValues
		
		MIGroup pVal= res.getPValues();
		for(Object o:values.keySet())
		{
			pVal.add(o,new DoubleMIO(extremerValues.get(indexMap.get(o))));
		}		
		return res;
	}
	
	/**
	 * Sets minimum and maximum values for the calculation of WAD test statistic.  
	 * @param values The genes the WAD is to be calculated on.
	 * @param cl Class assignments
	 */
	private void setExtremes(PermutableMatrix m1, PermutableMatrix m2)
	{
		double[] avg1=new double[m1.nrow()];
		double[] avg2=new double[m1.nrow()];
		
		int xi=0;
		for(int i=0; i!=m1.nrow(); ++i)
		{
			avg1[xi]=m1.getRow(i).mean();
			avg2[xi]=m2.getRow(i).mean();
			++xi;
		}
		min=Double.MAX_VALUE;
		max=Double.MIN_VALUE;
		for(int i=0; i!= avg1.length; ++i)
		{
			double ave= 0.5*(avg1[i]+avg2[i]);
			if(ave < min)
			{
				min=ave;
			}
			if(ave > max)
			{
				max=ave;
			}
		}
	}
	

	/**
	 * Calculates the WAD test statistic for a single gene. <br>
	 * See Kadota et al, Algorithms for Molecuar Biology 3:8 (2008) 
	 * This implementation is based on <a href="http://www.almob.org/content/supplementary/1748-7188-3-8-s2.txt">the R implementation of Kadota et al.</a>
	 * @param x The measures values of the gene
	 * @param cl The class assignment
	 * @return The WAD test statistic for 
	 */
	private double WAD(AbstractVector v1, AbstractVector v2)
	{
		double meanClass1=v1.mean();
		double meanClass2=v2.mean();
		double avFoldChange= (meanClass1 + meanClass2) / 2.0;
		double weight = (avFoldChange - min) / (max-min);
		double statistic = weight* (meanClass2 - meanClass1);
	    return(Math.abs(statistic));
	}
	
	/**
	 * Cacluclates the WAD statistic for each object in <code>values</code>
	 * @param values The genes 
	 * @param cl Class assignment for the experiments.
	 * @return
	 */
	private AbstractVector WAD(PermutableMatrix m1, PermutableMatrix m2)
	{
		// frist, calculate extremes values
		setExtremes(m1, m2);	
		// iterate over values and to calculate WAD scores
		AbstractVector wadStatistic=new DoubleVector(m1.nrow());
		
		for(int i=0; i!=m1.nrow(); ++i)
		{
			wadStatistic.set(i, WAD(m1.getRow(i), m2.getRow(i)));
		}
		return wadStatistic;
	}
	
}
