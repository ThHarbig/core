package mayday.vis3.plots.genomeviz.genomeheatmap.scrollpane;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JScrollBar;

import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeoverview.Spaces;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.ChromosomeMarker;

public class ChromosomeScalaScrollBar extends JScrollBar {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 8230119891668891217L;
	protected GenomeHeatMapTableModel tableModel = null;
	private int marker_diff = 70;		// space between markers
    private int top_margin = 5;			// top space
    private int bottom_margin = 5;		// bottom space
	protected Spaces spaces = null;
	
	public ChromosomeScalaScrollBar(GenomeHeatMapTableModel tableModel) {
		super();
		this.tableModel = tableModel;
		this.spaces = new Spaces();
		this.addMouseMotionListener(new MouseMotionListener(){

			
			public void mouseDragged(MouseEvent e) {
				
			}

			
			public void mouseMoved(MouseEvent e) {
				//System.out.println(e.getPoint().getY());
			}
			
		});
	}

	public ChromosomeScalaScrollBar(int orientation, int value, int extent, int min,
			int max) {
		super(orientation, value, extent, min, max);
	}

	public ChromosomeScalaScrollBar(int orientation) {
		super(orientation);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		//System.out.println("Height " + height + " width " + width);
		Graphics2D g2 = (Graphics2D) g;
		//  g2.setBackground(Color.LIGHT_GRAY);
		g2.setColor(Color.BLACK);

		int width = getWidth();
		int height = getHeight();

		int reducedHeight = height - top_margin - bottom_margin;

		double lowestChromePos = tableModel.getViewStart();
		double highestChromePos = tableModel.getViewEnd();

		spaces.setUserSpace(reducedHeight);
		spaces.setValueSpace(highestChromePos - lowestChromePos);

		double[] tickmarks = ChromosomeMarker.tickmarks(lowestChromePos,
				highestChromePos, Math.max((reducedHeight) / marker_diff, 2));

		// draw zero line
		g2.drawLine(3, top_margin, 3, height - bottom_margin);

		double[] powers = new double[tickmarks.length];
		String[] units = ChromosomeMarker.units(tickmarks, powers, false, true);

		double y0 = top_margin + spaces.getFunctionValueOf(tickmarks[0]);
		String tick = String.format("%.2f " + units[0],
				(tickmarks[0] * powers[0]));
		g2.drawLine(0, (int) y0, width - 3, (int) y0);
		g2.drawString(tick, width - 10, (int) y0 - 2 + 1);
		System.out.println("y0 " + y0 + " tick " + tick);
		for (int i = 1; i != tickmarks.length; ++i) {
			double d = tickmarks[i];

			double x1 = top_margin + spaces.getFunctionValueOf(d);
			g2.drawLine(width - 3, (int) x1, width, (int) x1);

			tick = String
					.format("%.2f " + units[i], (tickmarks[i] * powers[i]));
			g2.drawString(tick, (int) (x1 - (i != tickmarks.length - 1 ? g2
					.getFontMetrics().stringWidth(tick) >> 1 : Math.min(g2
					.getFontMetrics().stringWidth(tick) >> 1, g2
					.getFontMetrics().stringWidth(tick)
					+ Math.abs(height - x1) - 1

			))), width - 18);

			double x2 = ((int) (y0 + x1)) >> 1;
			g2.drawLine((int) x2, width - 10, (int) x2, width - 5);

			if (i == tickmarks.length - 1) {
				x2 = ((int) Math.abs(y0 - x1)) >> 1;
				g2.drawLine((int) (x1 + x2), width - 10, (int) (x1 + x2),
						width - 5);
			}

			if (x1 > height - bottom_margin)
				break;
			y0 = x1;
		}
	}
}
