package mayday.genemining2.methods;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import mayday.core.ClassSelectionModel;
import mayday.core.math.scoring.DefaultScoringResult;
import mayday.core.math.scoring.ScoringPlugin;
import mayday.core.math.scoring.ScoringResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.linalg.Algebra;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;


public class SignalToNoisePlugin extends AbstractMiningMethod {
	
	public SignalToNoisePlugin() {
		
	}
	
	@Override
	public ScoringResult runTest(Map<Object, double[]> values,
			ClassSelectionModel classes) {
		ScoringResult res = new DefaultScoringResult(false);
		// transform the data into a form that is much easier to work with for RP
		Map<Object, Integer> indexMap = new TreeMap<Object, Integer>();		
		PermutableMatrix allInputData = Algebra.matrixFromMap(values, indexMap);			
		Integer[][] index = classIndices(classes);
		PermutableMatrix data1 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[0]));
		PermutableMatrix data2 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[1]));
		int num_gene = values.size();
		
		double[] vals = new double[num_gene];
		
		for (int i=0; i!=num_gene; ++i) {
			AbstractVector v1 = data1.getRow(i);
			AbstractVector v2 = data2.getRow(i);
			double mean1=v1.mean();
			double mean2=v2.mean();
			double sd1=v1.sd();
			double sd2=v2.sd();
			vals[i]=(mean1-mean2)/(sd1+sd2);
		}
		
		MIGroup resgroup = res.getRawScore();
		
		for (Object o : values.keySet()) {
			int i = indexMap.get(o);
			resgroup.add(o, new DoubleMIO(vals[i]));
		}
		
		return res;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.statistics.SignalToNoise",
				new String[0],
				ScoringPlugin.MC,
				new HashMap<String, Object>(),
				"Roland Keller",
				"kellerr@informatik.uni-tuebingen.de",
				"Calculates the signal-to-noise ratio",
				"Signal-to-noise"
		);
		return pli;
	}
	


}

