package mayday.genemining2.methods;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import mayday.core.ClassSelectionModel;
import mayday.core.math.Statistics;
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

public class PearsonPlugin extends AbstractMiningMethod {
	public PearsonPlugin() {
		
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
			double numerator=0,sumX2=0,sumY2=0;
			List<Double> listOfAllValues = new LinkedList<Double>();
			listOfAllValues.addAll(v1.asList());
			listOfAllValues.addAll(v2.asList());
			double meanX=Statistics.mean(listOfAllValues);
			double meanY=((double)v1.size()*1+v2.size()*(-1))/(v1.size()+v2.size());
			for(int j=0;j!=v1.size();j++) {
				numerator+=(v1.get(j)-meanX)*(1-meanY);
				sumX2+=Math.pow(v1.get(j)-meanX,2);
				sumY2+=Math.pow(1-meanY,2);
			}
			for(int j=0;j!=v2.size();j++) {
				numerator+=(v2.get(j)-meanX)*(-1-meanY);
				sumX2+=Math.pow(v2.get(j)-meanX,2);
				sumY2+=Math.pow(-1-meanY,2);
			}
			vals[i]=numerator/Math.sqrt(sumX2*sumY2);
			
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
				"PAS.statistics.Pearson",
				new String[0],
				ScoringPlugin.MC,
				new HashMap<String, Object>(),
				"Roland Keller",
				"kellerr@informatik.uni-tuebingen.de",
				"Calculates the Pearson Correlation Coefficient between two classes",
				"Pearson Correlation"
		);
		return pli;
	}


}
