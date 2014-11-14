package mayday.vis3.plots.genomeviz.genomeoverview.panels.buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;

import mayday.vis3.components.PlotButton;

public class MoveDown_Button extends PlotButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3664494296195807976L;

	public MoveDown_Button() {
		super();
		setSize(new Dimension(11,11));
		setPreferredSize(new Dimension(11,11));
		this.setBackground(Color.WHITE);
		this.setBorder(BorderFactory.createLineBorder(Color.black, 1));
	}

	public void paint(Graphics g) {
		super.paint(g);
		if (isShowing()) { 
			Graphics2D g2D = (Graphics2D)g;
			g2D.setColor(Color.BLACK);

			int[] xcoords = {1, 5, 10}; 
		    int[] ycoords = {5, 10, 5}; 
		    g.fillPolygon (xcoords, ycoords, xcoords.length); 
		}
	}
}
