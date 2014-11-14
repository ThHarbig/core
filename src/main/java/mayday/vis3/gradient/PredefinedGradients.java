package mayday.vis3.gradient;

import java.awt.Color;

/**
 * @author symons
 * Defines constants for handling predefined color gradients. 
 */
public enum PredefinedGradients {
	
	GREEN_BLACK_RED_GRADIENT (Color.green, Color.black, Color.red, "Green - Black - Red"),
	RED_BLACK_GREEN_GRADIENT (Color.red, Color.black, Color.green, "Red - Black - Green"),

	BLUE_WHITE_RED_GRADIENT (Color.blue, Color.white, Color.red, "Blue - White - Red"),
	RED_WHITE_BLUE_GRADIENT (Color.RED, Color.white, Color.BLUE, "Red - White - Blue"),

	HEAT_COLOR_GRADIENT (Color.red, Color.yellow, Color. white, "Heat colors (Red - Yellow - White)"),
	INVERSE_HEAT_COLOR_GRADIENT (Color.white, Color.yellow, Color.red, "Inverse Heat colors (White - Yellow - Red)"),

	BLUE_YELLOW_RED_GRADIENT (Color.blue, Color.yellow, Color. red, "Blue - Yellow - Red"),
	RED_YELLOW_BLUE_GRADIENT (Color.red, Color.yellow, Color. blue, "Red - Yellow - Blue"),


	WHITE_RED_GRADIENT (Color.white, null, Color.red, "White - Red"),
	RED_WHITE_GRADIENT (Color.red, null, Color.white, "Red - White"),

	WHITE_BLUE_GRADIENT (Color.white, null, Color.blue,"White - Blue"),
	BLUE_WHITE (Color.blue, null, Color.white,"Blue - White"),

	BLUE_RED_GRADIENT (Color.blue, null, Color.red,"Blue - Red"),
	RED_BLUE_GRADIENT (Color.red, null, Color.blue,"Red - Blue"),

	// grayscale
	GRAYSCALE_GRADIENT (Color.white, null, Color.black, "Grayscale (White - Black)"),
	GRAYSCALE_INVERSE_GRADIENT (Color.black, null, Color.white, "Grayscale (Black - White)"),

	// black body 
	BLACK_BODY_RADIATION (Color.black, Color.red, Color.white, "Black Body Radiation (Black - Red - White"),
	INVERSE_BLACK_BODY_RADIATION (Color.white, Color.red, Color.black, "Inverse Black Body Radiation (White - Red - Black");
	
	private final Color lower;
	private final Color mid;
	private final Color upper;
	private final String name;


	private PredefinedGradients(Color l, Color m, Color u, String n) {
		lower=l;
		mid=m;
		upper=u;
		name=n;
	}

	public Color getLower() {
		return lower;
	}

	public Color getMid() {
		return mid;
	}

	public Color getUpper() {
		return upper;
	}

	public String toString() {
		return name;
	}

}
