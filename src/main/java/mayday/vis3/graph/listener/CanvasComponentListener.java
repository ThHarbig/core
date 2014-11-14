package mayday.vis3.graph.listener;

import mayday.vis3.graph.components.CanvasComponent;

public interface CanvasComponentListener
{
	public void componentSelectionChanged(CanvasComponent component);
	public void componentMoveFinished();
	public void componentMoved(CanvasComponent sender, int dx, int dy);
}
