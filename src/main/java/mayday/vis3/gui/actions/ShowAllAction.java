package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.model.Visualizer;
import mayday.vis3.model.VisualizerMember;

@SuppressWarnings("serial")
public class ShowAllAction extends AbstractAction {

	private Visualizer visualizer;
	
	public ShowAllAction(Visualizer pg) {
		super("Show All");
		visualizer=pg;
	}
	
	public void actionPerformed(ActionEvent e) {
		for (VisualizerMember pgm : visualizer.getMembers())
			pgm.toFront();		
	}

}
