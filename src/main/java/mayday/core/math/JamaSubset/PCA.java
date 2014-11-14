package mayday.core.math.JamaSubset;

public class PCA {

	private Matrix input_data;
	private Matrix output_data;
	private double[] Evals;
	
	public PCA(double[][] input) {
		input_data = new Matrix(input);
		// no calc yet, let's be lazy
	}
	
	public Matrix getResult() {
		if (output_data==null)
			output_data = doPCA(input_data);
		return output_data;
	}
	
	public double[] getEigenValues() {
		if (output_data == null)
			output_data = doPCA(input_data);
		return Evals;
	}
	

	  private Matrix doPCA(Matrix indat) {
		  int m=indat.getColumnDimension();
	      double[][] indatstd = Standardize(indat).getArray();

	      Matrix X = new Matrix(indatstd);

	      Matrix Xprime = X.transpose();
	      Matrix SSCP   = Xprime.times(X);

	      // Eigen decomposition
	      EigenvalueDecomposition evaldec = SSCP.eig();
	      Matrix evecs = evaldec.getV();
	      double[] evals = evaldec.getRealEigenvalues();

//	      double tot = 0.0; 
//	      for (int j = 0; j < evals.length; j++)  {
//	          tot += evals[j]; 
//	      }

	      // reverse order of evals into Evals
	      Evals = new double[m];
	      for (int j = 0; j < m; j++) {
	          Evals[j] = evals[m - j - 1];
	      }
	      // reverse order of Matrix evecs into Matrix Evecs
	      double[][] tempold = evecs.getArray();
	      double[][] tempnew = new double[m][m]; 
	      for (int j1 = 0; j1 < m; j1++) {
	          for (int j2 = 0; j2 < m; j2++) {
	              tempnew[j1][j2] = tempold[j1][m - j2 - 1];
	 	 }
	      }
	      Matrix Evecs = new Matrix(tempnew);

	       //-------------------------------------------------------------------
	       // Projections - row, and col
	       // Row projections in new space, X U  Dims: (n x m) x (m x m)
	       Matrix rowproj = X.times(Evecs); 

	       return rowproj;
	  }
	  
	  
	  private Matrix Standardize(Matrix A) {
		  return new Matrix(Standardize(A.getRowDimension(), A.getColumnDimension(), A.getArray()));
	  }

	  private double[][] Standardize(int nrow, int ncol, double[][] A)
	  {
	  double[] colmeans = new double[ncol];
	  double[] colstdevs = new double[ncol];
	  // Adat will contain the standardized data and will be returned
	  double[][] Adat = new double[nrow][ncol];
	  double[] tempcol = new double[nrow];
	  double tot; 

	  // Determine means and standard deviations of variables/columns
	  for (int j=0; j<ncol; j++)
	      {
	       tot = 0.0;
	       for (int i=0; i<nrow; i++)
	           {
	              tempcol[i] = A[i][j];
	              tot += tempcol[i];
	           }

		 // For this col, det mean
	       colmeans[j] = tot/(double)nrow;
	       for (int i=0; i<nrow; i++) {
	              colstdevs[j] += Math.pow(tempcol[i]-colmeans[j], 2.0);
	           }
	           colstdevs[j] = Math.sqrt(colstdevs[j]/((double)nrow));
	           if (colstdevs[j] < 0.0001) { colstdevs[j] = 1.0; }
		}

		// Now ceter to zero mean, and reduce to unit standard deviation
	  for (int j=0; j<ncol; j++)
	      {
	       for (int i=0; i<nrow; i++)
	           {
	             Adat[i][j] = (A[i][j] - colmeans[j])/
	               (Math.sqrt((double)nrow)*colstdevs[j]);
	           }
	      }
	  return Adat;
	  } // Standardize


	
}
