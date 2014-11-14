package mayday.core.structures.linalg.impl;

import mayday.core.MasterTable;

public class MutableMasterTableColumn extends MasterTableColumn {

	public MutableMasterTableColumn(MasterTable mt, int colIndex) {
		super(mt, colIndex);
	}
	
	public MutableMasterTableColumn(MasterTable mt, int colIndex, String[] rowNames) {
		super(mt, colIndex, rowNames);
	}

	@Override
	protected void set0(int i, double v) {
		if (get0(i)!=v) {
			mt.getProbe(rows[i]).setValue(v,col);
		}
	}
	
}
