package mayday.vis3.gui;



public interface PlotComponent {
	
	public void setup(PlotContainer plotContainer);
	
	/** Discard any buffered plot image and recreate the plot */
	public void updatePlot();
	
//	NOT ready yet...
//	/** return a setting object containing all info necessary to recreate the plot as it is now.
//	 * Recreation must be possible using the ViewModel and the Setting object. */
//	public boolean getPlotSetting();
//	
}
