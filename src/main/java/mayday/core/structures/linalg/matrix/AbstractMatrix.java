package mayday.core.structures.linalg.matrix;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;


/** Package naming conventions:
 * All functions ending in "0" (get0, set0, dim0, ...) are direct access methods that DO NEVER transform indices
 * Their partner functions NOT ending in "0" apply transformations as necessary 
 * @author fb
 *
 */
public abstract class AbstractMatrix  {

	// access to the underlying data structure
	protected abstract double get0(int row, int col);
	protected abstract void set0(int row, int col, double value);
	protected abstract int dim0(int dimension);
	
	// creation of the most efficient vector-based access class
	protected abstract AbstractVector getDimVec0(int dimension, int index);
	
	
	// intermediate access functions
	protected double get(int row, int col) {
		return get0(row, col);
	}
	
	protected void set(int row, int col, double value) {
		set0(row, col, value);
	}
	
	protected int dim(int dimension) {
		return dim0(dimension);
	}
	
	protected AbstractVector getDimVec(int dimension, int index) {
		return getDimVec0(0, index);
	}
	
	/** produce a clone of this matrix that is not changed due to permutation, transposition etc. (data can change though) */
	public abstract AbstractMatrix staticShallowClone();

	
	public DoubleMatrix deepClone() {
		return new DoubleMatrix(this);
	}
	
	// ============ Public access ===========================
	
	public final int nrow() {
		return dim(0);
	}
	
	public final int ncol() {
		return dim(1);
	}
	
	public final boolean isSquare() {
		return dim0(0)==dim0(1);
	}	
	
	public final double getValue(int row, int column) {
		return get(row, column);
	}
	public final void setValue(int row, int column, double value) {
		set(row,column,value);
	}
	
	public final void setColumn( int columnIndex, AbstractVector v ) {
		for(int i=0; i!=nrow(); ++i)
			set(i, columnIndex, v.get(i));
	}
	
	public final void setRow( int rowIndex, AbstractVector v ) {
		for(int i=0; i!=ncol(); ++i)
			set(rowIndex, i, v.get(i));
	}
	
	public final void setColumn( int columnIndex, List<Double> v ) {
		for(int i=0; i!=nrow(); ++i)
			set(i, columnIndex, v.get(i));
	}
	
	public final void setRow( int rowIndex, List<Double> v ) {
		for(int i=0; i!=ncol(); ++i)
			set(rowIndex, i, v.get(i));
	}
	
	public final void setColumn( int columnIndex, double[] v ) {
		for(int i=0; i!=nrow(); ++i)
			set(i, columnIndex, v[i]);
	}
	
	public final void setRow( int rowIndex, double[] v ) {
		for(int i=0; i!=ncol(); ++i)
			set(rowIndex, i, v[i]);
	}
		
	public final AbstractVector getRow(final int row) {
		return getDimVec(0, row);
	}

	public final AbstractVector getColumn(final int column) {
		return getDimVec(1, column);
	}
	
	public final AbstractVector getDiagonal() {
		if (!isSquare())
			throw new RuntimeException("Only square matrices have diagonals, this one is "+nrow()+" x "+ncol()+" in size.");
		return new MatrixDiagonal(staticShallowClone());
	}
	
	// Submatrix extraction
	
	public final PermutableMatrix submatrix(final int[] rows, final int[] columns) {
		return new PermutableSubMatrix(staticShallowClone(), rows, columns);	
	}
	
	
	// functions from the old core matrix
	/**
	 * Normalizes this matrix rowwise
	 *
	 */
	public final void normalizeRowWise(boolean ignoreNA) {
		for (int i=0; i!=nrow(); ++i)
			getRow(i).normalize(ignoreNA);
	}
	
	public final void normalizeRowWise() {
		normalizeRowWise(false);
	}
	
	/**
	 * Normalizes this matrix colwise
	 *
	 */
	public final void normalizeColWise(boolean ignoreNA) {		
		for (int i=0; i!=ncol(); ++i)
			getColumn(i).normalize(ignoreNA);
	}
	
	public final void normalizeColWise() {
		normalizeColWise(false);
	}
    
