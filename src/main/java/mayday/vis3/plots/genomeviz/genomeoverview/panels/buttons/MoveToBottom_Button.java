package mayday.vis3.plots.genomeviz.genomeoverview.panels.buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;

import mayday.vis3.components.PlotButton;

public class MoveToBottom_Button extends PlotButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = 416120268126525926L;

	public MoveToBottom_Button() {
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
			g2D.setColor(Color.BLACK);
			
//			int[] xcoords = {0, getWidth()/2, getWidth()}; 
			int[] xcoords = {1, 5, 10};
			int[] ycoords = {3, 8, 3}; 
			
//		    int[] ycoords = {getHeight()/2-2, getHeight()-2, getHeight()/2-2}; 
		    g.fillPolygon (xcoords, ycoords, xcoords.length); 
		    
		    g2D.drawLine(1, 9, 9, 9);
			g2D.drawLine(1, 8, 9,8);
		}
	}
}
