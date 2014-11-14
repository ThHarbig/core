package mayday.genemining2.methods;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import mayday.core.ClassSelectionModel;
import mayday.core.math.scoring.AbstractScoringResult;
import mayday.core.math.scoring.ScoringPlugin;
import mayday.core.math.scoring.ScoringResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.structures.linalg.Algebra;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;

public class FoldChangePlugin extends AbstractMiningMethod {
	
	protected BooleanSetting withLog;
	protected HierarchicalSetting setting;
	
	public FoldChangePlugin() {
		initSetting();
	}

	public Setting initSetting() {
		withLog = new BooleanSetting("Data is unlogged","If the data is unlogged, the logarithm \n" +
				"will be applied before computing the fold changes.", true);
		setting = new HierarchicalSetting("Fold change setting");
		setting.addSetting(withLog);
		return setting;
	}

	@Override
	public Setting getSetting() {
		if(setting==null) {
			initSetting();
		}
		return setting;
	}

	@Override
	public ScoringResult runTest(Map<Object, double[]> values,
			ClassSelectionModel classes) {
		ScoringResult res;
		boolean log = withLog.getBooleanValue();

		res = new AbstractScoringResult() {
			public Comparator<Double> getRawScoreComparator() {
				return new Comparator<Double>() {
					public int compare(Double o1, Double o2) {
						Double m = Math.abs(o1);
						return m.compareTo(Math.abs(o2));
					}
				};
			}
		};

		// transform the data into a form that is much easier to work with for RP
		Map<Object, Integer> indexMap = new TreeMap<Object, Integer>();		
		PermutableMatrix allInputData = Algebra.matrixFromMap(values, indexMap);			
		Integer[][] index = classIndices(classes);
		PermutableMatrix data1 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[0]));
		PermutableMatrix data2 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[1]));
		int num_gene = values.size();

		double[] vals = new double[num_gene];
		double log2div = 1.0/Math.log(2);

		for (int i=0; i!=num_gene; ++i) {
			AbstractVector v1 = data1.getRow(i);
			AbstractVector v2 = data2.getRow(i);
			if(log) {
				vals[i]=Math.log(v1.mean())*log2div - Math.log(v2.mean())*log2div;
			}
			else {
				vals[i]=v1.mean() - v2.mean();
			}
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
				"PAS.statistics.FoldChange",
				new String[0],
				ScoringPlugin.MC,
				new HashMap<String, Object>(),
				"Roland Keller",
				"kellerr@informatik.uni-tuebingen.de",
				"Calculates the mean fold change between two classes",
				"Fold change"
		);
		return pli;
	}


}
