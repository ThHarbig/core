package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.model.Visualizer;

@SuppressWarnings("serial")
public class CloseAllAction extends AbstractAction {

	private Visualizer visualizer;
	
	public CloseAllAction(Visualizer pg) {
		super("Close All");
		visualizer=pg;
	}
	
	public void actionPerformed(ActionEvent e) {
		while (visualizer.getMembers().size()>0)
			visualizer.removePlot(visualizer.getMembers().iterator().next());		
	}

}
