package mayday.vis3.plots.genomeviz.genomeheatmap.view.images;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class ForwardPanel extends JPanel{
	
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = -7761471566954971528L;



	public ForwardPanel(){
		this.setBackground(Color.BLACK);
		this.setVisible(true);
		this.setOpaque(true);
		
	}
	
	
	
	public void paintComponent (Graphics g)
    {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setBackground(Color.BLACK);
		g2d.setColor(Color.red);
		g2d.drawLine(3, this.getHeight()/2, this.getWidth()-3, this.getHeight()/2);
		g2d.drawLine(this.getWidth() /2, this.getHeight() -3 , this.getWidth() /2, 3);
    }		
}
