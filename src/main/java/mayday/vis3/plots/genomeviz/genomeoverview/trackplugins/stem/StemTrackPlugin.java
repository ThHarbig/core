package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.stem;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class StemTrackPlugin extends AbstractTrackPlugin{

	/* we are an plugin
	and extend trackplugin
	 */
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo((Class) this.getClass(),
				"PAS.core.ChromeOverview.StemtrackPlugin", 
				new String[0], 
				MC1,
				new HashMap<String, Object>(),
				"Christian Zipplies",
				"zipplies@informatik.uni-tuebingen.de",
				"Track rendering like stemtrack.",
				"Stem");
		return pli;
	}

	public StemTrackPlugin() {}

	protected int getDefaultHeight() {
		return 60;
	}
	
	protected AbstractTrackSettings makeSetting() {
		return new StemTrackSettings(model, this);
	}
	
	protected AbstractTrackRenderer makeRenderer() {
		return new StemTrackRenderer(model, this,paintingpanel);
	}
	
}
