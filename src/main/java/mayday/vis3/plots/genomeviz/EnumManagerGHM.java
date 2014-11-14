package mayday.vis3.plots.genomeviz;

public class EnumManagerGHM{
	
	public enum GHMStyle{CLASSIC,MODERN};
	
	public enum StrandInformation{PLUS,MINUS,PLACEHOLDER,BORDER,BOTH};
	
	public enum KindOfChromeView {WHOLE,CONDENSED};

	public enum SplitView {mean,max,min};
	
	public enum ZoomLevel{one,two,five,ten,fifteen,twenty,twentyfive,fifty,hundred,twohundred,thousand, twothousand, fivethousand,fit}
	
	public enum ClickedSelection{ALL,NONE,SINGLE,PENDING};
	
	public enum ModelEvents{UPDATE,GRID};
	
	public enum KindOfData{STANDARD,BY_POSITION}
	
	public enum ProbeListColoring{COLOR_ALL_PROBELISTS,COLOR_HIGHEST_PROBELIST};
	
	public enum ActionMode{MODE_A,MODE_B,MODE_C,MODE_D,MODE_E};
}
