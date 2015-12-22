package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.usageinfo;

import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.Track;

@SuppressWarnings("serial")
public class InfoTrack extends Track{
	
	public InfoTrack(GenomeOverviewModel genomeOverviewModel, int y_pos, int freeIndex,
			int width_LayeredPane, int height_trackpanel, AbstractTrackPlugin TrackPlugin) {
		super(genomeOverviewModel, y_pos, freeIndex, width_LayeredPane, height_trackpanel, TrackPlugin);

	}

	@Override
	public void resizeTrackheight(int h) {
		//not on this track
	}

}
