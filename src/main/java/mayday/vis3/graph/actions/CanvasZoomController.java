package mayday.vis3.graph.actions;

import mayday.vis3.ZoomController;
import mayday.vis3.graph.GraphCanvas;

public class CanvasZoomController extends ZoomController
{
	protected void zoom(boolean zoomIn, boolean zoomX, boolean zoomY) 
	{
		double zoomFac = zoomIn?1.2:0.8;
		((GraphCanvas)target).setZoomFactorIncrease(zoomFac);
		((GraphCanvas)target).updatePlot();
		super.zoom(zoomIn,zoomX,zoomY);
		
	}
}
