package mayday.core.math.binning;

import java.util.List;

import mayday.core.MaydayDefaults;
import mayday.core.structures.linalg.matrix.AbstractMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.vis3.model.ViewModel;

public interface BinningStrategy 
{
	
	/** bin the values in a viewmodel. 
	 * @param vm the viewmodel to bin
	 * @return a matrix int[number_of_bins][number_of_experiments]
	 */
	int[][] execute(ViewModel vm);

	/** bin the values in a matrix, each column is binned independently 
	 * @param vm the matrix to bin
	 * @return a matrix int[number_of_bins][number_of_matrix_columns]
	 */
	int[][] execute(AbstractMatrix am);

	
	/** bin the values from an abstractvector
	 * @param av the vector to bin
	 * @return the sizes of the bins 
	 */
	int[] execute(AbstractVector av);
	
	
	public String MC = MaydayDefaults.Plugins.CATEGORY_VISUALIZATION+"/ProfileLogo Binning Strategies";
	
	public List<Double> getThresholds(ViewModel viewModel);
	
}
