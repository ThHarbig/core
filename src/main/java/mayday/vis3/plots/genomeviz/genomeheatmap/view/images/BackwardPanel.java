package mayday.vis3.plots.genomeviz.genomeheatmap.view.images;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class BackwardPanel extends JPanel{
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 2684040868406305375L;

	public BackwardPanel(){
		this.setBackground(Color.BLACK);
		this.setForeground(Color.BLACK);
		this.setVisible(true);
		this.setOpaque(true);
		this.setSize(30, 30);
		this.setPreferredSize(new Dimension(30,30));
	}
	
	public void paintComponent (Graphics g)
    {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setBackground(Color.black);
		g2d.setColor(Color.BLACK);
		g2d.setColor(Color.red);
		g2d.drawLine(3, this.getHeight()/2, this.getWidth() - 3, this.getHeight()/2);
    }
}
