package mayday.vis3.plots.genomeviz.genomeoverview.caching;

import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrack;

class SimpleTrackPaintTask extends AbstractTrackPaintTask {

	public SimpleTrackPaintTask(ITrack tp) {
		super(tp);
	}

	public void pushCurrentView() {
		if (sizeChanged())
			firstInit();
	}

	@Override
	protected void initializeAfterScrolling() {
		// nothing to do		
	}

}