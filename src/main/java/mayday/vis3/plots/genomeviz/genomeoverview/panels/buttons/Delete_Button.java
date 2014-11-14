package mayday.vis3.plots.genomeviz.genomeoverview.panels.buttons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.BorderFactory;

import mayday.vis3.components.PlotButton;

public class Delete_Button extends PlotButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2607924071087671958L;


	public Delete_Button() {
		super();
		setSize(new Dimension(11,11));
		setPreferredSize(new Dimension(11,11));
		this.setBackground(Color.WHITE);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
	}

	
	public void paint(Graphics g) {
		super.paint(g);
		if (isShowing()) { 
			Graphics2D g2D = (Graphics2D)g;
			g2D.setColor(Color.RED);
			
			 Stroke stroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND, 0,
				        new float[] { 2, 0 }, 0);
			 
			 
			g2D.setStroke(stroke);
			g2D.drawLine(1, 1, 9,9);
			g2D.drawLine(1, 9, 9, 1);
			
//			g2D.drawLine(1, 1, 9, 9);
//			g2D.drawLine(1, 1, 9, 9);
//			g2D.drawLine(0, getHeight(), getWidth(), 0);
		}
	}
}