	   /**
     * This function returns the coordinates of the smallest value in this matrix
     * (only the first occurance is reported). If param "ignoreDiagonalelements" 
     * is true, then all values where i=j (the diagonal elements) will be ignored.
     * This is useful for finding the minimum in distance matrices.
     * 
     *  @param ignoreDiagonalElements
     *  @return coordinates of min value
     */
    public final Point2D getPositionOfMinValue(boolean ignoreDiagonalElements) {
        Point2D p = new Point2D.Double();
        double min = Double.MAX_VALUE;
        for (int i = 0; i != nrow(); ++i) {
            for (int j = 0; j != ncol(); ++j) {
            	double thisVal = this.getValue(i,j);
                if (!Double.isNaN(thisVal) && thisVal <min && (ignoreDiagonalElements == false || i != j)) {
                    min = thisVal;
                    p.setLocation(i,j);
                }
            }
        }
        return p;
    }
    /**
     * This function returns the coordinates of the highest value in this matrix
     * (only the first occurance is reported). If param "ignoreDiagonalelements" 
     * is true, then all values where i=j (the diagonal elements) will be ignored.
     * 
     *  @param ignoreDiagonalElements
     *  @return coordinates of min value
     */
    public final Point2D getPositionOfMaxValue(boolean ignoreDiagonalElements) {
    	Point2D p = new Point2D.Double();
        double max = Double.MIN_VALUE;
        for (int i = 0; i != nrow(); ++i) {
            for (int j = 0; j != ncol(); ++j) {
            	double thisVal = this.getValue(i,j);
                if (!Double.isNaN(thisVal) && thisVal > max && (ignoreDiagonalElements == false || i != j)) {
                    max = thisVal;
                    p.setLocation(i,j);
                }
            }
        }
        return p;
    }
    
   /**
    * If param "ignoreDiagonalelements" is true, then all values where i=j 
    * (the diagonal elements) will be ignored. This is useful for finding the 
    * minimum in distance matrices where the diagonal elements are zero
    * and all other values are greater than zero
    * 
    * @param ignoreDiagonalElements
    * @return the smallest value in matrix
    */ 
    public final double getMinValue(boolean ignoreDiagonalElements) {
    	Point2D p = getPositionOfMinValue(ignoreDiagonalElements);
        return getValue((int)p.getX(), (int)p.getY());
    }
    
    /**
    * If param "ignoreDiagonalelements" is true, then all values where i=j 
    * (the diagonal elements) will be ignored. 
    * 
    * @param ignoreDiagonalElements
    * @return the highest value in matrix
    */ 
    public final double getMaxValue(boolean ignoreDiagonalElements) {
    	Point2D p = getPositionOfMaxValue(ignoreDiagonalElements);
        return getValue((int)p.getX(), (int)p.getY());
    }
    
    /**
     * This function returns the overall mean of the matrix
     */
    public final double getMeanValue() {
    	double div = nrow()*ncol();
        double mean = 0;
        for (int i = 0; i != nrow(); ++i) {
            for (int j = 0; j != ncol(); ++j) {
            	mean += (getValue(i,j)/div);
            	if (Double.isNaN(mean) || Double.isInfinite(mean))
            		break;
            }
        }
        return mean;
    }
    
    /**
     * This function returns the overall standard deviation of the matrix
     */
    public final double getStdDev() {
    	double mean = getMeanValue();
    	if (Double.isNaN(mean))
    		return mean;
    	
		double stdev = 0.0d;
    	double div = nrow()*ncol();

        for (int i = 0; i != nrow(); ++i) {
            for (int j = 0; j != ncol(); ++j) {
            	double val = getValue(i,j);
				stdev+=(val-mean)*(val-mean);
			}
        }
        return Math.sqrt(stdev/(div-1));
    }
	
