package mayday.vis3.plots.genomeviz.genomeoverview.panels.buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;

import mayday.vis3.components.PlotButton;

public class MoveToTop_Button extends PlotButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3203390001546300189L;

	public MoveToTop_Button() {
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
			
			int[] xcoords = {0, getWidth()/2, getWidth()}; 
		    int[] ycoords = {getHeight()/2+3, 2, getHeight()/2+3}; 
		    
		    g.setColor (Color.BLACK); 
		    g.fillPolygon (xcoords, ycoords, xcoords.length); 

//			g2D.drawLine(0, 1, getWidth(), 1);
//			g2D.drawLine(0, 2, getWidth(), 2);
		    
		    g2D.drawLine(1, 2, 9, 2);
		    g2D.drawLine(1, 1, 9, 1);
//			g2D.drawLine(0, 2, getWidth(), 2);
		}
	}
}
