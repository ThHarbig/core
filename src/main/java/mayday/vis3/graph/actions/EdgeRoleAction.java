package mayday.vis3.graph.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.graph.GraphCanvas;

@SuppressWarnings("serial")
public class EdgeRoleAction extends AbstractAction
{
	private GraphCanvas canvas;
	private String role;

	public EdgeRoleAction(GraphCanvas canvas, String role) 
	{
		super(role);
		this.canvas = canvas;
		this.role = role;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		canvas.getHighlightEdge().setRole(role);		
	}
}
