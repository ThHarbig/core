package mayday.core.structures.linalg.matrix;

import java.util.Random;

import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;



public abstract class PermutableMatrix extends NamedMatrix {

	/** if true, use rowNames instead of colNames, rowPermute instead of colPermute etc.*/
	protected boolean transposed = false;
	protected int[] rowPermute, colPermute;
	protected int[] reverseRowPermute, reverseColPermute;
		
	// ========= Internal handling of transposition =============
	protected boolean isTransposed() {
		return transposed;
	}
	
	protected int[] getPermutation(int dimension) {
		return dimension==0?rowPermute:colPermute;
	}

	// reverse perm is for speedup in getDimIndex
	protected int[] getReversePermutation(int dimension) {		
		int rperm[] = dimension==0?reverseRowPermute:reverseColPermute;
		if (rperm==null) {
			int[] perm = getPermutation(dimension);
			if (perm==null)
				return null;
			DoubleVector d = new DoubleVector(perm);
			rperm = d.order();
			if (dimension==0)
				reverseRowPermute = rperm;
			else 
				reverseColPermute = rperm;
		}
		return rperm; 
	}
	
	protected int mapDim(int dimension) {
		return transposed?1-dimension:dimension;
	}

	// ========= Internal handling of permutations ============= (after transposition!)
	protected int mapIndex(int dimension, int index) {
		int[] perm = getPermutation(mapDim(dimension));
		return perm==null?index:perm[index];	
	}
	
	protected int dim(int dimension) {
		return dim0(mapDim(dimension));
	}
	
	
	// ========== Internal handling of access =================== (after permutation and transposition)
	protected double get(int row, int column) {
		int rI = mapIndex(0,row);
		int cI = mapIndex(1,column);
		if (!isTransposed())
			return get0(rI, cI);
		else
			return get0(cI, rI);
	}
	
	protected void set(int row, int column, double value) {
		int rI = mapIndex(0,row);
		int cI = mapIndex(1,column);
		if (!isTransposed())
			set0(rI, cI, value);
		else
			set0(cI, rI, value);
	}
	
	// Naming: Apply transformations
	protected String getDimName(int dim, int index) {
		index = mapIndex(dim, index);
		return getDimName0(mapDim(dim),index);
	}
	
	public void setDimName(int dim, int index, String name) {
		index = mapIndex(dim, index);
		setDimName0(mapDim(dim), index, name); 
	}
	
	protected AbstractVector getDimVec(int dimension, int index) {
		int realIndex = mapIndex(dimension, index);
		int realDim = mapDim(dimension);
		return getDimVec0(realDim, realIndex);
	}
	
	protected AbstractVector getDimVec(int dimension, String name) {
		return getDimVec0(mapDim(dimension), name);
	}
	
	
	// ========= Cloning ========================================
	/** returns a view of the matrix that will not be affected by transpositions and permutations of the original matrix.
	 *  however, it will be affected by changes to the data in the original matrix! Changes to the shallow clone's data will
	 *  affect the parent matrix.
	 */
	public PermutableMatrix staticShallowClone() {
		return new PermutableMatrixClone(this);
	}


	// ============ Public access ===========================

	public void transpose() {
		transposed = !transposed;
	}	
	
	// Permutations

	private static int[] createPermutation(int length, Random r) {
		int[] permutation = new int[length];
		for (int i=0; i!=length; ++i)
			permutation[i] = i;
		
        for (int i=length; i>1; i--) {
        	int rndi = r.nextInt(length);
        	int tmp = permutation[i-1];
        	permutation[i-1] = permutation[rndi];
        	permutation[rndi] = tmp;
        }
        return permutation;
	}

	/** permute the columns of this matrix, i.e. rearrange their order.
	 */
	public void permuteColumns(Random r) {
        setColumnPermutation(createPermutation(ncol(), r));
	}
	
	/** permute the rows of this matrix, i.e. rearrange their order.
	 */
	public void permuteRows(Random r) {
		setRowPermutation(createPermutation(nrow(), r));
	}
	
	
	// Pseudo-sampling
	private static int[] createPseudoSample(int length, Random r) {
		int[] psample = new int[length];
		for (int i=0; i!=length; ++i)
			psample[i] = r.nextInt(length);
        return psample;
	}
	
	/** draw a pseudo-sample of the columns of this matrix. 
	 *  This is like permuteColumns BUT the resulting permutation
	 *  may contain each column more than once, or zero times.
	 *  I.e., this is sampling WITH replacement.
	 */
	public void pseudoSampleColumns(Random r) {		
        setColumnPermutation(createPseudoSample(ncol(), r));
	}
	
	/** draw a pseudo-sample of the rows of this matrix. 
	 *  This is like permuteRows BUT the resulting permutation
	 *  may contain each row more than once, or zero times.
	 *  I.e., this is sampling WITH replacement.
	 */
	public void pseudoSampleRows(Random r) {
        setRowPermutation(createPseudoSample(nrow(), r));
	}
	
	/** permute the columns of this matrix, i.e. rearrange their order.
	 */
	public void setRowPermutation(int[] permutation) {
		if (isTransposed()) {
			colPermute = permutation;
			reverseColPermute = null;
		}
		else {
			rowPermute = permutation;
			reverseRowPermute = null;
		}
		
	}
	
	public void setColumnPermutation(int[] permutation) {
		if (!isTransposed()){
			colPermute = permutation;
			reverseColPermute = null;
		} else {
			rowPermute = permutation;
			reverseRowPermute = null;
		}
	}
	
	public void unpermuteRows() {
		setRowPermutation(null);
		
	}
	
	public void unpermuteColumns() {
		setColumnPermutation(null);
	}

	protected Integer getDimIndex(int dimension, String name) {
		Integer idx = super.getDimIndex(mapDim(dimension), name);
		int[] perm = getReversePermutation(mapDim(dimension));
		if (perm!=null && idx!=null) {
			idx = perm[idx];
		}		
		return idx;
	}
	
}
