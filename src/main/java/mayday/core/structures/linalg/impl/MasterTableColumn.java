package mayday.core.structures.linalg.impl;

import mayday.core.MasterTable;
import mayday.core.structures.linalg.vector.AbstractVector;

public class MasterTableColumn extends AbstractVector {

	protected MasterTable mt;
	protected int col;
	protected String[] rows;
	
	public MasterTableColumn(MasterTable mt, int colIndex) {
		this(mt, colIndex, null);
	}
	
	/** Efficiency method: If you already have the array of row names for the MasterTable, this method is faster
	 * and used less memory.
	 */
	public MasterTableColumn(MasterTable mt, int colIndex, String[] rowNames) {
		this.mt = mt;		
		this.col = colIndex;
		if (col>=mt.getNumberOfExperiments() || col<0)
			throw new IllegalArgumentException("Column index is out of range for the mastertable: "+col+", where max is "+(mt.getNumberOfExperiments()-1));
		rows = rowNames;
		if (rows==null)
			rows = mt.getProbes().keySet().toArray(new String[0]);
	}
	
	public String[] getRowNames() {
		return rows;
	}
	
	@Override
	protected double get0(int i) {
		Double d = mt.getProbe(rows[i]).getValue(col);
		if (d==null)
			return Double.NaN;
		return d;
	}

	@Override
	protected String getName0(int i) {
		return rows[i];
	}

	@Override
	protected void set0(int i, double v) {
		throw new UnsupportedOperationException("MasterTable values are immutable");
	}

	@Override
	protected void setName0(int i, String name) {
		throw new UnsupportedOperationException("MasterTable probe names are immutable");
	}

	@Override
	public int size() {
		return rows.length;
	}

}
