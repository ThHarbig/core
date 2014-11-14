package mayday.core.structures.trees.painter;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.font.FontRenderContext;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import mayday.core.MaydayDefaults;

@SuppressWarnings("serial")
public abstract class NodeShape extends Polygon {

	protected double x,y,angle;
	protected String label;
	
	protected static final FontRenderContext frc = new FontRenderContext(
			MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.getTransform(), 
			false, 
			MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.usesFractionalMetrics() );
	
	public NodeShape(double X, double Y, String label, double Angle) {
		x=X;
		y=Y;
		angle=Angle;
		this.label=label;
	}
	
	public abstract void paintNodeShape(Graphics2D g, boolean selected);
	
	protected void addPoint(Point2D p) {
		addPoint((int)Math.round(p.getX()), (int)Math.round(p.getY()));
	}
	
	public double distance(double x, double y) {
		if (contains(x,y))
			return 0;
		double min = Double.POSITIVE_INFINITY;
		for(int i=0; i<npoints-1; ++i) {
			double x1=xpoints[i], x2=xpoints[i+1], y1=ypoints[i], y2=ypoints[i+1];
			min = Math.min(min, Line2D.ptSegDist(x1, y1, x2, y2, x, y));			
		}
		min = Math.min(min, Line2D.ptSegDist(xpoints[0], ypoints[0], xpoints[npoints-1], ypoints[npoints-1], x, y));
		return min;		
	}
}
