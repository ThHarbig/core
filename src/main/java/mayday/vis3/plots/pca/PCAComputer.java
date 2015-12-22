package mayday.vis3.plots.pca;

import java.util.Collection;

import javax.swing.SwingUtilities;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.math.JamaSubset.Matrix;
import mayday.core.math.JamaSubset.PCA;
import mayday.core.tasks.AbstractTask;

public class PCAComputer extends AbstractTask {

	public PCAPlotComponent callBack;
	
	public PCAComputer(PCAPlotComponent pcc) {
		super("Computing PCA");
		callBack = pcc;
		start();
	}
	
	public void doWork() {
		try {
			Object[] temp = doPCA(callBack.viewModel.getProbes(), callBack.viewModel.getDataSet().getMasterTable());
			callBack.PCAData=(Matrix)temp[0];
			callBack.EigenValues=(double[])temp[1];
		} catch (Throwable e) {
			callBack.PCAData=null;
			throw new RuntimeException("PCA Computation failed. The reason was a "+e.getClass().getCanonicalName(),e);
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				callBack.updateWithPCAResult(PCAComputer.this);					
			}
		});
	}


	public Object[] doPCA (Collection<Probe> probeList, MasterTable masterTable)
	{
		int n = probeList.size();
		int m = masterTable.getNumberOfExperiments();

		double[][] indat;
		if (callBack.transpose_first)
			indat = new double[m][n];
		else 
			indat = new double[n][m];

		try {
			if (callBack.transpose_first) {
				int i=0;
				for (Probe tmp : probeList) { 
					for (int j=0; j!=m; ++j) {
						indat[j][i] = tmp.getValue(j);
					}
					++i;
				}
			} else { 
				int i=0;
				for (Probe tmp : probeList) { 
					for (int j=0; j!=m; ++j) {
						indat[i][j] = tmp.getValue(j);
					}
					++i;
				}
			}

		} catch (NullPointerException e){
			throw new RuntimeException("Cannot work on Probes containing missing values");
		}
		
		PCA pca = new PCA(indat);
		
		return new Object[]{pca.getResult(),pca.getEigenValues()};

	}

	@Override
	protected void initialize() {
	}
	
	
}
