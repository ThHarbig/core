package mayday.vis3.plots.genomeviz.genomeoverview.panels;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;

import mayday.core.settings.SettingsDialog;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;

@SuppressWarnings("serial")
public final class AddTrackAction extends AbstractAction{

	protected GenomeOverviewModel model;
	protected Controller c;
	protected AbstractTrackPlugin trackPlugin;
	
	public AddTrackAction(String text, GenomeOverviewModel Model, Controller C, AbstractTrackPlugin TrackPlugin){
		super(text);
		model = Model;
		c = C;
		trackPlugin = TrackPlugin;
	}
	
	public void actionPerformed(ActionEvent e){
		if(trackPlugin != null && trackPlugin.getTrackSettings()!= null){
			model.createNewTrack(trackPlugin);
			final SettingsDialog sd = trackPlugin.getTrackSettings().getDialog();
			if(sd != null){
				sd.setVisible(true);
				sd.addWindowListener(new WindowAdapter() {
					public void windowClosed(WindowEvent evt) {
						if (sd.canceled())
							model.removeTrack(trackPlugin);						
					}
				});
			}
		}
	}
}
