
package mayday.vis3.graph.edges.strokes;

import static mayday.vis3.graph.edges.strokes.LineStyle.SOLID;

import java.awt.BasicStroke;

public class SolidStroke extends BasicStroke implements WidthStroke {

	private float width;

	public SolidStroke(float width) {
		super(width,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
		this.width = width;
	}

	public WidthStroke newInstanceForWidth(float w) {
		return new SolidStroke(w);
	}

	public LineStyle getLineStyle() {
		return SOLID;
	}

	public String toString() { return SOLID.toString() + " " + Float.toString(width); }
}


