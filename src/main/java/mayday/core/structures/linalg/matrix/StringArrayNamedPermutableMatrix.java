package mayday.core.structures.linalg.matrix;



public abstract class StringArrayNamedPermutableMatrix extends PermutableMatrix {

	protected String[] rowNames, colNames;

	public StringArrayNamedPermutableMatrix() {}
	
	// Naming functions
	protected String[] getNamesArray0(int dim) {
		int exp = dim0(dim);
		if (dim==0) {
			if (rowNames==null)
				rowNames = new String[exp];
			return rowNames;
		} else {
			if (colNames==null)
				colNames = new String[exp];
			return colNames;
		}
	}
	
	@Override
	protected String getDimName0(int dim, int index) {
		String[] arr = dim==0?rowNames:colNames;
		if (arr==null)
			return null;
		return arr[index];
	}

	@Override
	protected void setDimName0(int dim, int index, String name) {
		String[] arr = getNamesArray0(dim);
		arr[index] = name;		
	}	
	
}
