package mayday.vis3.plots.genomeviz.genomeoverview.panels;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.pluma.PluginInfo;
import mayday.genetics.basic.Strand;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

@SuppressWarnings("serial")
public final class AddAllExperimentsAction extends AbstractAction{

	protected GenomeOverviewModel model;
	protected Controller c;
	protected PluginInfo pli;

	public AddAllExperimentsAction(GenomeOverviewModel Model, Controller C, PluginInfo pli){
		super(pli.getName());
		model = Model;
		c = C;
		this.pli = pli;
	}

	public void actionPerformed(ActionEvent e){

		for (int experiment = 0; experiment!=model.getMasterTable().getNumberOfExperiments(); ++experiment) {

			AbstractTrackPlugin trackPlugin = (AbstractTrackPlugin)pli.newInstance();
			trackPlugin.init(model, c);
			model.createNewTrack(trackPlugin);
			AbstractTrackSettings sett = trackPlugin.getTrackSettings();
			sett.setInitialExperiment(experiment);

			if (sett.getStrand()!=null && sett.getStrand()!=Strand.BOTH) { // add a second track for the minus strand
				trackPlugin = (AbstractTrackPlugin)pli.newInstance();
				trackPlugin.init(model, c);
				model.createNewTrack(trackPlugin);
				sett = trackPlugin.getTrackSettings();
				sett.setInitialExperiment(experiment);
				sett.setStrand(Strand.MINUS);
			}

		}

	}

}
