/**
 * 
 */
package mayday.vis3.plots.genomeviz.genomeoverview.caching;

import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.VisibleRange_Object;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrack;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrackRenderer;

class TilingBufferedTrackPaintTask extends BufferedTrackPaintTask {

	protected TileCache cache;

	public TilingBufferedTrackPaintTask(ITrack tp, VisibleRange_Object range, GenomeOverviewModel model) {
		super(tp, range, model);
		cache = new TileCache();
		tp.setTileCache(cache);
	}


	protected void render() {
		if (model.isDirectpaint())
			super.render(); // will go up to AbstractTrackPaintTask.render();
		else {
			// render all waiting windows
			ITrackRenderer rd = t.getTrackPlugin().getTrackRenderer();
			while (needsUpdating()) {

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
				
				int h = t.getTrackPlugin().getPaintingPanel().getHeight();
				int w = t.getTrackPlugin().getPaintingPanel().getWidth();

				if (!windowDone(nextWindow)) {
					int windowStart = nextWindow * TrackPaintManager.WINDOW_SIZE;
					int windowEnd = (nextWindow+1)*TrackPaintManager.WINDOW_SIZE; 
					int tileStart = (windowStart/TrackPaintManager.getTileSize()) * TrackPaintManager.getTileSize();
					Graphics2D tileGraphics = cache.getGraphicsForTile(tileStart, h, w);
					windowEnd = Math.min(windowEnd,w); // the very last window need not be painted more than the length of the track
					windowStart = Math.min(windowStart, w);
//					System.out.println("Rendering "+t.getTrackPlugin().getClass().getSimpleName()+" rendering "+windowStart+" - "+windowEnd);
					try {
						rd.renderWindow(tileGraphics, windowStart, windowEnd, w, h);
						addDone(nextWindow);							
					} catch (Throwable e) {
						synchronized(this) {
							if (windowsToRender!=null)
								windowsToRender.addFirst(nextWindow);
						}
						e.printStackTrace();
					}
					//						System.out.println("Rendering "+t.getTrackPlugin().getClass().getSimpleName()+" rendering "+left+" - "+right+" finished");
					if (windowEnd >= range.getVis_leftPos_x() && windowStart <= range.getVis_rightPos_x() ) {
						t.getTrackPlugin().repaintTrack();
					}
				}
			}
		}
	}

	protected void initializeAfterMajorChange() {
		t.getTrackPlugin().getTrackRenderer().updateInternalVariables();
		cache.dropTiles();
		alreadyRendered.clear();
		// use tiling mode if the threshold is reached
		int w = t.getTrackPlugin().getPaintingPanel().getWidth();
		if (w > TrackPaintManager.getTotalSize()) {
			cache.setTileSize(TrackPaintManager.getTileSize()); // start tiling
		} else {
			cache.setTileSize(0); // no tiling
		}
		initializeAfterScrolling();
	}	

	@Override
	protected void initializeAfterScrolling() {
		if (!model.isDirectpaint()) {
			int start_of_tiling, end_of_tiling;
			int start_of_windows, end_of_windows;
			windowsToRender = new LinkedList<Integer>();
			
			if (cache.isTiling()) {
				int centerView = range.getVis_centerPos_x();				
				int totalCoveredArea = TrackPaintManager.getTotalSize();			
				// find out which tiles need to be done
				start_of_tiling = centerView - totalCoveredArea/2;
				end_of_tiling = centerView + totalCoveredArea/2 - TrackPaintManager.getTileSize();
				start_of_tiling = Math.max(0, start_of_tiling);
				end_of_tiling = Math.min(
						t.getTrackPlugin().getPaintingPanel().getWidth()+TrackPaintManager.getTileSize()-1, 
						end_of_tiling);
				start_of_windows = start_of_tiling/TrackPaintManager.WINDOW_SIZE;
				end_of_windows = end_of_tiling/TrackPaintManager.WINDOW_SIZE;

				int ts = TrackPaintManager.getTileSize();
				// find out which tiles are will be needed 
				// this will also remove tiles from the cache that are outside of the current tiling region
				List<Integer> missingTiles = new LinkedList<Integer>();
				for (int tile = start_of_tiling/ts; tile<=end_of_tiling/ts; tile++) {  //correct end condition?
					missingTiles.add(tile*ts);
				}
				cache.updateMissingTiles(missingTiles);
				// from the list of already rendered windows, remove those that were rendered to tiles we no longer have cached
				if (missingTiles.size()>0) {
					Iterator<Integer> i = alreadyRendered.iterator();
					while (i.hasNext()) {
						int win = i.next() * TrackPaintManager.WINDOW_SIZE;
						for (int tile : missingTiles) {
							if (win>=tile && win<tile+ts)
								i.remove();
						}
					}
				}					
				
			} else {
				start_of_windows = 0; 
				end_of_windows = t.getTrackPlugin().getPaintingPanel().getWidth()/TrackPaintManager.WINDOW_SIZE;
			}		
			
			// add all prospective windows to the rendering queue
			for (int w = start_of_windows; w<=end_of_windows; ++w) {
				if (!alreadyRendered.contains(w))
					windowsToRender.addFirst(w);
			}
			
			// add current viewport in front
			int start = range.getVis_leftPos_x()/TrackPaintManager.WINDOW_SIZE;
			int end = range.getVis_rightPos_x()/TrackPaintManager.WINDOW_SIZE;
			for (int w = start; w<=end; ++w) {
				if (!alreadyRendered.contains(w))
					windowsToRender.addFirst(w);
			}
		}
	}
	
}
