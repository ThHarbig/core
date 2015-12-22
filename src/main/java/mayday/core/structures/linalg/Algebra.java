package mayday.core.structures.linalg;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import mayday.core.structures.linalg.matrix.AbstractMatrix;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.matrix.VectorBasedMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;

public class Algebra {

	public static PermutableMatrix matrixFromMap(Map<Object, double[]> values, Map<Object, Integer> indexMap) {
		double[][] data = new double[values.size()][]; 

		DoubleMatrix ret = new DoubleMatrix(data, false);
		
		int j=0;
		for (Entry<Object, double[]> e : values.entrySet()) {
			indexMap.put(e.getKey(), j);
			data[j] = e.getValue();
			ret.setColumnName(j, e.getKey().toString());
			++j;
		}


		ret.transpose(); // one row per object
		return ret;
	}
	
	public static Object createTypedArray(Class<?> elementClass, int size) {

		Class<?> typeClass;
		
		// catch primitives types 		
		try {
			Field typeField = elementClass.getDeclaredField("TYPE");
			Object typeValue = typeField.get(null);
			typeClass = (Class<?>)typeValue;
			return Array.newInstance(typeClass, size);
		} catch (NoSuchFieldException nsfe) {
			;
		} catch (IllegalAccessException iae) {
			;
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <ArrayType> ArrayType createNativeArray(Object[] array) {
		ArrayType arr = (ArrayType)createTypedArray(array[0].getClass(), array.length);
		int j=0;
		for (Object i : array)
			Array.set(arr, j++, i);
		return arr;
	}
	

	 /**
     * 
     * @param Vec1
     * @param Vec2
     * @return the covariance 
     */
	public static double cov(AbstractVector Vec1, AbstractVector Vec2) {
		if (Vec1.size() != Vec2.size()) {
			throw new IllegalArgumentException("Dimensions of the vectors are different");
		}
		int dim = Vec1.size();
		double Covariance = 0.0;
		double mean_vec1 = Vec1.mean();
		double mean_vec2 = Vec2.mean();
		
		for (int x = 0; x < dim; x++) {
			Covariance +=  (Vec1.get(x)-mean_vec1) * (Vec2.get(x)-mean_vec2);
		}
		
		Covariance /= dim - 1;
		return Covariance;
		
	}
	
	
	/**
	 * 
	 * @param Vec1
	 * @param Vec2
	 * @return the pearson correlation coefficent
	 */
	public static double cor(AbstractVector Vec1, AbstractVector Vec2) {
		if (Vec1.size() != Vec2.size()) {
			throw new IllegalArgumentException("Dimensions of the vectors are different");
		}
		int dim = Vec1.size();
		double S_xy = 0.0;
		double S_x  = 0.0;
		double S_y  = 0.0;
		
		double mean_vec1 = Vec1.mean();
		double mean_vec2 = Vec2.mean();
		
		for (int x = 0; x < dim; x++) {
			S_xy +=  (Vec1.get(x)-mean_vec1) * (Vec2.get(x)-mean_vec2);
			/*
			 * is this significantly faster in Java than Math.pow(,2)???
			 */
			S_x  +=  (Vec1.get(x)-mean_vec1) * (Vec1.get(x)-mean_vec1);
			S_y  +=  (Vec2.get(x)-mean_vec2) * (Vec2.get(x)-mean_vec2);
		}
		
		return S_xy / (Math.sqrt(S_x) * Math.sqrt(S_y));
		
	}
	
	
	/**
	 * This function calculates cosinus alpha between two NumVectors<p>
	 * 
	 * Formula: scalarProduct(vec1, vec2) / ( norm(a) * norm(b) )
	 * 
	 * @param Vec1
	 * @param Vec2
	 * @return cos alpha
	 */
	public static double angle(AbstractVector Vec1, AbstractVector Vec2) {
		return ( scalarProduct(Vec1,Vec2)/ (Vec1.norm() * Vec2.norm() ) );
	}
	
	/**
	 * This functions returns the scalar ("inner") product of two NumVectors
	 * 
	 * @param Vec1
	 * @param Vec2
	 * @return
	 */
	public static double scalarProduct(AbstractVector Vec1, AbstractVector Vec2) {
		if (Vec1.size() != Vec2.size()) {
			throw new IllegalArgumentException("Dimensions of the vectors are different.");
		}
		int dim = Vec1.size();
		double product = 0.0;
		
		for (int x = 0; x < dim; x++) {
			product +=  Vec1.get(x) * Vec2.get(x);
		}
		
		return product;
	}
	
	
	/**
	 * 
	 * Matrix-vector multiplication 
	 * 
	 * @param m1 Matrix A(m x n)
	 * @param m2 Vector x (n x 1)
	 * @return  Matrix Ax (m x 1)
	 */
	public static DoubleMatrix multiply(AbstractMatrix m, AbstractVector vec) {
	    return multiply(m, new DoubleMatrix(vec));
	}
	
	
	/**
	 * 
	 * The naive O(n^3) implementation of the matrix multiplication 
	 * (which is reportedly very hard to beat in praxis)
	 * 
	 * @param m1 Matrix A (m x n)
	 * @param m2 Matrix B (n x p)
	 * @return  Matrix AB (m x p)
	 */
	public static DoubleMatrix multiply(AbstractMatrix m1, AbstractMatrix m2) {
	    if (m1.ncol() != m2.nrow()) {
	        throw new IllegalArgumentException("An \"m x n\" matrix requires an \"n x p\" matrix for multiplication.");
	    }
	    DoubleMatrix result = new DoubleMatrix(m1.nrow(), m2.ncol());
	    for (int m = 0; m != m1.nrow(); ++m) {
	        for (int p = 0; p != m2.ncol(); ++p) {
	            double c = result.getValue(m,p);
	            c = 0.0;
	            for (int n = 0; n != m1.ncol(); ++n) {
	                c += m1.getValue(m,n) * m2.getValue(n,p);
	            }
	            result.setValue(m,p,c);
	        }
	    }
	    return result;
	}
	
	
	public static PermutableMatrix bindColumns(AbstractVector... cd) {
		return new VectorBasedMatrix(cd, false);
	}
	
	public static PermutableMatrix bindColumns(Collection<AbstractVector> cd) {
		return bindColumns(cd.toArray(new DoubleVector[0]));		
	}
	
	public static PermutableMatrix bindRows(AbstractVector... cd) {
		return new VectorBasedMatrix(cd, true);
	}
	
	public static PermutableMatrix bindRows(Collection<AbstractVector> cd) {
		return bindColumns(cd.toArray(new DoubleVector[0]));		
	}
}
