package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.scale;

import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;

public class ScaleTrackSettings extends AbstractTrackSettings{
	
	public ScaleTrackSettings(GenomeOverviewModel Model, AbstractTrackPlugin Tp) {
		super(Model, Tp);		
		
		strand = null;
		coloring.removeNotify();
		coloring = null;
		identString = null;
	}

	@Override
	public void setInitialExperiment(int experiment) {
		// never called		
	}
	
}