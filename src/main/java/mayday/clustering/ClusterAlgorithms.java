/*
 * Created on 25.07.2003
 *
 */
package mayday.clustering;

import mayday.core.structures.linalg.matrix.PermutableMatrix;

/**
 * 0.2:  switch to Matrix
 * 
 * @author Janko Dietzsch
 * @author Markus Riester
 * @version 0.2
 */
public abstract class ClusterAlgorithms {

	protected PermutableMatrix ClusterData;
	protected int rows_ClusterData,cols_ClusterData;
	protected IProgressState ProgressHook;
	
	public ClusterAlgorithms(PermutableMatrix data) {
	    ClusterData = data;
		rows_ClusterData=ClusterData.nrow();
		cols_ClusterData=ClusterData.ncol();
	}
	
	public IProgressState getProgressHook() {
		return ProgressHook;
	}

	public void setProgressHook(IProgressState progressHook) {
		ProgressHook = progressHook;
	}

	public abstract int [] runClustering();
	
}
