package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public interface ITrackRenderer {

	
	public BufferedImage getBufferedImage();

	public Dimension getPreferredSize();

	public void paint(Graphics g);

	public void render(Graphics g, int width, int height);
	
	public void renderWindow(Graphics g, int start, int end, int Width, int Height);
	
	/** all work that has to be done only once for a certain zoom level can be done here. */
	public void updateInternalVariables();
	
	/** update the tooltip */
	public String getInformationAtMousePosition(Point point);
	
	/** return null for default behaviour (probe selection etc), or your own listener for special actions.
	 * Right-clicking will ALWAYS open the default track popup menu 
	 * */
	
	public MouseListener getMouseClickHandler();

//	public BufferedImage createBufferedImage();
	
	public BufferedImage createEmptyBufferedImage();

}
