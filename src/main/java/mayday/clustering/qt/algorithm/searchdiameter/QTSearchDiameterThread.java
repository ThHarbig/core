package mayday.clustering.qt.algorithm.searchdiameter;

import java.util.ArrayList;

import javax.swing.SwingWorker;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.structures.linalg.matrix.AbstractMatrix;
import mayday.clustering.qt.algorithm.QTPMath;
/**
 * @author Sebastian Nagel
 *
 */
public class QTSearchDiameterThread extends SwingWorker<Void,Void> {
	
	private DistanceMeasurePlugin measure;
	private AbstractMatrix matrix;
	private int indexFrom;
	private int indexTo;
	private ArrayList<Double> distances;
	private int spaceCount;

	public QTSearchDiameterThread(DistanceMeasurePlugin measure, 
			AbstractMatrix matrix, int indexFrom, int indexTo, int spaceCount) {
		this.measure = measure;
		this.matrix = matrix;
		this.indexFrom = indexFrom;
		this.indexTo = indexTo;
		this.spaceCount = spaceCount;
		this.distances = new ArrayList<Double>();
	}

	@Override
	protected Void doInBackground() throws Exception {
		
		int element = 0;
		for (int i=indexFrom; i <= indexTo; i++) {
			element = QTPMath.random((i-1)*spaceCount+1, i*spaceCount);
			int[] indizes = QTStatistics.getIndizes(matrix, element);
			distances.add(measure.getDistance(
					matrix.getRow(indizes[0]), matrix.getRow(indizes[1])));
			
			if(this.isCancelled()) {
				return null;
			}
		}
				
		return null;
	}
	
	public ArrayList<Double> getDistances() {
		return distances;
	}
}
