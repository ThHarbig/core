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
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.structures.linalg.Algebra;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class QuartetPuzzling extends AbstractMiningMethod {
	
	protected BooleanSetting heuristic;
	protected ObjectSelectionSetting<String> distanceMeasure;
	private HierarchicalSetting setting;
	
	public Setting getSetting() {
		if(setting == null) {
			setting = new HierarchicalSetting("Quartet Puzzling")
			.addSetting(distanceMeasure = QuartetDistanceMeasures.createSetting())
			.addSetting(heuristic = new BooleanSetting("heuristic", "Calculate only 1000 randomly chosen quartets.", false));
		}
		return setting;
	}

	@Override
	public ScoringResult runTest(Map<Object, double[]> values,
			ClassSelectionModel classes) {
		boolean heuristic = isHeuristic();
		
		ScoringResult res = new DefaultScoringResult(false);
		MIGroup rawScores = res.getRawScore();
		
		
		// transform the data into a form that is much easier to work with
		Map<Object, Integer> indexMap = new TreeMap<Object, Integer>();		
		PermutableMatrix allInputData = Algebra.matrixFromMap(values, indexMap);			
		Integer[][] index = classIndices(classes);
		PermutableMatrix data1 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[0]));
		PermutableMatrix data2 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[1]));
		
		int geneSize = values.size();
		
		if(data1.ncol() < 2 || data2.ncol() < 2) {
			throw new IllegalArgumentException("Each cluster needs at least 2 profiles");
		}
		
		int leftSize = data1.ncol();
		int rightSize = data2.ncol();
		
		AbstractVector A, B, C, D;
		double a_ = 0, b_ = 0, c_ = 0, d_ = 0, x_ = 0, y_ = 0, z_ = 0;
		
		/*
		 * calculate number of quartets
		 */
		int quartetNumbers = ((leftSize * (leftSize-1)) / 2) * ((rightSize * (rightSize-1)) / 2);
		
		if(quartetNumbers > 10000 && heuristic==true){ 
			// nur 1/10 Quartette berechnen
			quartetNumbers=(int)(quartetNumbers/10);
			
			System.out.println(quartetNumbers +" quartets for each gene will be calculated");
			
			Object[] genes = values.keySet().toArray();
			
			for(int i = 0; i < genes.length; i++) {
				setProgress(i*10000 / geneSize);

				/*
				 * generate quartets
				 */
				for(int l1 = 0; l1 < leftSize-1; l1++) {
					A = data1.getColumn(l1);
					for(int l2 = l1+1; l2 < leftSize; l2++) {
						B = data1.getColumn(l2);
						for(int r1 = 0; r1 < rightSize-1; r1++) {
							C = data2.getColumn(r1);
							for(int r2 = r1+1; r2 < rightSize; r2++) {
								D = data2.getColumn(r2);
								/*
								 * calculate
								 */
								double delta_a_b = getDistance(A.get(i), B.get(i));
								double delta_a_c = getDistance(A.get(i), C.get(i));
								double delta_a_d = getDistance(A.get(i), D.get(i));
								double delta_b_c = getDistance(B.get(i), C.get(i));
								double delta_b_d = getDistance(B.get(i), D.get(i));				
								double delta_c_d = getDistance(C.get(i), D.get(i));
								
								double max = Math.max(Math.max((delta_a_b + delta_c_d), (delta_a_c + delta_b_d)), (delta_a_d + delta_b_c));
								
								double a = (delta_a_b + delta_a_c + delta_a_d - max)/2;
								double b = (delta_a_b + delta_b_c + delta_b_d - max)/2;
								double c = (delta_a_c + delta_b_c + delta_c_d - max)/2;
								double d = (delta_a_d + delta_b_d + delta_c_d - max)/2;
								
								double x = (max - (delta_a_b + delta_c_d))/2;
								double y = (max - (delta_a_c + delta_b_d))/2;
								double z = (max - (delta_a_d + delta_b_c))/2;
								
								a_ += a;
								b_ += b;
								c_ += c;
								d_ += d;
								x_ += x;
								y_ += y;
								z_ += z;
								
								/*
								 * check invariants
								 */
								if(x != 0 && y != 0 && z != 0) {
									throw new IllegalArgumentException("Invariant #1 is wrong.");
								}
							}
						}
					}
				}
				
				a_ /= quartetNumbers;
				b_ /= quartetNumbers;
				c_ /= quartetNumbers;
				d_ /= quartetNumbers;
				x_ /= quartetNumbers;
				y_ /= quartetNumbers;
				z_ /= quartetNumbers;
				
				/*
				 * check for significant Genes
				 */
				if( x_ > 0 && x_ > y_  && x_ > z_ && y_ > 0 && z_ > 0){	
					//significantGenes.add(name, x_ , this.leftSplit.get(0).getGeneClassId(name));
					double val = (x_ / (y_ + z_)/2);
					rawScores.add(genes[i], new DoubleMIO(val));
				}
				
			}
		} else {
			System.out.println(quartetNumbers +" quartets for each gene will be calculated");
			
			Object[] genes = values.keySet().toArray();
			
			for(int i = 0; i < genes.length; i++) {
				setProgress(i*10000 / geneSize);
				/*
				 * generate quartets
				 */
				for(int l1 = 0; l1 < leftSize-1; l1++) {
					A = data1.getColumn(l1);
					for(int l2 = l1+1; l2 < leftSize; l2++) {
						B = data1.getColumn(l2);
						for(int r1 = 0; r1 < rightSize-1; r1++) {
							C = data2.getColumn(r1);
							for(int r2 = r1+1; r2 < rightSize; r2++) {
								D = data2.getColumn(r2);
								/*
								 * calculate
								 */
								double delta_a_b = getDistance(A.get(i), B.get(i));
								double delta_a_c = getDistance(A.get(i), C.get(i));
								double delta_a_d = getDistance(A.get(i), D.get(i));
								double delta_b_c = getDistance(B.get(i), C.get(i));
								double delta_b_d = getDistance(B.get(i), D.get(i));				
								double delta_c_d = getDistance(C.get(i), D.get(i));
								
								double max = Math.max(Math.max((delta_a_b + delta_c_d), (delta_a_c + delta_b_d)), (delta_a_d + delta_b_c));

								double a = (delta_a_b + delta_a_c + delta_a_d - max)/2;
								double b = (delta_a_b + delta_b_c + delta_b_d - max)/2;
								double c = (delta_a_c + delta_b_c + delta_c_d - max)/2;
								double d = (delta_a_d + delta_b_d + delta_c_d - max)/2;
								
								double x = (max - (delta_a_b + delta_c_d))/2;
								double y = (max - (delta_a_c + delta_b_d))/2;
								double z = (max - (delta_a_d + delta_b_c))/2;
								
								a_ += a;
								b_ += b;
								c_ += c;
								d_ += d;
								x_ += x;
								y_ += y;
								z_ += z;
								
								/*
								 * check invariants
								 */
								if(x != 0 && y != 0 && z != 0) {
									throw new IllegalArgumentException("Invariant #1 is wrong.");
								}
							}
						}
					}
				}
				
				a_ /= quartetNumbers;
				b_ /= quartetNumbers;
				c_ /= quartetNumbers;
				d_ /= quartetNumbers;
				x_ /= quartetNumbers;
				y_ /= quartetNumbers;
				z_ /= quartetNumbers;
				
				/*
				 * check for significant Genes
				 */
				if( x_ > 0 && x_ > y_  && x_ > z_ && y_ > 0 && z_ > 0){	
					double val = (x_ / (y_ + z_)/2);
					rawScores.add(genes[i], new DoubleMIO(val));
				}
			}
		}
		
		return res;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(this.getClass(),
				"PAS.genemining.quartetpuzzling", new String[0], ScoringPlugin.MC,
				new HashMap<String, Object>(), "G\u00FCnter J\u00E4ger",
				"jaeger@informatik.uni-tuebingen.de",
				"Quartet Mining is based on the information content of clustered expression profiles.\n " +
				"Quartet puzzling is an alternative formulation of quartet mining.",
				
				"Quartet Puzzling");
		return pli;
	}
	
	
	protected boolean isHeuristic() {
		return heuristic.getBooleanValue();
	}
	
	protected double getDistance(double a, double b) {
		return QuartetDistanceMeasures.getDistance(a, b, distanceMeasure.getSelectedIndex());
	}

	
}
