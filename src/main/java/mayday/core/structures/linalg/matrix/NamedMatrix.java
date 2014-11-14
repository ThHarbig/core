package mayday.core.structures.linalg.matrix;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mayday.core.structures.linalg.vector.AbstractVector;


public abstract class NamedMatrix extends AbstractMatrix {

	protected String name;
	
	protected Map<String, Integer> nameCache0;
	protected Map<String, Integer> nameCache1;
	
	
	public String getName() {
		return name;
	}	
	public void setName(String name) {
		this.name = name;
	}
	
	protected abstract String getDimName0(int dim, int index);
	protected abstract void setDimName0(int dim, int index, String name);
	
	protected String getDimName(int dim, int index) {
		return getDimName0(dim, index);
	}
	
	protected void setDimName(int dim, int index, String name){
		setDimName0(dim, index, name);
	}

	// Row/Column names 
	public String getRowName(int row) {
		return getDimName(0, row);
	}
	
	public String getColumnName(int column) {
		return getDimName(1, column);
	}
	
	public void setRowName(int row, String name) {
		setDimName(0, row, name);
	}
	
	public void setColumnName(int column, String name) {
		setDimName(1, column, name);
	}
	
	
	
	public void setRowNames(String[] RowNames) {
		if (RowNames.length!=nrow())
			throw new RuntimeException("Number of row names must match number of rows: "+nrow());
		int i=0;
		for (String s : RowNames)
			setRowName(i++, s);
	}
	
	public void setColumnNames(String[] ColNames) {
		if (ColNames.length!=ncol())
			throw new RuntimeException("Number of column names must match number of column: "+ncol());
		int i=0;
		for (String s : ColNames)
			setColumnName(i++, s);
	}
	
	public void setRowNames(Collection<String> RowNames) {
		if (RowNames.size()!=nrow())
			throw new RuntimeException("Number of row names must match number of rows: "+nrow());
		int i=0;
		for (String s : RowNames)
			setRowName(i++, s);
	}
	
	public void setColumnNames(Collection<String> ColNames) {
		if (ColNames.size()!=ncol())
			throw new RuntimeException("Number of column names must match number of column: "+ncol());
		int i=0;
		for (String s : ColNames)
			setColumnName(i++, s);
	}
	
	public void setRowNames(AbstractVector v) {
		setRowNames(v.getNamesList());
	}
	
	public void setColumnNames(AbstractVector v) {
		setColumnNames(v.getNamesList());
	}
	
	public void setRowNames(AbstractMatrix m) {
		setRowNames(m.getColumn(0));
	}
	
	public void setColumnNames(AbstractMatrix m) {
		setColumnNames(m.getRow(0));
	}


	
	// Vector access via name
	
	protected Map<String, Integer> buildNameCache(int dim) {
		Map<String, Integer> nc;
		int exp = dim0(dim);
		if (dim==0) {
			if (nameCache0==null)
				nameCache0=new HashMap<String, Integer>();
			nc = nameCache0;
		} else {
			if (nameCache1==null)
				nameCache1=new HashMap<String, Integer>();
			nc = nameCache1;
		}
		if (nc.size()!=exp) {
			nc.clear();
			for (int i=0; i!=exp; ++i)
				nc.put(getDimName0(dim, i), i);
		}
		return nc;
	}
	
	protected AbstractVector getDimVec0(int dimension, String name) {
		Map<String, Integer> nc = buildNameCache(dimension);
		int i = nc.get(name);
		return getDimVec0(dimension, i);
	}
	
	protected AbstractVector getDimVec(int dimension, String name) {
		return getDimVec0(dimension, name);
	}

	public final AbstractVector getRow(String name) {
		return getDimVec(0, name);
	}
	
	public final AbstractVector getColumn(String name) {
		return getDimVec(1, name);
	}

	protected Integer getDimIndex(int dimension, String name) {
		Map<String, Integer> nc = buildNameCache(dimension);
		Integer idx = nc.get(name);
		return idx;
	}
	
	public final Integer getRowIndex(String rowName) {
		return getDimIndex(0, rowName);
	}
	
	public final Integer getColumnIndex(String colName) {
		return getDimIndex(1, colName);
	}
	
	/** Access a value by its row&col name. Works only if there are names and is not reliable if names are not unique. 
	 * For speedup, this function will build a cache of names (might be memory expensive)
	 * If some names are not unique, the cache will be built EVERY TIME the function is called. (Which is slow).
	 * @return NaN if the name is not found
	 * */	
	public final double getValue( String rowName, String colName ) {
		Integer row = getRowIndex(rowName);
		if (row==null)
			return Double.NaN;
		Integer column = getColumnIndex(colName);
		if (column==null)
			return Double.NaN;
		return super.getValue(row, column);
	}
	
	/** Access a value by name. Works only if there are names and is not reliable if names are not unique. 
	 * For speedup, this function will build a cache of names (might be memory expensive)
	 * If some names are not unique, the cache will be built EVERY TIME the function is called. (Which is slow).
	 * @return NaN if the name is not found
	 * */	
	public final double getValue( String rowName, int column ) {
		Integer row = getRowIndex(rowName);
		if (row==null)
			return Double.NaN;
		return super.getValue(row, column);
	}
	
	/** Access a value by name. Works only if there are names and is not reliable if names are not unique. 
	 * For speedup, this function will build a cache of names (might be memory expensive)
	 * If some names are not unique, the cache will be built EVERY TIME the function is called. (Which is slow).
	 * @return NaN if the name is not found
	 * */	
	public final double getValue( int row, String colName) {
		Integer column = getColumnIndex(colName);
		if (column==null)
			return Double.NaN;
		return super.getValue(row, column);
	}
	
	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();
		res.append("{{");
		for (int i=0; i!=ncol(); ++i) {
			res.append(" ");
			res.append(getColumnName(i));
		}
		res.append("\n");
		for (int i=0; i!=nrow(); ++i) {
			res.append(getRowName(i));
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
