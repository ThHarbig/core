package mayday.genemining2.methods;

import java.util.ArrayList;
import java.util.Arrays;
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

/**
 * @author G\u00FCnter J\u00E4ger
 * 
 */
public class SVM extends AbstractMiningMethod {

	private double svmWeight = 1.0;

	@SuppressWarnings("unchecked")
	@Override
	public ScoringResult runTest(Map<Object, double[]> values,
			ClassSelectionModel classes) {
		ScoringResult res = new DefaultScoringResult(true);
		MIGroup rawScores = res.getRawScore();

		// transform the data into a form that is much easier to work with for
		// RP
		Map<Object, Integer> indexMap = new TreeMap<Object, Integer>();
		PermutableMatrix allInputData = Algebra.matrixFromMap(values, indexMap);
		Integer[][] index = classIndices(classes);
		PermutableMatrix data1 = allInputData.submatrix(null, Algebra
				.<int[]> createNativeArray(index[0]));
		PermutableMatrix data2 = allInputData.submatrix(null, Algebra
				.<int[]> createNativeArray(index[1]));

		double smallest, largest, impurity;

		/*
		 * index 0 : category index 1 : value index 2 : left total index 3 :
		 * right total
		 */
		ArrayList<double[]> candidates1, candidates2;

		int n_l = index[0].length;
		int n_r = index[1].length;
		int n = n_l + n_r;

		Object[] probes = values.keySet().toArray();

		for (int i = 0; i < probes.length; i++) {
			int from = 0, to = 0;
			Gene<Integer>[] profile = new Gene[n];
			candidates1 = new ArrayList<double[]>(n_l);
			candidates2 = new ArrayList<double[]>(n_r);
			for (int j = 0; j < n_l; j++) {
				profile[j] = new Gene<Integer>(data1.getColumn(j).get(i), 0);
				candidates1.add(j, new double[4]);
			}
			for (int j = 0; j < n_r; j++) {
				profile[n_l + j] = new Gene<Integer>(data2.getColumn(j).get(i), 1);
				candidates2.add(j, new double[4]);
			}
			Arrays.sort(profile);
			smallest = profile[0].getDataValue();
			largest = profile[n - 1].getDataValue();

			if (largest==smallest) {
				// set invariant probes to NA
				impurity = Double.NaN;
				
			} else {

				for (int j = 0; j < n; j++) {
					Integer geneClassId = profile[j].getAnnotationValue();
					double geneValue = profile[j].getDataValue();

					if (geneClassId == 0) {
						double[] cand_from = candidates1.get(from);
						cand_from[0] = 0;
						if (geneValue == smallest) {
							cand_from[1] = 0;
						} else if (geneValue == largest) {
							cand_from[1] = 1;
						} else {
							cand_from[1] = (geneValue - smallest) / (largest - smallest);
							from++;
						}
					} else {
						double[] cand_to = candidates2.get(to);
						cand_to[0] = 1;
						if (geneValue == smallest) {
							cand_to[1] = 0;
						} else if (geneValue == largest) {
							cand_to[1] = 1;
						} else {
							cand_to[1] = (geneValue - smallest) / (largest - smallest);
							to++;
						}
					}
				}

				for (int j = 0; j < n_l; j++) {
					if (j == 0) {
						candidates1.get(j)[2] = 0;
						candidates1.get(n_l - 1)[3] = 0;
					} else {
						candidates1.get(j)[2] = candidates1.get(j - 1)[2]
								+ candidates1.get(j - 1)[1];
						candidates1.get(n_l - j - 1)[3] = candidates1.get(n_l - j)[3]
								+ candidates1.get(n_l - j)[1];
					}
				}

				for (int j = 0; j < n_r; j++) {
					if (j == 0) {
						candidates2.get(j)[2] = 0;
						candidates2.get(n_r - 1)[3] = 0;
					} else {
						candidates2.get(j)[2] = candidates2.get(j - 1)[2]
								+ candidates2.get(j - 1)[1];
						candidates2.get(n_r - j - 1)[3] = candidates2.get(n_r - j)[3]
								+ candidates2.get(n_r - j)[1];
					}
				}

				if (n_l <= n_r) {
					impurity = computeLp(0, candidates1, candidates2, svmWeight);

					for (int j = 1; j < n_l; j++) {
						double tmp = computeLp(j, candidates1, candidates2,
								svmWeight);
						if (impurity == 0) {
							impurity = tmp;
						} else {
							if (tmp > 0 && tmp < impurity) {
								impurity = tmp;
							}
						}
					}
				} else {
					impurity = computeLp(0, candidates2, candidates1, svmWeight);
					for (int j = 1; j < n_r; j++) {
						double tmp = computeLp(i, candidates2, candidates1,
								svmWeight);
						if (impurity == 0) {
							impurity = tmp;
						} else {
							if (tmp > 0 && tmp < impurity) {
								impurity = tmp;
							}
						}
					}
				}
			}
			
			rawScores.add(probes[i], new DoubleMIO(impurity));
		}

		return res;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(this.getClass(), "PAS.genemining.svm",
				new String[0], ScoringPlugin.MC,
				new HashMap<String, Object>(), "G\u00FCnter J\u00E4ger",
				"jaeger@informatik.uni-tuebingen.de",
				"One Dimensional Support Vector Machine uses the SVM principle of \n" +
				"separating hyperplanes for one individual gene to calculate the number of errors \n" +
				"as a quality score.  ", 
				"SVM");
		return pli;
	}



	private double computeLp(int i, ArrayList<double[]> class1,
			ArrayList<double[]> class2, double costParameter) {
		double w, tmp1 = 0, tmp2 = 0;
		int c1 = class1.size();
		int c2 = class2.size();
		if (class1.get(i)[1] < class2.get(c1 - i - 1)[1]) {
			w = 2 / (class1.get(i)[1] - class2.get(c1 - i - 1)[1]);
			tmp1 = w * w / 2 + costParameter * (2 * (c1 - i - 1) - w * (class1.get(i)[3] - class2.get(c1 - i - 1)[2]));
		}
		if (class1.get(i)[1] > class2.get(c2 - i - 1)[1]) {
			w = 2 / (class1.get(i)[1] - class2.get(c2 - i - 1)[1]);
			tmp2 = w * w / 2 + costParameter * (2 * i - w * (class1.get(i)[2] - class2.get(c2 - i - 1)[3]));
		}
		if (tmp1 == 0) {
			if (tmp2 > 0)
				return tmp2;
			else
				return 0;
		} else if (tmp2 == 0) {
			if (tmp1 > 0)
				return tmp1;
			else
				return 0;
		} else {
			if (tmp1 < tmp2)
				return tmp1;
			else
				return tmp2;
		}
	}

}
