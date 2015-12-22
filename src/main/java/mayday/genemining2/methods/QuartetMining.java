package mayday.genemining2.methods;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
import mayday.core.structures.linalg.vector.DoubleVector;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class QuartetMining extends AbstractMiningMethod {
	
	protected BooleanSetting heuristic;
	protected ObjectSelectionSetting<String> distanceMeasure;
	private HierarchicalSetting setting;
	
	public Setting getSetting() {
		if (setting==null) {
			this.setting = new HierarchicalSetting("Quartet Mining")
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
		double x_ = 0, y_ = 0, z_ = 0;
		
		/*
		 * calculate number of quartets
		 */
		int quartetNumbers = ((leftSize * (leftSize-1)) / 2) * ((rightSize * (rightSize-1)) / 2);
		
		if(quartetNumbers > 10000 && heuristic==true){ 
			quartetNumbers=1000;
			Random r = new Random();
			
			System.out.println(quartetNumbers +" quartets for each gene will be calculated");
			
			double[] xArray = new double[quartetNumbers];
			double[] yArray = new double[quartetNumbers];
			double[] zArray = new double[quartetNumbers];
			
			HashMap<Object,Double> sd = new HashMap<Object, Double>(geneSize);
			HashMap<Object,Double> xMean = new HashMap<Object, Double>(geneSize);
			HashMap<Object,Double> yzMean = new HashMap<Object, Double>(geneSize);
			
			AbstractVector sdArray = new DoubleVector(geneSize);
			
			Object[] genes = values.keySet().toArray();
			
			for(int i = 0; i < genes.length; i++) {
				setProgress(i*10000 / geneSize);
				/*
				 * generate quartets
				 */
				for(int counter = 0; counter <  quartetNumbers; counter++) {
					int l1 = 0; 
					int l2 = 0;
					int r1 = 0;
					int r2 = 0;
					
					while(l1==l2){
						l1=r.nextInt(leftSize);
						l2=r.nextInt(leftSize);
					}
					while(r1==r2){
						r1=r.nextInt(rightSize);
						r2=r.nextInt(rightSize);
					}
					
					A = data1.getColumn(l1);
					B = data1.getColumn(l2);
					C = data2.getColumn(r1);
					D = data2.getColumn(r2);
					
					double delta_a_b = getDistance(A.get(i), B.get(i));
					double delta_a_c = getDistance(A.get(i), C.get(i));
					double delta_a_d = getDistance(A.get(i), D.get(i));
					double delta_b_c = getDistance(B.get(i), C.get(i));
					double delta_b_d = getDistance(B.get(i), D.get(i));				
					double delta_c_d = getDistance(C.get(i), D.get(i));
					
					double max = Math.max(Math.max((delta_a_b + delta_c_d), (delta_a_c + delta_b_d)), (delta_a_d + delta_b_c));
					
					double x = (max - (delta_a_b + delta_c_d))/2;
					double y = (max - (delta_a_c + delta_b_d))/2;
					double z = (max - (delta_a_d + delta_b_c))/2;
					
					x_ += x;
					y_ += y;
					z_ += z;
					
					xArray[counter] = x;
					yArray[counter] = y;
					zArray[counter] = z;
					
					/*
					 * check invariants
					 */
					if(x != 0 && y != 0 && z != 0) {
						throw new IllegalArgumentException("Invariant #1 is wrong: "+
								"\nx="+x+
								"\ny="+y+
								"\nz="+z
							);
					}
				}
				
				x_ /= (double)quartetNumbers;
				y_ /= (double)quartetNumbers;
				z_ /= (double)quartetNumbers;
				
				xMean.put(genes[i], x_);
				yzMean.put(genes[i], (y_ + z_)/2);
				
				double var = 0;
				
				for(int k = 0; k < quartetNumbers; k++) {
					var += (xArray[k] - x_) * (xArray[k] - x_);
				}
				for(int k = 0; k < quartetNumbers; k++) {
					var += (yArray[k] - y_) * (yArray[k] - y_);
				}
				for(int k = 0; k < quartetNumbers; k++) {
					var += (zArray[k] - z_) * (zArray[k] - z_);
				}

				double stdDev = Math.sqrt( ((1/(double)quartetNumbers) + (1/(double)quartetNumbers) + (1/(double)quartetNumbers)) * var);
				
				sd.put(genes[i], stdDev);
				sdArray.set(i, stdDev);
			}
			
			double fudgeFactor = sdArray.median();
			
			for(int i = 0; i < genes.length; i++) {
				double qmScore = Math.abs(xMean.get(genes[i]) - yzMean.get(genes[i])) / (sd.get(genes[i]) + fudgeFactor);
				rawScores.add(genes[i], new DoubleMIO(qmScore));
			}
		} else {
			System.out.println(quartetNumbers +" quartets for each gene will be calculated");
			
			double[] xArray = new double[quartetNumbers];
			double[] yArray = new double[quartetNumbers];
			double[] zArray = new double[quartetNumbers];
			
			HashMap<Object,Double> sd = new HashMap<Object, Double>(geneSize);
			HashMap<Object,Double> xMean = new HashMap<Object, Double>(geneSize);
			HashMap<Object,Double> yzMean = new HashMap<Object, Double>(geneSize);
			
			AbstractVector sdArray = new DoubleVector(geneSize);
			
			Object[] genes = values.keySet().toArray();
			
			for(int i = 0; i < genes.length; i++) {
				setProgress(i*10000 / geneSize);

				/*
				 * generate quartets
				 */
				int counter = 0;
				for(int l1 = 0; l1 < leftSize-1; l1++) {
					A = data1.getColumn(l1);
					for(int l2 = l1+1; l2 < leftSize; l2++) {
						B = data1.getColumn(l2);
						for(int r1 = 0; r1 < rightSize-1; r1++) {
							C = data2.getColumn(r1);
							for(int r2 = r1+1; r2 < rightSize; r2++) {
								D = data2.getColumn(r2);
								
								double delta_a_b = getDistance(A.get(i), B.get(i));
								double delta_a_c = getDistance(A.get(i), C.get(i));
								double delta_a_d = getDistance(A.get(i), D.get(i));
								double delta_b_c = getDistance(B.get(i), C.get(i));
								double delta_b_d = getDistance(B.get(i), D.get(i));				
								double delta_c_d = getDistance(C.get(i), D.get(i));
								
								double max = Math.max(Math.max((delta_a_b + delta_c_d), (delta_a_c + delta_b_d)), (delta_a_d + delta_b_c));
								
								double x = (max - (delta_a_b + delta_c_d))/2;
								double y = (max - (delta_a_c + delta_b_d))/2;
								double z = (max - (delta_a_d + delta_b_c))/2;
								
								x_ += x;
								y_ += y;
								z_ += z;
								
								xArray[counter] = x;
								yArray[counter] = y;
								zArray[counter] = z;
								counter++;
								
								/*
								 * check invariants
								 */
								if(x != 0 && y != 0 && z != 0) {
									throw new IllegalArgumentException("Invariant #1 is wrong: "+
											"\nx="+x+
											"\ny="+y+
											"\nz="+z
										);
								}
							}
						}
					}
				}
				
				x_ /= (double)quartetNumbers;
				y_ /= (double)quartetNumbers;
				z_ /= (double)quartetNumbers;
				
				xMean.put(genes[i], x_);
				yzMean.put(genes[i], (y_ + z_)/2);
				
				double var = 0;

				for(int k = 0; k < quartetNumbers; k++) {
					var += (xArray[k] - x_) * (xArray[k] - x_);
				}
				for(int k = 0; k < quartetNumbers; k++) {
					var += (yArray[k] - y_) * (yArray[k] - y_);
				}
				for(int k = 0; k < quartetNumbers; k++) {
					var += (zArray[k] - z_) * (zArray[k] - z_);
				}

				double stdDev = Math.sqrt( ((1/(double)quartetNumbers) + (1/(double)quartetNumbers) + (1/(double)quartetNumbers)) * var);
				
				sd.put(genes[i], stdDev);
				sdArray.set(i, stdDev);
			}
			
			double fudgeFactor = sdArray.median();
			
			for(int i = 0; i < genes.length; i++) {
				double qmScore = Math.abs(xMean.get(genes[i]) - yzMean.get(genes[i])) / (sd.get(genes[i]) + fudgeFactor);
				rawScores.add(genes[i], new DoubleMIO(qmScore));
			}
		}
		
		return res;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(this.getClass(),
				"PAS.genemining.quartetmining", new String[0], ScoringPlugin.MC,
				new HashMap<String, Object>(), "G\u00FCnter J\u00E4ger",
				"jaeger@informatik.uni-tuebingen.de", 
				"Quartet Mining is based on the information content of clustered expression profiles.\n " +
				"QM investigates all quartets induced by 4 experiments of two classes and calculates the \n" +
				"average ratio of distances in the induces quartet and the alternative quartets.",
				"Quartet Mining");
		return pli;
	}
	
	protected boolean isHeuristic() {
		return heuristic.getBooleanValue();
	}
	
	protected double getDistance(double a, double b) {
		return QuartetDistanceMeasures.getDistance(a, b, distanceMeasure.getSelectedIndex());
	}
	
}