    /** Apply a function to each row or each column of the matrix and return the vector of results.
     * The applicable function MUST have the signature
     *   <Number> name ( AbstractVector, ... )
     * where <Number> can be Double, Integer etc, or double, int, ...
     * and the types of ... must match the types of "furtherParams". Primitives DO NOT WORK
     * 
     * @param dimension 0=apply for each row, 1=apply for each column
     * @param functionBase the object that implements the applicable function 
     * @param functionName the name of the function to apply
     * @param furtherParams further parameters to the function
     * @return an AbstractVector of the functions results
     * @throws All kinds of nasty reflection exceptions.
     */
    public final AbstractVector apply(int dimension, Object functionBase, String functionName, Object... furtherParams) {
    	// get the function
    	Class<?>[] argumentClasses  = new Class[furtherParams.length+1];
    	argumentClasses[0] = AbstractVector.class;
    	for (int i=0; i!=furtherParams.length; ++i)
    		argumentClasses[i+1] = furtherParams[i].getClass();
    	
    	Method fun;
    	
    	try {
    		fun = functionBase.getClass().getMethod(functionName, argumentClasses);
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new RuntimeException("Can not find function to apply.");
    	}
    	
       	if (!Number.class.isAssignableFrom(fun.getReturnType()) && !fun.getReturnType().isPrimitive()) {
    		throw new RuntimeException("Can only apply functions that return a numeric OBJECT value\n" +
    				"reported return type is "+fun.getReturnType().getCanonicalName());
    	}
    	
    	AbstractVector result = new DoubleVector(dim(dimension));

    	Object[] input = new Object[furtherParams.length+1];
		System.arraycopy(furtherParams, 0, input, 1, furtherParams.length);
		
		try {
			for (int i=0; i!=result.size(); ++i) {
				AbstractVector dimElement = getDimVec(dimension, i);
				input[0] = dimElement;
				Object ret = fun.invoke(functionBase, input);
				Number n = (Number)ret;
				Double d = n.doubleValue();
				result.set(i, d);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException("Function "+fun.getName()+" can not be applied!");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Function "+fun.getName()+" can not be applied!");
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException("Function "+fun.getName()+" can not be applied!");
		}
    	
    	return result;    	
    }
    
    /** Apply a AbstractVector function to each row or each column of the matrix and return the vector of results.
     * The applicable function is called on the row/column AbstractVector
     * @see apply(int, Object, String, Object...)
     * 
     * @param dimension 0=apply for each row, 1=apply for each column
     * @param functionName the name of the function to apply from class AbstractVector
     * @param furtherParams further parameters to the function
     * @return an AbstractVector of the functions results
     * @throws All kinds of nasty reflection exceptions.
     */
    public final AbstractVector applyVec(int dimension, String functionName, Object... furtherParams) {
    	// get the function
    	Class<?>[] argumentClasses  = new Class[furtherParams.length];
    	for (int i=0; i!=furtherParams.length; ++i)
    		argumentClasses[i] = furtherParams[i].getClass();
    	
    	Method fun;
    	
    	try {
    		fun = AbstractVector.class.getMethod(functionName, argumentClasses);
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new RuntimeException("Can not find function to apply.");
    	}
    	
    	if (!Number.class.isAssignableFrom(fun.getReturnType()) && !fun.getReturnType().isPrimitive()) {
    		throw new RuntimeException("Can only apply functions that return a numeric OBJECT value\n" +
    				"reported return type is "+fun.getReturnType().getCanonicalName());
    	}
    	
    	AbstractVector result = new DoubleVector(dim(dimension));

		try {
			for (int i=0; i!=result.size(); ++i) {
				AbstractVector dimElement = getDimVec(dimension, i);
				Object ret = fun.invoke(dimElement, furtherParams);
				Number n = (Number)ret;
				Double d = n.doubleValue();
				result.set(i, d);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException("Function "+fun.getName()+" can not be applied!");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Function "+fun.getName()+" can not be applied!");
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException("Function "+fun.getName()+" can not be applied!");
		}
    	
    	return result;    	
    }
    
	public String toString() {
		StringBuffer res = new StringBuffer();
		res.append("{{\n");
		for (int i=0; i!=nrow(); ++i) {
			for (int j=0; j!=ncol(); ++j) {
				res.append(" ");
				res.append(get(i,j));
			}
			res.append("\n");
		}
		res.append("}}");
		return res.toString();
	}
	
	
}
