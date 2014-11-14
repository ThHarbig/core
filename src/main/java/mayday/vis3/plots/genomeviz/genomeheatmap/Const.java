package mayday.vis3.plots.genomeviz.genomeheatmap;

public final class Const {
 
	// stores how many columns are reserved as placeholder (not filled with probes) at the beginning/end of table
	public final static int FRONT_UNUSEDCOLUMNS = 1;
	public final static int BACK_UNUSEDCOLUMNS = 1;
	// stores number of columns which contains no experiment Data
	public final static int UNUSEDCOLUMNS = 2;
	
	public final static int FRONT_UNUSEDROWS = 0;
	public final static int BACK_UNUSEDROWS = 1;
	// stores number of rows which contains no experiment Data
	public final static int UNUSEDROWS = 1;
	
	// number of additional rows which are painted
	public final static int IMAGE_ROW_BUFFER = 7;
	
	// default startposition each chromosome
	public final static int CHROMOSOME_STARTPOSITION = 1;
}
