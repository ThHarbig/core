package mayday.vis3.plots.genomeviz.genomeoverview;

import java.awt.Color;

public class ConstantData {


	public static final int INITIAL_EXPERIMENT_VALUE = 0;
	
	public static final int INITIAL_TRACK_HEIGHT = 40;
	public static final int INITIAL_SCALA_HEIGHT = 40;
	public static final int INITIAL_CHROME_HEIGHT = 40;
	public static final int INITIAL_TRACKPAINTING_HEIGHT = 20;
	public static final int INITIAL_TRACKPAINTING_YPOSITION = 10;
	
	public static final long INITIAL_STARTPOSITION_OF_CHROMOSOME = 1;
	
	// Width needed for userpanel and infopanel (each 20 pixel)

	public static final int USER_PANEL_WIDTH = 180;
	
	public static final int LEFT_MARGIN = 5;
	public static final int RIGHT_MARGIN = 5;
	
	public static final int DUMMY_PANEL_WIDTH = USER_PANEL_WIDTH;


	public static final int INITIAL_UNUSABLE_SPACE_HEIGHT = 0;

	public static final int INITIAL_UNUSABLE_SPACE_WIDTH = USER_PANEL_WIDTH;
	
	public static final int INITIAL_LOCATION_USERPANEL_X = 1;

	public static final int INITIAL_LOCATION_USERPANEL_Y = 1;

	public static final Color TRANSPARENT_COLOR_GREY = new Color( 191, 191, 191, 127);

	public static final Color TRANSPARENT_COLOR_RED = new Color( 250, 191, 191, 127);	

	public static int INITIAL_HEIGHT_CHROMEPANEL = 40;

	public static int MARKER_DIFF = 70;
	
	public static final int DIRECTPAINT_BELOW_BP_PER_PIXELS = -1; // was 5 // set to negative to ALWAYS use buffered painting.
}
