package mayday.clustering.qt.algorithm.searchdiameter;

import java.util.Arrays;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.structures.linalg.matrix.AbstractMatrix;
import mayday.core.tasks.AbstractTask;
import mayday.clustering.qt.algorithm.QTPMath;



/**
 * @author Sebastian Nagel
 * @author G&uuml;nter J&auml;ger
 * @version 0.1
 */
public class QTStatistics {
	
	/**
	 * Calculates all n(n-1)/2 distance values
	 * @param measure
	 * @param matrix
	 * @param numberOfDistances 
	 * @param sdTask 
	 * @return distances
	 */
	public static double[] distances(final DistanceMeasurePlugin measure, 
			final AbstractMatrix matrix, final int numberOfDistances) {

		int rows = matrix.nrow();
		int rowsrows=(rows*(rows-1))/2;
		final int spaceCount = (int) Math.floor(rowsrows/numberOfDistances);
		if (rowsrows<0)
			throw new OutOfMemoryError();
		final double[] distances = new double[numberOfDistances];

		AbstractTask task = new AbstractTask("Calculating distances for qt search diameter plugin ...") {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				int element = 0;
				for (int i=1; i <= numberOfDistances; i++) {
					element = QTPMath.random((i-1)*spaceCount+1, i*spaceCount);
					int[] indizes = getIndizes(matrix, element);
					double d = measure.getDistance(matrix.getRow(indizes[0]), matrix.getRow(indizes[1]));
					distances[i-1] = d;
					
					if(hasBeenCancelled()) {
						writeLog("Search for optimal diameter has been canceled!");
						processingCancelRequest();
						return;
					}
				}
				
				Arrays.sort(distances);
				QTPMath.round(distances);
			}
		};

		task.start();
		task.waitFor();
		
		return distances;
	}
	
	public static int[] getIndizes (AbstractMatrix matrix, int element) {
		int[] indizes = new int[2];
		
		for (int i = 1; i <= matrix.nrow(); i++) {
			int nrow = matrix.nrow() - i;
			if (element <= nrow) {
				indizes[0] = i-1;
				indizes[1] = matrix.nrow()-nrow+element-1;
				break;
			}
			element = element-nrow;
		}

		return indizes;
	}
	
	/**
	 * @param numOfValues
	 * @param distances
	 * @return distribution, the distribution of the distance values
	 */
	public static int[] distribution(int numOfValues, double[] distances) {
		int[] distribution = new int[numOfValues];
		int value = 0;
		double distanceValue = distances[0];
		for(int i = 0; i < distances.length; i++) {
			if(distanceValue == distances[i]) {
				distribution[value]++;
			} else {
				value++;
				distanceValue = distances[i];
				distribution[value]++;
			}
		}
		return distribution;
	}
	
	/**
	 * Calculates an optimal value for the threshold diameter
	 * @param gaussian 
	 * @param distances 
	 * @return diameter, a proposed value for the diameter
	 */
	public static double getBestDiameter(double[] distances) {
		int diamIndex = QTStatistics.getArrayMaxIndex(distances);
		return distances[diamIndex];
	}
	
	/**
	 * @param values
	 * @return index of maximum
	 */
	public static int getArrayMaxIndex(double[] values) {	
		int index = 0;
		double value = values[0];
		for(int i = 0; i < values.length; i++) {
			if(value < values[i]) {
				value = values[i];
				index = i;
			}
		}
		return index;
	}

	/**
	 * @param distances
	 * @param distribution 
	 * @return normal-distribution
	 */
	public static double[] normalDistribution(double[] distances, int[] distribution) {
		double[] gaussian = new double[distances.length];
		double meanValue = getMean(distances, distribution);
		double stdev = getStDev(meanValue, distances, distribution);
		double cof = 1/(stdev * Math.sqrt(2*Math.PI));
		for(int i = 0; i < distances.length; i++) {
			gaussian[i] = cof * Math.exp(-0.5 * ((distances[i] - meanValue)/stdev) * ((distances[i] - meanValue)/stdev));
		}
		return gaussian;
	}

	private static double getMean(double[] distances, int[] distribution) {
		double mean = 0.;
		int sum = 0;
		for(int i = 0; i < distances.length; i++) {
			mean += distances[i]*distribution[i];
			sum += distribution[i];
		}
		return mean/sum;
	}

	private static double getStDev(double meanValue, double[]distances, int[] distribution) {
		double result = 0.;
		int sum = 0;
		for(int i = 0; i < distances.length; i++) {
			result += (distances[i] - meanValue)*(distances[i] - meanValue)*distribution[i];
			sum += distribution[i];
		}
		result = result/(sum-1);
		return Math.sqrt(result);
	}
}
