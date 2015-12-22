package mayday.vis3.plots.genomeviz.genomeoverview.caching;

import java.util.HashMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.DelayedUpdateTask;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.VisibleRange_Object;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrack;

public class TrackPaintManager implements SettingChangeListener, ChangeListener {

	public final static int WINDOW_SIZE = 50;
	public final static int WINDOWS_PER_TILE = 20; // tile=1000 px
	public final static int TILES_PER_TRACK = 20; // 20k pixels per track in memory
	
	protected HashMap<ITrack, AbstractTrackPaintTask> tracks = new HashMap<ITrack, AbstractTrackPaintTask>();
	protected VisibleRange_Object range;
	protected GenomeOverviewModel model;
	protected VisibleRangeUpdater visibleRangeUpdater = new VisibleRangeUpdater("Visible Range Updater");
	protected long BPdelta = -1;

	public TrackPaintManager(VisibleRange_Object vro, GenomeOverviewModel mdl) {
		range = vro;
		model = mdl;
		range.addListener(this);
	}
	
	public void stateChanged(ChangeEvent e) {
		visibleRangeUpdater.trigger();
	}
	
	public void destroy() {
		while (tracks.size()>0) {
			removeTrack(tracks.keySet().iterator().next());
		}
		range.removeListener(this);
	}

	
	public void addTrack(ITrack tp) {
		synchronized(tracks) {
			if (tp.getTrackPlugin().isBufferedPaint()) {
				tracks.put(tp, new TilingBufferedTrackPaintTask(tp, range, model));
			} else {
				tracks.put(tp, new SimpleTrackPaintTask(tp));
			}
			if (tp.getTrackPlugin()!=null && tp.getTrackPlugin().getTrackSettings()!=null)
				tp.getTrackPlugin().getTrackSettings().getRoot().addChangeListener(this);
		}
		tracks.get(tp).firstInit();
	}
	
	public void removeTrack(ITrack tp) {
		synchronized(tracks) {
			if (tracks.get(tp)!=null) {
				tracks.get(tp).setInactive(true);
				tracks.remove(tp);
				if (tp.getTrackPlugin()!=null && tp.getTrackPlugin().getTrackSettings()!=null)
					tp.getTrackPlugin().getTrackSettings().getRoot().removeChangeListener(this);
			}
		}
	}
	
	public void zoomChanged() {
		synchronized(tracks) {
			for (ITrack t : tracks.keySet()) {
				tracks.get(t).firstInit();
			}
		}		
	}

	public void stateChanged(SettingChangeEvent e) {
		synchronized(tracks) {
			for (ITrack t : tracks.keySet()) {
				if (t.getTrackPlugin().getTrackSettings()!=null) {
					if (e.hasSource(t.getTrackPlugin().getTrackSettings().getRoot())) {
						tracks.get(t).firstInit();
					}
				}
			}
		}
	}
	
	public static int getTileSize() {
		return WINDOW_SIZE*WINDOWS_PER_TILE;
	}
	
	public static int getTotalSize() {
		return WINDOW_SIZE*WINDOWS_PER_TILE*TILES_PER_TRACK;
	}

	
	protected class VisibleRangeUpdater extends DelayedUpdateTask {

		public VisibleRangeUpdater(String name) {
			super(name, 100);
		}

		@Override
		protected boolean needsUpdating() {
			return true;
		}

		@Override
		protected void performUpdate() {
			synchronized(tracks) {
				for (ITrack t : tracks.keySet()) {
					tracks.get(t).pushCurrentView();
				}
			}
		}
		
	}


	
}
