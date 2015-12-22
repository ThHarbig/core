package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.heatmap;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.genetics.basic.Strand;
import mayday.vis3.plots.genomeviz.genomeoverview.caching.TileCache;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.Track;

public class HeatmapTrackPlugin extends AbstractTrackPlugin {

	/* we are an plugin
	and extend trackplugin
	 */
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo((Class) this.getClass(),
				"PAS.core.ChromeOverview.HeatmaptrackPlugin", 
				new String[0], 
				MC1,
				new HashMap<String, Object>(),
				"Christian Zipplies",
				"zipplies@informatik.uni-tuebingen.de",
				"Track rendering like double heatmap.",
				"Heatmap");
		return pli;
	}

	public HeatmapTrackPlugin() {}



	protected AbstractTrackSettings makeSetting() {
		return new HeatmapTrackSettings(model, this);
	}

	protected AbstractTrackRenderer makeRenderer() {
		if (trackSettings.getStrand().equals(Strand.BOTH)) {
			return new TrackRenderer_dhm(model, this);
		} else {
			return new TrackRenderer_hm(model, this);
		}
	}
	 
	public void internalInit() {
		super.internalInit();
		if (trackSettings.getStrand()==Strand.BOTH && trackRenderer instanceof TrackRenderer_hm ||
			trackSettings.getStrand()!=Strand.BOTH && trackRenderer instanceof TrackRenderer_dhm) {
			// swapping tack renderers is unusual behaviour and should not be done. this is code to support the oldest genomeviz plugin ;) --fb
			trackRenderer = makeRenderer();
			Track t = (Track)getPaintingPanel().getParent();
			TileCache c = t.getTileCache();
			c.dropTiles();
			model.computeVisiblePositions(); //trigger repaint;
		}
	}

}
