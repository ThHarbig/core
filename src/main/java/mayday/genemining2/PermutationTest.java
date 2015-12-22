package mayday.genemining2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import mayday.core.ClassSelectionModel;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.scoring.ScoringResult;
import mayday.core.math.scoring.TestPlugin;
import mayday.core.math.stattest.StatTestResult;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.tasks.AbstractTask;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class PermutationTest {

	protected static int permutations = 100;

	/**
	 * @param classes
	 * @param result
	 * @param method
	 * @param probes
	 * @param task
	 */
	public static void permutationTest(ClassSelectionModel classes,
			StatTestResult result, TestPlugin<ScoringResult> method, ProbeList probes, AbstractTask task) {
			
		//if there are no raw scores, permutation test cannot be performed.
		//return in this case!
		if(!result.hasRawScore()){
			return;
		}
		
		Collection<Object> objects = result.getRawScore().getObjects();
		Integer[][] classIndices = Genemining.classIndices(classes);
		// get all indices in one array
		Integer[] indices = new Integer[classIndices[0].length + classIndices[1].length];
		//are there enough experiments ? continue : return
		if (indices.length <= 4){
			return;
		}

		for (int i = 0; i < indices.length; i++) {
			for (int j = 0; j < classIndices[0].length; j++) {
				indices[j] = classIndices[0][j];
			}
			for (int j = 0; j < classIndices[1].length; j++) {
				indices[j + classIndices[0].length] = classIndices[1][j];
			}
		}

		boolean[] sum = new boolean[indices.length];
		boolean[] one = new boolean[indices.length];
		one[indices.length - 1] = true;
		int numExperiments = indices.length;
		int numberofSteps = (int)Math.pow(2,(indices.length));
		int iteration = -1;

		if (numExperiments <= 12) {
			task.setProgress(0);
			while (cardinality(sum) != numExperiments) {
				iteration++;
				Integer[][] perm = nextPermutation(indices, sum, one, classIndices[0].length);
				// continue if permutation is not valid
				if (cardinality(sum) != classIndices[0].length)
					continue;
				task.setProgress(((numberofSteps*iteration)/numberofSteps)*10);
				// Create new ClassSelectionModel according to the permuted
				// classes
				ClassSelectionModel newClasses = new ClassSelectionModel();
				for (int k = 0; k < perm[0].length; k++) {
					newClasses.addObject(classes.getObjectName(perm[0][k]), "Class_1");
				}
				for (int k = 0; k < perm[1].length; k++) {
					newClasses.addObject(classes.getObjectName(perm[1][k]),	"Class_2");
				}

				// run chosen method given the new ClassSelectionModel
				ScoringResult perResult = method.runTest(probes.getAllProbes(), newClasses);

				for (Object o : objects) {
					// Compare new and old raw scores and modify p-value if
					// needed
					double oldValue = ((DoubleMIO) result.getRawScore().getMIO(o)).getValue();
					double newValue = ((DoubleMIO) perResult.getRawScore().getMIO(o)).getValue();
					
					if(result.getRawScoreComparator().compare(newValue, oldValue) >= 0){
						if(result.getPValues().getMIO(o) != null){
							((DoubleMIO) result.getPValues().getMIO(o))
							.setValue(oldValue + 1.0);
						} else {
							result.getPValues().add(o, new DoubleMIO(oldValue + 1.0));
						}
					}
				}
			}
			for (Object o : objects) {
				Double oldValue = ((DoubleMIO) result.getPValues().getMIO(o)).getValue();
				if (oldValue > 0) {
					((DoubleMIO) result.getPValues().getMIO(o))
							.setValue(oldValue.doubleValue() - 1.0);
				}
			}
		} else {
			task.setProgress(0);
			for (int i = 0; i < permutations; i++) {
				task.setProgress(((numberofSteps*i)/numberofSteps)*10);
				Integer[][] perm = generateRandomPermutation(indices, classIndices[0].length);
				// Create new ClassSelectionModel according to the permuted
				// classes
				ClassSelectionModel newClasses = new ClassSelectionModel();
				for (int k = 0; k < perm[0].length; k++) {
					newClasses.addObject(classes.getObjectName(perm[0][k]),	"Class_1");
				}
				for (int k = 0; k < perm[1].length; k++) {
					newClasses.addObject(classes.getObjectName(perm[1][k]),	"Class_2");
				}
				// run chosen method given the new ClassSelectionModel
				ScoringResult perResult = method.runTest(probes.getAllProbes(), newClasses);

				for (Object o : objects) {
					// Compare new and old raw scores and modify p-value if
					// needed
					double oldValue = ((DoubleMIO) result.getRawScore().getMIO(o)).getValue();
					double newValue = ((DoubleMIO) perResult.getRawScore().getMIO(o)).getValue();
					
					if(result.getRawScoreComparator().compare(newValue, oldValue) >= 0){
						if(result.getPValues().getMIO(o) != null){
							((DoubleMIO) result.getPValues().getMIO(o))
							.setValue(oldValue + 1.0);
						} else {
							result.getPValues().add(o, new DoubleMIO(oldValue + 1.0));
						}
					}
				}
			}
		}
		// Calculate significance of original results
		for (Object o : objects) {
			Double oldValue = ((DoubleMIO) result.getPValues().getMIO(o)).getValue();
			if (oldValue != null) {
				((DoubleMIO) result.getPValues().getMIO(o))
				.setValue(oldValue.doubleValue() / permutations);
			} else {
				((DoubleMIO) result.getPValues().getMIO(o)).setValue(0.0);
			}
		}
	}
	
	/**
	 * @param classes
	 * @param result
	 * @param task
	 */
	public static void permutationTestHeuristic(ClassSelectionModel classes,
			StatTestResult result, AbstractTask task){
		//start permutation test
		permutations = 10000;
		if(!result.hasRawScore()) return;
		Collection<Object> objects = result.getRawScore().getObjects();
		Integer[][] classIndices = Genemining.classIndices(classes);

		// get all indices in one array
		Integer[] indices = new Integer[classIndices[0].length + classIndices[1].length];
		//are there enough experiments ? continue : return
		if (indices.length <= 4)
			return;

		for (int i = 0; i < indices.length; i++) {
			for (int j = 0; j < classIndices[0].length; j++) {
				indices[j] = classIndices[0][j];
			}
			for (int j = 0; j < classIndices[1].length; j++) {
				indices[j + classIndices[0].length] = classIndices[1][j];
			}
		}
		
		boolean[] sum = new boolean[indices.length];
		boolean[] one = new boolean[indices.length];
		one[indices.length - 1] = true;
		
		Map<Object, ArrayList<Double>> permDifferences = new HashMap<Object, ArrayList<Double>>();
		Map<Object, Double> originalDifferences = new HashMap<Object, Double>();
		
		for(Object o:objects){
			double sum1 = 0, sum2 = 0;
			for(int i = 0; i < classIndices[0].length; i++) {
				double v = ((Probe)o).getValue(classIndices[0][i]);
				sum1 += v;
			}
			for(int m = 0; m < classIndices[1].length; m++) {
				double v = ((Probe)o).getValue(classIndices[1][m]);
				sum2 += v;
			}
			assert(classIndices[0].length != 0);
			assert(classIndices[1].length != 0);
			//save difference
			originalDifferences.put(o, (sum1/classIndices[0].length)-(sum2/classIndices[1].length));
			permDifferences.put(o, new ArrayList<Double>());
		}
		
		int numExperiments = indices.length;
		int numberofSteps = (int)Math.pow(2,(indices.length));
		int iteration = 0;
		
		if(numExperiments <= 12){
			task.setProgress(0);
			while (cardinality(sum) != numExperiments) {
				task.setProgress((((numberofSteps*iteration) / numberofSteps)));
				iteration++;
				Integer[][] perm = nextPermutation(indices, sum, one, classIndices[0].length);
				// continue if permutation is not valid
				if (cardinality(sum) != classIndices[0].length)
					continue;
				for(Object o:objects){
					double sum1 = 0, sum2 = 0;
					for(int i = 0; i < perm[0].length; i++) {
						double v = ((Probe)o).getValue(perm[0][i]);
						sum1 += v;
					}
					for(int i = 0; i < perm[1].length; i++) {
						double v = ((Probe)o).getValue(perm[1][i]);
						sum2 += v;
					}
					assert(perm[0].length != 0);
					assert(perm[1].length != 0);
					permDifferences.get(o).add((sum1/perm[0].length)-(sum2/perm[1].length));
				}
			}
		}else{
			numberofSteps = permutations;
			for (int i = 0; i < permutations; i++) {
				task.setProgress((((numberofSteps*i) /numberofSteps)));
				Integer[][] perm = generateRandomPermutation(indices, classIndices[0].length);
				for(Object o:objects){
					double sum1 = 0, sum2 = 0;
					for(int j = 0; j < perm[0].length; j++){
						double v = ((Probe)o).getValue(perm[0][j]);
						sum1 += v;
					}
					for(int j = 0; j < perm[1].length; j++) {
						double v = ((Probe)o).getValue(perm[1][j]);
						sum2 += v;
					}
					assert(perm[0].length != 0);
					assert(perm[1].length != 0);
					permDifferences.get(o).add((sum1/perm[0].length) - (sum2/perm[1].length));
				}
			}
		}
		//Calculate the significance of the original value
		NormalDistributionImpl ndi = new NormalDistributionImpl();
		
		for(Object o:objects){
			ArrayList<Double> values = permDifferences.get(o);
			double sumForAverage = 0;
			for(int i = 0; i < values.size(); i++){
				sumForAverage += values.get(i);
			}
			double average = sumForAverage / values.size();
			
			double sumForSD = 0;
			for(int i = 0; i < values.size(); i++){
				sumForSD += (values.get(i)-average)*(values.get(i)-average);
			}
			double stDeviation = Math.sqrt((sumForSD)/(values.size()-1));
			
			double originalDif = originalDifferences.get(o);
			double pVal = Double.NaN;
			originalDif = (originalDif - average)/stDeviation;
			
			try{
				pVal = ndi.cumulativeProbability(originalDif);
			}catch(MathException me){
				System.out.println(((Probe)o).getName() + ": does not converge -> Can't compute p-value.");
			}
			
			if(pVal >= 0.5 && pVal != Double.NaN){
				pVal = 1 - pVal;	
			}
			if(result.getPValues().getMIO(o) != null){
				((DoubleMIO)result.getPValues().getMIO(o)).setValue(pVal);
			} else {
				result.getPValues().add(o, new DoubleMIO(pVal));
			}
		}
	}

	/*
	 * Generate a random class permutation
	 */
	private static Integer[][] generateRandomPermutation(Integer[] indices,
			int length) {
		Random r = new Random(length);
		boolean[] classes = new boolean[indices.length];
		int pos = 0;

		for (int i = 0; i < length; i++) {
			while (classes[pos = r.nextInt(indices.length)])
				continue;
			classes[pos] = true;
		}

		assert (cardinality(classes) == length);

		Integer[][] perm = new Integer[2][];
		perm[0] = new Integer[length];
		perm[1] = new Integer[indices.length - length];
		int class1 = 0, class2 = 0;

		for (int i = 0; i < classes.length; i++) {
			if (classes[i]) {
				perm[0][class1] = indices[i];
				class1++;
			} else {
				perm[1][class2] = indices[i];
				class2++;
			}
		}
		return perm;
	}

	protected static int cardinality(boolean[] sum) {
		int numberOfTrues = 0;
		for (int i = 0; i < sum.length; i++) {
			if (sum[i] == true) {
				numberOfTrues++;
			}
		}
		return numberOfTrues;
	}

	private static Integer[][] nextPermutation(Integer[] indices,
			boolean[] sum, boolean[] one, int size1) {
		binaryAddition(sum, one);
		Integer[][] result = new Integer[2][];
		result[0] = new Integer[size1];
		result[1] = new Integer[indices.length - size1];
		// perform this step, iff permutation is valid
		if (cardinality(sum) == size1) {
			int r1 = 0, r2 = 0;
			for (int i = 0; i < indices.length; i++) {
				if (sum[i] == true) {
					result[0][r1] = indices[i];
					r1++;
				} else {
					result[1][r2] = indices[i];
					r2++;
				}
			}
		}
		return result;
	}

	protected static void binaryAddition(boolean[] array1, boolean[] array2) {
		boolean overflow = false;

		for (int i = array1.length - 1; i >= 0; i--) {
			if (array1[i] && array2[i]) { // 1 AND 1
				if (overflow) {
					array1[i] = true;
					overflow = true;
				} else {
					array1[i] = false;
					overflow = true;
				}
			} else if (array1[i] || array2[i]) { // (1 AND 0) or (0 AND 1)
				if (overflow) {
					array1[i] = false;
					overflow = true;
				} else {
					array1[i] = true;
					overflow = false;
				}
			} else { // 0 AND 0
				if (overflow) {
					array1[i] = true;
					overflow = false;
				} else {
					array1[i] = false;
					overflow = false;
				}
			}
		}
	}
}
