package mayday.genemining2.methods;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import mayday.core.ClassSelectionModel;
import mayday.core.math.scoring.DefaultScoringResult;
import mayday.core.math.scoring.ScoringResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.structures.linalg.Algebra;
import mayday.core.structures.linalg.matrix.PermutableMatrix;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public abstract class AbstractImpurityMiningMethod extends AbstractMiningMethod {
	
	@SuppressWarnings("unchecked")
	@Override
	public ScoringResult runTest(Map<Object, double[]> values,
			ClassSelectionModel classes) {
			ScoringResult res = new DefaultScoringResult(true);

		// transform the data into a form that is much easier to work with for
		// RP
		Map<Object, Integer> indexMap = new TreeMap<Object, Integer>();
		PermutableMatrix allInputData = Algebra.matrixFromMap(values, indexMap);
		Integer[][] index = classIndices(classes);
		PermutableMatrix data1 = allInputData.submatrix(null, Algebra
				.<int[]> createNativeArray(index[0]));
		PermutableMatrix data2 = allInputData.submatrix(null, Algebra
				.<int[]> createNativeArray(index[1]));
		int num_gene = values.size();

		int n_l = index[0].length; // number of experiments in class 1
		int n_r = index[1].length; // number of experiments in class 2
		int n = n_l + n_r; // total number of experiments

		double[] impurities = new double[num_gene];
		Arrays.fill(impurities, 0.0);

		for (int k = 0; k < num_gene; k++) {
			double[] v1 = data1.getRow(k).toArray();
			double[] v2 = data2.getRow(k).toArray();

			Gene<Integer>[] profile = new Gene[n];
			for (int i = 0; i < v1.length; i++) {
				profile[i] = new Gene<Integer>(v1[i], 1);
			}
			for (int i = 0; i < v2.length; i++) {
				profile[v1.length + i] = new Gene<Integer>(v2[i], 2);
			}

			double right_count[] = new double[2];
			right_count[0] = n_l;
			right_count[1] = n_r;

			double left_count[] = new double[2];
			Arrays.fill(left_count, 0);

			Arrays.sort(profile);
			double impurity = calculate(left_count, right_count);

			int from, to;
			for (int i = 0; i < n; i++) {
				from = i;
				for (to = from + 1; to < n
						&& profile[from].dataValue == profile[to].dataValue; to++)
					;
				to -= 1;

				for (int j = from; j <= to; j++) {
					if (profile[j].getAnnotationValue() == 1) {
						right_count[0]--;
						left_count[0]++;
					} else {
						right_count[1]--;
						left_count[1]++;
					}
				}

				i = to;
				double tmp = calculate(left_count, right_count);

				if (tmp <= impurity) {
					impurity = tmp;
					if (impurity == 0)
						break;
				}
			}
			impurities[k] = impurity;
		}

		MIGroup valueGroup = res.getRawScore();
		valueGroup.setName("values");

		for (Object o : values.keySet()) {
			int i = indexMap.get(o);
			valueGroup.add(o, new DoubleMIO(impurities[i]));
		}

		return res;
	}

	protected abstract double calculate(double[] left_count, double[] right_count);
	

}
