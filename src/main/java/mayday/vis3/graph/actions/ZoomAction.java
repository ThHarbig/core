package mayday.vis3.graph.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.dialog.ComponentZoomFrame;

@SuppressWarnings("serial")
public class ZoomAction extends AbstractAction
{
	private CanvasComponent component;
	private ComponentZoomFrame zoom;
	
	public ZoomAction(CanvasComponent comp, ComponentZoomFrame zoom)
	{
		super();
		component=comp;
		this.zoom=zoom;
		putValue(NAME, component.getLabel());
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		zoom=new ComponentZoomFrame(component,component.getRenderer());
		zoom.setLocation(component.getLocationOnScreen());
		zoom.setVisible(true);
	}		
}
