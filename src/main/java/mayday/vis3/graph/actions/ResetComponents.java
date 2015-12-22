package mayday.vis3.graph.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class ResetComponents extends AbstractAction 
{
	private GraphCanvas canvas;

	public ResetComponents(GraphCanvas canvas) 
	{
		super("Reset Components");
		this.canvas=canvas;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		for(CanvasComponent comp:canvas.getModel().getComponents())
		{
			comp.resetSize();
		}
		canvas.message("Component size restored");
	}
}
