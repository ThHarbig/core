package mayday.vis3.graph.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;


@SuppressWarnings("serial")
public class ShowAllAction extends AbstractAction
{
	private GraphModel model;
	public ShowAllAction(GraphModel model)
	{
		super("Show All");
		this.model=model;
	}

	public void actionPerformed(ActionEvent e)
	{
		for(CanvasComponent comp:model.getComponents())
		{
			comp.setVisible(true);
		}		
	}

}
