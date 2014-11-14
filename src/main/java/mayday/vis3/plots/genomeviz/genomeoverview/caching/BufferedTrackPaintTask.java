/**
 * 
 */
package mayday.vis3.plots.genomeviz.genomeoverview.caching;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.LinkedList;

import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.VisibleRange_Object;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrack;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrackRenderer;

class BufferedTrackPaintTask extends AbstractTrackPaintTask {

	protected BufferedImage img; 		
	protected LinkedList<Integer> windowsToRender = null;
	protected HashSet<Integer> alreadyRendered  = new HashSet<Integer>();
	protected VisibleRange_Object range;
	protected GenomeOverviewModel model;

	public BufferedTrackPaintTask(ITrack tp, VisibleRange_Object range, GenomeOverviewModel model) {
		super(tp);
		this.range = range;
		this.model = model;
	}

	protected boolean moreWindows() {
		synchronized(this) {
			return windowsToRender==null || !windowsToRender.isEmpty();
		}
	}

	protected int getNextWindow() {
		synchronized (this) {
			if (windowsToRender==null || windowsToRender.size()==0)
				return -1;
			return windowsToRender.removeFirst();
		}
	}

	protected void addDone(int win) {
		synchronized(this) {
			alreadyRendered.add(win);
		}
	}

	public void pushCurrentView() {
		windowsToRender = null;
		if (sizeChanged())
			firstInit();
		else {
			trigger();
		}
	}

	protected boolean windowDone(int win) {
		synchronized(this) {
			return alreadyRendered.contains(win);				
		}
	}

	protected void render() {
		if (model.isDirectpaint())
			super.render();
		else {
			// render all waiting windows
			ITrackRenderer rd = t.getTrackPlugin().getTrackRenderer();
			while (moreWindows()) {

				synchronized(this) {
					if (die)
						return;
					if (model.isDirectpaint()) { // switch to other rendering method
						trigger(); 
						return;
					}
				}

				int nextWindow = getNextWindow();
				if (nextWindow==-1)
					return;
				
				if (!windowDone(nextWindow)) {
					int right = Math.min( img.getWidth(), (nextWindow+1)*TrackPaintManager.WINDOW_SIZE );
					int left = nextWindow*TrackPaintManager.WINDOW_SIZE;
//					System.out.println("Rendering "+t.getTrackPlugin().getClass().getSimpleName()+" rendering "+left+" - "+right+": "+(right-left));
					try {
						rd.renderWindow(img.getGraphics(), left, right, w_old, img.getHeight());
						addDone(nextWindow);							
					} catch (Throwable e) {
						synchronized(this) {
							if (windowsToRender!=null)
								windowsToRender.addFirst(nextWindow);
						}
						e.printStackTrace();
					}
//					System.out.println("Rendering "+t.getTrackPlugin().getClass().getSimpleName()+" rendering "+left+" - "+right+" finished");
					if (right >= range.getVis_leftPos_x() && left <= range.getVis_rightPos_x() ) {
						t.getTrackPlugin().repaintTrack();
					}
				}
			}
		}
	}

	protected void initializeAfterMajorChange() {
		super.initializeAfterMajorChange();
		if (!model.isDirectpaint()) {
			// add all windows
			img = t.getTrackPlugin().getTrackRenderer().createEmptyBufferedImage();
			windowsToRender = new LinkedList<Integer>();
			int s = 0;
			int e = img.getWidth()/TrackPaintManager.WINDOW_SIZE;
			for (int w = s; w<=e; ++w) {
				windowsToRender.add(w);
			}
			t.setBufferedImage(img);
			// add windows for current view in front
			initializeAfterScrolling();
		} else {
			t.setBufferedImage(null);
			windowsToRender = null;
		}
		alreadyRendered.clear();
	}

	@Override
	protected void initializeAfterScrolling() {
		if (!model.isDirectpaint()) {
			if (windowsToRender==null)
				windowsToRender = new LinkedList<Integer>();
			int start = range.getVis_leftPos_x()/TrackPaintManager.WINDOW_SIZE;
			int end = range.getVis_rightPos_x()/TrackPaintManager.WINDOW_SIZE;
			for (int w = start; w<=end; ++w) {
				windowsToRender.addFirst(w);
			}
		}
	}

}