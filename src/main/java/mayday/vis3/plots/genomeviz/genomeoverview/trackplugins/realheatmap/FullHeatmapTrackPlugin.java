package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.realheatmap;

import java.util.HashMap;

import javax.swing.AbstractAction;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class FullHeatmapTrackPlugin extends AbstractTrackPlugin{

	/* we are an plugin
	and extend trackplugin
	 */
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo((Class) this.getClass(),
				"PAS.core.ChromeOverview.FullHeatmapPlugin", 
				new String[0], 
				MC1,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Rendering a whole heatmap of all experiments",
				"Heatmap for all experiments");
		return pli;
	}
	
	public FullHeatmapTrackPlugin() {}
	
	protected AbstractTrackSettings makeSetting() {
		return new FullHeatmapTrackSettings(model, this);
	}
	
	protected AbstractTrackRenderer makeRenderer() {
		return new FullHeatmapTrackRenderer(model, this);
	}
	
	public AbstractAction getAddTracksForAllExperimentsAction(){
		return null;
	}
	
	protected int getDefaultHeight() {
		return 80;
	}

}
