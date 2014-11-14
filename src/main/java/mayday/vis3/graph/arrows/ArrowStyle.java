package mayday.vis3.graph.arrows;

/**
 * Enumeration of arrow head types. Supports Open Arrows as well as triangles, circles, squared, diamonds, ellipses.
 * @see ArrowSettings
 * @author Stephan Symons
 * @version 1.0
 */
public enum ArrowStyle
{
	ARROW_OPEN,
	ARROW_TRIANGLE,
	ARROW_CIRCLE,
	ARROW_DIAMOND,
	ARROW_BAR,
	ARROW_BAR_AND_TRIANGLE,
	ARROW_BOX;
	public static final String[] styles={"open","triangle","circle","diamond","bar","bar and triangle","box","none"};
}
