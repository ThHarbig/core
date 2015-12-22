package mayday.vis3.graph.arrows;

import java.awt.Color;


/**
 * Base class for all arrow types. The arrow is defined as (source) --Arrow--> (target). It 
 * can have arrow heads with different properties both at source and target. The actual 
 * implementation of the line depends on the subclasses. 
 * <br>
 * The shapes of the arrow heads are defined in {@link ArrowStyle}.
 * The further properties of arrow heads are filled, length, angle.
 *  
 * @author Stephan Symons
 * @version 1.0
 * @see ArrowStyle
 */
/**
 * @author symons
 *
 */
public class ArrowSettings
{
	/** How the arrow head should be rendered. */
	private ArrowStyle targetStyle=ArrowStyle.ARROW_TRIANGLE;
	
	/** How the arrow start should be rendered. */
	private ArrowStyle sourceStyle=ArrowStyle.ARROW_TRIANGLE;
	
	/** The angle of the arrow at the source. */
	private double sourceAngle=(Math.PI * 30.0 /180.0);
	
	/** The angle of the arrow at the target. */
	private double targetAngle=(Math.PI * 30.0 /180.0);
	
	/** The angle of the arrow at the source. */
	private int sourceLength=10;
	
	/** The angle of the arrow at the target. */
	private int targetLength=10;
	
	/** whether the arrow heat at the target should be filled */
	private boolean fillTarget=false;
	
	/** whether the arrow heat at the target should be filled */
	private boolean fillSource=false;
	
	/** the color the arrowheads should be filled with */
	private Color fillColor=Color.white;
	
	/** whether the arrow heat at the target should be rendered */
	private boolean renderTarget=true;
	
	/** whether the arrow heat at the target should be rendered */
	private boolean renderSource=false;

	/** the color the edge is painted in */
	private Color edgeColor=Color.black;
	
	/**
	 * Creates a default arrow: only the target head is drawn as a triangle, length 10, angle=30 degrees (in radians)
	 */
	public ArrowSettings() 
	{
	}
	
	
	/**
	 * @return the targetStyle
	 */
	public ArrowStyle getTargetStyle() {
		return targetStyle;
	}

	/**
	 * @param targetStyle the targetStyle to set
	 */
	public void setTargetStyle(ArrowStyle targetStyle) {
		this.targetStyle = targetStyle;
	}

	/**
	 * @return the sourceStyle
	 */
	public ArrowStyle getSourceStyle() {
		return sourceStyle;
	}

	/**
	 * @param sourceStyle the sourceStyle to set
	 */
	public void setSourceStyle(ArrowStyle sourceStyle) {
		this.sourceStyle = sourceStyle;
	}

	/**
	 * @return the sourceAngle
	 */
	public double getSourceAngle() {
		return sourceAngle;
	}

	/**
	 * @param sourceAngle the sourceAngle to set
	 */
	public void setSourceAngle(double sourceAngle) {
		this.sourceAngle = sourceAngle;
	}

	/**
	 * @return the targetAngle
	 */
	public double getTargetAngle() {
		return targetAngle;
	}

	/**
	 * @param targetAngle the targetAngle to set
	 */
	public void setTargetAngle(double targetAngle) {
		this.targetAngle = targetAngle;
	}

	/**
	 * @return the sourceLength
	 */
	public int getSourceLength() {
		return sourceLength;
	}

	/**
	 * @param sourceLength the sourceLength to set
	 */
	public void setSourceLength(int sourceLength) {
		this.sourceLength = sourceLength;
	}

	/**
	 * @return the targetLength
	 */
	public int getTargetLength() {
		return targetLength;
	}

	/**
	 * @param targetLength the targetLength to set
	 */
	public void setTargetLength(int targetLength) {
		this.targetLength = targetLength;
	}

	/**
	 * @return the fillTarget
	 */
	public boolean isFillTarget() {
		return fillTarget;
	}

	/**
	 * @param fillTarget the fillTarget to set
	 */
	public void setFillTarget(boolean fillTarget) {
		this.fillTarget = fillTarget;
	}

	/**
	 * @return the fillSource
	 */
	public boolean isFillSource() {
		return fillSource;
	}

	/**
	 * @param fillSource the fillSource to set
	 */
	public void setFillSource(boolean fillSource) {
		this.fillSource = fillSource;
	}

	/**
	 * @return the renderTarget
	 */
	public boolean isRenderTarget() {
		return renderTarget;
	}

	/**
	 * @param renderTarget the renderTarget to set
	 */
	public void setRenderTarget(boolean renderTarget) {
		this.renderTarget = renderTarget;
	}

	/**
	 * @return the renderSource
	 */
	public boolean isRenderSource() {
		return renderSource;
	}

	/**
	 * @param renderSource the renderSource to set
	 */
	public void setRenderSource(boolean renderSource) {
		this.renderSource = renderSource;
	}
	
	public static final ArrowSettings noArrows()
	{
		ArrowSettings res=new ArrowSettings();
		res.setRenderSource(false);
		res.setRenderTarget(false);		
		return res;
	}
	
	public static final ArrowSettings bothArrows()
	{
		ArrowSettings res=new ArrowSettings();
		res.setRenderSource(true);
		res.setRenderTarget(true);		
		return res;
	}


	/**
	 * @return the fillColor
	 */
	public Color getFillColor() {
		return fillColor;
	}


	/**
	 * @param fillColor the fillColor to set
	 */
	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}


	/**
	 * @return the edgeColor
	 */
	public Color getEdgeColor() {
		return edgeColor;
	}


	/**
	 * @param edgeColor the edgeColor to set
	 */
	public void setEdgeColor(Color edgeColor) {
		this.edgeColor = edgeColor;
	}
}
