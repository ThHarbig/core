package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.model.VisualizerMember;

@SuppressWarnings("serial")
public class ShowPlotAction extends AbstractAction {

	private VisualizerMember visualizerMember;
	
	public ShowPlotAction(String title, VisualizerMember pgm) {
		super(title.length()>40?title.substring(0,40)+"...":title);
		visualizerMember=pgm;
	}
	
	public void actionPerformed(ActionEvent e) {
		visualizerMember.toFront();
		visualizerMember.requestFocus();		
	}

}
