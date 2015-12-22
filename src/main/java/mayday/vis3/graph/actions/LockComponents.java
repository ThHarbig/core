package mayday.vis3.graph.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class LockComponents extends AbstractAction
{
	private GraphCanvas canvas;

	public LockComponents(GraphCanvas canvas) 
	{
		super("Lock/Unlock Components");
		this.canvas=canvas;		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		for(CanvasComponent comp:canvas.getModel().getComponents())
		{
			comp.toggleLocked();
		}
		canvas.message("Components locked/unlocked");
		
	}
}
