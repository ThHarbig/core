package mayday.vis3.graph.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.edges.router.EdgeRouter;

@SuppressWarnings("serial")
public class EdgeRouterAction extends AbstractAction
{
	private EdgeRouter router;
	private GraphCanvas parent;
	
	public EdgeRouterAction(EdgeRouter router, String name, GraphCanvas parent)
	{
		super(name);
		this.router=router;
		this.parent=parent;
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		parent.setEdgeRouter(router);
		parent.updatePlot();
	}

}
