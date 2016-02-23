package mayday.vis3.gradient;

import java.awt.Color;

/**
 * @author symons
 * Defines constants for handling predefined color gradients. 
 */
public enum PredefinedGradients {
	
	GREEN_BLACK_RED_GRADIENT (Color.green, Color.black, Color.red, "Green - Black - Red"),
	RED_BLACK_GREEN_GRADIENT (Color.red, Color.black, Color.green, "Red - Black - Green"),
	
	BREWER_BLUE_WHITE_RED (new Color(33,102,172), Color.WHITE, new Color(178,24,43), "ColorBrewer: Blue-White-Red"),
	BREWER_BLUE_BLACK_RED (new Color(33,102,172), Color.BLACK, new Color(178,24,43), "ColorBrewer: Blue-Black-Red"),
	
	BREWER_RED_WHITE_BLUE (new Color(178,24,43), Color.WHITE, new Color(33,102,172), "ColorBrewer: Red-White-Blue"),
	BREWER_RED_BLACK_BLUE (new Color(178,24,43), Color.BLACK, new Color(33,102,172), "ColorBrewer: Red-Black-Blue"),
	
	BREWER_GREEN_YELLOW_RED (new Color(26,152,80), new Color(254,224,139), new Color(215,48,39), "ColorBrewer: Green-Yellow-Red"),
	BREWER_RED_YELLOW_GREEN (new Color(215,48,39), new Color(254,224,139), new Color(26,152,80), "ColorBrewer: Red-Yellow-Green"),
	
	BREWER_GREEN_BLACK_RED (new Color(26,152,80), Color.BLACK, new Color(215,48,39), "ColorBrewer: Green-Black-Red"),
	BREWER_RED_BLACK_GREEN (new Color(215,48,39), Color.BLACK, new Color(26,152,80), "ColorBrewer: Green-Black-Red"),
	
	BREWER_BROWN_WHITE_PURPLE (new Color(179,88,6), Color.WHITE, new Color(84,39,136), "ColorBrewer: Brown-White-Purple"),
	BREWER_PURPLE_WHITE_BROWN (new Color(84,39,136), Color.WHITE, new Color(179,88,6), "ColorBrewer: Purple-White-Brown"),
	
	BREWER_BROWN_WHITE_CYAN (new Color(140,81,10), Color.WHITE, new Color(1,102,94), "ColorBrewer: Brown-White-Cyan"),
	BREWER_CYAN_WHITE_BROWN (new Color(1,102,94), Color.WHITE, new Color(140,81,10), "ColorBrewer: Cyan-White-Brown"),
	
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
