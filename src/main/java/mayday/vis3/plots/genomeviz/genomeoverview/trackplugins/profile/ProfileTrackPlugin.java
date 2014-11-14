package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.profile;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class ProfileTrackPlugin extends AbstractTrackPlugin{

	/* we are an plugin
	and extend trackplugin
	 */
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo((Class) this.getClass(),
				"PAS.core.ChromeOverview.ProfiletrackPlugin", 
				new String[0], 
				MC1,
				new HashMap<String, Object>(),
				"Christian Zipplies",
				"zipplies@informatik.uni-tuebingen.de",
				"Track rendering like profile track.",
				"Profiles");
		return pli;
	}
	
	public ProfileTrackPlugin() {}
	
	protected AbstractTrackSettings makeSetting() {
		return new ProfileTrackSettings(model, this);
	}
	
	protected AbstractTrackRenderer makeRenderer() {
		return new ProfileTrackRenderer(model, this);
	}

}
