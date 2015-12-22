package mayday.vis3.graph.actions;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class ResetViewAction extends AbstractAction {

	private GraphCanvas canvas;
	
	public ResetViewAction(GraphCanvas canvas) 
	{
		super("Reset View");
		this.canvas=canvas;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		canvas.zoom(1);
		if(!canvas.getSelectionModel().getSelectedComponents().isEmpty())
		{
			CanvasComponent c=canvas.getSelectionModel().getSelectedComponents().get(0);
			Rectangle r=new Rectangle(c.getLocation(),c.getSize());
			for(int i=1; i!= canvas.getSelectionModel().getSelectedComponents().size(); ++i)
			{
				c=canvas.getSelectionModel().getSelectedComponents().get(i);
				r.add(c.getBounds());
			}
			canvas.center(r, true);
		}else
		{
			canvas.centerSilent(new Rectangle(0,0,100,100), true);
		}

	}

}
