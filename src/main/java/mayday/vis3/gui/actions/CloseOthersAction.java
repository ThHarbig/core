package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.model.Visualizer;
import mayday.vis3.model.VisualizerMember;

@SuppressWarnings("serial")
public class CloseOthersAction extends AbstractAction {

	private Visualizer visualizer;
	private VisualizerMember pgm;
	
	public CloseOthersAction(Visualizer pg, VisualizerMember dontCloseThisOne) {
		super("Close Others");
		visualizer=pg;
		pgm = dontCloseThisOne;
	}
	
	public void actionPerformed(ActionEvent e) {
		while (visualizer.getMembers().size()>1) {
			for (VisualizerMember nextpgm: visualizer.getMembers()) {
				if (nextpgm!=pgm) {
					visualizer.removePlot(nextpgm);		
					break;
				}
			}
		}
	}

}
