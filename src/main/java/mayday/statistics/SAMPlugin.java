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

public class SAMPlugin extends StatTestPlugin 
{
	private PermutationTestSetting setting=new PermutationTestSetting();

	private double s0;
	
	public SAMPlugin() 
	{
	}
	
	/* (non-Javadoc)
	 * @see mayday.core.pluma.AbstractPlugin#register()
	 */
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.Statistics.SAMPlugin",
				new String[0],
				StatTestPlugin.MC,
				new HashMap<String, Object>(),
				"Sina Beier, Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Significance Analysis of Microarrays",
				"SAM"
		);
		pli.loadDefaultSettings(setting);
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
	
	@Override
	public StatTestResult runTest(Map<Object, double[]> values,	ClassSelectionModel classes)
	{	
		Map<Object, Integer> indexMap = new TreeMap<Object, Integer>();		
		PermutableMatrix allInputData = Algebra.matrixFromMap(values, indexMap);			
		Integer[][] index = classIndices(classes);
		PermutableMatrix data1 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[0]));
		PermutableMatrix data2 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[1]));
		
		s0=estimateS0byMedian(data1, data2);
		UncorrectedStatTestResult res = new UncorrectedStatTestResult();
		AbstractVector samStatistic=SAM(data1, data2);		

		// add raw scores to result
		res.initRawScore();
		MIGroup rawScores= res.getRawScore();	
		for(Object o: indexMap.keySet())
		{
			DoubleMIO mio=new DoubleMIO(samStatistic.get(indexMap.get(o)));
			rawScores.add(o, mio);
		}
		
		// do permuations
		DoubleVector extremerValues = new DoubleVector(data1.nrow());
		samStatistic.abs();
		
		ClassSelectionModel mod=classes.permute();
		for(int i=0; i!= setting.getNumberOfPermutations(); ++i )
		{
			Integer[][] per_index = classIndices(mod);
			PermutableMatrix per_data1 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(per_index[0]));
			PermutableMatrix per_data2 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(per_index[1]));
			
			AbstractVector currentSAM=SAM(per_data1, per_data2);	
			currentSAM.abs();

			for (int p=0; p!=currentSAM.size(); ++p) {
				if (currentSAM.get(p) > samStatistic.get(p))
					extremerValues.add(1, p, p);
			}
			mod=mod.permute();
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
	
	private double SAM(AbstractVector v1, AbstractVector v2)
	{
		double mean1=v1.mean();
		double mean2=v2.mean();
		double si = geneSpecificScatter(v1,v2);
		
		double di = (mean2 - mean1) / (si + s0);
		return di;
	}
	
	private double geneSpecificScatter(AbstractVector v1, AbstractVector v2)
	{
		double mean1=v1.mean();
		double mean2=v2.mean();
		
		double[] class1=v1.toArrayUnpermuted();
		double[] class2=v2.toArrayUnpermuted();
		
		double sqDev1=squaredDev(mean1, class1);
		double sqDev2=squaredDev(mean2, class2);
				
		double a =( (1.0 / class1.length) + (1.0 / class1.length)) / (class1.length + class2.length -2.0);		
		double si = Math.sqrt( a*(sqDev1+sqDev2));		
		return si;
	}
	

	private double estimateS0byMedian(PermutableMatrix m1, PermutableMatrix m2)
	{
		//calculate all si
		DoubleVector sis = new DoubleVector(m1.nrow());
		
		for(int i=0; i!=m1.nrow(); ++i)
		{
			sis.set(i, geneSpecificScatter(m1.getRow(i), m2.getRow(i)));
		}
		double s0 = sis.median(true);
		return s0;
	}
	
	private double squaredDev(double mean, double[] x)
	{
		double res=0.0;
		for(double d:x)
		{
			res+=Math.pow( (d-mean),2);
		}
		return res;
	}
	
	/**
	 * Cacluclates the SAM statistic for each object in <code>values</code>
	 * @param values The genes 
	 * @param cl Class assignment for the experiments.
	 * @return
	 */
	private AbstractVector SAM(PermutableMatrix m1, PermutableMatrix m2)
	{
		// iterate over values and to calculate WAD scores
		AbstractVector samStatistic=new DoubleVector(m1.nrow());
		
		for(int i=0; i!=m1.nrow(); ++i)
		{
			samStatistic.set(i, SAM(m1.getRow(i), m2.getRow(i)));
		}
		return samStatistic;
	}
	

}
