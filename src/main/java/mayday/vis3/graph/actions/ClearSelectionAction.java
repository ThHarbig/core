package mayday.vis3.graph.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.graph.GraphCanvas;


@SuppressWarnings("serial")
public class ClearSelectionAction extends AbstractAction 
{
	private GraphCanvas canvas;
	
	public ClearSelectionAction(GraphCanvas canvas)
	{
		super("Clear Selection");
		this.canvas=canvas;
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		canvas.getSelectionModel().clearSelection();
	}

}
