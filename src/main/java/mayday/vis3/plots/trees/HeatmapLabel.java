package mayday.vis3.plots.trees;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import mayday.core.Probe;
import mayday.core.structures.trees.layout.NodeLayout;
import mayday.core.structures.trees.layout.ObjectMapper;
import mayday.core.structures.trees.painter.NodeShape;
import mayday.core.structures.trees.painter.node.LabelWithAngle;
import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.structures.trees.tree.Node;
import mayday.vis3.ColorProvider;
import mayday.vis3.model.ViewModel;

public class HeatmapLabel extends LabelWithAngle {

	protected ColorProvider cp;	
	protected ObjectMapper mpr;
	protected ViewModel viewModel;

	protected final int boxsize = 4;

	public HeatmapLabel(ColorProvider cp, ObjectMapper mpr, ViewModel vm) {
		this.cp=cp;
		this.mpr=mpr;
		viewModel = vm;
	}

	public String toString() {
		return "Heatmap Nodes";
	}

	public void paint(Node n, Graphics g, ScreenLayout l, boolean selected) {
		Graphics2D g2 = ((Graphics2D)g);
		NodeShape ns = getNodeShape(n, l);
		g2.setColor(Color.black);
		ns.paintNodeShape(g2,selected);
		if (selected) {
			g2.setColor(Color.cyan);
			g2.draw(ns);
		}
	}

	protected NodeShape getNodeShape(double x, double y, Node n, NodeLayout nl, ScreenLayout sl, double angle) {
		if (n.isLeaf())
			return new HeatMapNodeShape(x,y,sl.getLabel(n).trim(),n,nl,angle);
		else
			return new QuadraticNodeShape(x,y,sl.getLabel(n).trim(),nl); 		
	}

	protected double[] getValues(Node n) {
		Probe pb = (Probe)mpr.getObject(n);
		if (pb!=null)
			return viewModel.getProbeValues(pb);
		return null;
	}

	@SuppressWarnings("serial")
	public class HeatMapNodeShape extends NodeShape {

		protected AffineTransform at;
		protected double h,w;
		protected Probe pb;

		protected void produceTransform() {
			at = new AffineTransform();			
			at.translate(x, y);
			at.rotate((90+angle)*Math.PI/180d);
			if (angle<0)
				at.translate(-w-5, (h-boxsize)/2);
			else
				at.translate(5, (h-boxsize)/2);
		}

		public HeatMapNodeShape(double X, double Y, String label, Node n, NodeLayout nl, double Angle) {
			super(X,Y,label,Angle);
			angle=Angle;
			pb = (Probe)mpr.getObject(n);
			double[] values = viewModel.getProbeValues(pb);			
			h=boxsize;
			if (values!=null) {
				w = boxsize * values.length;
			} else {
				w = boxsize;
			}
			produceTransform();						
			addPoint(at.transform(new Point2D.Double(0,0), null));
			addPoint(at.transform(new Point2D.Double(0,h), null));
			addPoint(at.transform(new Point2D.Double(w,h), null));	
			addPoint(at.transform(new Point2D.Double(w,0), null));
		}

		public void paintNodeShape(Graphics2D g, boolean seelcted) {
			g.setColor(Color.black);
			if (pb==null) {
				g.draw(this);
			} else {
				g.fill(this);
				AffineTransform before = g.getTransform();
				g.transform(at);
				for (double d : viewModel.getProbeValues(pb)) {
					Color c;
					try {
						c = cp.getColor(d);
					} catch (Exception e) {						
						c = cp.getColor(cp.getGradient().getMin());
					}
					g.setColor(c);
					g.fillRect(0, 0, boxsize, boxsize);
					g.translate(boxsize,0);
				}
				g.setTransform(before);
			}			
		}

	}
}
