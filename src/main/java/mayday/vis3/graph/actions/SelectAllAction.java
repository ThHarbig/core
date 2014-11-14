package mayday.vis3.graph.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.graph.GraphCanvas;


@SuppressWarnings("serial")
public class SelectAllAction extends AbstractAction 
{
	private GraphCanvas canvas;
	
	public SelectAllAction(GraphCanvas canvas )
	{
		super("Select All");
		this.canvas=canvas;
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		canvas.getSelectionModel().selectAll();
	}

}
