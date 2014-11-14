package mayday.vis3.plots.genomeviz.genomeoverview.caching;

import mayday.core.DelayedUpdateTask;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrack;

public abstract class AbstractTrackPaintTask extends DelayedUpdateTask {

	protected ITrack t;
	protected int w_old=-1;
	protected int h_old=-1;	
	protected boolean die = false;
	protected boolean needInit = false;
	
	public AbstractTrackPaintTask(ITrack tp) {
		super("Track Updater", 50);
		t = tp;
	}
	
	public final void firstInit() {
		// add everything to the render queue
		synchronized(this) {
			needInit = true;
		}
		trigger();
	}
	
	protected boolean sizeChanged() {
		int w_new = t.getTrackPlugin().getPaintingPanel().getWidth();
		int h_new = t.getTrackPlugin().getPaintingPanel().getHeight();
		if (w_old!=w_new || h_old!=h_new) {
			w_old=w_new;
			h_old=h_new;
			return true;
		}
		return false;
	}
	
	public abstract void pushCurrentView();
	
	protected abstract void initializeAfterScrolling();
	
	protected final boolean needsUpdating() {
		return true;
	}
	
	protected void initializeAfterMajorChange() {
		t.getTrackPlugin().getTrackRenderer().updateInternalVariables();
	}
	
	protected void render() {
		t.getTrackPlugin().repaintTrack();	
	}
	
	protected final void performUpdate() {
		
		// STOP immediately if asked to do so.
		synchronized(this) {
			if (die)
				return;
		}
		
		// after zoom or settings change, do a first init of the track and add all windows to the rendering queue
		synchronized(this) {
			if (needInit) {
				initializeAfterMajorChange();
				needInit = false;
			} else {
				initializeAfterScrolling();
			}
		}
		
		t.setDrawing(true);

		render();
	}
	
	public final void setInactive(boolean inactive) {
		die = inactive;
	}
	
}