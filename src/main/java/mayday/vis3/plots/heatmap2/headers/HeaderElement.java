package mayday.vis3.plots.heatmap2.headers;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import mayday.core.settings.Setting;
import mayday.vis3.plots.heatmap2.interaction.UpdateListener;

public interface HeaderElement {

	/** 
	 * 
	 * @return the size of the header element in the fixed dimension (x for row headers, y for col headers);
	 */
	public int getSize();
	
	public String getName();	
	public Setting getSetting();
	
	public MouseListener getMouseListener();
	public MouseWheelListener getMouseWheelListener();
	public MouseMotionListener getMouseMotionListener();
	
	public void addUpdateListener(UpdateListener ul);
	public void removeUpdateListener(UpdateListener ul);
	
	public String getPluginID();
	

	/** you have been removed from the view, please remove all your listeners */
	public void dispose();

}
