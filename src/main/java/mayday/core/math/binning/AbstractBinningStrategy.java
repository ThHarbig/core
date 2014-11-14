package mayday.core.math.binning;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.structures.linalg.matrix.AbstractMatrix;
import mayday.core.structures.linalg.matrix.VectorBasedMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.vis3.model.ViewModel;

public abstract class AbstractBinningStrategy extends AbstractPlugin implements BinningStrategy {
	
	public int[][] execute(ViewModel vm) {
		return execute(vm.asMatrix());
	}
	
	public int[][] execute(AbstractMatrix matrix) {
		
		initThresholds(matrix);
		
		int noe = matrix.ncol();
		double[] thresholds = getThresholdValues();
		int not = thresholds.length;
		
		int[][] result=new int[not+1][noe];		
		
		for(int i=0; i!= noe; ++i) {
			int[] oneColumn = execute(matrix.getColumn(i), thresholds);
			for (int k=0; k!=oneColumn.length; ++k)
				result[k][i] = oneColumn[k];			
		}		
		return result;		
	}
	
	public int[] execute(AbstractVector vector) {
		initThresholds(new VectorBasedMatrix(true, vector));
		double[] thresholds = getThresholdValues();
		return execute(vector, thresholds);
	}
	
	protected int[] execute(AbstractVector vector, double[] thresholds) {
		
		int[] result = new int[thresholds.length+1];
		
		for (int k=0; k!=vector.size(); ++k) {
			
			double val = vector.get(k);
			
			boolean a=false;
			for(int j=0; j!= thresholds.length; ++j ) {
				if(val <= thresholds[j]) {
					result[j]++;
					a=true;
					break;
				}
			}
			if(!a) 
				result[thresholds.length]++;
		}
		
		return result; 
	}
	
	protected abstract double[] getThresholdValues();

	protected abstract void initThresholds(AbstractMatrix am);
	
}
