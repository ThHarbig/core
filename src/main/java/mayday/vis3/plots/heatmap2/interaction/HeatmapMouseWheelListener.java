package mayday.vis3.plots.heatmap2.interaction;

import java.awt.Toolkit;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import mayday.vis3.plots.heatmap2.data.HeatmapStructure;

public class HeatmapMouseWheelListener implements MouseWheelListener {

	protected HeatmapStructure data;
	protected final static double MINZOOM=2;
	protected final static double MAXZOOM=50;
	
	public HeatmapMouseWheelListener(HeatmapStructure struct) {
		data=struct;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
		int CONTROLMASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		if ((e.getModifiers()&CONTROLMASK) == CONTROLMASK) {
			boolean zoomWidth = !e.isShiftDown();
			if (e.getWheelRotation()>0)
				zoom(false,zoomWidth,true);
			else 
				zoom(true,zoomWidth,true);
		}
	}
	
	public void zoom(boolean in, boolean x, boolean y) {
		double delta = in?1.2:0.8;
		double xdelta = x?delta:1;
		double ydelta = y?delta:1;
		double boxSizeX = data.getScaleX();
		double boxSizeY = data.getScaleY();
		boxSizeX*=xdelta;
		boxSizeY*=ydelta;
		boxSizeX = (boxSizeX<MINZOOM)?MINZOOM:boxSizeX;
		boxSizeX = (boxSizeX>MAXZOOM)?MAXZOOM:boxSizeX;
		boxSizeY = (boxSizeY<MINZOOM)?MINZOOM:boxSizeY;
		boxSizeY = (boxSizeY>MAXZOOM)?MAXZOOM:boxSizeY;
		data.scale(boxSizeX, boxSizeY);
	}

}
