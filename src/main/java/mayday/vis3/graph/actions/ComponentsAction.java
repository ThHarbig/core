package mayday.vis3.graph.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.dialog.ComponentList;


@SuppressWarnings("serial")
public class ComponentsAction extends AbstractAction
{
	private GraphCanvas canvas; 
	
	public ComponentsAction(GraphCanvas canvas)
	{
		super("Node Inspector...");
		this.canvas=canvas;
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		ComponentList dialog=canvas.getComponentList();
		if(dialog==null)
		{
			dialog=new ComponentList(canvas.getModel(),canvas);
			canvas.setComponentList(dialog);
			
		}
		dialog.setVisible(true);
	}		
}
