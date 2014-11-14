package mayday.vis3.plots.genomeviz.genomeoverview.panels.buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;

import mayday.vis3.components.PlotButton;

public class MoveUp_Button extends PlotButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8918638197442703078L;

	public MoveUp_Button() {
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
			
			int[] xcoords = {0, getWidth()/2, getWidth()}; 
		    int[] ycoords = {getHeight()/2+1, 0, getHeight()/2+1}; 

		    g2D.setColor (Color.BLACK); 
		    g2D.fillPolygon (xcoords, ycoords, xcoords.length); 
		}
	}
}
